package com.blyndchat.ui.auth

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.background
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.compose.ui.platform.LocalContext
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.blyndchat.BuildConfig
import com.blyndchat.data.auth.LocalAuthRepository
import com.blyndchat.data.auth.RemoteAuthRepository
import com.blyndchat.data.auth.SupabaseAuthRepository
import com.blyndchat.data.auth.CredentialStore
import com.blyndchat.data.auth.AuthRepository
import com.blyndchat.ui.theme.ShaadiGold
import com.blyndchat.network.ProfilesService
import com.blyndchat.network.SupabaseRestApiClient
import com.blyndchat.util.JwtUtils
import kotlinx.coroutines.launch
import android.net.Uri
import android.util.Log

@Composable
fun LoginScreen(navController: NavController) {
    val context = LocalContext.current
    val store = remember { CredentialStore(context.applicationContext) }
    var email by remember { mutableStateOf(store.getEmail() ?: "") }
    var password by remember { mutableStateOf("") }
    var error by remember { mutableStateOf<String?>(null) }
    val auth: AuthRepository = remember {
        when {
            BuildConfig.USE_SUPABASE_AUTH -> SupabaseAuthRepository.getInstance(context)
            BuildConfig.USE_REMOTE_AUTH -> RemoteAuthRepository.getInstance(context)
            else -> LocalAuthRepository.getInstance(context)
        }
    }
    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        // Top ~60%: full-bleed logo
        AsyncImage(
            model = ImageRequest.Builder(context)
                .data("file:///android_asset/Logo.png")
                .build(),
            contentDescription = "Blynd Chat logo",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxWidth()
                .weight(0.605f)
        )

        // Bottom ~40%: login fields
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(0.395f)
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = Color.Black,
                unfocusedTextColor = Color.Black,
                focusedBorderColor = ShaadiGold,
                focusedLabelColor = ShaadiGold,
                cursorColor = ShaadiGold
            ),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            visualTransformation = PasswordVisualTransformation(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = Color.Black,
                unfocusedTextColor = Color.Black,
                focusedBorderColor = ShaadiGold,
                focusedLabelColor = ShaadiGold,
                cursorColor = ShaadiGold
            ),
            modifier = Modifier.fillMaxWidth()
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            TextButton(
                onClick = { navController.navigate("forgot_password") },
                contentPadding = PaddingValues(horizontal = 0.dp, vertical = 0.dp)
            ) {
                Text(
                    text = "Forgot Password?",
                    color = ShaadiGold,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = androidx.compose.ui.text.font.FontWeight.SemiBold
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

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
                                    val pendingName = store.getPendingName() ?: email.substringBefore('@')
                                    val pendingAge = store.getPendingAge()
                                    val pendingGender = store.getPendingGender()
                                    var avatarUrl = store.getPendingImageUri()

                                    // If a pending avatar image exists, try uploading to Supabase Storage
                                    if (avatarUrl != null) {
                                        runCatching {
                                            val storageRetrofit = com.blyndchat.network.SupabaseStorageApiClient.authedRetrofit(token)
                                            val storage = storageRetrofit.create(com.blyndchat.network.SupabaseStorageService::class.java)
                                            val uri = Uri.parse(avatarUrl)
                                            val ext = if (avatarUrl?.endsWith(".png", true) == true) "png" else "jpg"
                                            val filename = "users_${userId}.$ext"
                                            val path = "users/$userId/$filename"
                                            val part = com.blyndchat.network.SupabaseStorageService.buildFilePart(context, uri, filename = filename)
                                            val uploadResp = storage.upload(bucket = "profiles", path = path, file = part)
                                            if (uploadResp.isSuccessful) {
                                                avatarUrl = "${BuildConfig.SUPABASE_URL}/storage/v1/object/public/profiles/$path"
                                            }
                                        }.onFailure {
                                            Log.e("LoginScreen", "Avatar upload failed, will fallback to asset URI", it)
                                        }
                                    }

                                    val pendingHeight = store.getPendingHeight()
                                    val pendingProfession = store.getPendingProfession()
                                    val pendingBio = store.getPendingBio() ?: ""
                                    val pendingLookingFor = store.getPendingLookingFor()
                                    val pendingAgeRange = store.getPendingPreferredAgeRange()
                                    val pendingTopics = store.getPendingPreferredTopics()
                                    val pendingPersonality = store.getPendingPersonalityType()
                                    val pendingIntensity = store.getPendingConversationIntensity()
                                    val pendingAvoided = store.getPendingAvoidedTopics()

                                    val encodedAbout = com.blyndchat.data.model.ProfileMetadataParser.encode(
                                        bio = pendingBio,
                                        lookingFor = pendingLookingFor,
                                        preferredAgeRange = pendingAgeRange,
                                        preferredTopics = pendingTopics,
                                        personalityType = pendingPersonality,
                                        conversationIntensity = pendingIntensity,
                                        avoidedTopics = pendingAvoided
                                    )

                                    val upsertResult = runCatching {
                                        val retrofit = SupabaseRestApiClient.authedRetrofit(token)
                                        val service = retrofit.create(ProfilesService::class.java)
                                        val body = listOf(
                                            ProfilesService.ProfileUpsertDto(
                                                id = userId,
                                                name = pendingName,
                                                age = pendingAge,
                                                gender = pendingGender,
                                                height = pendingHeight,
                                                profession = pendingProfession,
                                                imageUrl = avatarUrl,
                                                about = encodedAbout
                                            )
                                        )
                                        service.upsertProfiles(body)
                                    }
                                    if (pendingGender != null) {
                                        store.saveUserGender(pendingGender)
                                    }
                                    if (upsertResult.isSuccess) {
                                        store.clearPendingProfile()
                                    } else {
                                        Log.e("LoginScreen", "Profile upsert failed", upsertResult.exceptionOrNull())
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
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = ShaadiGold)
        ) {
            Text("Login")
        }

        Spacer(modifier = Modifier.height(8.dp))

        TextButton(
            onClick = { navController.navigate("signup") },
            colors = ButtonDefaults.textButtonColors(contentColor = ShaadiGold)
        ) {
            Text("Don't have an account? Sign Up")
        }
        }
    }
}
