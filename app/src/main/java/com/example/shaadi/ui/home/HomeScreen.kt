package com.example.shaadi.ui.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.shaadi.data.model.Profile
import com.example.shaadi.data.profile.ProfilesRepository
import com.example.shaadi.data.auth.CredentialStore
import com.example.shaadi.network.SupabaseRestApiClient
import com.example.shaadi.network.InterestsService
import com.example.shaadi.network.InterestDto
import com.example.shaadi.network.SupabaseApiClient
import com.example.shaadi.network.SupabaseAuthService
import com.example.shaadi.util.JwtUtils
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavController) {
    val profiles = remember { mutableStateListOf<Profile>() }
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val repo = remember { ProfilesRepository.getInstance(navController.context) }
    val store = remember { CredentialStore(navController.context.applicationContext) }
    val currentTab = remember { mutableStateOf(0) } // 0: Profiles, 1: Received, 2: Chats
    val received = remember { mutableStateListOf<InterestDto>() }
    val chats = remember { mutableStateListOf<InterestDto>() } // accepted interests act as conversations
    // Track the resolved current user id so we can correctly compute peer ids in the Chats list
    val myUserIdState = remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        val result = repo.fetchProfiles(limit = 100)
        if (result.isSuccess) {
            profiles.clear()
            // Hide current user's own profile from the list
            val uid = store.getToken()?.let { JwtUtils.getUserIdFromToken(it) }
            val list = result.getOrDefault(emptyList())
            profiles.addAll(if (uid.isNullOrBlank()) list else list.filter { it.id != uid })
        } else {
            scope.launch {
                snackbarHostState.showSnackbar(
                    message = result.exceptionOrNull()?.message ?: "Failed to load profiles",
                    withDismissAction = true
                )
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Shaadi") },
                actions = {
                    androidx.compose.material3.TextButton(onClick = {
                        store.clearToken()
                        store.clearPendingImageUri()
                        navController.navigate("login") {
                            popUpTo("home") { inclusive = true }
                        }
                    }) { Text("Logout") }
                }
            )
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    selected = currentTab.value == 0,
                    onClick = { currentTab.value = 0 },
                    label = { Text("Profiles") },
                    icon = {}
                )
                NavigationBarItem(
                    selected = currentTab.value == 1,
                    onClick = {
                        currentTab.value = 1
                        // Load received interests
                        val token = store.getToken()
                        if (token != null) {
                            scope.launch {
                                // Resolve user id robustly
                                var uid = JwtUtils.getUserIdFromToken(token)
                                if (uid.isNullOrBlank()) {
                                    val fetched = runCatching {
                                        val authRetrofit = SupabaseApiClient.authedRetrofit(token)
                                        val authService = authRetrofit.create(SupabaseAuthService::class.java)
                                        authService.getUser().id
                                    }
                                    if (fetched.isSuccess && !fetched.getOrNull().isNullOrBlank()) {
                                        uid = fetched.getOrNull()
                                    }
                                }
                                if (uid.isNullOrBlank()) {
                                    snackbarHostState.showSnackbar("Unable to identify user for interests")
                                } else {
                                    myUserIdState.value = uid
                                    val retrofit = SupabaseRestApiClient.authedRetrofit(token)
                                    val svc = retrofit.create(InterestsService::class.java)
                                    val resp = runCatching {
                                        svc.listMyInterestsResponse(
                                            select = "*",
                                            orFilter = null,
                                            order = "updated_at.desc",
                                            receiverId = "eq.$uid",
                                            status = "eq.pending"
                                        )
                                    }
                                    resp.onSuccess { r ->
                                        if (r.isSuccessful) {
                                            val list = r.body().orEmpty()
                                            received.clear(); received.addAll(list)
                                            if (list.isEmpty()) {
                                                snackbarHostState.showSnackbar("No pending requests")
                                            }
                                        } else {
                                            val err = try { r.errorBody()?.string() } catch (_: Exception) { null }
                                            snackbarHostState.showSnackbar("Load interests failed: http=${r.code()} ${err ?: ""}")
                                        }
                                    }.onFailure {
                                        snackbarHostState.showSnackbar(it.message ?: "Failed to load interests")
                                    }
                                }
                            }
                        }
                    },
                    label = { Text("Received") },
                    icon = {}
                )
                NavigationBarItem(
                    selected = currentTab.value == 2,
                    onClick = {
                        currentTab.value = 2
                        // Load accepted interests (chats)
                        val token = store.getToken()
                        if (token != null) {
                            scope.launch {
                                // Resolve user id robustly
                                var uid = JwtUtils.getUserIdFromToken(token)
                                if (uid.isNullOrBlank()) {
                                    val fetched = runCatching {
                                        val authRetrofit = SupabaseApiClient.authedRetrofit(token)
                                        val authService = authRetrofit.create(SupabaseAuthService::class.java)
                                        authService.getUser().id
                                    }
                                    if (fetched.isSuccess && !fetched.getOrNull().isNullOrBlank()) {
                                        uid = fetched.getOrNull()
                                    }
                                }
                                if (uid.isNullOrBlank()) {
                                    snackbarHostState.showSnackbar("Unable to identify user for chats")
                                } else {
                                    myUserIdState.value = uid
                                    val retrofit = SupabaseRestApiClient.authedRetrofit(token)
                                    val svc = retrofit.create(InterestsService::class.java)
                                    val resp = runCatching {
                                        // Order by created_at asc so the first item in a pair becomes canonical
                                        svc.listMyInterestsResponse(
                                            select = "*",
                                            orFilter = "(sender_id.eq.$uid,receiver_id.eq.$uid)",
                                            order = "created_at.asc",
                                            status = "eq.accepted"
                                        )
                                    }
                                    resp.onSuccess { r ->
                                        if (r.isSuccessful) {
                                            val list = r.body().orEmpty()
                                            // Dedupe: collapse multiple accepted interests for the same pair into one canonical thread
                                            val deduped = list
                                                .groupBy { d ->
                                                    val a = minOf(d.senderId, d.receiverId)
                                                    val b = maxOf(d.senderId, d.receiverId)
                                                    "$a|$b"
                                                }
                                                .values
                                                .map { group ->
                                                    group.minByOrNull { it.createdAt ?: it.updatedAt ?: "" } ?: group.first()
                                                }
                                            chats.clear(); chats.addAll(deduped)
                                            if (list.isEmpty()) {
                                                snackbarHostState.showSnackbar("No chats yet")
                                            }
                                        } else {
                                            val err = try { r.errorBody()?.string() } catch (_: Exception) { null }
                                            snackbarHostState.showSnackbar("Load chats failed: http=${r.code()} ${err ?: ""}")
                                        }
                                    }.onFailure {
                                        snackbarHostState.showSnackbar(it.message ?: "Failed to load chats")
                                    }
                                }
                            }
                        }
                    },
                    label = { Text("Chats") },
                    icon = {}
                )
            }
        }
    ) { paddingValues ->
        when (currentTab.value) {
            0 -> {
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    modifier = Modifier.padding(paddingValues)
                ) {
                    items(profiles, key = { it.id }) { profile ->
                        ProfileCard(
                            profile = profile,
                            onConnectClick = {
                                val index = profiles.indexOf(profile)
                                if (index != -1) {
                                    profiles.removeAt(index)
                                    scope.launch {
                                        val result = snackbarHostState.showSnackbar(
                                            message = "Selected ${profile.name}",
                                            actionLabel = "Undo",
                                            withDismissAction = true,
                                            duration = SnackbarDuration.Short
                                        )
                                        if (result == SnackbarResult.ActionPerformed) {
                                            profiles.add(index.coerceAtMost(profiles.size), profile)
                                        }
                                    }
                                }
                            },
                            onSkipClick = {
                                val index = profiles.indexOf(profile)
                                if (index != -1) {
                                    profiles.removeAt(index)
                                    scope.launch {
                                        val result = snackbarHostState.showSnackbar(
                                            message = "Rejected ${profile.name}",
                                            actionLabel = "Undo",
                                            withDismissAction = true,
                                            duration = SnackbarDuration.Short
                                        )
                                        if (result == SnackbarResult.ActionPerformed) {
                                            profiles.add(index.coerceAtMost(profiles.size), profile)
                                        }
                                    }
                                }
                            },
                            modifier = Modifier.clickable { navController.navigate("profile/${profile.id}") }
                        )
                    }
                }
            }
            1 -> {
                LazyColumn(contentPadding = PaddingValues(16.dp), modifier = Modifier.padding(paddingValues)) {
                    items(received, key = { it.id ?: kotlin.random.Random.nextInt().toString() }) { interest ->
                        Text(text = "From: ${interest.senderId}")
                        androidx.compose.material3.Button(onClick = {
                            val token = store.getToken() ?: return@Button
                            scope.launch {
                                runCatching {
                                    val retrofit = SupabaseRestApiClient.authedRetrofit(token)
                                    val svc = retrofit.create(InterestsService::class.java)
                                    svc.updateInterest(idEq = "eq.${interest.id}", body = mapOf("status" to "accepted"))
                                }.onSuccess {
                                    snackbarHostState.showSnackbar("Accepted")
                                    received.remove(interest)
                                    chats.add(interest.copy(status = "accepted"))
                                }.onFailure {
                                    snackbarHostState.showSnackbar(it.message ?: "Failed to accept")
                                }
                            }
                        }) { Text("Accept") }
                        Spacer(modifier = Modifier.padding(bottom = 12.dp))
                    }
                }
            }
            2 -> {
                LazyColumn(contentPadding = PaddingValues(16.dp), modifier = Modifier.padding(paddingValues)) {
                    items(chats, key = { it.id ?: kotlin.random.Random.nextInt().toString() }) { convo ->
                        val me = myUserIdState.value ?: JwtUtils.getUserIdFromToken(store.getToken() ?: "")
                        val peer = if (!me.isNullOrBlank() && convo.senderId == me) convo.receiverId else convo.senderId
                        Text(text = "Chat with: ${peer}")
                        androidx.compose.material3.Button(onClick = {
                            val convoId = convo.id
                            if (convoId.isNullOrBlank() || peer.isNullOrBlank()) {
                                scope.launch { snackbarHostState.showSnackbar("Missing chat info") }
                            } else {
                                navController.navigate("chat/${convoId}/${peer}")
                            }
                        }) { Text("Open Chat") }
                        Spacer(modifier = Modifier.padding(bottom = 12.dp))
                    }
                }
            }
        }
    }
}
