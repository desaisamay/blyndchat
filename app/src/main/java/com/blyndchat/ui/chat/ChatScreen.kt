package com.blyndchat.ui.chat

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.blyndchat.data.ai.AiBotConfig
import com.blyndchat.data.ai.AiBotEngine
import com.blyndchat.data.ai.BotMessageStore
import com.blyndchat.data.ai.GeminiChatService
import com.blyndchat.data.auth.CredentialStore
import com.blyndchat.network.MessageDto
import com.blyndchat.network.MessagesService
import com.blyndchat.network.SupabaseApiClient
import com.blyndchat.network.SupabaseAuthService
import com.blyndchat.network.SupabaseRestApiClient
import com.blyndchat.ui.theme.LuxuryEmerald
import com.blyndchat.ui.theme.LuxuryGold
import com.blyndchat.ui.theme.LuxuryRose
import com.blyndchat.notifications.NotificationUtils
import com.blyndchat.util.JwtUtils
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(navController: NavController, conversationId: String?, peerId: String?) {
    val context = navController.context
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val store = remember { CredentialStore(navController.context.applicationContext) }
    val messages = remember { mutableStateListOf<MessageDto>() }
    val input = remember { mutableStateOf("") }
    val isTyping = remember { mutableStateOf(false) }
    val listState = rememberLazyListState()

    val token = store.getToken()
    val isBotChat = AiBotConfig.isBot(peerId)
    val myId = store.getUserId()?.takeIf { it.isNotBlank() }
        ?: token?.let { JwtUtils.getUserIdFromToken(it) }
        ?: if (isBotChat) "user_local" else null
    val botProfile = peerId?.let { AiBotConfig.getBotProfile(it) }
    val chatTitle = botProfile?.name ?: "Chat"

    // Load messages
    LaunchedEffect(conversationId, token) {
        if (conversationId.isNullOrBlank()) {
            snackbarHostState.showSnackbar("Missing chat context")
            return@LaunchedEffect
        }
        if (isBotChat) {
            if (!BotMessageStore.hasConversation(conversationId) && peerId != null) {
                val greeting = AiBotEngine.getGreeting(peerId)
                BotMessageStore.addMessage(
                    conversationId, MessageDto(
                        id = "bot_greet_${System.currentTimeMillis()}",
                        conversationId = conversationId,
                        senderId = peerId,
                        receiverId = myId ?: "user_local",
                        content = greeting,
                        createdAt = BotMessageStore.nowTimestamp()
                    )
                )
            }
            messages.clear()
            messages.addAll(BotMessageStore.getMessages(conversationId))
        } else {
            if (token.isNullOrBlank()) {
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
    }

    // Auto-scroll to bottom when messages change or typing starts
    LaunchedEffect(messages.size, isTyping.value) {
        if (messages.isNotEmpty()) {
            val target = (messages.size - 1 + if (isTyping.value) 1 else 0).coerceAtLeast(0)
            listState.animateScrollToItem(target)
        }
    }

    val colors = com.blyndchat.ui.theme.LocalAppColors.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        if (isBotChat && botProfile != null) {
                            Box(
                                modifier = Modifier
                                    .size(38.dp)
                                    .border(1.5.dp, if (colors.isDark) LuxuryGold else LuxuryRose, CircleShape)
                                    .padding(1.dp)
                                    .clip(CircleShape)
                            ) {
                                AsyncImage(
                                    model = botProfile.imageUrl,
                                    contentDescription = botProfile.name,
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                        }
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = chatTitle,
                                    fontSize = 17.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = colors.topBarTitle
                                )
                                if (isBotChat) {
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Surface(
                                        shape = RoundedCornerShape(6.dp),
                                        color = if (colors.isDark) Color(0x33FFD700) else Color(0x22D64978)
                                    ) {
                                        Text(
                                            text = "AI",
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (colors.isDark) LuxuryGold else Color(0xFF9A2D55),
                                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                        )
                                    }
                                }
                            }
                            if (isBotChat && isTyping.value) {
                                Text(
                                    "typing...",
                                    fontSize = 11.sp,
                                    color = if (colors.isDark) LuxuryEmerald else Color(0xFF008A4B),
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = colors.topBarTitle)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = colors.topBarContainer
                )
            )
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(colors.screenBackgroundBrush)
        ) {
            LazyColumn(
                state = listState,
                contentPadding = PaddingValues(horizontal = 14.dp, vertical = 12.dp),
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                items(messages, key = { it.id ?: it.createdAt ?: (it.content + it.senderId) }) { msg ->
                    val isMine = if (isBotChat) msg.senderId != peerId else (msg.senderId == myId)
                    MessageBubble(message = msg, isMine = isMine)
                }

                // Typing indicator
                if (isTyping.value) {
                    item {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            horizontalArrangement = Arrangement.Start
                        ) {
                            Surface(
                                shape = RoundedCornerShape(16.dp, 16.dp, 16.dp, 4.dp),
                                color = colors.chatBubblePeerContainer,
                                border = androidx.compose.foundation.BorderStroke(1.dp, colors.chatBubblePeerBorder),
                                modifier = Modifier.padding(end = 48.dp)
                            ) {
                                Text(
                                    text = "● ● ●",
                                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                                    color = if (colors.isDark) LuxuryGold else Color(0xFF9A2D55),
                                    fontSize = 14.sp
                                )
                            }
                        }
                    }
                }
            }

            // Input row
            Surface(
                color = colors.chatInputContainer,
                border = androidx.compose.foundation.BorderStroke(1.dp, colors.dividerColor)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = input.value,
                        onValueChange = { input.value = it },
                        modifier = Modifier.weight(1f),
                        placeholder = { Text("Type a message...", color = colors.chatInputPlaceholder) },
                        shape = RoundedCornerShape(24.dp),
                        singleLine = false,
                        maxLines = 4,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = colors.chatInputText,
                            unfocusedTextColor = colors.chatInputText,
                            focusedBorderColor = if (colors.isDark) LuxuryGold else LuxuryRose,
                            unfocusedBorderColor = colors.chatInputBorder,
                            cursorColor = if (colors.isDark) LuxuryGold else LuxuryRose,
                            focusedContainerColor = colors.chatInputFieldContainer,
                            unfocusedContainerColor = colors.chatInputFieldContainer
                        )
                    )
                    Spacer(modifier = Modifier.size(10.dp))
                    IconButton(
                        onClick = {
                            scope.launch {
                                val text = input.value.trim()
                                if (text.isEmpty()) {
                                    snackbarHostState.showSnackbar("Message is empty")
                                    return@launch
                                }
                                val t = token
                                if (t.isNullOrBlank() && !isBotChat) {
                                    snackbarHostState.showSnackbar("Login required to send messages")
                                    return@launch
                                }
                                var me = myId
                                if (me.isNullOrBlank()) {
                                    if (isBotChat) {
                                        me = "user_local"
                                    } else if (!t.isNullOrBlank()) {
                                        val fetched = runCatching {
                                            val authRetrofit = SupabaseApiClient.authedRetrofit(t)
                                            val authService = authRetrofit.create(SupabaseAuthService::class.java)
                                            authService.getUser().id
                                        }
                                        if (fetched.isSuccess && !fetched.getOrNull().isNullOrBlank()) {
                                            me = fetched.getOrNull()
                                        }
                                    }
                                }
                                val other = peerId
                                val convo = conversationId
                                if (me.isNullOrBlank() || other.isNullOrBlank() || convo.isNullOrBlank()) {
                                    snackbarHostState.showSnackbar("Unable to identify the user")
                                    return@launch
                                }

                                if (isBotChat) {
                                    val userMsg = MessageDto(
                                        id = "user_${System.currentTimeMillis()}",
                                        conversationId = convo,
                                        senderId = me,
                                        receiverId = other,
                                        content = text,
                                        createdAt = BotMessageStore.nowTimestamp()
                                    )
                                    BotMessageStore.addMessage(convo, userMsg)
                                    messages.add(userMsg)
                                    input.value = ""

                                    val startTime = System.currentTimeMillis()
                                    // Natural 1.5s reading pause before typing begins
                                    delay(1500L)
                                    isTyping.value = true

                                    val reply = GeminiChatService.generateReply(
                                        botId = other,
                                        userMessage = text,
                                        messagesHistory = messages.toList()
                                    )

                                    // Maintain 10 seconds total delay before reply is posted
                                    val elapsed = System.currentTimeMillis() - startTime
                                    val remainingDelay = (10_000L - elapsed).coerceAtLeast(500L)
                                    delay(remainingDelay)
                                    isTyping.value = false

                                    val botMsg = MessageDto(
                                        id = "bot_${System.currentTimeMillis()}",
                                        conversationId = convo,
                                        senderId = other,
                                        receiverId = me,
                                        content = reply,
                                        createdAt = BotMessageStore.nowTimestamp()
                                    )
                                    BotMessageStore.addMessage(convo, botMsg)
                                    messages.add(botMsg)

                                    // Show system notification for the incoming message
                                    NotificationUtils.showMessageNotification(
                                        context = context,
                                        conversationId = convo,
                                        peerId = other,
                                        title = chatTitle,
                                        body = reply
                                    )
                                } else {
                                    runCatching {
                                        val retrofit = SupabaseRestApiClient.authedRetrofit(t!!)
                                        val svc = retrofit.create(MessagesService::class.java)
                                        svc.sendMessage(
                                            MessageDto(
                                                id = null,
                                                conversationId = convo,
                                                senderId = me,
                                                receiverId = other,
                                                content = text,
                                                createdAt = null
                                            )
                                        )
                                    }.onSuccess { resp ->
                                        if (resp.isSuccessful) {
                                            val created = MessageDto(
                                                id = null,
                                                conversationId = convo,
                                                senderId = me,
                                                receiverId = other,
                                                content = text,
                                                createdAt = null
                                            )
                                            messages.add(created)
                                            input.value = ""
                                        } else {
                                            val err = try { resp.errorBody()?.string() } catch (_: Exception) { null }
                                            snackbarHostState.showSnackbar("Send failed: http=${resp.code()} ${err ?: ""}")
                                        }
                                    }.onFailure {
                                        snackbarHostState.showSnackbar(it.message ?: "Failed to send message")
                                    }
                                }
                            }
                        },
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.horizontalGradient(
                                    listOf(LuxuryRose, Color(0xFFFF5E7E))
                                )
                            )
                    ) {
                        Icon(
                            Icons.Default.Send,
                            contentDescription = "Send",
                            tint = Color.White
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun MessageBubble(message: MessageDto, isMine: Boolean) {
    val colors = com.blyndchat.ui.theme.LocalAppColors.current
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = if (isMine) Arrangement.End else Arrangement.Start
    ) {
        if (isMine) {
            Surface(
                shape = RoundedCornerShape(18.dp, 18.dp, 4.dp, 18.dp),
                color = Color.Transparent,
                modifier = Modifier
                    .widthIn(max = 280.dp)
                    .clip(RoundedCornerShape(18.dp, 18.dp, 4.dp, 18.dp))
                    .background(
                        Brush.horizontalGradient(
                            listOf(LuxuryRose, Color(0xFFFF5E7E))
                        )
                    )
            ) {
                Text(
                    text = message.content,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
                    color = Color.White,
                    fontSize = 15.sp,
                    lineHeight = 20.sp
                )
            }
        } else {
            Surface(
                shape = RoundedCornerShape(18.dp, 18.dp, 18.dp, 4.dp),
                color = colors.chatBubblePeerContainer,
                border = androidx.compose.foundation.BorderStroke(1.dp, colors.chatBubblePeerBorder),
                modifier = Modifier.widthIn(max = 280.dp)
            ) {
                Text(
                    text = message.content,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
                    color = colors.chatBubblePeerText,
                    fontSize = 15.sp,
                    lineHeight = 20.sp
                )
            }
        }
    }
}
