package com.blyndchat.ui.home

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.blyndchat.data.model.Profile
import com.blyndchat.ui.theme.LuxuryEmerald
import com.blyndchat.ui.theme.LuxuryGold
import com.blyndchat.ui.theme.LuxuryRose

@Composable
fun ProfileCard(
    profile: Profile,
    onConnectClick: () -> Unit,
    onSkipClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = com.blyndchat.ui.theme.LocalAppColors.current
    val glowAnim by rememberInfiniteTransition(label = "glow").animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(2400, easing = FastOutSlowInEasing)),
        label = "pulse"
    )

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 10.dp)
            .shadow(
                elevation = 16.dp,
                shape = RoundedCornerShape(24.dp),
                spotColor = colors.cardShadowSpot,
                ambientColor = if (colors.isDark) LuxuryGold.copy(alpha = 0.2f) else Color(0x10000000)
            )
            .border(
                width = 1.5.dp,
                brush = colors.cardBorder,
                shape = RoundedCornerShape(24.dp)
            ),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = colors.cardContainer
        )
    ) {
        Column {
            // Photo Hero Section with Badges & Bottom Scrim
            Box(
                modifier = Modifier
                    .height(340.dp)
                    .fillMaxWidth()
            ) {
                AsyncImage(
                    model = profile.imageUrl,
                    contentDescription = "Profile Picture",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )

                // Top Floating Badges
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Left: AI / Verified Badge
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = colors.badgeBackground,
                        border = androidx.compose.foundation.BorderStroke(1.dp, colors.badgeBorder)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text("✨", fontSize = 12.sp)
                            Text(
                                text = "AI Verified",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (colors.isDark) LuxuryGold else Color(0xFF942852)
                            )
                        }
                    }

                    // Right: Online Indicator Badge
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = colors.badgeBackground,
                        border = androidx.compose.foundation.BorderStroke(1.dp, if (colors.isDark) LuxuryEmerald.copy(alpha = 0.6f) else Color(0x6600B865))
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(5.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(7.dp)
                                    .clip(CircleShape)
                                    .background(if (colors.isDark) LuxuryEmerald else Color(0xFF00A859))
                            )
                            Text(
                                text = "Online",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = if (colors.isDark) LuxuryEmerald else Color(0xFF008A4B)
                            )
                        }
                    }
                }

                // Smooth Scrim at bottom of photo
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(80.dp)
                        .align(Alignment.BottomCenter)
                        .background(
                            Brush.verticalGradient(
                                colors = colors.scrimColors
                            )
                        )
                )
            }

            // Profile Information Body
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 18.dp, vertical = 14.dp)
            ) {
                // Name & Age Row
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
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
                    Text("✓", color = if (colors.isDark) LuxuryGold else Color(0xFFB33664), fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.height(4.dp))

                // Profession and Location
                Text(
                    text = "💼 ${profile.profession}  •  📍 ${profile.location}",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.Medium,
                        fontSize = 13.sp
                    ),
                    color = colors.textSecondary
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Trait Chips
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    if (profile.height.isNotBlank()) {
                        TraitChip(text = "📏 ${profile.height}")
                    }
                    if (profile.religion.isNotBlank()) {
                        val relText = if (!profile.caste.isNullOrBlank()) "${profile.religion} • ${profile.caste}" else profile.religion
                        TraitChip(text = "🕉️ $relText")
                    }
                    if (!profile.annualIncome.isNullOrBlank()) {
                        TraitChip(text = "💰 ${profile.annualIncome}")
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Action Buttons Row: Skip & Connect
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Skip Button
                    IconButton(
                        onClick = onSkipClick,
                        modifier = Modifier
                            .size(52.dp)
                            .clip(CircleShape)
                            .background(colors.skipButtonContainer)
                            .border(1.dp, colors.skipButtonBorder, CircleShape)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Pass",
                            tint = colors.skipButtonIcon,
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    // Connect Now ✨ Button
                    Button(
                        onClick = onConnectClick,
                        shape = RoundedCornerShape(26.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                        contentPadding = PaddingValues(0.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(52.dp)
                            .scale(1f + 0.025f * glowAnim)
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
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = "Connect Now ✨",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp,
                                    letterSpacing = 0.5.sp
                                ),
                                color = Color.White
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun TraitChip(text: String) {
    val colors = com.blyndchat.ui.theme.LocalAppColors.current
    Surface(
        shape = RoundedCornerShape(10.dp),
        color = colors.traitChipContainer,
        border = androidx.compose.foundation.BorderStroke(1.dp, colors.traitChipBorder)
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium,
            color = colors.traitChipText
        )
    }
}
