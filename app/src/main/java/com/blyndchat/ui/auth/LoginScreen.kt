package com.blyndchat.ui.auth

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.compose.ui.platform.LocalContext
import com.blyndchat.BuildConfig
import com.blyndchat.data.auth.LocalAuthRepository
import com.blyndchat.data.auth.RemoteAuthRepository
import com.blyndchat.data.auth.SupabaseAuthRepository
import com.blyndchat.data.auth.CredentialStore
import com.blyndchat.data.auth.AuthRepository
import com.blyndchat.network.ProfilesService
import com.blyndchat.network.SupabaseRestApiClient
import com.blyndchat.util.JwtUtils
import kotlinx.coroutines.launch
import android.net.Uri
import android.util.Log

@Composable
fun LoginScreen(navController: NavController) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var error by remember { mutableStateOf<String?>(null) }
    val context = LocalContext.current
    val auth: AuthRepository = remember {
        when {
            BuildConfig.USE_SUPABASE_AUTH -> SupabaseAuthRepository.getInstance(context)
            BuildConfig.USE_REMOTE_AUTH -> RemoteAuthRepository.getInstance(context)
            else -> LocalAuthRepository.getInstance(context)
        }
    }
    val store = remember { CredentialStore(context.applicationContext) }
    val scope = rememberCoroutineScope()

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
                scope.launch {
                    val result = auth.login(email.trim(), password)
                    if (result.isSuccess) {
                        // Best-effort profile upsert for Supabase users
                        if (BuildConfig.USE_SUPABASE_AUTH) {
                            val token = store.getToken()
                            if (token != null) {
                                val userId = JwtUtils.getUserIdFromToken(token)
                                if (userId != null) {
                                    val upsertResult = runCatching {
                                        val retrofit = SupabaseRestApiClient.authedRetrofit(token)
                                        val service = retrofit.create(ProfilesService::class.java)
                                        val body = listOf(
                                            ProfilesService.ProfileUpsertDto(
                                                id = userId,
                                                name = email.substringBefore('@')
                                            )
                                        )
                                        service.upsertProfiles(body)
                                    }
                                    if (upsertResult.isFailure) {
                                        Log.e("LoginScreen", "Profile upsert failed", upsertResult.exceptionOrNull())
                                    }
                                    // If a pending signup image exists, upload it and set image_url
                                    val pending = store.getPendingImageUri()
                                    if (pending != null) {
                                        val uploadResult = runCatching {
                                            val storageRetrofit = com.blyndchat.network.SupabaseStorageApiClient.authedRetrofit(token)
                                            val storage = storageRetrofit.create(com.blyndchat.network.SupabaseStorageService::class.java)
                                            val uri = Uri.parse(pending)
                                            val filename = "users_${userId}.jpg"
                                            val path = "users/$userId/$filename"
                                            val part = com.blyndchat.network.SupabaseStorageService.buildFilePart(context, uri, filename = filename)
                                            storage.upload(bucket = "profiles", path = path, file = part)
                                            val publicUrl = "${BuildConfig.SUPABASE_URL}/storage/v1/object/public/profiles/$path"
                                            val retrofit2 = SupabaseRestApiClient.authedRetrofit(token)
                                            val svc2 = retrofit2.create(ProfilesService::class.java)
                                            val update = listOf(
                                                ProfilesService.ProfileUpsertDto(
                                                    id = userId,
                                                    name = null,
                                                    imageUrl = publicUrl
                                                )
                                            )
                                            svc2.upsertProfiles(update)
                                            store.clearPendingImageUri()
                                        }
                                        if (uploadResult.isFailure) {
                                            Log.e("LoginScreen", "Image upload or profile image_url update failed", uploadResult.exceptionOrNull())
                                        }
                                    }
                                }
                            }
                        }
                        navController.navigate("home")
                    } else {
                        error = result.exceptionOrNull()?.message ?: "Login failed"
                    }
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
