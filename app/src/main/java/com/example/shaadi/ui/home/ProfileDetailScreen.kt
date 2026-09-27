package com.example.shaadi.ui.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.shaadi.data.model.Profile
import com.example.shaadi.data.profile.ProfilesRepository
import com.example.shaadi.data.auth.CredentialStore
import com.example.shaadi.network.SupabaseRestApiClient
import com.example.shaadi.network.InterestsService
import com.example.shaadi.network.InterestDto
import com.example.shaadi.util.JwtUtils
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileDetailScreen(navController: NavController, profileId: String?) {
    val repo = remember { ProfilesRepository.getInstance(navController.context) }
    var profile by remember { mutableStateOf<Profile?>(null) }
    var error by remember { mutableStateOf<String?>(null) }
    var showPhone by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val store = remember { CredentialStore(navController.context.applicationContext) }
    var info by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(profileId) {
        error = null
        val result = repo.fetchProfiles(limit = null)
        if (result.isSuccess) {
            val list = result.getOrDefault(emptyList())
            profile = list.firstOrNull { it.id == profileId }
            if (profile == null) {
                error = "Profile not found"
            }
        } else {
            error = result.exceptionOrNull()?.message ?: "Failed to load profile"
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Profile Details") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
        ) {
            Box(modifier = Modifier.height(400.dp).fillMaxWidth()) {
                AsyncImage(
                    model = profile?.imageUrl ?: "",
                    contentDescription = "Profile Picture",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }
            
            Column(modifier = Modifier.padding(16.dp)) {
                if (error != null) {
                    Text(text = error!!, color = MaterialTheme.colorScheme.error)
                }
                val p = profile
                if (p != null) {
                    Text(text = "${p.name}, ${p.age}", style = MaterialTheme.typography.headlineMedium)
                    Text(text = "${p.profession} in ${p.location}", style = MaterialTheme.typography.bodyLarge)
                
                    Spacer(modifier = Modifier.height(16.dp))
                
                    Text(text = "About Me", style = MaterialTheme.typography.titleMedium)
                    Text(text = p.about.ifBlank { "—" }, style = MaterialTheme.typography.bodyMedium)

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(text = "Personal Details", style = MaterialTheme.typography.titleMedium)
                    fun show(value: String?): String = if (value.isNullOrBlank()) "Not specified" else value
                    Text(text = "Name: ${show(p.name)}")
                    Text(text = "Age: ${if (p.age > 0) p.age.toString() else "Not specified"}")
                    Text(text = "Gender: ${show(p.gender)}")
                    Text(text = "Height: ${show(p.height)}")
                    Text(text = "Religion: ${show(p.religion)}")
                    Text(text = "Caste: ${show(p.caste)}")
                    Text(text = "Profession: ${show(p.profession)}")
                    Text(text = "Location: ${show(p.location)}")
                    Text(text = "Annual Income: ${show(p.annualIncome)}")
                    if (!p.createdAt.isNullOrBlank()) {
                        Text(text = "Member since: ${p.createdAt.take(10)}")
                    }
                    if (!p.phoneNumber.isNullOrBlank()) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            val phoneText = if (showPhone) "Phone: ${p.phoneNumber}" else "Phone: ••••••••••"
                            Text(text = phoneText, modifier = Modifier.weight(1f))
                            Spacer(modifier = Modifier.width(12.dp))
                            FilledTonalButton(
                                onClick = { showPhone = true },
                                enabled = !showPhone
                            ) {
                                Text(if (showPhone) "Shown" else "Show Number")
                            }
                        }
                    } else {
                        Text(text = "Phone: Not specified")
                    }
                
                    Spacer(modifier = Modifier.height(32.dp))
                
                    Button(
                        onClick = {
                            info = null; error = null
                            val token = store.getToken()
                            val receiverId = p.id
                            if (token.isNullOrBlank()) {
                                error = "Please login to send interest"
                            } else {
                                // Prefer the user id cached at login; fall back to decoding the JWT.
                                val uid = store.getUserId()?.takeIf { it.isNotBlank() }
                                    ?: JwtUtils.getUserIdFromToken(token)
                                android.util.Log.d("SendInterest", "storedUid=${store.getUserId()} jwtUid=${JwtUtils.getUserIdFromToken(token)} tokenLen=${token.length} receiver=$receiverId")
                                if (uid.isNullOrBlank()) {
                                    error = "Unable to identify user"
                                } else {
                                    scope.launch {
                                        runCatching {
                                            val retrofit = SupabaseRestApiClient.authedRetrofit(token)
                                            val svc = retrofit.create(InterestsService::class.java)
                                            val body = InterestDto(
                                                id = null,
                                                senderId = uid,
                                                receiverId = receiverId,
                                                status = "pending",
                                                createdAt = null,
                                                updatedAt = null
                                            )
                                            svc.sendInterest(body)
                                        }.onSuccess {
                                            info = "Interest sent"
                                        }.onFailure {
                                            error = it.message ?: "Failed to send interest"
                                        }
                                    }
                                }
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) { Text("Send Interest") }

                    if (info != null) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(text = info!!, color = MaterialTheme.colorScheme.primary)
                    }
                }
            }
        }
    }
}
