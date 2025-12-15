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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileDetailScreen(navController: NavController, profileId: String?) {
    val repo = remember { ProfilesRepository.getInstance(navController.context) }
    var profile by remember { mutableStateOf<Profile?>(null) }
    var error by remember { mutableStateOf<String?>(null) }
    var showPhone by remember { mutableStateOf(false) }

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
                    Text(text = p.about, style = MaterialTheme.typography.bodyMedium)
                
                    Spacer(modifier = Modifier.height(16.dp))
                
                    Text(text = "Personal Details", style = MaterialTheme.typography.titleMedium)
                    Text(text = "Height: ${p.height}")
                    Text(text = "Religion: ${p.religion}")
                    Text(text = "Caste: ${p.caste}")
                    if (!p.gender.isNullOrBlank()) Text(text = "Gender: ${p.gender}")
                    if (!p.annualIncome.isNullOrBlank()) Text(text = "Annual Income: ${p.annualIncome}")
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
                    }
                
                    Spacer(modifier = Modifier.height(32.dp))
                
                    Button(
                        onClick = { /* TODO: Send Interest */ },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Send Interest")
                    }
                }
            }
        }
    }
}
