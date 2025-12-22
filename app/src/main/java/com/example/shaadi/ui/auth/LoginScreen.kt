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
import com.example.shaadi.data.auth.CredentialStore
import com.example.shaadi.data.auth.AuthRepository
import com.example.shaadi.network.ProfilesService
import com.example.shaadi.network.SupabaseRestApiClient
import com.example.shaadi.util.JwtUtils
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
                                var userId = JwtUtils.getUserIdFromToken(token)
                                if (userId == null) {
                                    Log.w("LoginScreen", "JWT parse failed; attempting /auth/v1/user to get id")
                                    val fetched = runCatching {
                                        val authRetrofit = com.example.shaadi.network.SupabaseApiClient.authedRetrofit(token)
                                        val authService = authRetrofit.create(com.example.shaadi.network.SupabaseAuthService::class.java)
                                        authService.getUser().id
                                    }
                                    if (fetched.isSuccess && !fetched.getOrNull().isNullOrBlank()) {
                                        userId = fetched.getOrNull()
                                        Log.i("LoginScreen", "Fetched userId from /user: ${'$'}userId")
                                    }
                                }
                                if (userId != null) {
                                    Log.i("LoginScreen", "Supabase auth on; preparing profile upsert for userId=$userId")
                                    val upsertResult = runCatching {
                                        // Fetch user metadata from Supabase Auth
                                        val authRetrofit = com.example.shaadi.network.SupabaseApiClient.authedRetrofit(token)
                                        val authService = authRetrofit.create(com.example.shaadi.network.SupabaseAuthService::class.java)
                                        val me = authService.getUser()
                                        val meta = (me.userMetadata ?: me.rawUserMetaData) ?: emptyMap()
                                        fun str(key: String): String? = (meta[key] as? String)?.takeIf { it.isNotBlank() }
                                        fun intVal(key: String): Int? = when (val v = meta[key]) {
                                            is Number -> v.toInt()
                                            is String -> v.toIntOrNull()
                                            else -> null
                                        }

                                        val retrofit = SupabaseRestApiClient.authedRetrofit(token)
                                        val service = retrofit.create(ProfilesService::class.java)
                                        val body = listOf(
                                            ProfilesService.ProfileUpsertDto(
                                                id = userId,
                                                name = str("name") ?: email.substringBefore('@'),
                                                age = intVal("age"),
                                                height = str("height"),
                                                religion = str("religion"),
                                                caste = str("caste"),
                                                profession = str("profession"),
                                                location = str("location"),
                                                imageUrl = str("image_url"),
                                                about = str("about"),
                                                gender = str("gender"),
                                                annualIncome = str("annual_income"),
                                                phoneNumber = str("phone_number")
                                            )
                                        )
                                        val resp = service.upsertProfiles(body)
                                        Log.i("LoginScreen", "Profile upsert response code=${resp.code()} success=${resp.isSuccessful}")
                                        if (!resp.isSuccessful) {
                                            val err = try { resp.errorBody()?.string() } catch (_: Exception) { null }
                                            Log.w("LoginScreen", "Profile upsert not successful; http=${resp.code()} body=${err}")
                                        }
                                    }
                                    if (upsertResult.isFailure) {
                                        Log.e("LoginScreen", "Profile upsert failed", upsertResult.exceptionOrNull())
                                    } else {
                                        Log.i("LoginScreen", "Profile upsert finished without exception")
                                    }
                                    // If a pending signup image exists, upload it and set image_url
                                    val pending = store.getPendingImageUri()
                                    if (pending != null) {
                                        Log.i("LoginScreen", "Found pending image; starting upload")
                                        val uploadResult = runCatching {
                                            val storageRetrofit = com.example.shaadi.network.SupabaseStorageApiClient.authedRetrofit(token)
                                            val storage = storageRetrofit.create(com.example.shaadi.network.SupabaseStorageService::class.java)
                                            val uri = Uri.parse(pending)
                                            val filename = "users_${userId}.jpg"
                                            val path = "users/$userId/$filename"
                                            val part = com.example.shaadi.network.SupabaseStorageService.buildFilePart(context, uri, filename = filename)
                                            val uploadResp = storage.upload(bucket = "profiles", path = path, file = part)
                                            Log.i("LoginScreen", "Image upload response code=${uploadResp.code()} success=${uploadResp.isSuccessful}")
                                            if (!uploadResp.isSuccessful) {
                                                val err = try { uploadResp.errorBody()?.string() } catch (_: Exception) { null }
                                                Log.w("LoginScreen", "Image upload not successful; http=${uploadResp.code()} body=${err}")
                                            }
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
                                            val updateResp = svc2.upsertProfiles(update)
                                            Log.i("LoginScreen", "Profile image_url update response code=${updateResp.code()} success=${updateResp.isSuccessful}")
                                            if (!updateResp.isSuccessful) {
                                                val err = try { updateResp.errorBody()?.string() } catch (_: Exception) { null }
                                                Log.w("LoginScreen", "Profile image_url update not successful; http=${updateResp.code()} body=${err}")
                                            }
                                            store.clearPendingImageUri()
                                        }
                                        if (uploadResult.isFailure) {
                                            Log.e("LoginScreen", "Image upload or profile image_url update failed", uploadResult.exceptionOrNull())
                                        } else {
                                            Log.i("LoginScreen", "Image upload + profile update finished without exception")
                                        }
                                    }
                                }
                                else {
                                    Log.w("LoginScreen", "Unable to parse userId from JWT; skipping upsert")
                                }
                            }
                            else {
                                Log.w("LoginScreen", "No token after login; skipping upsert")
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
