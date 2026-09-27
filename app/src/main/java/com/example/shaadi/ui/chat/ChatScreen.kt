package com.example.shaadi.ui.chat

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
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.shaadi.data.auth.CredentialStore
import com.example.shaadi.network.MessageDto
import com.example.shaadi.network.MessagesService
import com.example.shaadi.network.SupabaseApiClient
import com.example.shaadi.network.SupabaseAuthService
import com.example.shaadi.network.SupabaseRestApiClient
import com.example.shaadi.util.JwtUtils
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

    LaunchedEffect(conversationId, token) {
        if (token.isNullOrBlank() || conversationId.isNullOrBlank()) {
            snackbarHostState.showSnackbar("Missing chat context")
            return@LaunchedEffect
        }
        scope.launch {
            runCatching {
                val retrofit = SupabaseRestApiClient.authedRetrofit(token)
                val svc = retrofit.create(MessagesService::class.java)
                svc.getMessages(conversationIdEq = "eq.$conversationId")
            }.onSuccess {
                messages.clear(); messages.addAll(it)
            }.onFailure {
                snackbarHostState.showSnackbar(it.message ?: "Failed to load messages")
            }
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
                        val convo = conversationId
                        if (me.isNullOrBlank() || other.isNullOrBlank() || convo.isNullOrBlank()) {
                            snackbarHostState.showSnackbar("Unable to identify the user")
                            return@launch
                        }
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
                                messages.add(
                                    MessageDto(
                                        id = null,
                                        conversationId = convo,
                                        senderId = me!!,
                                        receiverId = other!!,
                                        content = text,
                                        createdAt = null
                                    )
                                )
                                input.value = ""
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
