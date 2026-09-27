package com.example.shaadi.ui.settings

import android.app.Activity
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Surface
import androidx.compose.material3.RadioButton
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.shaadi.data.auth.CredentialStore
import com.example.shaadi.ui.theme.Gradients
import com.example.shaadi.ui.theme.ShaadiGold
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.background

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(navController: NavController) {
    val context = navController.context
    val store = remember { CredentialStore(context.applicationContext) }
    var showThemeDialog by remember { mutableStateOf(false) }
    var currentMode by remember { mutableStateOf(store.getThemeMode()) }

    Scaffold(
        topBar = {
            androidx.compose.foundation.layout.Column {
                androidx.compose.foundation.layout.Box(
                    modifier = Modifier.background(brush = Gradients.PrimaryHorizontal)
                ) {
                TopAppBar(
                    title = { Text("Settings") },
                    navigationIcon = {
                        TextButton(onClick = { navController.popBackStack() }) { Text("Back") }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color.Transparent,
                        titleContentColor = Color.White,
                        navigationIconContentColor = Color.White
                    )
                )
                }
                Divider(color = ShaadiGold, thickness = 1.dp)
            }
        }
    ) { padding ->
        Surface(
            modifier = Modifier.fillMaxSize().padding(padding),
            color = MaterialTheme.colorScheme.background
        ) {
            val items = listOf("Theme")
            LazyColumn(modifier = Modifier.padding(16.dp)) {
                items(items) { item ->
                    Column(modifier = Modifier
                        .clickable {
                            if (item == "Theme") {
                                showThemeDialog = true
                            }
                        }
                        .padding(vertical = 12.dp)) {
                        Text(text = item, style = MaterialTheme.typography.titleMedium)
                        if (item == "Theme") {
                            val label = if (currentMode == "black") "Black" else "White"
                            Text(text = "Current: $label", style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                }
            }
            if (showThemeDialog) {
                ThemeSelectionDialog(
                    current = currentMode,
                    onDismiss = { showThemeDialog = false },
                    onSelect = { mode ->
                        currentMode = mode
                        store.saveThemeMode(mode)
                        showThemeDialog = false
                        // Restart activity to apply theme immediately
                        (navController.context as? Activity)?.recreate()
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
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Select Theme") },
        text = {
            Column {
                RowOption(label = "White", selected = selected == "white") { selected = "white" }
                RowOption(label = "Black", selected = selected == "black") { selected = "black" }
            }
        },
        confirmButton = {
            TextButton(onClick = { onSelect(selected) }) { Text("Apply") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

@Composable
private fun RowOption(label: String, selected: Boolean, onSelect: () -> Unit) {
    androidx.compose.foundation.layout.Row(modifier = Modifier
        .clickable { onSelect() }
        .padding(vertical = 8.dp)) {
        RadioButton(selected = selected, onClick = onSelect)
        Text(text = label, modifier = Modifier.padding(start = 8.dp))
    }
}
