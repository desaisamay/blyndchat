package com.example.shaadi.ui.auth

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.compose.ui.platform.LocalContext
import com.example.shaadi.BuildConfig
import com.example.shaadi.data.auth.LocalAuthRepository
import com.example.shaadi.data.auth.RemoteAuthRepository
import com.example.shaadi.data.auth.SupabaseAuthRepository

@Composable
fun LoginScreen(navController: NavController) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var error by remember { mutableStateOf<String?>(null) }
    val context = LocalContext.current
    val auth = remember {
        when {
            BuildConfig.USE_SUPABASE_AUTH -> SupabaseAuthRepository.getInstance(context)
            BuildConfig.USE_REMOTE_AUTH -> RemoteAuthRepository.getInstance(context)
            else -> LocalAuthRepository.getInstance(context)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = "Welcome Back", style = MaterialTheme.typography.headlineMedium)
        
        Spacer(modifier = Modifier.height(32.dp))

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(24.dp))

        if (error != null) {
            Text(text = error!!, color = MaterialTheme.colorScheme.error)
            Spacer(modifier = Modifier.height(12.dp))
        }

            Button(
            onClick = {
                error = null
                val result = auth.login(email.trim(), password)
                if (result.isSuccess) {
                    navController.navigate("home")
                } else {
                    error = result.exceptionOrNull()?.message ?: "Login failed"
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Login")
        }

            Spacer(modifier = Modifier.height(8.dp))
            OutlinedButton(
                onClick = { navController.navigate("phone_login") },
                modifier = Modifier.fillMaxWidth()
            ) { Text("Login with Phone (OTP)") }

        Spacer(modifier = Modifier.height(16.dp))

        TextButton(onClick = { navController.navigate("signup") }) {
            Text("Don't have an account? Sign Up")
        }
    }
}
