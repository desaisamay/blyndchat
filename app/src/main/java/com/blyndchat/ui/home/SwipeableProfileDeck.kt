package com.blyndchat.ui.home

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.blyndchat.data.model.Profile
import com.blyndchat.ui.theme.LuxuryEmerald
import com.blyndchat.ui.theme.LuxuryGold
import com.blyndchat.ui.theme.LuxuryRose
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.abs

/**
 * Premium swipeable profile card deck with Tinder / Bumble style gestures:
 * - Swipe Right: Request Sent (Connect) ✨
 * - Swipe Left: Rejected (Pass) ❌
 * - Stamp overlays that reveal smoothly during swipe
 * - Quick action buttons: Undo, Pass, Details, Connect
 * - Stack visual depth with scaled background card
 */
@Composable
fun SwipeableProfileDeck(
    profiles: List<Profile>,
    onSwipeLeft: (Profile) -> Unit,
    onSwipeRight: (Profile) -> Unit,
    onViewDetails: (Profile) -> Unit,
    onUndo: (() -> Unit)? = null,
    canUndo: Boolean = false,
    modifier: Modifier = Modifier
) {
    if (profiles.isEmpty()) return

    val density = LocalDensity.current
    val configuration = LocalConfiguration.current
    val screenWidthPx = with(density) { configuration.screenWidthDp.dp.toPx() }
    val swipeThreshold = screenWidthPx * 0.32f

    val scope = rememberCoroutineScope()
    val offsetX = remember { Animatable(0f) }
    val offsetY = remember { Animatable(0f) }
    var isSwipingOut by remember { mutableStateOf(false) }

    val topProfile = profiles[0]
    val nextProfile = if (profiles.size > 1) profiles[1] else null
    val colors = com.blyndchat.ui.theme.LocalAppColors.current

    // Reset offset whenever top profile changes
    LaunchedEffect(topProfile.id) {
        offsetX.snapTo(0f)
        offsetY.snapTo(0f)
        isSwipingOut = false
    }

    fun animateSwipe(toRight: Boolean) {
        if (isSwipingOut) return
        isSwipingOut = true
        scope.launch {
            val targetX = if (toRight) screenWidthPx * 1.3f else -screenWidthPx * 1.3f
            offsetX.animateTo(
                targetValue = targetX,
                animationSpec = tween(durationMillis = 280)
            )
            if (toRight) onSwipeRight(topProfile) else onSwipeLeft(topProfile)
            offsetX.snapTo(0f)
            offsetY.snapTo(0f)
            isSwipingOut = false
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 6.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Card Stack Area (Flexible height)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            contentAlignment = Alignment.Center
        ) {
            // Background Card (if available)
            if (nextProfile != null) {
                val dragFraction = (abs(offsetX.value) / screenWidthPx).coerceIn(0f, 1f)
                val bgScale = 0.94f + (0.06f * dragFraction)
                val bgAlpha = 0.65f + (0.35f * dragFraction)
                val bgOffsetY = (14.dp * (1f - dragFraction))

                DeckCardItem(
                    profile = nextProfile,
                    onCardClick = { onViewDetails(nextProfile) },
                    modifier = Modifier
                        .fillMaxSize()
                        .offset(y = bgOffsetY)
                        .scale(bgScale)
                        .alpha(bgAlpha)
                )
            }

            // Foreground Top Card
            val rotationDegree = (offsetX.value / 32f).coerceIn(-18f, 18f)

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer {
                        translationX = offsetX.value
                        translationY = offsetY.value
                        rotationZ = rotationDegree
                    }
                    .pointerInput(topProfile.id) {
                        detectDragGestures(
                            onDragEnd = {
                                if (isSwipingOut) return@detectDragGestures
                                if (offsetX.value > swipeThreshold) {
                                    animateSwipe(toRight = true)
                                } else if (offsetX.value < -swipeThreshold) {
                                    animateSwipe(toRight = false)
                                } else {
                                    // Spring back
                                    scope.launch {
                                        launch {
                                            offsetX.animateTo(
                                                0f,
                                                spring(
                                                    dampingRatio = Spring.DampingRatioMediumBouncy,
                                                    stiffness = Spring.StiffnessLow
                                                )
                                            )
                                        }
                                        launch {
                                            offsetY.animateTo(
                                                0f,
                                                spring(
                                                    dampingRatio = Spring.DampingRatioMediumBouncy,
                                                    stiffness = Spring.StiffnessLow
                                                )
                                            )
                                        }
                                    }
                                }
                            },
                            onDragCancel = {
                                scope.launch {
                                    offsetX.animateTo(0f, spring(Spring.DampingRatioMediumBouncy))
                                    offsetY.animateTo(0f, spring(Spring.DampingRatioMediumBouncy))
                                }
                            },
                            onDrag = { change, dragAmount ->
                                change.consume()
                                if (!isSwipingOut) {
                                    scope.launch {
                                        offsetX.snapTo(offsetX.value + dragAmount.x)
                                        offsetY.snapTo(offsetY.value + dragAmount.y * 0.4f)
                                    }
                                }
                            }
                        )
                    }
            ) {
                DeckCardItem(
                    profile = topProfile,
                    onCardClick = { onViewDetails(topProfile) },
                    modifier = Modifier.fillMaxSize()
                )

                // Swipe Right Stamp: "REQUEST SENT ✨"
                val rightStampAlpha = (offsetX.value / (swipeThreshold * 0.7f)).coerceIn(0f, 1f)
                if (rightStampAlpha > 0.05f) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .padding(start = 24.dp, top = 28.dp)
                            .rotate(-15f)
                            .alpha(rightStampAlpha)
                            .background(
                                color = if (colors.isDark) Color(0xDD362B07) else Color(0xDDFFFBEA),
                                shape = RoundedCornerShape(12.dp)
                            )
                            .border(
                                width = 2.5.dp,
                                color = LuxuryGold,
                                shape = RoundedCornerShape(12.dp)
                            )
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        Text(
                            text = "REQUEST SENT ✨",
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 18.sp,
                            color = LuxuryGold,
                            letterSpacing = 1.sp
                        )
                    }
                }

                // Swipe Left Stamp: "REJECTED ❌"
                val leftStampAlpha = (-offsetX.value / (swipeThreshold * 0.7f)).coerceIn(0f, 1f)
                if (leftStampAlpha > 0.05f) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(end = 24.dp, top = 28.dp)
                            .rotate(15f)
                            .alpha(leftStampAlpha)
                            .background(
                                color = Color(0xDD4A0E17),
                                shape = RoundedCornerShape(12.dp)
                            )
                            .border(
                                width = 2.5.dp,
                                color = LuxuryRose,
                                shape = RoundedCornerShape(12.dp)
                            )
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        Text(
                            text = "REJECTED ❌",
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 18.sp,
                            color = Color(0xFFFF6B81),
                            letterSpacing = 1.sp
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Action Buttons Row: Undo | Pass ❌ | Info ℹ️ | Connect 💖
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            val undoBackgroundBrush = when {
                canUndo && colors.isDark -> Brush.linearGradient(
                    listOf(Color(0xFF382D06), Color(0xFF503F08))
                )
                canUndo && !colors.isDark -> Brush.linearGradient(
                    listOf(Color(0xFFFFFBEA), Color(0xFFFFF3CD))
                )
                !canUndo && colors.isDark -> Brush.linearGradient(
                    listOf(Color(0xFF1E1E24), Color(0xFF1E1E24))
                )
                else -> Brush.linearGradient(
                    listOf(Color(0xFFEDEBF2), Color(0xFFEDEBF2))
                )
            }

            val undoBorderBrush = when {
                canUndo && colors.isDark -> Brush.linearGradient(
                    listOf(LuxuryGold, Color(0xFFFFB03A))
                )
                canUndo && !colors.isDark -> Brush.linearGradient(
                    listOf(Color(0xFFD97706), Color(0xFFB45309))
                )
                !canUndo && colors.isDark -> Brush.linearGradient(
                    listOf(Color(0x33FFFFFF), Color(0x33FFFFFF))
                )
                else -> Brush.linearGradient(
                    listOf(Color(0xFFD5D2E0), Color(0xFFD5D2E0))
                )
            }

            val undoTint = when {
                canUndo && colors.isDark -> LuxuryGold
                canUndo && !colors.isDark -> Color(0xFFB45309)
                !canUndo && colors.isDark -> Color(0x55FFFFFF)
                else -> Color(0xFF8E889E)
            }

            // Undo button
            IconButton(
                onClick = { onUndo?.invoke() },
                enabled = canUndo,
                modifier = Modifier
                    .size(48.dp)
                    .then(
                        if (canUndo) {
                            Modifier.shadow(
                                elevation = 10.dp,
                                shape = CircleShape,
                                spotColor = if (colors.isDark) LuxuryGold.copy(alpha = 0.5f) else Color(0xFFD97706).copy(alpha = 0.35f)
                            )
                        } else {
                            Modifier
                        }
                    )
                    .clip(CircleShape)
                    .background(undoBackgroundBrush)
                    .border(
                        width = if (canUndo) 1.5.dp else 1.dp,
                        brush = undoBorderBrush,
                        shape = CircleShape
                    )
            ) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = "Undo previous swipe",
                    tint = undoTint,
                    modifier = Modifier.size(22.dp)
                )
            }

            // Reject / Pass Button (Left Swipe)
            IconButton(
                onClick = { animateSwipe(toRight = false) },
                modifier = Modifier
                    .size(60.dp)
                    .shadow(12.dp, CircleShape, spotColor = LuxuryRose.copy(alpha = 0.5f))
                    .clip(CircleShape)
                    .background(colors.skipButtonContainer)
                    .border(2.dp, LuxuryRose, CircleShape)
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Pass",
                    tint = LuxuryRose,
                    modifier = Modifier.size(28.dp)
                )
            }

            // View Details Button (Big & Appealing Info Button)
            IconButton(
                onClick = { onViewDetails(topProfile) },
                modifier = Modifier
                    .size(56.dp)
                    .shadow(14.dp, CircleShape, spotColor = Color(0xFF00C6FF).copy(alpha = 0.5f))
                    .clip(CircleShape)
                    .background(
                        if (colors.isDark) {
                            Brush.linearGradient(
                                listOf(
                                    Color(0xFF0D253F),
                                    Color(0xFF133B63)
                                )
                            )
                        } else {
                            Brush.linearGradient(
                                listOf(
                                    Color(0xFFE0F2FE),
                                    Color(0xFFBAE6FD)
                                )
                            )
                        }
                    )
                    .border(
                        2.dp,
                        Brush.linearGradient(
                            listOf(
                                Color(0xFF38BDF8),
                                Color(0xFF0284C7)
                            )
                        ),
                        CircleShape
                    )
            ) {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = "View Details",
                    tint = if (colors.isDark) Color(0xFF38BDF8) else Color(0xFF0284C7),
                    modifier = Modifier.size(30.dp)
                )
            }

            // Connect / Request Sent Button (Right Swipe)
            IconButton(
                onClick = { animateSwipe(toRight = true) },
                modifier = Modifier
                    .size(60.dp)
                    .shadow(14.dp, CircleShape, spotColor = LuxuryGold.copy(alpha = 0.5f))
                    .clip(CircleShape)
                    .background(
                        if (colors.isDark) {
                            Brush.linearGradient(
                                listOf(
                                    Color(0xFF362B07),
                                    Color(0xFF52400B)
                                )
                            )
                        } else {
                            Brush.linearGradient(
                                listOf(
                                    Color(0xFFFFFBEA),
                                    Color(0xFFFFF3CD)
                                )
                            )
                        }
                    )
                    .border(2.dp, LuxuryGold, CircleShape)
            ) {
                Icon(
                    imageVector = Icons.Default.Favorite,
                    contentDescription = "Connect",
                    tint = if (colors.isDark) LuxuryGold else Color(0xFFB8860B),
                    modifier = Modifier.size(28.dp)
                )
            }
        }
    }
}

