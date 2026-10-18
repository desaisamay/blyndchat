package com.blyndchat.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.blyndchat.BuildConfig
import com.blyndchat.data.ai.AiBotConfig
import com.blyndchat.data.ai.AiBotEngine
import com.blyndchat.data.ai.BotMessageStore
import com.blyndchat.data.auth.CredentialStore
import com.blyndchat.data.model.Profile
import com.blyndchat.data.profile.ProfilesRepository
import com.blyndchat.network.InterestDto
import com.blyndchat.network.InterestsService
import com.blyndchat.network.MessageDto
import com.blyndchat.network.SupabaseRealtimeManager
import com.blyndchat.network.SupabaseRestApiClient
import com.blyndchat.notifications.NotificationUtils
import com.blyndchat.ui.theme.LuxuryEmerald
import com.blyndchat.ui.theme.LuxuryGold
import com.blyndchat.ui.theme.LuxuryRose
import com.blyndchat.util.JwtUtils
import kotlinx.coroutines.delay
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

    // Realtime: keep last-message + timestamp maps fresh.
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
                                if (msg.content.isNotBlank()) {
                                    lastMessages[msg.conversationId] = msg.content
                                    if (msg.senderId != myUid) {
                                        NotificationUtils.showMessageNotification(
                                            context = context,
                                            conversationId = msg.conversationId,
                                            peerId = msg.senderId,
                                            title = "New Message",
                                            body = msg.content
                                        )
                                    }
                                }
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

    val colors = com.blyndchat.ui.theme.LocalAppColors.current

    val tabs = listOf(
        Triple("Profiles", Icons.Filled.Person, 0),
        Triple("Chats", Icons.Filled.Favorite, 1),
        Triple("Received", Icons.Filled.Email, 2),
        Triple("Settings", Icons.Filled.Settings, 3)
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Circular Blynd Chat BC logo
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                        ) {
                            AsyncImage(
                                model = "file:///android_asset/app_logo_circle.png",
                                contentDescription = "Blynd Chat Logo",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Fit
                            )
                        }

                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "Blynd Chat",
                                    style = MaterialTheme.typography.titleLarge.copy(
                                        fontWeight = FontWeight.Normal,
                                        letterSpacing = 0.5.sp
                                    ),
                                    color = colors.topBarTitle
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("✨", fontSize = 14.sp)
                            }
                            Text(
                                text = "CONNECT ANONYMOUSLY",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 1.5.sp,
                                    fontSize = 9.sp
                                ),
                                color = colors.topBarSubtitle
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = colors.topBarContainer
                )
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = colors.bottomBarContainer,
                tonalElevation = 8.dp
            ) {
                tabs.forEach { (label, icon, index) ->
                    val selected = targetTab == index
                    NavigationBarItem(
                        selected = selected,
                        onClick = { targetTab = index },
                        icon = {
                            Icon(
                                imageVector = icon,
                                contentDescription = label,
                                tint = if (selected) colors.bottomBarSelectedIcon else colors.bottomBarUnselected
                            )
                        },
                        label = {
                            Text(
                                text = label,
                                fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
                                color = if (selected) (if (colors.isDark) LuxuryGold else Color(0xFF9A2D55)) else colors.bottomBarUnselected,
                                fontSize = 11.sp
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            indicatorColor = colors.bottomBarSelectedIndicator,
                            selectedIconColor = colors.bottomBarSelectedIcon,
                            unselectedIconColor = colors.bottomBarUnselected,
                            selectedTextColor = if (colors.isDark) LuxuryGold else Color(0xFF9A2D55),
                            unselectedTextColor = colors.bottomBarUnselected
                        )
                    )
                }
            }
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(colors.screenBackgroundBrush)
        ) {
            when (targetTab) {
                0 -> ProfilesTab(
                    profiles = profiles,
                    onProfileClick = { navController.navigate("profile/${it.id}") },
                    onConnect = { profile ->
                        if (AiBotConfig.isBot(profile.id)) {
                            val effectiveUid = myUid?.takeIf { it.isNotBlank() } ?: "user_local"
                            val convoId = BotMessageStore.conversationId(effectiveUid, profile.id)
                            if (!BotMessageStore.hasConversation(convoId)) {
                                val greeting = AiBotEngine.getGreeting(profile.id)
                                BotMessageStore.addMessage(
                                    convoId, MessageDto(
                                        id = "bot_greet_${System.currentTimeMillis()}",
                                        conversationId = convoId,
                                        senderId = profile.id,
                                        receiverId = effectiveUid,
                                        content = greeting,
                                        createdAt = BotMessageStore.nowTimestamp()
                                    )
                                )
                            }
                            profiles.remove(profile)
                            scope.launch {
                                snackbarHostState.showSnackbar("Connection sent")
                                delay(600)
                                NotificationUtils.showMessageNotification(
                                    context = context,
                                    conversationId = convoId,
                                    peerId = profile.id,
                                    title = profile.name,
                                    body = "hi"
                                )
                            }
                        } else {
                            if (!token.isNullOrBlank() && !myUid.isNullOrBlank()) {
                                scope.launch {
                                    runCatching {
                                        val retrofit = SupabaseRestApiClient.authedRetrofit(token)
                                        val svc = retrofit.create(InterestsService::class.java)
                                        val body = InterestDto(
                                            id = null,
                                            senderId = myUid,
                                            receiverId = profile.id,
                                            status = "pending",
                                            createdAt = null,
                                            updatedAt = null
                                        )
                                        svc.sendInterest(body)
                                    }.onSuccess {
                                        profiles.remove(profile)
                                        snackbarHostState.showSnackbar("Connection sent")
                                    }.onFailure {
                                        snackbarHostState.showSnackbar(it.message ?: "Failed to send connection")
                                    }
                                }
                            } else {
                                navController.navigate("profile/${profile.id}")
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
                    },
                    onBotChatClick = { botId ->
                        val effectiveUid = myUid?.takeIf { it.isNotBlank() } ?: "user_local"
                        val convoId = BotMessageStore.conversationId(effectiveUid, botId)
                        navController.navigate("chat/$convoId/$botId")
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
    if (profiles.isEmpty()) {
        EmptyState("Finding potential matches...")
        return
    }
    LazyColumn(contentPadding = PaddingValues(top = 8.dp, bottom = 24.dp)) {
        items(profiles, key = { it.id }) { profile ->
            ProfileCard(
                profile = profile,
                onViewDetailsClick = { onProfileClick(profile) },
                onSkipClick = { onSkip(profile) },
                modifier = Modifier.clickable { onProfileClick(profile) }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ChatsTab(
    chats: SnapshotStateList<InterestDto>,
    myUid: String?,
    lastMessages: SnapshotStateMap<String, String>,
    onChatClick: (InterestDto) -> Unit,
    onBotChatClick: (String) -> Unit
) {
    val colors = com.blyndchat.ui.theme.LocalAppColors.current
    val effectiveUid = myUid?.takeIf { it.isNotBlank() } ?: "user_local"
    val botConversations = AiBotConfig.ALL_BOT_IDS.filter { botId ->
        BotMessageStore.hasConversation(BotMessageStore.conversationId(effectiveUid, botId))
    }

    LazyColumn(contentPadding = PaddingValues(16.dp)) {
        // Top section: "Recent Matches" horizontal stories (only for connected profiles)
        if (botConversations.isNotEmpty()) {
            item {
                Column(modifier = Modifier.padding(bottom = 16.dp)) {
                    Text(
                        text = "Recent Matches ✨",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        ),
                        color = if (colors.isDark) LuxuryGold else Color(0xFF9A2D55)
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        items(botConversations) { botId ->
                            val botProfile = AiBotConfig.getBotProfile(botId)
                        val firstName = botProfile?.name?.split(" ")?.firstOrNull() ?: "AI"
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .clickable { onBotChatClick(botId) }
                                .padding(4.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(62.dp)
                                    .border(
                                        width = 2.dp,
                                        brush = Brush.sweepGradient(
                                            listOf(
                                                LuxuryGold,
                                                LuxuryRose,
                                                Color(0xFF9D4EDD),
                                                LuxuryGold
                                            )
                                        ),
                                        shape = CircleShape
                                    )
                                    .padding(3.dp)
                                    .clip(CircleShape)
                            ) {
                                AsyncImage(
                                    model = botProfile?.imageUrl ?: "",
                                    contentDescription = firstName,
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = firstName,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = colors.textPrimary
                        )
                    }
                }
            }
        }
    }

        item {
            Text(
                text = "Conversations",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                ),
                color = colors.textPrimary,
                modifier = Modifier.padding(vertical = 8.dp)
            )
        }

        if (chats.isEmpty() && botConversations.isEmpty()) {
            item {
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = colors.cardContainer,
                    border = androidx.compose.foundation.BorderStroke(1.dp, colors.cardBorderSubtle),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("💬", fontSize = 32.sp)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "No active conversations yet",
                            fontWeight = FontWeight.Bold,
                            color = colors.textPrimary,
                            fontSize = 16.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Connect with profiles in the Profiles tab to start chatting!",
                            color = colors.textSecondary,
                            fontSize = 13.sp,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        } else {
            // Bot conversations
            items(botConversations, key = { "bot_$it" }) { botId ->
                val botProfile = AiBotConfig.getBotProfile(botId)
                val convoId = BotMessageStore.conversationId(effectiveUid, botId)
                val lastMsg = BotMessageStore.lastMessage(convoId)
                val preview = lastMsg?.content ?: "Tap to chat"

                Card(
                    onClick = { onBotChatClick(botId) },
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = colors.cardContainer),
                    border = androidx.compose.foundation.BorderStroke(1.dp, colors.cardBorderSubtle),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Avatar
                        Box(
                            modifier = Modifier
                                .size(50.dp)
                                .clip(CircleShape)
                        ) {
                            AsyncImage(
                                model = botProfile?.imageUrl ?: "",
                                contentDescription = botProfile?.name,
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Text(
                                        text = botProfile?.name ?: "AI",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 15.sp,
                                        color = colors.textPrimary
                                    )
                                    Surface(
                                        shape = RoundedCornerShape(6.dp),
                                        color = if (colors.isDark) Color(0x22FFD700) else Color(0x20D64978)
                                    ) {
                                        Text(
                                            text = "AI",
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (colors.isDark) LuxuryGold else Color(0xFF9A2D55),
                                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                        )
                                    }
                                }
                                Text(
                                    text = "Now",
                                    fontSize = 11.sp,
                                    color = if (colors.isDark) LuxuryGold else Color(0xFF9A2D55)
                                )
                            }

                            Spacer(modifier = Modifier.height(4.dp))

                            Text(
                                text = preview,
                                style = MaterialTheme.typography.bodyMedium,
                                color = colors.textSecondary,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                fontSize = 13.sp
                            )
                        }
                    }
                }
            }

            // Real chats
            items(chats, key = { it.id ?: (it.senderId + it.receiverId) }) { interest ->
                val peer = peerOf(interest, myUid)
                val convoId = conversationIdFor(myUid, peer)
                val preview = lastMessages[convoId] ?: "Tap to open conversation"

                Card(
                    onClick = { onChatClick(interest) },
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = colors.cardContainer),
                    border = androidx.compose.foundation.BorderStroke(1.dp, colors.cardBorderSubtle),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(50.dp)
                                .clip(CircleShape)
                                .background(if (colors.isDark) Color(0xFF24143D) else Color(0xFFEDE8F5)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("👤", fontSize = 22.sp)
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = peer.ifBlank { "User" },
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                color = colors.textPrimary
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = preview,
                                color = colors.textSecondary,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                fontSize = 13.sp
                            )
                        }
                    }
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
    val colors = com.blyndchat.ui.theme.LocalAppColors.current
    if (received.isEmpty()) {
        EmptyState("No received interests yet ✨")
        return
    }
    LazyColumn(contentPadding = PaddingValues(16.dp)) {
        items(received, key = { it.id ?: it.senderId }) { interest ->
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = colors.cardContainer),
                border = androidx.compose.foundation.BorderStroke(1.dp, colors.cardBorderSubtle),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Interest from ${interest.senderId.take(8)}...",
                            fontWeight = FontWeight.Bold,
                            color = colors.textPrimary,
                            fontSize = 15.sp
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Wants to connect with you anonymously",
                            color = colors.textSecondary,
                            fontSize = 12.sp
                        )
                    }
                    Button(
                        onClick = { onAccept(interest) },
                        shape = RoundedCornerShape(20.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = LuxuryRose
                        )
                    ) {
                        Text("Accept ✨", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
private fun EmptyState(message: String) {
    val colors = com.blyndchat.ui.theme.LocalAppColors.current
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(24.dp)
        ) {
            Text("✨", fontSize = 36.sp)
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = message,
                color = colors.textSecondary,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}
