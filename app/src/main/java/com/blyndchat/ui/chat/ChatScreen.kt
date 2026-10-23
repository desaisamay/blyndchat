package com.blyndchat.ui.chat

import androidx.activity.compose.BackHandler
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
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
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
import com.blyndchat.data.ai.BotInterestStore
import com.blyndchat.data.ai.BotMessageStore
import com.blyndchat.data.ai.BotReplyScheduler
import com.blyndchat.data.ai.GeminiChatService
import com.blyndchat.data.auth.CredentialStore
import com.blyndchat.data.profile.SeedProfiles
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
    val remoteMessages = remember { mutableStateListOf<MessageDto>() }
    val input = remember { mutableStateOf("") }
    val localTyping = remember { mutableStateOf(false) }
    val listState = rememberLazyListState()
    var showMenu by remember { mutableStateOf(false) }
    var showBlockDialog by remember { mutableStateOf(false) }
    var showReportDialog by remember { mutableStateOf(false) }
    var selectedReportReason by remember { mutableStateOf("Harassment or inappropriate messages") }

    val onNavigateBack: () -> Unit = {
        navController.previousBackStackEntry?.savedStateHandle?.set("targetTab", 1)
        if (!navController.popBackStack()) {
            navController.navigate("home") {
                popUpTo("home") { inclusive = true }
            }
        }
    }

    BackHandler(onBack = onNavigateBack)

    val token = store.getToken()
    val isBotChat = AiBotConfig.isBot(peerId)
    val myId = store.getUserId()?.takeIf { it.isNotBlank() }
        ?: token?.let { JwtUtils.getUserIdFromToken(it) }
        ?: if (isBotChat) "user_local" else null
    val userGender = store.getUserGender()
    val botProfile = peerId?.let { AiBotConfig.getBotProfile(it, userGender) }
        ?: peerId?.let { SeedProfiles.getProfileById(it) }
    val chatTitle = botProfile?.name ?: "Chat"

    // For bot chats, directly observe the SnapshotStateList from BotMessageStore
    // so background additions by BotReplyScheduler automatically trigger recomposition.
    val displayedMessages: List<MessageDto> = if (isBotChat && !conversationId.isNullOrBlank()) {
        BotMessageStore.getObservableMessages(conversationId)
    } else {
        remoteMessages
    }

    // Observe typing state reactively from BotMessageStore for bot chats
    val isCurrentlyTyping = if (isBotChat && !conversationId.isNullOrBlank()) {
        BotMessageStore.isTyping(conversationId)
    } else {
        localTyping.value
    }

    // Track active conversation and mark as read while user is inside this chat
    DisposableEffect(conversationId) {
        if (!conversationId.isNullOrBlank()) {
            BotMessageStore.setActiveConversation(conversationId)
            BotMessageStore.markAsRead(conversationId)
        }
        onDispose {
            BotMessageStore.setActiveConversation(null)
        }
    }

    // Load messages
    LaunchedEffect(conversationId, token) {
        if (conversationId.isNullOrBlank()) {
            snackbarHostState.showSnackbar("Missing chat context")
            return@LaunchedEffect
        }
        if (isBotChat) {
            BotMessageStore.initConversation(conversationId)
            BotMessageStore.markAsRead(conversationId)
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
                    remoteMessages.clear()
                    remoteMessages.addAll(it)
                }.onFailure {
                    snackbarHostState.showSnackbar(it.message ?: "Failed to load messages")
                }
            }
        }
    }

    // Auto-scroll to bottom when messages change or typing starts
    LaunchedEffect(displayedMessages.size, isCurrentlyTyping) {
        if (displayedMessages.isNotEmpty()) {
            val target = (displayedMessages.size - 1 + if (isCurrentlyTyping) 1 else 0).coerceAtLeast(0)
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
                            }
                            if (isBotChat && isCurrentlyTyping) {
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
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = colors.topBarTitle)
                    }
                },
                actions = {
                    Box {
                        IconButton(onClick = { showMenu = true }) {
                            Icon(Icons.Default.MoreVert, contentDescription = "More options", tint = colors.topBarTitle)
                        }
                        DropdownMenu(
                            expanded = showMenu,
                            onDismissRequest = { showMenu = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("Block", color = Color(0xFFFF5252), fontWeight = FontWeight.SemiBold) },
                                leadingIcon = {
                                    Icon(Icons.Default.Close, contentDescription = null, tint = Color(0xFFFF5252))
                                },
                                onClick = {
                                    showMenu = false
                                    showBlockDialog = true
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Report & Block", color = Color(0xFFFF9800), fontWeight = FontWeight.SemiBold) },
                                leadingIcon = {
                                    Icon(Icons.Default.Warning, contentDescription = null, tint = Color(0xFFFF9800))
                                },
                                onClick = {
                                    showMenu = false
                                    showReportDialog = true
                                }
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = colors.topBarContainer
                )
            )
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { paddingValues ->
        if (showBlockDialog) {
            AlertDialog(
                onDismissRequest = { showBlockDialog = false },
                title = {
                    Text("Block $chatTitle?", fontWeight = FontWeight.Bold)
                },
                text = {
                    Text("Are you sure you want to block $chatTitle? You will no longer receive any messages or connection requests from them.")
                },
                confirmButton = {
                    Button(
                        onClick = {
                            showBlockDialog = false
                            peerId?.let { id ->
                                BotInterestStore.blockPeer(context, id)
                            }
                            if (!conversationId.isNullOrBlank()) {
                                BotMessageStore.removeConversation(conversationId)
                            }
                            onNavigateBack()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF5252))
                    ) {
                        Text("Block", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showBlockDialog = false }) {
                        Text("Cancel")
                    }
                }
            )
        }

        if (showReportDialog) {
            val reportReasons = listOf(
                "Harassment or inappropriate messages",
                "Spam or promotional content",
                "Fake profile or impersonation",
                "Offensive language",
                "Other violation"
            )
            AlertDialog(
                onDismissRequest = { showReportDialog = false },
                title = {
                    Text("Report & Block $chatTitle", fontWeight = FontWeight.Bold)
                },
                text = {
                    Column {
                        Text(
                            "Please select a reason for reporting. $chatTitle will be blocked immediately.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = colors.textSecondary
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        reportReasons.forEach { reason ->
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { selectedReportReason = reason }
                                    .padding(vertical = 4.dp)
                            ) {
                                RadioButton(
                                    selected = (reason == selectedReportReason),
                                    onClick = { selectedReportReason = reason },
                                    colors = RadioButtonDefaults.colors(selectedColor = Color(0xFFFF9800))
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(reason, style = MaterialTheme.typography.bodyMedium)
                            }
                        }
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            showReportDialog = false
                            peerId?.let { id ->
                                BotInterestStore.blockPeer(context, id)
                            }
                            if (!conversationId.isNullOrBlank()) {
                                BotMessageStore.removeConversation(conversationId)
                            }
                            onNavigateBack()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF9800))
                    ) {
                        Text("Report & Block", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showReportDialog = false }) {
                        Text("Cancel")
                    }
                }
            )
        }
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
                if (displayedMessages.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 28.dp, bottom = 16.dp, start = 12.dp, end = 12.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Surface(
                                shape = RoundedCornerShape(22.dp),
                                color = colors.cardContainer,
                                border = androidx.compose.foundation.BorderStroke(
                                    1.dp,
                                    if (colors.isDark) LuxuryGold.copy(alpha = 0.35f) else LuxuryRose.copy(alpha = 0.35f)
                                ),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(
                                    modifier = Modifier.padding(22.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(76.dp)
                                            .border(
                                                width = 2.dp,
                                                brush = Brush.sweepGradient(
                                                    listOf(LuxuryGold, LuxuryRose, Color(0xFFFFB03A), LuxuryGold)
                                                ),
                                                shape = CircleShape
                                            )
                                            .padding(3.dp)
                                            .clip(CircleShape)
                                    ) {
                                        AsyncImage(
                                            model = botProfile?.imageUrl ?: "",
                                            contentDescription = chatTitle,
                                            modifier = Modifier.fillMaxSize(),
                                            contentScale = ContentScale.Crop
                                        )
                                    }

                                    Spacer(modifier = Modifier.height(14.dp))

                                    Text(
                                        text = "You and $chatTitle are connected ✨",
                                        fontWeight = FontWeight.Bold,
                                        color = colors.textPrimary,
                                        fontSize = 17.sp,
                                        textAlign = TextAlign.Center
                                    )

                                    Spacer(modifier = Modifier.height(6.dp))

                                    Text(
                                        text = "Do you want to start the chat?",
                                        color = if (colors.isDark) LuxuryGold else LuxuryRose,
                                        fontWeight = FontWeight.SemiBold,
                                        fontSize = 14.sp,
                                        textAlign = TextAlign.Center
                                    )

                                    Spacer(modifier = Modifier.height(10.dp))

                                    Text(
                                        text = "Break the ice with a warm hello or pick a conversation starter below ✨",
                                        color = colors.textSecondary,
                                        fontSize = 12.sp,
                                        textAlign = TextAlign.Center
                                    )

                                    Spacer(modifier = Modifier.height(16.dp))

                                    // Quick icebreaker chips
                                    val starters = listOf("Hey! 👋", "Chai or coffee? ☕", "How was your day? ✨")
                                    LazyRow(
                                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                                        contentPadding = PaddingValues(horizontal = 4.dp)
                                    ) {
                                        items(starters) { starter ->
                                            Surface(
                                                shape = RoundedCornerShape(16.dp),
                                                color = if (colors.isDark) Color(0x33FFD700) else Color(0x22D64978),
                                                border = androidx.compose.foundation.BorderStroke(
                                                    1.dp,
                                                    if (colors.isDark) LuxuryGold.copy(alpha = 0.5f) else LuxuryRose.copy(alpha = 0.5f)
                                                ),
                                                modifier = Modifier.clickable { input.value = starter }
                                            ) {
                                                Text(
                                                    text = starter,
                                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                                    fontSize = 12.sp,
                                                    fontWeight = FontWeight.Medium,
                                                    color = if (colors.isDark) LuxuryGold else Color(0xFF9A2D55)
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                } else {
                    items(displayedMessages, key = { it.id ?: it.createdAt ?: (it.content + it.senderId) }) { msg ->
                        val isMine = if (isBotChat) msg.senderId != peerId else (msg.senderId == myId)
                        MessageBubble(message = msg, isMine = isMine)
                    }
                }

                // Typing indicator
                if (isCurrentlyTyping) {
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
                                    BotMessageStore.addMessage(convo, userMsg, isIncoming = false)
                                    input.value = ""

                                    // Schedule bot reply according to individual persona timing rules:
                                    // - Bot 1 (Aria/Kabir): Persona "as is", starts typing after 10s
                                    // - Bot 2 (Maya/Rohan): Persona "a bit casual", replies after 10 minutes
                                    // - Bot 3 (Zara/Aarav): Persona "caring", replies after 1 to 3 minutes
                                    BotReplyScheduler.scheduleReply(
                                        context = context,
                                        conversationId = convo,
                                        botId = other,
                                        userMessage = text,
                                        chatTitle = chatTitle,
                                        receiverId = me,
                                        botProfile = botProfile
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
                                            remoteMessages.add(created)
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
