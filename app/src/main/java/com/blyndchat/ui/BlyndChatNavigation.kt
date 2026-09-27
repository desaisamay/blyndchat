package com.blyndchat.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.blyndchat.ui.auth.LoginScreen
import com.blyndchat.ui.auth.SignupScreen
import com.blyndchat.ui.home.HomeScreen
import com.blyndchat.ui.home.ProfileDetailScreen
import com.blyndchat.ui.chat.ChatScreen
import com.blyndchat.ui.settings.SettingsScreen
import com.blyndchat.ui.settings.ThemeSettingsScreen
import com.blyndchat.ui.settings.ProfileSettingsScreen

@Composable
fun BlyndChatApp(initialRoute: String? = null) {
    val navController = rememberNavController()
    val context = LocalContext.current
    val token = com.blyndchat.data.auth.CredentialStore(context).getToken()
    val startDestination = if (!token.isNullOrBlank()) (initialRoute ?: "home") else "login"

    NavHost(navController = navController, startDestination = startDestination) {
        composable("login") {
            LoginScreen(navController = navController)
        }
        composable("signup") {
            SignupScreen(navController = navController)
        }
            composable("phone_login") {
                com.blyndchat.ui.auth.PhoneLoginScreen(navController)
            }
        composable("home") {
            HomeScreen(navController = navController)
        }
        composable("settings") {
            SettingsScreen(navController = navController)
        }
        composable("settings/theme") {
            ThemeSettingsScreen(navController = navController)
        }
        composable("settings/profile") {
            ProfileSettingsScreen(navController = navController)
        }
        composable("profile/{profileId}") { backStackEntry ->
            val profileId = backStackEntry.arguments?.getString("profileId")
            ProfileDetailScreen(navController = navController, profileId = profileId)
        }
        composable("chat/{conversationId}/{peerId}") { backStackEntry ->
            val conversationId = backStackEntry.arguments?.getString("conversationId")
            val peerId = backStackEntry.arguments?.getString("peerId")
            ChatScreen(navController = navController, conversationId = conversationId, peerId = peerId)
        }
    }
}