/**
 * Visual card item used inside the deck.
 */
@Composable
private fun DeckCardItem(
    profile: Profile,
    onCardClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = com.blyndchat.ui.theme.LocalAppColors.current

    Card(
        modifier = modifier
            .shadow(
                elevation = 16.dp,
                shape = RoundedCornerShape(12.dp),
                spotColor = colors.cardShadowSpot,
                ambientColor = if (colors.isDark) LuxuryGold.copy(alpha = 0.25f) else Color(0x15000000)
            )
            .border(
                width = 1.5.dp,
                brush = colors.cardBorder,
                shape = RoundedCornerShape(12.dp)
            )
            .clickable { onCardClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = colors.cardContainer)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Hero Photo Section (occupies majority of card)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                AsyncImage(
                    model = profile.imageUrl,
                    contentDescription = profile.name,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )

                // Scrim at the bottom of the photo
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(110.dp)
                        .align(Alignment.BottomCenter)
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    Color.Transparent,
                                    colors.cardContainer.copy(alpha = 0.6f),
                                    colors.cardContainer
                                )
                            )
                        )
                )

                // Verified tick on top right
                Surface(
                    shape = CircleShape,
                    color = colors.cardContainer.copy(alpha = 0.85f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, LuxuryGold.copy(alpha = 0.4f)),
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(14.dp)
                ) {
                    Text(
                        text = "✨",
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        fontSize = 12.sp
                    )
                }
            }

            // Info Body Section
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 18.dp, vertical = 12.dp)
            ) {
                // Name & Age Row with Appealing Card Info Button
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = "${profile.name}, ${profile.age}",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 22.sp,
                                letterSpacing = 0.3.sp
                            ),
                            color = colors.textPrimary
                        )
                        Text(
                            text = "✓",
                            color = if (colors.isDark) LuxuryGold else Color(0xFFB33664),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    // Appealing & Big Info Button on Profile Card
                    IconButton(
                        onClick = onCardClick,
                        modifier = Modifier
                            .size(38.dp)
                            .shadow(8.dp, CircleShape, spotColor = Color(0xFF00C6FF).copy(alpha = 0.4f))
                            .clip(CircleShape)
                            .background(
                                if (colors.isDark) Color(0xFF132A44)
                                else Color(0xFFE0F2FE)
                            )
                            .border(
                                1.5.dp,
                                Brush.linearGradient(
                                    listOf(
                                        Color(0xFF38BDF8),
                                        Color(0xFF0284C7)
                                    )
                                ),
                                CircleShape
                            )
                    ) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = "View Profile Info",
                            tint = if (colors.isDark) Color(0xFF38BDF8) else Color(0xFF0284C7),
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(3.dp))

                // Profession & Location
                Text(
                    text = "💼 ${profile.profession}  •  📍 ${profile.location}",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.Medium,
                        fontSize = 13.sp
                    ),
                    color = colors.textSecondary,
                    maxLines = 1
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Bio
                if (profile.about.isNotBlank()) {
                    Text(
                        text = profile.about,
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontSize = 12.5.sp,
                            lineHeight = 16.sp
                        ),
                        color = colors.textPrimary.copy(alpha = 0.85f),
                        maxLines = 2
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }

                // Trait Badges
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    if (profile.height.isNotBlank()) {
                        DeckTraitBadge(text = "📏 ${profile.height}")
                    }
                    if (!profile.lookingFor.isNullOrBlank()) {
                        DeckTraitBadge(text = "🎯 ${profile.lookingFor}")
                    }
                    if (!profile.personalityType.isNullOrBlank()) {
                        DeckTraitBadge(text = "🧠 ${profile.personalityType}")
                    }
                }
            }
        }
    }
}

