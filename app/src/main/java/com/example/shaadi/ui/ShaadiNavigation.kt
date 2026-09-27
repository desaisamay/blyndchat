package com.example.shaadi.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.shaadi.ui.auth.LoginScreen
import com.example.shaadi.ui.auth.SignupScreen
import com.example.shaadi.ui.home.HomeScreen
import com.example.shaadi.ui.home.ProfileDetailScreen
import com.example.shaadi.ui.chat.ChatScreen
import com.example.shaadi.ui.settings.SettingsScreen
import com.example.shaadi.ui.settings.ThemeSettingsScreen
import com.example.shaadi.ui.settings.ProfileSettingsScreen

@Composable
fun ShaadiApp(initialRoute: String? = null) {
    val navController = rememberNavController()
    val context = LocalContext.current
    val token = com.example.shaadi.data.auth.CredentialStore(context).getToken()
    val startDestination = if (!token.isNullOrBlank()) (initialRoute ?: "home") else "login"

    NavHost(navController = navController, startDestination = startDestination) {
        composable("login") {
            LoginScreen(navController = navController)
        }
        composable("signup") {
            SignupScreen(navController = navController)
        }
            composable("phone_login") {
                com.example.shaadi.ui.auth.PhoneLoginScreen(navController)
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
