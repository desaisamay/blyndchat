package com.example.shaadi.ui

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.shaadi.ui.auth.LoginScreen
import com.example.shaadi.ui.auth.SignupScreen
import com.example.shaadi.ui.home.HomeScreen
import com.example.shaadi.ui.home.ProfileDetailScreen
import com.example.shaadi.ui.chat.ChatScreen

@Composable
fun ShaadiApp() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "login") {
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
