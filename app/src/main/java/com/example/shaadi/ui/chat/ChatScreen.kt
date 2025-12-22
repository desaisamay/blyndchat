package com.example.shaadi.ui.chat

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.shaadi.BuildConfig
import com.example.shaadi.data.auth.CredentialStore
import com.example.shaadi.network.MessageDto
import com.example.shaadi.network.MessagesService
import com.example.shaadi.network.SupabaseApiClient
import com.example.shaadi.network.SupabaseAuthService
import com.example.shaadi.network.InterestsService
import com.example.shaadi.network.SupabaseRealtimeManager
import com.example.shaadi.network.SupabaseRestApiClient
import com.example.shaadi.util.JwtUtils
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(navController: NavController, conversationId: String?, peerId: String?) {
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val store = remember { CredentialStore(navController.context.applicationContext) }
    val messages = remember { mutableStateListOf<MessageDto>() }
    val input = remember { mutableStateOf("") }

    val token = store.getToken()
    val myId = token?.let { JwtUtils.getUserIdFromToken(it) }
    val isRefreshing = remember { mutableStateOf(false) }
    val resolvedConversationId = remember { mutableStateOf(conversationId) }
    val realtimeManager = remember { SupabaseRealtimeManager(BuildConfig.SUPABASE_URL, BuildConfig.SUPABASE_ANON_KEY) }
    var realtimeHandle by remember { mutableStateOf<SupabaseRealtimeManager.Handle?>(null) }

    fun refreshMessages() {
        val t = token
        val convo = resolvedConversationId.value
        if (t.isNullOrBlank() || convo.isNullOrBlank()) return
        isRefreshing.value = true
        scope.launch {
            runCatching {
                val retrofit = SupabaseRestApiClient.authedRetrofit(t)
                val svc = retrofit.create(MessagesService::class.java)
                svc.getMessages(conversationIdEq = "eq.$convo")
            }.onSuccess {
                messages.clear(); messages.addAll(it)
            }.onFailure {
                snackbarHostState.showSnackbar(it.message ?: "Failed to refresh messages")
            }
            isRefreshing.value = false
        }
    }

    LaunchedEffect(conversationId, token) {
        if (token.isNullOrBlank() || conversationId.isNullOrBlank()) {
            snackbarHostState.showSnackbar("Missing chat context")
            return@LaunchedEffect
        }
        // Resolve canonical conversation id for the pair to avoid duplicates
        var me = myId
        if (me.isNullOrBlank()) {
            val fetched = runCatching {
                val authRetrofit = SupabaseApiClient.authedRetrofit(token!!)
                val authService = authRetrofit.create(SupabaseAuthService::class.java)
                authService.getUser().id
            }
            if (fetched.isSuccess && !fetched.getOrNull().isNullOrBlank()) {
                me = fetched.getOrNull()
            }
        }
        val other = peerId
        if (!me.isNullOrBlank() && !other.isNullOrBlank()) {
            runCatching {
                val retrofit = SupabaseRestApiClient.authedRetrofit(token!!)
                val svc = retrofit.create(InterestsService::class.java)
                val filter = "(and(sender_id.eq.$other,receiver_id.eq.$me),and(sender_id.eq.$me,receiver_id.eq.$other))"
                svc.listMyInterests(
                    select = "id,sender_id,receiver_id,created_at",
                    orFilter = filter,
                    order = "created_at.asc",
                    status = "accepted"
                )
            }.onSuccess { rows ->
                val canonical = rows.firstOrNull()?.id
                resolvedConversationId.value = if (!canonical.isNullOrBlank()) canonical else conversationId
            }.onFailure {
                resolvedConversationId.value = conversationId
            }
        } else {
            resolvedConversationId.value = conversationId
        }
        scope.launch {
            runCatching {
                val retrofit = SupabaseRestApiClient.authedRetrofit(token)
                val svc = retrofit.create(MessagesService::class.java)
                val convo = resolvedConversationId.value ?: conversationId
                Log.d("ChatScreen", "Initial load: convo=${convo} myId=${myId} peerId=${peerId}")
                svc.getMessages(conversationIdEq = "eq.$convo")
            }.onSuccess {
                messages.clear(); messages.addAll(it)
            }.onFailure {
                snackbarHostState.showSnackbar(it.message ?: "Failed to load messages")
            }
        }
    }

    androidx.compose.runtime.DisposableEffect(resolvedConversationId.value, token) {
        val convo = resolvedConversationId.value
        if (!token.isNullOrBlank() && !convo.isNullOrBlank()) {
            Log.d("ChatScreen", "Subscribing: convo=${convo} myId=${myId} peerId=${peerId}")
            realtimeHandle = realtimeManager.subscribeToMessages(
                userJwt = token!!,
                conversationId = convo!!,
                onMessage = { msg ->
                    // Avoid duplicates: only add if not present by id+content+createdAt
                    val exists = messages.any { (it.id ?: "") == (msg.id ?: "") && it.content == msg.content && (it.createdAt ?: "") == (msg.createdAt ?: "") }
                    if (!exists) messages.add(msg)
                },
                onError = { e ->
                    scope.launch { snackbarHostState.showSnackbar("Realtime error: ${e.message ?: "unknown"}") }
                }
            )
        }
        onDispose {
            realtimeHandle?.webSocket?.close(1000, "dispose")
            realtimeHandle = null
        }
    }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Chat") }) },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { paddingValues ->
        Column(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
            LazyColumn(
                contentPadding = PaddingValues(12.dp),
                modifier = Modifier.weight(1f).fillMaxWidth()
            ) {
                items(messages, key = { it.id ?: it.createdAt ?: it.content }) { msg ->
                    val isMine = msg.senderId == myId
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                        horizontalArrangement = if (isMine) Arrangement.End else Arrangement.Start,
                        verticalAlignment = Alignment.CenterVertically
                    ) { Text(text = msg.content) }
                }
            }

            Row(modifier = Modifier.fillMaxWidth().padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                OutlinedTextField(
                    value = input.value,
                    onValueChange = { input.value = it },
                    modifier = Modifier.weight(1f),
                    placeholder = { Text("Type a message") }
                )
                Spacer(modifier = Modifier.size(8.dp))
                Button(onClick = { refreshMessages() }) { Text(if (isRefreshing.value) "Refreshing..." else "Refresh") }
                Spacer(modifier = Modifier.size(8.dp))
                Button(onClick = {
                    scope.launch {
                        val text = input.value.trim()
                        if (text.isEmpty()) {
                            snackbarHostState.showSnackbar("Message is empty")
                            return@launch
                        }
                        val t = token
                        if (t.isNullOrBlank()) {
                            snackbarHostState.showSnackbar("Login required to send messages")
                            return@launch
                        }
                        var me = myId
                        if (me.isNullOrBlank()) {
                            val fetched = runCatching {
                                val authRetrofit = SupabaseApiClient.authedRetrofit(t)
                                val authService = authRetrofit.create(SupabaseAuthService::class.java)
                                authService.getUser().id
                            }
                            if (fetched.isSuccess && !fetched.getOrNull().isNullOrBlank()) {
                                me = fetched.getOrNull()
                            }
                        }
                        val other = peerId
                        val convo = resolvedConversationId.value ?: conversationId
                        if (me.isNullOrBlank() || other.isNullOrBlank() || convo.isNullOrBlank()) {
                            snackbarHostState.showSnackbar("Unable to identify the user")
                            return@launch
                        }
                        Log.d("ChatScreen", "Sending: convo=${convo} sender=${me} receiver=${other} content=\"${text}\"")
                        runCatching {
                            val retrofit = SupabaseRestApiClient.authedRetrofit(t)
                            val svc = retrofit.create(MessagesService::class.java)
                            svc.sendMessage(
                                MessageDto(
                                    id = null,
                                    conversationId = convo,
                                    senderId = me!!,
                                    receiverId = other!!,
                                    content = text,
                                    createdAt = null
                                )
                            )
                        }.onSuccess { resp ->
                            if (resp.isSuccessful) {
                                input.value = ""
                                // Realtime will deliver the insert; optionally do a one-shot refresh as backup
                                refreshMessages()
                            } else {
                                val err = try { resp.errorBody()?.string() } catch (_: Exception) { null }
                                snackbarHostState.showSnackbar("Send failed: http=${resp.code()} ${err ?: ""}")
                            }
                        }.onFailure {
                            snackbarHostState.showSnackbar(it.message ?: "Failed to send message")
                        }
                    }
                }) { Text("Send") }
            }
        }
    }
}
