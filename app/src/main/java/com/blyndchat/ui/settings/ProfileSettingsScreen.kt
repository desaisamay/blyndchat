package com.blyndchat.ui.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.blyndchat.data.auth.CredentialStore
import com.blyndchat.data.model.ProfileMetadataParser
import com.blyndchat.network.ProfilesService
import com.blyndchat.network.SupabaseRestApiClient
import com.blyndchat.util.JwtUtils
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
    var profession by remember { mutableStateOf("") }
    var professionExpanded by remember { mutableStateOf(false) }
    var location by remember { mutableStateOf("") }
    var imageUrl by remember { mutableStateOf("") }
    var bio by remember { mutableStateOf("") }
    var gender by remember { mutableStateOf("") }

    var lookingFor by remember { mutableStateOf("") }
    var preferredAgeRange by remember { mutableStateOf("") }
    var preferredTopics by remember { mutableStateOf("") }
    var personalityType by remember { mutableStateOf("") }
    var conversationIntensity by remember { mutableStateOf("") }
    var avoidedTopics by remember { mutableStateOf("") }

    var existingPhoneNumber by remember { mutableStateOf<String?>(null) }

    val professionOptions = listOf(
        "Software Engineer",
        "Product / UX Designer",
        "Doctor / Healthcare",
        "Finance / Banking",
        "Student",
        "Entrepreneur / Business",
        "Teacher / Professor",
        "Marketing / Growth",
        "Architect",
        "Writer / Content Creator",
        "Lawyer / Legal",
        "Consultant",
        "Sales / BD",
        "Other"
    )

    // Load current profile
    LaunchedEffect(token) {
        val jwt = token ?: return@LaunchedEffect
        val uid = store.getUserId() ?: JwtUtils.getUserIdFromToken(jwt) ?: return@LaunchedEffect
        val authed = SupabaseRestApiClient.authedRetrofit(jwt)
        val svc = authed.create(ProfilesService::class.java)
        runCatching {
            svc.getProfilesById("eq.$uid", select = "*", limit = 1)
        }.onSuccess { list: List<com.blyndchat.network.ProfileDto> ->
            val p = list.firstOrNull()
            if (p != null) {
                name = p.name ?: ""
                age = (p.age ?: 0).toString()
                height = p.height ?: ""
                profession = p.profession ?: ""
                location = p.location ?: ""
                imageUrl = p.imageUrl ?: ""
                gender = p.gender ?: ""
                if (!p.gender.isNullOrBlank()) {
                    store.saveUserGender(p.gender)
                }
                existingPhoneNumber = p.phoneNumber

                val parsed = ProfileMetadataParser.parse(p.about)
                bio = parsed.bio.ifBlank { p.about ?: "" }
                lookingFor = parsed.lookingFor ?: ""
                preferredAgeRange = parsed.preferredAgeRange ?: ""
                preferredTopics = parsed.preferredTopics ?: ""
                personalityType = parsed.personalityType ?: ""
                conversationIntensity = parsed.conversationIntensity ?: ""
                avoidedTopics = parsed.avoidedTopics ?: ""
            }
        }.onFailure {
            scope.launch { snackbarHostState.showSnackbar(it.message ?: "Failed to load profile") }
        }
    }

    val colors = com.blyndchat.ui.theme.LocalAppColors.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Profile Settings", fontWeight = androidx.compose.ui.text.font.FontWeight.Bold, color = colors.topBarTitle) },
                navigationIcon = {
                    IconButton(
                        onClick = { navController.popBackStack() },
                        modifier = Modifier.padding(start = 6.dp)
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = if (colors.isDark) Color.White else Color(0xFF150D24),
                            shadowElevation = 3.dp,
                            modifier = Modifier.size(38.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Filled.ArrowBack,
                                    contentDescription = "Back",
                                    tint = if (colors.isDark) Color(0xFF150D24) else Color.White,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = colors.topBarContainer,
                    navigationIconContentColor = if (colors.isDark) Color.White else Color(0xFF150D24),
                    titleContentColor = colors.topBarTitle
                )
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(colors.screenBackgroundBrush)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Name") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = age, onValueChange = { age = it }, label = { Text("Age") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = height, onValueChange = { height = it }, label = { Text("Height (e.g. 5'8\")") }, modifier = Modifier.fillMaxWidth())

            // Profession dropdown
            ExposedDropdownMenuBox(
                expanded = professionExpanded,
                onExpandedChange = { professionExpanded = !professionExpanded },
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = profession,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Profession") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = professionExpanded) },
                    modifier = Modifier.fillMaxWidth().menuAnchor()
                )
                ExposedDropdownMenu(
                    expanded = professionExpanded,
                    onDismissRequest = { professionExpanded = false }
                ) {
                    professionOptions.forEach { opt ->
                        DropdownMenuItem(
                            text = { Text(opt) },
                            onClick = {
                                profession = opt
                                professionExpanded = false
                            }
                        )
                    }
                }
            }

            OutlinedTextField(value = location, onValueChange = { location = it }, label = { Text("Location") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = imageUrl, onValueChange = { imageUrl = it }, label = { Text("Image URL") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = bio, onValueChange = { bio = it }, label = { Text("Bio") }, minLines = 3, maxLines = 5, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = gender, onValueChange = { gender = it }, label = { Text("Gender") }, modifier = Modifier.fillMaxWidth())

            // Chat preferences
            OutlinedTextField(value = lookingFor, onValueChange = { lookingFor = it }, label = { Text("Looking For (e.g. Meaningful conversations)") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = preferredAgeRange, onValueChange = { preferredAgeRange = it }, label = { Text("Preferred Age Range (e.g. 22 - 28)") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = preferredTopics, onValueChange = { preferredTopics = it }, label = { Text("Preferred Topics (comma-separated)") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = personalityType, onValueChange = { personalityType = it }, label = { Text("Personality (Introvert / Extrovert / Ambivert)") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = conversationIntensity, onValueChange = { conversationIntensity = it }, label = { Text("Conversation Intensity (Lighthearted / Deep)") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = avoidedTopics, onValueChange = { avoidedTopics = it }, label = { Text("Topics to Avoid (comma-separated)") }, modifier = Modifier.fillMaxWidth())

            Spacer(modifier = Modifier.height(10.dp))

            Button(
                onClick = {
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

                    val encodedAbout = ProfileMetadataParser.encode(
                        bio = bio,
                        lookingFor = lookingFor.ifBlank { null },
                        preferredAgeRange = preferredAgeRange.ifBlank { null },
                        preferredTopics = preferredTopics.ifBlank { null },
                        personalityType = personalityType.ifBlank { null },
                        conversationIntensity = conversationIntensity.ifBlank { null },
                        avoidedTopics = avoidedTopics.ifBlank { null }
                    )

                    val upsert = ProfilesService.ProfileUpsertDto(
                        id = uid,
                        name = name,
                        age = age.toIntOrNull(),
                        height = height,
                        profession = profession,
                        location = location,
                        imageUrl = imageUrl,
                        about = encodedAbout,
                        gender = gender.ifBlank { null },
                        phoneNumber = existingPhoneNumber
                    )
                    scope.launch {
                        runCatching { svc.upsertProfiles(listOf(upsert)) }
                            .onSuccess {
                                if (gender.isNotBlank()) store.saveUserGender(gender)
                                snackbarHostState.showSnackbar("Profile updated successfully")
                            }
                            .onFailure { snackbarHostState.showSnackbar(it.message ?: "Update failed") }
                    }
                },
                modifier = Modifier.fillMaxWidth().height(50.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Update Profile", fontSize = 16.sp)
            }

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}
