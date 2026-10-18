package com.blyndchat.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.blyndchat.data.ai.AiBotConfig
import com.blyndchat.data.ai.AiBotEngine
import com.blyndchat.data.ai.BotMessageStore
import com.blyndchat.data.auth.CredentialStore
import com.blyndchat.data.model.Profile
import com.blyndchat.data.profile.ProfilesRepository
import com.blyndchat.network.InterestDto
import com.blyndchat.network.InterestsService
import com.blyndchat.network.MessageDto
import com.blyndchat.network.SupabaseRestApiClient
import com.blyndchat.notifications.NotificationUtils
import com.blyndchat.ui.theme.LuxuryEmerald
import com.blyndchat.ui.theme.LuxuryGold
import com.blyndchat.ui.theme.LuxuryRose
import com.blyndchat.util.JwtUtils
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun ProfileDetailScreen(navController: NavController, profileId: String?) {
    val repo = remember { ProfilesRepository.getInstance(navController.context) }
    var profile by remember { mutableStateOf<Profile?>(null) }
    val scope = rememberCoroutineScope()
    val store = remember { CredentialStore(navController.context.applicationContext) }
    var info by remember { mutableStateOf<String?>(null) }
    var error by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(profileId) {
        error = null
        val bot = profileId?.let { AiBotConfig.getBotProfile(it) }
        if (bot != null) {
            profile = bot
            return@LaunchedEffect
        }
        val result = repo.fetchProfiles(limit = null)
        if (result.isSuccess) {
            val list = result.getOrDefault(emptyList())
            profile = list.firstOrNull { it.id == profileId }
            if (profile == null) {
                error = "Profile not found"
            }
        } else {
            error = result.exceptionOrNull()?.message ?: "Failed to load profile"
        }
    }

    val colors = com.blyndchat.ui.theme.LocalAppColors.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "Profile Details",
                            fontWeight = FontWeight.Bold,
                            color = colors.topBarTitle
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("✨", fontSize = 14.sp)
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
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(colors.screenBackgroundBrush)
                .verticalScroll(rememberScrollState())
        ) {
            // Hero Image with smooth bottom scrim and status badge
            Box(modifier = Modifier.height(380.dp).fillMaxWidth()) {
                AsyncImage(
                    model = profile?.imageUrl ?: "",
                    contentDescription = "Profile Picture",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )

                // Top Badge
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = colors.badgeBackground,
                    border = androidx.compose.foundation.BorderStroke(1.dp, colors.badgeBorder),
                    modifier = Modifier
                        .padding(16.dp)
                        .align(Alignment.TopStart)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text("✨", fontSize = 12.sp)
                        Text(
                            text = if (AiBotConfig.isBot(profileId)) "AI Verified" else "Verified Profile",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (colors.isDark) LuxuryGold else Color(0xFF942852)
                        )
                    }
                }

                // Bottom gradient fade
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp)
                        .align(Alignment.BottomCenter)
                        .background(
                            Brush.verticalGradient(
                                colors = colors.scrimColors
                            )
                        )
                )
            }

            Column(modifier = Modifier.padding(20.dp)) {
                if (error != null) {
                    Text(text = error!!, color = MaterialTheme.colorScheme.error)
                }
                val p = profile
                if (p != null) {
                    // Name & Age
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "${p.name}, ${p.age}",
                            style = MaterialTheme.typography.headlineMedium.copy(
                                fontWeight = FontWeight.ExtraBold
                            ),
                            color = colors.textPrimary
                        )
                        Text("✓", color = if (colors.isDark) LuxuryGold else Color(0xFFB33664), fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    }

                    Text(
                        text = "💼 ${p.profession}  •  📍 ${p.location}",
                        style = MaterialTheme.typography.bodyLarge,
                        color = colors.textSecondary
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    // About Me Card
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = colors.cardContainer,
                        border = androidx.compose.foundation.BorderStroke(1.dp, colors.cardBorderSubtle),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "About",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold
                                ),
                                color = if (colors.isDark) LuxuryGold else Color(0xFF9A2D55)
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = p.about.ifBlank { "Open to meaningful connections and authentic conversations." },
                                style = MaterialTheme.typography.bodyMedium,
                                color = colors.textPrimary,
                                lineHeight = 20.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    fun show(value: String?): String = if (value.isNullOrBlank()) "—" else value

                    // Chat Preferences & Vibe Section
                    Text(
                        text = "Chat Preferences & Vibe",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        color = colors.textPrimary
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        DetailTile(label = "Looking For", value = show(p.lookingFor), icon = "🎯", modifier = Modifier.weight(1f))
                        DetailTile(label = "Personality", value = show(p.personalityType), icon = "🧠", modifier = Modifier.weight(1f))
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        DetailTile(label = "Intensity", value = show(p.conversationIntensity), icon = "⚡", modifier = Modifier.weight(1f))
                        DetailTile(label = "Partner Age", value = show(p.preferredAgeRange), icon = "👥", modifier = Modifier.weight(1f))
                    }

                    // Preferred Topics
                    if (!p.preferredTopics.isNullOrBlank()) {
                        Spacer(modifier = Modifier.height(18.dp))
                        Text(
                            text = "Preferred Topics",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold
                            ),
                            color = colors.textPrimary
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        FlowRow(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            p.preferredTopics.split(",").map { it.trim() }.filter { it.isNotBlank() }.forEach { topic ->
                                TopicBadge(text = topic, isAvoided = false)
                            }
                        }
                    }

                    // Topics to Avoid
                    if (!p.avoidedTopics.isNullOrBlank()) {
                        Spacer(modifier = Modifier.height(18.dp))
                        Text(
                            text = "Topics to Avoid",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold
                            ),
                            color = colors.textPrimary
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        FlowRow(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            p.avoidedTopics.split(",").map { it.trim() }.filter { it.isNotBlank() }.forEach { topic ->
                                TopicBadge(text = topic, isAvoided = true)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    // Personal Details Grid
                    Text(
                        text = "Personal Details",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        color = colors.textPrimary
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        DetailTile(label = "Age", value = if (p.age > 0) "${p.age} yrs" else "—", icon = "🎂", modifier = Modifier.weight(1f))
                        DetailTile(label = "Height", value = show(p.height), icon = "📏", modifier = Modifier.weight(1f))
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        DetailTile(label = "Profession", value = show(p.profession), icon = "💼", modifier = Modifier.weight(1f))
                        DetailTile(label = "Location", value = show(p.location), icon = "📍", modifier = Modifier.weight(1f))
                    }
                    if (!p.gender.isNullOrBlank()) {
                        Spacer(modifier = Modifier.height(10.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            DetailTile(label = "Gender", value = show(p.gender), icon = "👤", modifier = Modifier.weight(0.5f))
                            Spacer(modifier = Modifier.weight(0.5f))
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Connect Now CTA Button
                    Button(
                        onClick = {
                            info = null; error = null
                            val token = store.getToken()
                            val receiverId = p.id
                            val uid = store.getUserId()?.takeIf { it.isNotBlank() }
                                ?: token?.let { JwtUtils.getUserIdFromToken(it) }
                                ?: if (AiBotConfig.isBot(receiverId)) "user_local" else null

                            if (AiBotConfig.isBot(receiverId)) {
                                val currentUid = uid ?: "user_local"
                                scope.launch {
                                    val convoId = BotMessageStore.conversationId(currentUid, receiverId)
                                    val isNew = !BotMessageStore.hasConversation(convoId)
                                    if (isNew) {
                                        val greeting = AiBotEngine.getGreeting(receiverId)
                                        BotMessageStore.addMessage(
                                            convoId, MessageDto(
                                                id = "bot_greet_${System.currentTimeMillis()}",
                                                conversationId = convoId,
                                                senderId = receiverId,
                                                receiverId = currentUid,
                                                content = greeting,
                                                createdAt = BotMessageStore.nowTimestamp()
                                            )
                                        )
                                    }
                                    info = "Connection sent"
                                    delay(600)
                                    NotificationUtils.showMessageNotification(
                                        context = navController.context,
                                        conversationId = convoId,
                                        peerId = receiverId,
                                        title = p.name,
                                        body = "hi"
                                    )
                                }
                            } else if (token.isNullOrBlank()) {
                                error = "Please login to send interest"
                            } else if (uid.isNullOrBlank()) {
                                error = "Unable to identify user"
                            } else {
                                scope.launch {
                                    runCatching {
                                        val retrofit = SupabaseRestApiClient.authedRetrofit(token)
                                        val svc = retrofit.create(InterestsService::class.java)
                                        val body = InterestDto(
                                            id = null,
                                            senderId = uid,
                                            receiverId = receiverId,
                                            status = "pending",
                                            createdAt = null,
                                            updatedAt = null
                                        )
                                        svc.sendInterest(body)
                                    }.onSuccess {
                                        info = "Connection sent"
                                    }.onFailure {
                                        error = it.message ?: "Failed to send connection"
                                    }
                                }
                            }
                        },
                        enabled = info != "Connection sent",
                        shape = RoundedCornerShape(26.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Transparent,
                            disabledContainerColor = Color.Transparent
                        ),
                        contentPadding = PaddingValues(0.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(54.dp)
                            .background(
                                brush = if (info == "Connection sent") {
                                    Brush.horizontalGradient(listOf(Color(0xFF2A1B4E), Color(0xFF2A1B4E)))
                                } else {
                                    Brush.horizontalGradient(
                                        colors = listOf(
                                            LuxuryRose,
                                            Color(0xFFFF5E7E),
                                            Color(0xFFFFB03A)
                                        )
                                    )
                                },
                                shape = RoundedCornerShape(26.dp)
                            )
                            .border(
                                width = 1.dp,
                                color = if (info == "Connection sent") Color(0x44FFFFFF) else LuxuryGold.copy(alpha = 0.5f),
                                shape = RoundedCornerShape(26.dp)
                            )
                    ) {
                        Text(
                            text = if (info == "Connection sent") "Connection Sent ✓"
                            else if (AiBotConfig.isBot(p.id)) "Connect Now ✨"
                            else "Send Interest ✨",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            ),
                            color = if (info == "Connection sent") Color(0xFFC7B8E0) else Color.White
                        )
                    }

                    if (info != null) {
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(text = info!!, color = LuxuryGold, fontWeight = FontWeight.SemiBold)
                    }
                    if (error != null) {
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(text = error!!, color = LuxuryRose, fontWeight = FontWeight.SemiBold)
                    }
                }
            }
        }
    }
}

@Composable
private fun DetailTile(label: String, value: String, icon: String, modifier: Modifier = Modifier) {
    val colors = com.blyndchat.ui.theme.LocalAppColors.current
    Surface(
        shape = RoundedCornerShape(14.dp),
        color = colors.detailTileContainer,
        border = androidx.compose.foundation.BorderStroke(1.dp, colors.detailTileBorder),
        modifier = modifier
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(icon, fontSize = 14.sp)
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = label,
                    fontSize = 11.sp,
                    color = colors.detailTileLabel,
                    fontWeight = FontWeight.Medium
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = value,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = colors.detailTileValue
            )
        }
    }
}

@Composable
private fun TopicBadge(text: String, isAvoided: Boolean = false) {
    val colors = com.blyndchat.ui.theme.LocalAppColors.current
    Surface(
        shape = RoundedCornerShape(14.dp),
        color = if (isAvoided) {
            if (colors.isDark) Color(0x33FF4D4F) else Color(0x18D9363E)
        } else {
            colors.detailTileContainer
        },
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (isAvoided) {
                if (colors.isDark) Color(0x66FF4D4F) else Color(0x40D9363E)
            } else {
                colors.detailTileBorder
            }
        )
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            Text(if (isAvoided) "🚫" else "💬", fontSize = 12.sp)
            Text(
                text = text,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = if (isAvoided) {
                    if (colors.isDark) Color(0xFFFF7875) else Color(0xFFCF1322)
                } else {
                    colors.textPrimary
                }
            )
        }
    }
}
