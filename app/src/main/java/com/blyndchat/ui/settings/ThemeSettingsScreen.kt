package com.blyndchat.ui.settings

import android.app.Activity
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.blyndchat.data.auth.CredentialStore
import com.blyndchat.ui.theme.LuxuryRose
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ThemeSettingsScreen(navController: NavController) {
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current
    val store = remember { CredentialStore(context.applicationContext) }
    var selected by remember { mutableStateOf(store.getThemeMode()) }
    val scope = rememberCoroutineScope()
    val colors = com.blyndchat.ui.theme.LocalAppColors.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Theme Settings", fontWeight = FontWeight.Bold, color = colors.topBarTitle) },
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
                                    imageVector = Icons.Filled.ArrowBack,
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
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
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
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    "Select Theme",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = colors.textPrimary
                )

                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = colors.cardContainer),
                    border = androidx.compose.foundation.BorderStroke(1.dp, colors.cardBorderSubtle),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        RowOption(
                            label = "Dark Grey (Default)",
                            subtitle = "Sleek dark grey charcoal aesthetic with luxury accents",
                            selected = selected == "black",
                            textColor = colors.textPrimary,
                            subtextColor = colors.textSecondary
                        ) { selected = "black" }

                        Divider(color = colors.dividerColor, thickness = 1.dp, modifier = Modifier.padding(vertical = 4.dp))

                        RowOption(
                            label = "White",
                            subtitle = "Clean modern light theme with crisp elegance",
                            selected = selected == "white",
                            textColor = colors.textPrimary,
                            subtextColor = colors.textSecondary
                        ) { selected = "white" }
                    }
                }

                Spacer(modifier = Modifier.weight(1f))

                Button(
                    onClick = {
                        store.saveThemeMode(selected)
                        com.blyndchat.ui.theme.ThemeState.currentMode = selected
                        scope.launch { snackbarHostState.showSnackbar("Theme updated") }
                        navController.popBackStack()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = RoundedCornerShape(25.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = LuxuryRose)
                ) {
                    Text("Apply", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                }
            }
        }
    }
}

@Composable
private fun RowOption(
    label: String,
    subtitle: String,
    selected: Boolean,
    textColor: androidx.compose.ui.graphics.Color,
    subtextColor: androidx.compose.ui.graphics.Color,
    onSelect: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onSelect() }
            .padding(vertical = 10.dp, horizontal = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(selected = selected, onClick = onSelect)
        Column(modifier = Modifier.padding(start = 10.dp)) {
            Text(label, fontWeight = FontWeight.SemiBold, fontSize = 15.sp, color = textColor)
            Text(subtitle, fontSize = 12.sp, color = subtextColor)
        }
    }
}
