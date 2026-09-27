package com.example.shaadi.ui.auth

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.compose.ui.platform.LocalContext
import com.example.shaadi.data.auth.SupabaseAuthRepository

@Composable
fun PhoneLoginScreen(navController: NavController) {
    val context = LocalContext.current
    val auth = remember { SupabaseAuthRepository.getInstance(context) }
    var phone by remember { mutableStateOf("") }
    var otp by remember { mutableStateOf("") }
    var step by remember { mutableStateOf(1) }
    var error by remember { mutableStateOf<String?>(null) }
    var info by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = "Phone Login", style = MaterialTheme.typography.headlineMedium)
        Spacer(Modifier.height(24.dp))

        if (error != null) {
            Text(text = error!!, color = MaterialTheme.colorScheme.error)
            Spacer(Modifier.height(12.dp))
        }
        if (info != null) {
            Text(text = info!!, color = MaterialTheme.colorScheme.primary)
            Spacer(Modifier.height(12.dp))
        }

        if (step == 1) {
            OutlinedTextField(
                value = phone,
                onValueChange = { phone = it },
                label = { Text("Phone (+countrycode)") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(16.dp))
            Button(
                onClick = {
                    error = null; info = null
                    val result = runCatching {
                        kotlinx.coroutines.runBlocking {
                            com.example.shaadi.network.SupabaseAuthService.OtpRequest(phone = phone)
                            val repoField = auth // Make sure instance is created
                            val service = com.example.shaadi.network.SupabaseApiClient.retrofit.create(com.example.shaadi.network.SupabaseAuthService::class.java)
                            service.requestOtp(com.example.shaadi.network.SupabaseAuthService.OtpRequest(phone))
                        }
                    }
                    if (result.isSuccess) {
                        info = "OTP sent. Enter the code."
                        step = 2
                    } else {
                        error = result.exceptionOrNull()?.message ?: "Failed to send OTP"
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) { Text("Send OTP") }
        } else {
            OutlinedTextField(
                value = otp,
                onValueChange = { otp = it },
                label = { Text("OTP Code") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(16.dp))
            Button(
                onClick = {
                    error = null; info = null
                    val result = runCatching {
                        kotlinx.coroutines.runBlocking {
                            val service = com.example.shaadi.network.SupabaseApiClient.retrofit.create(com.example.shaadi.network.SupabaseAuthService::class.java)
                            val resp = service.verifyOtp(com.example.shaadi.network.SupabaseAuthService.VerifyRequest(phone = phone, token = otp))
                            val token = resp.accessToken ?: throw IllegalStateException("No token returned")
                            val store = com.example.shaadi.data.auth.CredentialStore(context)
                            store.saveToken(token)
                            // Persist userId from authed /user for reliable identification
                            runCatching {
                                val authed = com.example.shaadi.network.SupabaseApiClient.authedRetrofit(token)
                                val authedSvc = authed.create(com.example.shaadi.network.SupabaseAuthService::class.java)
                                val me = authedSvc.getUser()
                                if (!me.id.isNullOrBlank()) store.saveUserId(me.id!!)
                            }
                        }
                    }
                    if (result.isSuccess) {
                        navController.navigate("home")
                    } else {
                        error = result.exceptionOrNull()?.message ?: "Failed to verify OTP"
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) { Text("Verify & Continue") }

            Spacer(Modifier.height(8.dp))
            TextButton(onClick = { step = 1; info = null }) { Text("Resend OTP") }
        }
    }
}
