package com.blyndchat

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.content.ContextCompat
import com.blyndchat.ui.theme.FancyTheme
import android.content.Intent
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.blyndchat.data.auth.CredentialStore
import com.blyndchat.ui.BlyndChatApp
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val requestNotificationPermission = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { /* Permission result handled */ }

    private var targetRoute by mutableStateOf<String?>(null)

    private fun extractRouteFromIntent(intent: Intent?): String? {
        val convo = intent?.getStringExtra("conversation_id")
        val peer = intent?.getStringExtra("peer_id")
        return if (!convo.isNullOrBlank() && !peer.isNullOrBlank()) {
            "chat/$convo/$peer"
        } else null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        targetRoute = extractRouteFromIntent(intent)

        // Request POST_NOTIFICATIONS permission on Android 13+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                requestNotificationPermission.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }

        val initialTheme = CredentialStore(applicationContext).getThemeMode()
        com.blyndchat.ui.theme.ThemeState.currentMode = initialTheme

        setContent {
            com.blyndchat.ui.theme.BlyndAppTheme(themeMode = com.blyndchat.ui.theme.ThemeState.currentMode) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    BlyndChatApp(
                        initialRoute = targetRoute,
                        onRouteHandled = { targetRoute = null }
                    )
                }
            }
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        setIntent(intent)
        val route = extractRouteFromIntent(intent)
        if (route != null) {
            targetRoute = route
        }
    }
}
