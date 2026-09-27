package com.blyndchat.ui.settings

import android.app.Activity
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.IconButton
import androidx.compose.material3.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.blyndchat.data.auth.CredentialStore
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ThemeSettingsScreen(navController: NavController) {
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current
    val store = remember { CredentialStore(context.applicationContext) }
    var selected by remember { mutableStateOf(store.getThemeMode()) }
    val scope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Theme Settings") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text("Choose Theme")
            RowOption(label = "Black (Fancy)", selected = selected == "black") { selected = "black" }
            RowOption(label = "White (Light)", selected = selected == "white") { selected = "white" }
            Button(onClick = {
                store.saveThemeMode(selected)
                scope.launch { snackbarHostState.showSnackbar("Theme updated to $selected") }
                (context as? Activity)?.recreate()
                navController.popBackStack()
            }, modifier = Modifier.fillMaxWidth()) {
                Text("Apply")
            }
        }
    }
}

@Composable
private fun RowOption(label: String, selected: Boolean, onSelect: () -> Unit) {
    Column(modifier = Modifier.fillMaxWidth()) {
        androidx.compose.foundation.layout.Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            RadioButton(selected = selected, onClick = onSelect)
            Text(label, modifier = Modifier.padding(top = 12.dp))
        }
    }
}
