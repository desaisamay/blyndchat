package com.example.shaadi

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.shaadi.ui.theme.FancyTheme
import com.example.shaadi.ui.theme.ShaadiCloneTheme
import com.example.shaadi.data.auth.CredentialStore
import com.example.shaadi.ui.ShaadiApp
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private fun computeInitialRoute(): String? {
        val convo = intent?.getStringExtra("conversation_id")
        val peer = intent?.getStringExtra("peer_id")
        return if (!convo.isNullOrBlank() && !peer.isNullOrBlank()) {
            "chat/$convo/$peer"
        } else null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val store = CredentialStore(applicationContext)
            val mode = store.getThemeMode()
            val useFancy = mode == "black"
            if (useFancy) FancyTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    ShaadiApp(initialRoute = computeInitialRoute())
                }
            } else ShaadiCloneTheme(darkTheme = false, dynamicColor = false) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    ShaadiApp(initialRoute = computeInitialRoute())
                }
            }
        }
    }

    override fun onNewIntent(intent: android.content.Intent?) {
        super.onNewIntent(intent)
        setIntent(intent)
        // Recompose with new initialRoute is tricky; a simple approach is to restart Activity.
        // This keeps things reliable for deep links from notifications.
        recreate()
    }
}
