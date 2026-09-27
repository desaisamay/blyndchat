package com.example.shaadi.ui.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import com.example.shaadi.data.auth.CredentialStore
import com.example.shaadi.network.ProfilesService
import com.example.shaadi.network.SupabaseRestApiClient
import com.example.shaadi.util.JwtUtils
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileSettingsScreen(navController: NavController) {
    val snackbarHostState = remember { SnackbarHostState() }
    val store = remember { CredentialStore(navController.context.applicationContext) }
    val token = store.getToken()
    val scope = rememberCoroutineScope()

    var name by remember { mutableStateOf("") }
    var age by remember { mutableStateOf("") }
    var height by remember { mutableStateOf("") }
    var religion by remember { mutableStateOf("") }
    var caste by remember { mutableStateOf("") }
    var profession by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    var imageUrl by remember { mutableStateOf("") }
    var about by remember { mutableStateOf("") }
    var gender by remember { mutableStateOf("") }
    var annualIncome by remember { mutableStateOf("") }
    var phoneNumber by remember { mutableStateOf("") }

    // Load current profile
    LaunchedEffect(token) {
        val jwt = token ?: return@LaunchedEffect
        val uid = store.getUserId() ?: JwtUtils.getUserIdFromToken(jwt) ?: return@LaunchedEffect
        val authed = SupabaseRestApiClient.authedRetrofit(jwt)
        val svc = authed.create(ProfilesService::class.java)
        runCatching {
            svc.getProfilesById("eq.$uid", select = "*", limit = 1)
        }.onSuccess { list: List<com.example.shaadi.network.ProfileDto> ->
            val p = list.firstOrNull()
            if (p != null) {
                name = p.name ?: ""
                age = (p.age ?: 0).toString()
                height = p.height ?: ""
                religion = p.religion ?: ""
                caste = p.caste ?: ""
                profession = p.profession ?: ""
                location = p.location ?: ""
                imageUrl = p.imageUrl ?: ""
                about = p.about ?: ""
                gender = p.gender ?: ""
                annualIncome = p.annualIncome ?: ""
                phoneNumber = p.phoneNumber ?: ""
            }
        }.onFailure {
            scope.launch { snackbarHostState.showSnackbar(it.message ?: "Failed to load profile") }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Profile Settings") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(imageVector = Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Column(
            modifier = Modifier.padding(padding).padding(16.dp).verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Name") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = age, onValueChange = { age = it }, label = { Text("Age") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = height, onValueChange = { height = it }, label = { Text("Height") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = religion, onValueChange = { religion = it }, label = { Text("Religion") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = caste, onValueChange = { caste = it }, label = { Text("Caste") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = profession, onValueChange = { profession = it }, label = { Text("Profession") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = location, onValueChange = { location = it }, label = { Text("Location") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = imageUrl, onValueChange = { imageUrl = it }, label = { Text("Image URL") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = about, onValueChange = { about = it }, label = { Text("About") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = gender, onValueChange = { gender = it }, label = { Text("Gender") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = annualIncome, onValueChange = { annualIncome = it }, label = { Text("Annual Income") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = phoneNumber, onValueChange = { phoneNumber = it }, label = { Text("Phone Number") }, modifier = Modifier.fillMaxWidth())
            Spacer(modifier = Modifier.height(8.dp))
            Button(onClick = {
                val jwt = token
                if (jwt.isNullOrBlank()) {
                    scope.launch { snackbarHostState.showSnackbar("Please login") }
                    return@Button
                }
                val uid = store.getUserId() ?: JwtUtils.getUserIdFromToken(jwt)
                if (uid.isNullOrBlank()) {
                    scope.launch { snackbarHostState.showSnackbar("Unable to identify user") }
                    return@Button
                }
                val authed = SupabaseRestApiClient.authedRetrofit(jwt)
                val svc = authed.create(ProfilesService::class.java)
                val upsert = ProfilesService.ProfileUpsertDto(
                    id = uid,
                    name = name,
                    age = age.toIntOrNull(),
                    height = height,
                    religion = religion,
                    caste = caste,
                    profession = profession,
                    location = location,
                    imageUrl = imageUrl,
                    about = about,
                    gender = gender.ifBlank { null },
                    annualIncome = annualIncome.ifBlank { null },
                    phoneNumber = phoneNumber.ifBlank { null }
                )
                scope.launch {
                    runCatching { svc.upsertProfiles(listOf(upsert)) }
                        .onSuccess { snackbarHostState.showSnackbar("Profile updated") }
                        .onFailure { snackbarHostState.showSnackbar(it.message ?: "Update failed") }
                }
            }, modifier = Modifier.fillMaxWidth()) { Text("Update") }
        }
    }
}