@Composable
private fun DeckTraitBadge(text: String) {
    val colors = com.blyndchat.ui.theme.LocalAppColors.current
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = colors.traitChipContainer,
        border = androidx.compose.foundation.BorderStroke(1.dp, colors.traitChipBorder)
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp),
            fontSize = 10.5.sp,
            fontWeight = FontWeight.Medium,
            color = colors.traitChipText
        )
    }
}

/**
 * Beautiful 24-hour daily limit reached state:
 * - Shows countdown timer until reset
 * - Shows message explaining they have viewed all profiles for today
 * - CTA button to jump to Chats tab and keep conversing
 */
@Composable
fun DailyLimitReachedScreen(
    limitTimestamp: Long,
    onContinueChatting: () -> Unit,
    modifier: Modifier = Modifier,
    cooldownHours: Int = 12
) {
    val colors = com.blyndchat.ui.theme.LocalAppColors.current
    val totalCooldown = cooldownHours * 60 * 60 * 1000L
    var remainingMillis by remember(limitTimestamp) {
        val elapsed = System.currentTimeMillis() - limitTimestamp
        mutableStateOf(maxOf(0L, totalCooldown - elapsed))
    }

    LaunchedEffect(limitTimestamp) {
        while (remainingMillis > 0) {
            delay(1000L)
            val elapsed = System.currentTimeMillis() - limitTimestamp
            remainingMillis = maxOf(0L, totalCooldown - elapsed)
        }
    }

    val hours = (remainingMillis / (1000 * 60 * 60))
    val minutes = (remainingMillis / (1000 * 60)) % 60
    val seconds = (remainingMillis / 1000) % 60
    val timeFormatted = String.format("%02dh : %02dm : %02ds", hours, minutes, seconds)

    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Card(
            shape = RoundedCornerShape(28.dp),
            colors = CardDefaults.cardColors(containerColor = colors.cardContainer),
            border = androidx.compose.foundation.BorderStroke(1.5.dp, colors.cardBorder),
            modifier = Modifier
                .fillMaxWidth()
                .shadow(20.dp, RoundedCornerShape(28.dp), spotColor = LuxuryGold.copy(alpha = 0.3f))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(28.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Sparkling Hourglass Icon
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.radialGradient(
                                listOf(
                                    LuxuryGold.copy(alpha = 0.35f),
                                    Color.Transparent
                                )
                            )
                        )
                        .border(1.5.dp, LuxuryGold.copy(alpha = 0.5f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text("⏳", fontSize = 38.sp)
                }

                Spacer(modifier = Modifier.height(18.dp))

                Text(
                    text = "You've Swiped Enough for Today ✨",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 22.sp
                    ),
                    color = colors.textPrimary,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "You have swiped enough for today. Come back after 12 hours for new profiles!",
                    style = MaterialTheme.typography.bodyMedium,
                    color = colors.textSecondary,
                    textAlign = TextAlign.Center,
                    lineHeight = 19.sp
                )

                Spacer(modifier = Modifier.height(18.dp))

                // Countdown Timer Box
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = if (colors.isDark) Color(0x33FFD700) else Color(0x22F4C542),
                    border = androidx.compose.foundation.BorderStroke(1.dp, LuxuryGold.copy(alpha = 0.6f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "NEW PROFILES IN",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.2.sp,
                            color = if (colors.isDark) LuxuryGold else Color(0xFF8B6508)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = timeFormatted,
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 24.sp,
                                letterSpacing = 1.sp
                            ),
                            color = colors.textPrimary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Reassurance notice
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (colors.isDark) Color(0x18FFFFFF) else Color(0x0C000000))
                        .padding(horizontal = 12.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("💬", fontSize = 16.sp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "You can continue chatting with all your connections anytime!",
                        fontSize = 12.sp,
                        color = colors.textSecondary,
                        lineHeight = 16.sp
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Continue Chatting CTA Button
                Button(
                    onClick = onContinueChatting,
                    shape = RoundedCornerShape(26.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                    contentPadding = PaddingValues(0.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .background(
                            brush = Brush.horizontalGradient(
                                colors = listOf(
                                    LuxuryRose,
                                    Color(0xFFFF5E7E),
                                    Color(0xFFFFB03A)
                                )
                            ),
                            shape = RoundedCornerShape(26.dp)
                        )
                        .border(
                            width = 1.dp,
                            color = LuxuryGold.copy(alpha = 0.5f),
                            shape = RoundedCornerShape(26.dp)
                        )
                ) {
                    Text(
                        text = "Continue Chatting 💬",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            letterSpacing = 0.5.sp
                        ),
                        color = Color.White
                    )
                }
            }
        }
    }
}
