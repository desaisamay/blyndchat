package com.blyndchat.ui.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.snapshots.SnapshotStateMap
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.blyndchat.BuildConfig
import com.blyndchat.data.auth.CredentialStore
import com.blyndchat.data.model.Profile
import com.blyndchat.data.profile.ProfilesRepository
import com.blyndchat.network.InterestDto
import com.blyndchat.network.InterestsService
import com.blyndchat.network.SupabaseRealtimeManager
import com.blyndchat.network.SupabaseRestApiClient
import com.blyndchat.util.JwtUtils
import kotlinx.coroutines.launch

/**
 * Home screen with a bottom navigation bar.
 *
 * Tabs (in order): Profiles, Chats, Received, Settings.
 * A [targetTab] Int state selects which body content shows. It is seeded from the
 * nav back-stack SavedStateHandle key "targetTab" (default -1) so other screens can
 * deep-link to a tab; if >= 0 that tab is selected on entry.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavController) {
    val context = navController.context
    val store = remember { CredentialStore(context.applicationContext) }

    // ---- Current user id (prefer cached id, fall back to decoding the JWT). ----
    val token = store.getToken()
    val myUid = remember(token) {
        store.getUserId()?.takeIf { it.isNotBlank() }
            ?: token?.let { JwtUtils.getUserIdFromToken(it) }
    }

    // ---- Selected tab; seed from SavedStateHandle "targetTab" deep-link (>=0). ----
    var targetTab by remember { mutableStateOf(0) }
    LaunchedEffect(Unit) {
        val deepLink = navController.currentBackStackEntry
            ?.savedStateHandle
            ?.get<Int>("targetTab") ?: -1
        if (deepLink >= 0) targetTab = deepLink
    }

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    // ---- Profiles tab state (preserved from the working version). ----
    val repo = remember { ProfilesRepository.getInstance(context) }
    val profiles = remember { mutableStateListOf<Profile>() }

    LaunchedEffect(Unit) {
        val result = repo.fetchProfiles(limit = 100)
        if (result.isSuccess) {
            profiles.clear()
            // Only show profiles that have a real id and a name (skip half-created signup rows).
            profiles.addAll(
                result.getOrDefault(emptyList())
                    .filter { it.id.isNotBlank() && it.name.isNotBlank() }
            )
        }
    }

    // ---- Chats tab state: interests-as-conversations + last-message previews. ----
    val chats = remember { mutableStateListOf<InterestDto>() }
    val lastMessages = remember { mutableStateMapOf<String, String>() }
    val lastTimestamps = remember { mutableStateMapOf<String, String>() }

    LaunchedEffect(targetTab, token) {
        if (targetTab == 1 && !token.isNullOrBlank() && !myUid.isNullOrBlank()) {
            runCatching {
                val svc = SupabaseRestApiClient.authedRetrofit(token)
                    .create(InterestsService::class.java)
                // Conversations = interests involving me (as sender or receiver).
                svc.listMyInterests(
                    orFilter = "sender_id.eq.$myUid,receiver_id.eq.$myUid",
                    order = "updated_at.desc"
                )
            }.onSuccess {
                chats.clear()
                chats.addAll(it)
            }
        }
    }

    // Realtime: keep last-message + timestamp maps fresh. The original used
    // subscribeToAllMessages(...); the current SupabaseRealtimeManager only exposes
    // subscribeToMessages(token, conversationId, ...), so we subscribe per conversation.
    // Wrapped in runCatching so realtime never crashes the screen.
    DisposableEffect(token, chats.size) {
        val handles = mutableListOf<SupabaseRealtimeManager.Handle>()
        if (!token.isNullOrBlank() && !myUid.isNullOrBlank()) {
            runCatching {
                val realtime = SupabaseRealtimeManager(
                    BuildConfig.SUPABASE_URL,
                    BuildConfig.SUPABASE_ANON_KEY
                )
                chats.forEach { interest ->
                    val convoId = conversationIdFor(myUid, peerOf(interest, myUid))
                    if (convoId.isNotBlank()) {
                        val handle = realtime.subscribeToMessages(
                            userJwt = token,
                            conversationId = convoId,
                            onMessage = { msg ->
                                if (msg.content.isNotBlank()) lastMessages[msg.conversationId] = msg.content
                                msg.createdAt?.let { lastTimestamps[msg.conversationId] = it }
                            },
                            onError = { /* best-effort: ignore realtime errors */ }
                        )
                        handles.add(handle)
                    }
                }
            }
        }
        onDispose {
            handles.forEach { runCatching { it.webSocket.close(1000, "dispose") } }
        }
    }

    // ---- Received tab state: pending interests where I'm the receiver. ----
    val received = remember { mutableStateListOf<InterestDto>() }

    LaunchedEffect(targetTab, token) {
        if (targetTab == 2 && !token.isNullOrBlank() && !myUid.isNullOrBlank()) {
            runCatching {
                val svc = SupabaseRestApiClient.authedRetrofit(token)
                    .create(InterestsService::class.java)
                svc.listMyInterests(receiverId = "eq.$myUid", status = "eq.pending")
            }.onSuccess {
                received.clear()
                received.addAll(it)
            }
        }
    }

    // ---- Settings tab: navigate to the settings screen when selected. ----
    LaunchedEffect(targetTab) {
        if (targetTab == 3) {
            navController.navigate("settings")
            // Return to Profiles so re-selecting Settings navigates again.
            targetTab = 0
        }
    }

    val tabs = listOf(
        Triple("Profiles", Icons.Filled.Person, 0),
        Triple("Chats", Icons.Filled.Favorite, 1),
        Triple("Received", Icons.Filled.Email, 2),
        Triple("Settings", Icons.Filled.Settings, 3)
    )

    Scaffold(
        topBar = { TopAppBar(title = { Text("Blynd Chat") }) },
        bottomBar = {
            NavigationBar {
                tabs.forEach { (label, icon, index) ->
                    NavigationBarItem(
                        selected = targetTab == index,
                        onClick = { targetTab = index },
                        icon = { Icon(icon, contentDescription = label) },
                        label = { Text(label) }
                    )
                }
            }
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues)) {
            when (targetTab) {
                0 -> ProfilesTab(
                    profiles = profiles,
                    onProfileClick = { navController.navigate("profile/${it.id}") },
                    onConnect = { profile ->
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
                    onSkip = { profile ->
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
                    }
                )

                1 -> ChatsTab(
                    chats = chats,
                    myUid = myUid,
                    lastMessages = lastMessages,
                    onChatClick = { interest ->
                        val peer = peerOf(interest, myUid)
                        val convoId = conversationIdFor(myUid, peer)
                        if (convoId.isNotBlank() && peer.isNotBlank()) {
                            navController.navigate("chat/$convoId/$peer")
                        }
                    }
                )

                2 -> ReceivedTab(
                    received = received,
                    onAccept = { interest ->
                        val id = interest.id
                        if (id != null && !token.isNullOrBlank()) {
                            scope.launch {
                                runCatching {
                                    val svc = SupabaseRestApiClient.authedRetrofit(token)
                                        .create(InterestsService::class.java)
                                    svc.updateInterest(
                                        idEq = "eq.$id",
                                        body = mapOf("status" to "accepted")
                                    )
                                }.onSuccess {
                                    received.remove(interest)
                                    snackbarHostState.showSnackbar("Interest accepted")
                                }.onFailure {
                                    snackbarHostState.showSnackbar(it.message ?: "Failed to accept")
                                }
                            }
                        }
                    }
                )

                else -> {
                    // Settings tab triggers navigation via LaunchedEffect above.
                }
            }
        }
    }
}

