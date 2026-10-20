package com.blyndchat.ui.settings

import android.app.Activity
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.blyndchat.data.auth.CredentialStore
import com.blyndchat.ui.theme.LuxuryGold
import com.blyndchat.ui.theme.LuxuryRose
import com.blyndchat.ui.theme.ShaadiRed

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(navController: NavController) {
    val context = navController.context
    val store = remember { CredentialStore(context.applicationContext) }
    var showThemeDialog by remember { mutableStateOf(false) }
    var showLogoutDialog by remember { mutableStateOf(false) }
    val currentMode = com.blyndchat.ui.theme.ThemeState.currentMode
    val colors = com.blyndchat.ui.theme.LocalAppColors.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Settings",
                        fontWeight = FontWeight.Bold,
                        color = colors.topBarTitle
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = { navController.popBackStack() },
                        modifier = Modifier.padding(start = 6.dp)
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = if (colors.isDark) Color.White else Color(0xFF141416),
                            shadowElevation = 3.dp,
                            modifier = Modifier.size(38.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.ArrowBack,
                                    contentDescription = "Back",
                                    tint = if (colors.isDark) Color(0xFF141416) else Color.White,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = colors.topBarContainer,
                    navigationIconContentColor = if (colors.isDark) Color.White else Color(0xFF141416),
                    titleContentColor = colors.topBarTitle
                )
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(colors.screenBackgroundBrush)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = colors.cardContainer),
                    border = androidx.compose.foundation.BorderStroke(1.dp, colors.cardBorderSubtle),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(vertical = 4.dp)) {
                        // Edit Profile Option
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { navController.navigate("settings/profile") }
                                .padding(horizontal = 16.dp, vertical = 14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(if (colors.isDark) Color(0x22E05388) else Color(0x18D64978)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Person,
                                    contentDescription = "Profile",
                                    tint = LuxuryRose,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(14.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Edit Profile",
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                                    color = colors.textPrimary
                                )
                                Text(
                                    text = "Manage your anonymous bio and details",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = colors.textSecondary
                                )
                            }
                            Text(
                                text = "›",
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Bold,
                                color = colors.textMuted
                            )
                        }

                        Divider(color = colors.dividerColor, thickness = 1.dp, modifier = Modifier.padding(horizontal = 16.dp))

                        // Theme Option
                        val label = if (currentMode == "black") "Dark Grey (Default)" else "White"
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { showThemeDialog = true }
                                .padding(horizontal = 16.dp, vertical = 14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(if (colors.isDark) Color(0x22FFD700) else Color(0x22C9A961)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("🎨", fontSize = 18.sp)
                            }
                            Spacer(modifier = Modifier.width(14.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Theme",
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                                    color = colors.textPrimary
                                )
                                Text(
                                    text = "Current: $label",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = colors.textSecondary
                                )
                            }
                            Text(
                                text = "›",
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Bold,
                                color = colors.textMuted
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.weight(1f))

                Button(
                    onClick = { showLogoutDialog = true },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = RoundedCornerShape(25.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = ShaadiRed)
                ) {
                    Text("Log Out", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                }
            }

            if (showThemeDialog) {
                ThemeSelectionDialog(
                    current = com.blyndchat.ui.theme.ThemeState.currentMode,
                    onDismiss = { showThemeDialog = false },
                    onSelect = { mode ->
                        store.saveThemeMode(mode)
                        com.blyndchat.ui.theme.ThemeState.currentMode = mode
                        showThemeDialog = false
                    }
                )
            }
            if (showLogoutDialog) {
                AlertDialog(
                    onDismissRequest = { showLogoutDialog = false },
                    title = { Text("Log Out") },
                    text = { Text("Are you sure you want to log out?") },
                    confirmButton = {
                        TextButton(onClick = {
                            showLogoutDialog = false
                            store.clear()
                            navController.navigate("login") {
                                popUpTo(0) { inclusive = true }
                            }
                        }) { Text("Log Out") }
                    },
                    dismissButton = {
                        TextButton(onClick = { showLogoutDialog = false }) { Text("Cancel") }
                    }
                )
            }
        }
    }
}

@Composable
private fun ThemeSelectionDialog(
    current: String,
    onDismiss: () -> Unit,
    onSelect: (String) -> Unit
) {
    var selected by remember { mutableStateOf(if (current == "black") "black" else "white") }
    val colors = com.blyndchat.ui.theme.LocalAppColors.current
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = colors.cardContainer,
        titleContentColor = colors.textPrimary,
        textContentColor = colors.textSecondary,
        title = { Text("Select Theme", fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                RowOption(
                    label = "Dark Grey (Default)",
                    subtitle = "Sleek dark grey charcoal aesthetic with luxury accents",
                    selected = selected == "black"
                ) { selected = "black" }

                RowOption(
                    label = "White",
                    subtitle = "Clean modern white theme with crisp elegance",
                    selected = selected == "white"
                ) { selected = "white" }
            }
        },
        confirmButton = {
            TextButton(onClick = { onSelect(selected) }) { Text("Apply", fontWeight = FontWeight.Bold, color = colors.topBarSubtitle) }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel", color = colors.textSecondary) }
        }
    )
}

@Composable
private fun RowOption(label: String, subtitle: String? = null, selected: Boolean, onSelect: () -> Unit) {
    androidx.compose.foundation.layout.Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onSelect() }
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(selected = selected, onClick = onSelect)
        Column(modifier = Modifier.padding(start = 8.dp)) {
            Text(text = label, fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
            if (subtitle != null) {
                Text(
                    text = subtitle,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
