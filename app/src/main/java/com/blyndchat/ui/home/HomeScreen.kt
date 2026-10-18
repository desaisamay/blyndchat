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
import androidx.compose.material3.CircularProgressIndicator
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
import com.blyndchat.data.ai.BotInterestStore
import com.blyndchat.data.ai.BotMessageStore
import com.blyndchat.data.auth.CredentialStore
import com.blyndchat.data.model.Profile
import com.blyndchat.data.profile.ProfilesRepository
import com.blyndchat.data.profile.SeedProfiles
import com.blyndchat.network.InterestDto
import com.blyndchat.network.InterestsService
import com.blyndchat.network.MessageDto
import com.blyndchat.network.ProfilesService
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

    var userGender by remember { mutableStateOf(store.getUserGender()) }
    var dailyLimitTimestamp by remember { mutableStateOf(store.getDailyLimitTimestamp()) }
    var lastSwipedProfile by remember { mutableStateOf<Profile?>(null) }

    // Fetch gender from Supabase if not yet cached locally
    LaunchedEffect(token, myUid) {
        if (userGender.isNullOrBlank() && !token.isNullOrBlank() && !myUid.isNullOrBlank()) {
            runCatching {
                val authed = SupabaseRestApiClient.authedRetrofit(token)
                val svc = authed.create(ProfilesService::class.java)
                val list = svc.getProfilesById("eq.$myUid", select = "gender", limit = 1)
                val g = list.firstOrNull()?.gender
                if (!g.isNullOrBlank()) {
                    store.saveUserGender(g)
                    userGender = g
                }
            }
        }
    }

    // ---- Selected tab; seed from SavedStateHandle "targetTab" deep-link (>=0). ----
    var targetTab by remember { mutableStateOf(0) }
    LaunchedEffect(Unit) {
        BotInterestStore.init(context.applicationContext)
        val deepLink = navController.currentBackStackEntry
            ?.savedStateHandle
            ?.get<Int>("targetTab") ?: -1
        if (deepLink >= 0) targetTab = deepLink
    }

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    // ---- Profiles tab state ----
    val repo = remember { ProfilesRepository.getInstance(context) }
    val profiles = remember { mutableStateListOf<Profile>() }
    var isLoadingProfiles by remember { mutableStateOf(true) }

    // Load profiles respecting gender, ordering, 24h limit & shuffling
    LaunchedEffect(userGender, dailyLimitTimestamp) {
        val limitTime = store.getDailyLimitTimestamp()
        val now = System.currentTimeMillis()
        val total24h = 24 * 60 * 60 * 1000L

        if (limitTime > 0 && (now - limitTime) < total24h) {
            dailyLimitTimestamp = limitTime
            profiles.clear()
            isLoadingProfiles = false
            return@LaunchedEffect
        }

        val shouldShuffle = limitTime > 0 && (now - limitTime) >= total24h
        if (shouldShuffle) {
            store.clearDailyLimitTimestamp()
            dailyLimitTimestamp = 0L
        }

        isLoadingProfiles = true
        val result = repo.fetchProfiles(
            userGender = userGender,
            currentUserId = myUid,
            shuffled = shouldShuffle,
            limit = 100
        )
        if (result.isSuccess) {
            profiles.clear()
            profiles.addAll(
                result.getOrDefault(emptyList())
                    .filter { it.id.isNotBlank() && it.name.isNotBlank() }
            )
        }
        isLoadingProfiles = false
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
    val pendingBots = BotInterestStore.getPendingBots(userGender)
    val totalReceivedCount = received.size + pendingBots.size

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
                            if (index == 2 && totalReceivedCount > 0) {
                                androidx.compose.material3.BadgedBox(
                                    badge = {
                                        androidx.compose.material3.Badge(
                                            containerColor = LuxuryRose,
                                            contentColor = Color.White
                                        ) {
                                            Text("$totalReceivedCount", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                ) {
                                    Icon(
                                        imageVector = icon,
                                        contentDescription = label,
                                        tint = if (selected) colors.bottomBarSelectedIcon else colors.bottomBarUnselected
                                    )
                                }
                            } else {
                                Icon(
                                    imageVector = icon,
                                    contentDescription = label,
                                    tint = if (selected) colors.bottomBarSelectedIcon else colors.bottomBarUnselected
                                )
                            }
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
                    isLoading = isLoadingProfiles,
                    dailyLimitTimestamp = dailyLimitTimestamp,
                    onProfileClick = { navController.navigate("profile/${it.id}") },
                    onSwipeRight = { profile ->
                        profiles.remove(profile)
                        lastSwipedProfile = profile
                        if (AiBotConfig.isBot(profile.id)) {
                            val effectiveUid = myUid?.takeIf { it.isNotBlank() } ?: "user_local"
                            val convoId = BotMessageStore.conversationId(effectiveUid, profile.id)
                            BotInterestStore.acceptRequest(context, profile.id, effectiveUid)
                            scope.launch {
                                snackbarHostState.showSnackbar("It's a Match with ${profile.name}! Starting chat... ✨")
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
                                        snackbarHostState.showSnackbar("Request Sent ✨")
                                    }.onFailure {
                                        snackbarHostState.showSnackbar(it.message ?: "Failed to send connection")
                                    }
                                }
                            } else {
                                navController.navigate("profile/${profile.id}")
                            }
                        }
                        if (profiles.isEmpty()) {
                            val now = System.currentTimeMillis()
                            store.saveDailyLimitTimestamp(now)
                            dailyLimitTimestamp = now
                        }
                    },
                    onSwipeLeft = { profile ->
                        profiles.remove(profile)
                        lastSwipedProfile = profile
                        scope.launch {
                            val result = snackbarHostState.showSnackbar(
                                message = "Rejected ${profile.name}",
                                actionLabel = "Undo",
                                withDismissAction = true,
                                duration = SnackbarDuration.Short
                            )
                            if (result == SnackbarResult.ActionPerformed) {
                                profiles.add(0, profile)
                                lastSwipedProfile = null
                                store.clearDailyLimitTimestamp()
                                dailyLimitTimestamp = 0L
                            }
                        }
                        if (profiles.isEmpty()) {
                            val now = System.currentTimeMillis()
                            store.saveDailyLimitTimestamp(now)
                            dailyLimitTimestamp = now
                        }
                    },
                    onUndo = {
                        lastSwipedProfile?.let { restored ->
                            profiles.add(0, restored)
                            lastSwipedProfile = null
                            store.clearDailyLimitTimestamp()
                            dailyLimitTimestamp = 0L
                        }
                    },
                    canUndo = lastSwipedProfile != null,
                    onContinueChatting = { targetTab = 1 }
                )

                1 -> ChatsTab(
                    chats = chats,
                    myUid = myUid,
                    userGender = userGender,
                    lastMessages = lastMessages,
                    onChatClick = { interest ->
                        val peer = peerOf(interest, myUid)
                        val convoId = conversationIdFor(myUid, peer)
                        if (convoId.isNotBlank() && peer.isNotBlank()) {
                            navController.navigate("chat/$convoId/$peer")
                        }
                    },
                    onPeerChatClick = { convoId, peerId ->
                        navController.navigate("chat/$convoId/$peerId")
                    }
                )

                2 -> ReceivedTab(
                    received = received,
                    pendingBots = pendingBots,
                    onAcceptRemote = { interest ->
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
                                    snackbarHostState.showSnackbar("Interest accepted ✨")
                                }.onFailure {
                                    snackbarHostState.showSnackbar(it.message ?: "Failed to accept")
                                }
                            }
                        }
                    },
                    onAcceptBot = { botProfile ->
                        val effectiveUid = myUid?.takeIf { it.isNotBlank() } ?: "user_local"
                        BotInterestStore.acceptRequest(context, botProfile.id, effectiveUid)
                        val convoId = BotMessageStore.conversationId(effectiveUid, botProfile.id)
                        scope.launch {
                            val result = snackbarHostState.showSnackbar(
                                message = "Connected with ${botProfile.name}! ✨",
                                actionLabel = "Chat Now",
                                duration = SnackbarDuration.Short
                            )
                            if (result == SnackbarResult.ActionPerformed) {
                                navController.navigate("chat/$convoId/${botProfile.id}")
                            }
                        }
                    },
                    onRejectBot = { botProfile ->
                        BotInterestStore.rejectRequest(context, botProfile.id)
                        scope.launch {
                            snackbarHostState.showSnackbar("Request declined")
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
    isLoading: Boolean,
    dailyLimitTimestamp: Long,
    onProfileClick: (Profile) -> Unit,
    onSwipeLeft: (Profile) -> Unit,
    onSwipeRight: (Profile) -> Unit,
    onUndo: () -> Unit,
    canUndo: Boolean,
    onContinueChatting: () -> Unit
) {
    if (isLoading) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(
                color = LuxuryRose,
                modifier = Modifier.size(42.dp),
                strokeWidth = 3.dp
            )
        }
        return
    }

    val total24h = 24 * 60 * 60 * 1000L
    val isLimitActive = dailyLimitTimestamp > 0 &&
        (System.currentTimeMillis() - dailyLimitTimestamp) < total24h

    if (isLimitActive || profiles.isEmpty()) {
        val effectiveLimitTime = if (dailyLimitTimestamp > 0) dailyLimitTimestamp else System.currentTimeMillis()
        DailyLimitReachedScreen(
            limitTimestamp = effectiveLimitTime,
            onContinueChatting = onContinueChatting
        )
        return
    }

    SwipeableProfileDeck(
        profiles = profiles,
        onSwipeLeft = onSwipeLeft,
        onSwipeRight = onSwipeRight,
        onViewDetails = onProfileClick,
        onUndo = onUndo,
        canUndo = canUndo
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ChatsTab(
    chats: SnapshotStateList<InterestDto>,
    myUid: String?,
    userGender: String?,
    lastMessages: SnapshotStateMap<String, String>,
    onChatClick: (InterestDto) -> Unit,
    onPeerChatClick: (conversationId: String, peerId: String) -> Unit
) {
    val colors = com.blyndchat.ui.theme.LocalAppColors.current
    val effectiveUid = myUid?.takeIf { it.isNotBlank() } ?: "user_local"
    val acceptedBotIds = BotInterestStore.getAcceptedBotIds()
    val localConversations = BotMessageStore.conversationsFor(effectiveUid).filter { convoId ->
        val peerId = convoId.split("_").firstOrNull { it != effectiveUid } ?: ""
        !AiBotConfig.isBot(peerId) || acceptedBotIds.contains(peerId)
    }

    LazyColumn(contentPadding = PaddingValues(16.dp)) {
        // Top section: "Recent Matches" horizontal stories
        if (localConversations.isNotEmpty()) {
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
                        items(localConversations) { convoId ->
                            val peerId = convoId.split("_").firstOrNull { it != effectiveUid } ?: ""
                            val peerProfile = AiBotConfig.getBotProfile(peerId, userGender)
                                ?: SeedProfiles.getProfileById(peerId)
                            val firstName = peerProfile?.name?.split(" ")?.firstOrNull() ?: "Match"

                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .clickable { onPeerChatClick(convoId, peerId) }
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
                                        model = peerProfile?.imageUrl ?: "",
                                        contentDescription = firstName,
                                        modifier = Modifier.fillMaxSize(),
                                        contentScale = ContentScale.Crop
                                    )
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

        if (chats.isEmpty() && localConversations.isEmpty()) {
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
                            text = "Accept requests in the 'Received' tab or match with profiles to start chatting ✨",
                            color = colors.textSecondary,
                            fontSize = 13.sp,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        } else {
            // Local bot / seed conversations
            items(localConversations, key = { "local_$it" }) { convoId ->
                val peerId = convoId.split("_").firstOrNull { it != effectiveUid } ?: ""
                val peerProfile = AiBotConfig.getBotProfile(peerId, userGender)
                    ?: SeedProfiles.getProfileById(peerId)
                val isAi = AiBotConfig.isAiModel(peerId)
                val lastMsg = BotMessageStore.lastMessage(convoId)
                val preview = lastMsg?.content ?: "Tap to chat"

                Card(
                    onClick = { onPeerChatClick(convoId, peerId) },
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
                                model = peerProfile?.imageUrl ?: "",
                                contentDescription = peerProfile?.name,
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
                                        text = peerProfile?.name ?: "Chat",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 15.sp,
                                        color = colors.textPrimary
                                    )
                                    if (isAi) {
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

            // Real chats from Supabase
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
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = "Connected Match",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp,
                                    color = colors.textPrimary
                                )
                                Text(
                                    text = "Chat",
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
        }
    }
}

@Composable
private fun ReceivedTab(
    received: SnapshotStateList<InterestDto>,
    pendingBots: List<Profile>,
    onAcceptRemote: (InterestDto) -> Unit,
    onAcceptBot: (Profile) -> Unit,
    onRejectBot: (Profile) -> Unit
) {
    val colors = com.blyndchat.ui.theme.LocalAppColors.current
    if (received.isEmpty() && pendingBots.isEmpty()) {
        EmptyState("No received connection requests yet ✨\nCheck back soon!")
        return
    }
    LazyColumn(contentPadding = PaddingValues(16.dp)) {
        item {
            Column(modifier = Modifier.padding(bottom = 12.dp)) {
                Text(
                    text = "Connection Requests ✨",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    ),
                    color = if (colors.isDark) LuxuryGold else Color(0xFF9A2D55)
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "People & AI companions who want to connect with you anonymously",
                    style = MaterialTheme.typography.bodySmall,
                    color = colors.textSecondary,
                    fontSize = 12.sp
                )
            }
        }

        // 1. Pending AI Bot Requests
        items(pendingBots, key = { "bot_${it.id}" }) { bot ->
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = colors.cardContainer),
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    if (colors.isDark) LuxuryGold.copy(alpha = 0.35f) else LuxuryRose.copy(alpha = 0.35f)
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Avatar with luxury gold/rose sweep gradient
                        Box(
                            modifier = Modifier
                                .size(56.dp)
                                .border(
                                    width = 2.dp,
                                    brush = Brush.sweepGradient(
                                        listOf(LuxuryGold, LuxuryRose, Color(0xFF9D4EDD), LuxuryGold)
                                    ),
                                    shape = CircleShape
                                )
                                .padding(2.dp)
                                .clip(CircleShape)
                        ) {
                            AsyncImage(
                                model = bot.imageUrl ?: "",
                                contentDescription = bot.name,
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "${bot.name}, ${bot.age}",
                                    fontWeight = FontWeight.Bold,
                                    color = colors.textPrimary,
                                    fontSize = 15.sp
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = if (colors.isDark) LuxuryGold.copy(alpha = 0.18f) else LuxuryRose.copy(alpha = 0.18f)
                                ) {
                                    Text(
                                        text = "AI ✨",
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (colors.isDark) LuxuryGold else Color(0xFF9A2D55)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(2.dp))

                            Text(
                                text = "${bot.profession ?: "Companion"} • ${bot.location ?: "India"}",
                                color = colors.textSecondary,
                                fontSize = 12.sp
                            )

                            Spacer(modifier = Modifier.height(2.dp))

                            Text(
                                text = "Sent you a connection request ✨",
                                color = if (colors.isDark) LuxuryGold.copy(alpha = 0.85f) else LuxuryRose,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }

                    if (!bot.about.isNullOrBlank()) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = colors.chatBubblePeerContainer.copy(alpha = 0.45f),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "\"${bot.about}\"",
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                color = colors.textSecondary,
                                fontSize = 12.sp,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        androidx.compose.material3.TextButton(
                            onClick = { onRejectBot(bot) }
                        ) {
                            Text("Decline", color = colors.textSecondary, fontSize = 13.sp)
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        Button(
                            onClick = { onAcceptBot(bot) },
                            shape = RoundedCornerShape(20.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (colors.isDark) LuxuryGold else LuxuryRose,
                                contentColor = if (colors.isDark) Color(0xFF16111E) else Color.White
                            ),
                            contentPadding = PaddingValues(horizontal = 18.dp, vertical = 8.dp)
                        ) {
                            Text("Accept ✨", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        }
                    }
                }
            }
        }

        // 2. Pending Remote Interests
        items(received, key = { "remote_${it.id ?: it.senderId}" }) { interest ->
            val peerProfile = SeedProfiles.getProfileById(interest.senderId)
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = colors.cardContainer),
                border = androidx.compose.foundation.BorderStroke(1.dp, colors.cardBorderSubtle),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (peerProfile != null) {
                            Box(
                                modifier = Modifier
                                    .size(52.dp)
                                    .border(1.5.dp, if (colors.isDark) LuxuryGold else LuxuryRose, CircleShape)
                                    .padding(2.dp)
                                    .clip(CircleShape)
                            ) {
                                AsyncImage(
                                    model = peerProfile.imageUrl ?: "",
                                    contentDescription = peerProfile.name,
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                        }

                        Column(modifier = Modifier.weight(1f)) {
                            val displayName = peerProfile?.name ?: "Interest from ${interest.senderId.take(8)}..."
                            Text(
                                text = displayName,
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
                            onClick = { onAcceptRemote(interest) },
                            shape = RoundedCornerShape(20.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = LuxuryRose
                            ),
                            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
                        ) {
                            Text("Accept ✨", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        }
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