/** Peer participant of an interest relative to [myUid]. */
private fun peerOf(interest: InterestDto, myUid: String?): String =
    if (interest.senderId == myUid) interest.receiverId else interest.senderId

/**
 * Deterministic conversation id for a pair of participants: sorted ids joined by "_".
 * Both participants derive the same id regardless of who initiated.
 */
private fun conversationIdFor(a: String?, b: String?): String {
    if (a.isNullOrBlank() || b.isNullOrBlank()) return ""
    return listOf(a, b).sorted().joinToString("_")
}

@Composable
private fun ProfilesTab(
    profiles: SnapshotStateList<Profile>,
    onProfileClick: (Profile) -> Unit,
    onConnect: (Profile) -> Unit,
    onSkip: (Profile) -> Unit
) {
    LazyColumn(contentPadding = PaddingValues(16.dp)) {
        items(profiles, key = { it.id }) { profile ->
            ProfileCard(
                profile = profile,
                onConnectClick = { onConnect(profile) },
                onSkipClick = { onSkip(profile) },
                modifier = Modifier.clickable { onProfileClick(profile) }
            )
        }
    }
}

@Composable
private fun ChatsTab(
    chats: SnapshotStateList<InterestDto>,
    myUid: String?,
    lastMessages: SnapshotStateMap<String, String>,
    onChatClick: (InterestDto) -> Unit
) {
    if (chats.isEmpty()) {
        EmptyState("No chats yet")
        return
    }
    LazyColumn(contentPadding = PaddingValues(16.dp)) {
        items(chats, key = { it.id ?: (it.senderId + it.receiverId) }) { interest ->
            val peer = peerOf(interest, myUid)
            val convoId = conversationIdFor(myUid, peer)
            val preview = lastMessages[convoId] ?: "Tap to open conversation"
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp)
                    .clickable { onChatClick(interest) }
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(peer.ifBlank { "Unknown" })
                    Text(preview)
                }
            }
        }
    }
}

@Composable
private fun ReceivedTab(
    received: SnapshotStateList<InterestDto>,
    onAccept: (InterestDto) -> Unit
) {
    if (received.isEmpty()) {
        EmptyState("No received interests")
        return
    }
    LazyColumn(contentPadding = PaddingValues(16.dp)) {
        items(received, key = { it.id ?: it.senderId }) { interest ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("From: ${interest.senderId}")
                    Button(onClick = { onAccept(interest) }) { Text("Accept") }
                }
            }
        }
    }
}

@Composable
private fun EmptyState(message: String) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(message)
        }
    }
}
