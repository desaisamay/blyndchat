package com.blyndchat.ui.settings

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.blyndchat.data.auth.CredentialStore
import com.blyndchat.data.auth.SecurityQuestions
import com.blyndchat.data.model.BiodataExtractor
import com.blyndchat.data.model.ProfileMetadataParser
import com.blyndchat.data.profile.AvatarCatalog
import com.blyndchat.network.ProfileDto
import com.blyndchat.network.ProfilesService
import com.blyndchat.network.SupabaseRestApiClient
import com.blyndchat.ui.theme.LuxuryGold
import com.blyndchat.ui.theme.LuxuryRose
import com.blyndchat.util.JwtUtils
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileSettingsScreen(navController: NavController) {
    val snackbarHostState = remember { SnackbarHostState() }
    val store = remember { CredentialStore(navController.context.applicationContext) }
    val token = store.getToken()
    val scope = rememberCoroutineScope()
    val defaultBio = BiodataExtractor.default

    var name by remember { mutableStateOf(store.getPendingName() ?: defaultBio.name) }
    var age by remember { mutableStateOf(store.getPendingAge()?.toString() ?: defaultBio.age.toString()) }
    var height by remember { mutableStateOf(store.getPendingHeight() ?: defaultBio.height) }
    var profession by remember { mutableStateOf(store.getPendingProfession() ?: defaultBio.profession) }
    var professionExpanded by remember { mutableStateOf(false) }
    var location by remember { mutableStateOf(store.getPendingLocation() ?: defaultBio.location) }
    var imageUrl by remember { mutableStateOf(store.getPendingImageUri() ?: defaultBio.imageUrl) }
    var bio by remember { mutableStateOf(store.getPendingBio() ?: defaultBio.bio) }
    var gender by remember { mutableStateOf(store.getUserGender() ?: defaultBio.gender) }

    var religion by remember { mutableStateOf(store.getPendingReligion() ?: defaultBio.religion) }
    var caste by remember { mutableStateOf(store.getPendingCaste() ?: defaultBio.caste) }
    var annualIncome by remember { mutableStateOf(store.getPendingAnnualIncome() ?: defaultBio.annualIncome) }
    var phoneNumber by remember { mutableStateOf(store.getPendingPhoneNumber() ?: defaultBio.phoneNumber) }

    var lookingFor by remember { mutableStateOf(store.getPendingLookingFor() ?: defaultBio.lookingFor) }
    var preferredAgeRange by remember { mutableStateOf(store.getPendingPreferredAgeRange() ?: defaultBio.preferredAgeRange) }
    var preferredTopics by remember { mutableStateOf(store.getPendingPreferredTopics() ?: defaultBio.preferredTopics) }
    var personalityType by remember { mutableStateOf(store.getPendingPersonalityType() ?: defaultBio.personalityType) }
    var conversationIntensity by remember { mutableStateOf(store.getPendingConversationIntensity() ?: defaultBio.conversationIntensity) }
    var avoidedTopics by remember { mutableStateOf(store.getPendingAvoidedTopics() ?: defaultBio.avoidedTopics) }

    var securityQuestion by remember {
        mutableStateOf(store.getPendingSecurityQuestion() ?: store.getSecurityQuestion() ?: SecurityQuestions.defaultQuestion)
    }
    var securityQuestionExpanded by remember { mutableStateOf(false) }
    var securityAnswer by remember {
        mutableStateOf(store.getPendingSecurityAnswer() ?: store.getSecurityAnswer() ?: "")
    }

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

    fun applyProfileData(p: ProfileDto) {
        if (!p.name.isNullOrBlank()) name = p.name
        if (p.age != null && p.age > 0) age = p.age.toString()
        if (!p.height.isNullOrBlank()) height = p.height
        if (!p.profession.isNullOrBlank()) profession = p.profession
        if (!p.location.isNullOrBlank()) location = p.location
        if (!p.imageUrl.isNullOrBlank()) imageUrl = p.imageUrl
        if (!p.gender.isNullOrBlank()) {
            gender = p.gender
            store.saveUserGender(p.gender)
        }
        if (!p.religion.isNullOrBlank()) religion = p.religion
        if (!p.caste.isNullOrBlank()) caste = p.caste
        if (!p.annualIncome.isNullOrBlank()) annualIncome = p.annualIncome
        if (!p.phoneNumber.isNullOrBlank()) phoneNumber = p.phoneNumber

        val parsed = ProfileMetadataParser.parse(p.about)
        if (parsed.bio.isNotBlank()) bio = parsed.bio.ifBlank { p.about ?: "" }
        if (!parsed.lookingFor.isNullOrBlank()) lookingFor = parsed.lookingFor
        if (!parsed.preferredAgeRange.isNullOrBlank()) preferredAgeRange = parsed.preferredAgeRange
        if (!parsed.preferredTopics.isNullOrBlank()) preferredTopics = parsed.preferredTopics
        if (!parsed.personalityType.isNullOrBlank()) personalityType = parsed.personalityType
        if (!parsed.conversationIntensity.isNullOrBlank()) conversationIntensity = parsed.conversationIntensity
        if (!parsed.avoidedTopics.isNullOrBlank()) avoidedTopics = parsed.avoidedTopics
    }

    fun autofillFromBiodata(overwriteAll: Boolean = false) {
        val b = BiodataExtractor.default
        if (overwriteAll || name.isBlank()) name = b.name
        if (overwriteAll || age.isBlank()) age = b.age.toString()
        if (overwriteAll || gender.isBlank()) gender = b.gender
        if (overwriteAll || height.isBlank()) height = b.height
        if (overwriteAll || profession.isBlank()) profession = b.profession
        if (overwriteAll || location.isBlank()) location = b.location
        if (overwriteAll || annualIncome.isBlank()) annualIncome = b.annualIncome
        if (overwriteAll || religion.isBlank()) religion = b.religion
        if (overwriteAll || caste.isBlank()) caste = b.caste
        if (overwriteAll || phoneNumber.isBlank()) phoneNumber = b.phoneNumber
        if (overwriteAll || bio.isBlank()) bio = b.bio
        if (overwriteAll || lookingFor.isBlank()) lookingFor = b.lookingFor
        if (overwriteAll || preferredAgeRange.isBlank()) preferredAgeRange = b.preferredAgeRange
        if (overwriteAll || preferredTopics.isBlank()) preferredTopics = b.preferredTopics
        if (overwriteAll || personalityType.isBlank()) personalityType = b.personalityType
        if (overwriteAll || conversationIntensity.isBlank()) conversationIntensity = b.conversationIntensity
        if (overwriteAll || avoidedTopics.isBlank()) avoidedTopics = b.avoidedTopics
        if (overwriteAll || imageUrl.isBlank()) imageUrl = b.imageUrl
    }

    // Load profile with fallback to anon PostgREST, pending store, and biodata
    LaunchedEffect(Unit) {
        val jwt = store.getToken()
        val uid = store.getUserId() ?: (if (!jwt.isNullOrBlank()) JwtUtils.getUserIdFromToken(jwt) else null)
        
        if (!jwt.isNullOrBlank() && !uid.isNullOrBlank()) {
            var fetched = false
            val authed = SupabaseRestApiClient.authedRetrofit(jwt)
            val svc = authed.create(ProfilesService::class.java)
            runCatching {
                svc.getProfilesById("eq.$uid", select = "*", limit = 1)
            }.onSuccess { list ->
                val p = list.firstOrNull()
                if (p != null) {
                    applyProfileData(p)
                    fetched = true
                }
            }

            // Fallback to anon query if authed returned empty
            if (!fetched) {
                val anonSvc = SupabaseRestApiClient.retrofit.create(ProfilesService::class.java)
                runCatching {
                    anonSvc.getProfilesById("eq.$uid", select = "*", limit = 1)
                }.onSuccess { list ->
                    val p = list.firstOrNull()
                    if (p != null) {
                        applyProfileData(p)
                        fetched = true
                    }
                }
            }
        }

        // Fallback to CredentialStore pending fields
        store.getPendingName()?.let { if (name.isBlank()) name = it }
        store.getPendingAge()?.let { if (age.isBlank()) age = it.toString() }
        store.getPendingGender()?.let { if (gender.isBlank()) gender = it }
        store.getPendingHeight()?.let { if (height.isBlank()) height = it }
        store.getPendingProfession()?.let { if (profession.isBlank()) profession = it }
        store.getPendingBio()?.let { if (bio.isBlank()) bio = it }
        store.getPendingLookingFor()?.let { if (lookingFor.isBlank()) lookingFor = it }
        store.getPendingPreferredAgeRange()?.let { if (preferredAgeRange.isBlank()) preferredAgeRange = it }
        store.getPendingPreferredTopics()?.let { if (preferredTopics.isBlank()) preferredTopics = it }
        store.getPendingPersonalityType()?.let { if (personalityType.isBlank()) personalityType = it }
        store.getPendingConversationIntensity()?.let { if (conversationIntensity.isBlank()) conversationIntensity = it }
        store.getPendingAvoidedTopics()?.let { if (avoidedTopics.isBlank()) avoidedTopics = it }
        store.getPendingLocation()?.let { if (location.isBlank()) location = it }
        store.getPendingReligion()?.let { if (religion.isBlank()) religion = it }
        store.getPendingCaste()?.let { if (caste.isBlank()) caste = it }
        store.getPendingAnnualIncome()?.let { if (annualIncome.isBlank()) annualIncome = it }
        store.getPendingPhoneNumber()?.let { if (phoneNumber.isBlank()) phoneNumber = it }
        store.getUserGender()?.let { if (gender.isBlank()) gender = it }

        // Extract any remaining empty fields from available marriage biodata
        autofillFromBiodata(overwriteAll = false)
    }

    val colors = com.blyndchat.ui.theme.LocalAppColors.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Edit Profile", fontWeight = FontWeight.Bold, color = colors.topBarTitle) },
                navigationIcon = {
                    IconButton(
                        onClick = { navController.popBackStack() },
                        modifier = Modifier.padding(start = 6.dp)
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = if (colors.isDark) Color.White else Color(0xFF141416),
                            shadowElevation = 3.dp,
                            modifier = Modifier.size(38.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Filled.ArrowBack,
                                    contentDescription = "Back",
                                    tint = if (colors.isDark) Color(0xFF141416) else Color.White,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = colors.topBarContainer,
                    navigationIconContentColor = if (colors.isDark) Color.White else Color(0xFF141416),
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
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Spacer(modifier = Modifier.height(4.dp))

            // Biodata extraction banner & action button
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (colors.isDark) Color(0xFF1F1F24) else Color(0xFFF7F5FA)
                ),
                border = BorderStroke(1.dp, if (colors.isDark) LuxuryGold.copy(alpha = 0.4f) else LuxuryRose.copy(alpha = 0.4f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = null,
                                tint = if (colors.isDark) LuxuryGold else LuxuryRose,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Biodata Extracted",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = colors.textPrimary
                            )
                        }

                        FilledTonalButton(
                            onClick = {
                                autofillFromBiodata(overwriteAll = true)
                                scope.launch { snackbarHostState.showSnackbar("Re-extracted all biodata details ✨") }
                            },
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text("Re-extract ✨", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "All personal, career, and preference information from your marriage biodata and account profile has been populated below. Customize anything you wish.",
                        fontSize = 12.sp,
                        color = colors.textSecondary
                    )
                }
            }

            // Section 1: Personal Details
            SectionHeader(title = "Personal Details")

            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Name") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = age,
                    onValueChange = { age = it },
                    label = { Text("Age") },
                    modifier = Modifier.weight(1f),
                    singleLine = true
                )
                OutlinedTextField(
                    value = height,
                    onValueChange = { height = it },
                    label = { Text("Height") },
                    modifier = Modifier.weight(1f),
                    singleLine = true
                )
            }

            // Gender Selector
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Gender",
                    fontSize = 12.sp,
                    color = colors.textSecondary,
                    modifier = Modifier.padding(start = 4.dp, bottom = 4.dp)
                )
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                    listOf("Male", "Female", "Other").forEach { g ->
                        val selected = gender.equals(g, ignoreCase = true)
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = if (selected) {
                                if (colors.isDark) LuxuryGold.copy(alpha = 0.25f) else LuxuryRose.copy(alpha = 0.2f)
                            } else {
                                if (colors.isDark) Color(0xFF222226) else Color(0xFFF1EEF5)
                            },
                            border = BorderStroke(
                                1.dp,
                                if (selected) (if (colors.isDark) LuxuryGold else LuxuryRose) else Color(0x33888888)
                            ),
                            modifier = Modifier
                                .weight(1f)
                                .clickable { gender = g }
                        ) {
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier.padding(vertical = 10.dp)
                            ) {
                                Text(
                                    text = g,
                                    fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
                                    fontSize = 13.sp,
                                    color = if (selected) (if (colors.isDark) LuxuryGold else LuxuryRose) else colors.textPrimary
                                )
                            }
                        }
                    }
                }
            }

            OutlinedTextField(
                value = phoneNumber,
                onValueChange = { phoneNumber = it },
                label = { Text("Phone Number") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            // Section 2: Professional & Background
            SectionHeader(title = "Professional & Background")

            // Profession dropdown
            ExposedDropdownMenuBox(
                expanded = professionExpanded,
                onExpandedChange = { professionExpanded = !professionExpanded },
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = profession,
                    onValueChange = { profession = it },
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

            OutlinedTextField(
                value = location,
                onValueChange = { location = it },
                label = { Text("Work Location / City") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            OutlinedTextField(
                value = annualIncome,
                onValueChange = { annualIncome = it },
                label = { Text("Annual Income (e.g. ₹37 Lakhs per annum)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = religion,
                    onValueChange = { religion = it },
                    label = { Text("Religion") },
                    modifier = Modifier.weight(1f),
                    singleLine = true
                )
                OutlinedTextField(
                    value = caste,
                    onValueChange = { caste = it },
                    label = { Text("Caste") },
                    modifier = Modifier.weight(1f),
                    singleLine = true
                )
            }

            // Section 3: Bio & Avatar
            SectionHeader(title = "Bio & Avatar")

            OutlinedTextField(
                value = bio,
                onValueChange = { bio = it },
                label = { Text("Bio") },
                minLines = 3,
                maxLines = 5,
                modifier = Modifier.fillMaxWidth()
            )

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = imageUrl,
                    onValueChange = { imageUrl = it },
                    label = { Text("Avatar / Image URL") },
                    modifier = Modifier.weight(1f),
                    singleLine = true
                )
                if (imageUrl.isNotBlank()) {
                    AsyncImage(
                        model = imageUrl,
                        contentDescription = "Avatar preview",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(52.dp)
                            .clip(CircleShape)
                            .background(Color.DarkGray)
                            .border(1.dp, if (colors.isDark) LuxuryGold else LuxuryRose, CircleShape)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = "Choose AI Avatar (${if (gender.equals("Male", ignoreCase = true)) "Male" else "Female"} - 10 styles)",
                style = MaterialTheme.typography.bodySmall,
                color = colors.textSecondary
            )
            Spacer(modifier = Modifier.height(6.dp))
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val availableAvatars = AvatarCatalog.getAvatarsForGender(gender)
                items(availableAvatars) { avatarFile ->
                    val assetUri = AvatarCatalog.getAssetUri(avatarFile)
                    val isSelected = imageUrl == assetUri
                    Box(
                        modifier = Modifier
                            .size(52.dp)
                            .clip(CircleShape)
                            .border(
                                width = if (isSelected) 2.5.dp else 1.dp,
                                color = if (isSelected) (if (colors.isDark) LuxuryGold else LuxuryRose) else Color.Gray.copy(alpha = 0.35f),
                                shape = CircleShape
                            )
                            .clickable { imageUrl = assetUri }
                    ) {
                        AsyncImage(
                            model = assetUri,
                            contentDescription = "AI Avatar option",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    }
                }
            }

            // Section 4: Chat & Matching Preferences
            SectionHeader(title = "Chat & Matching Preferences")

            OutlinedTextField(
                value = lookingFor,
                onValueChange = { lookingFor = it },
                label = { Text("Looking For (e.g. Meaningful conversations)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            OutlinedTextField(
                value = preferredAgeRange,
                onValueChange = { preferredAgeRange = it },
                label = { Text("Preferred Age Range (e.g. 24 - 30)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            OutlinedTextField(
                value = preferredTopics,
                onValueChange = { preferredTopics = it },
                label = { Text("Preferred Topics (comma-separated)") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = personalityType,
                onValueChange = { personalityType = it },
                label = { Text("Personality (Introvert / Extrovert / Ambivert)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            OutlinedTextField(
                value = conversationIntensity,
                onValueChange = { conversationIntensity = it },
                label = { Text("Conversation Intensity (Lighthearted / Deep)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            OutlinedTextField(
                value = avoidedTopics,
                onValueChange = { avoidedTopics = it },
                label = { Text("Topics to Avoid (comma-separated)") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(10.dp))
            SectionHeader(title = "Account Security & Recovery")

            ExposedDropdownMenuBox(
                expanded = securityQuestionExpanded,
                onExpandedChange = { securityQuestionExpanded = !securityQuestionExpanded },
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = securityQuestion,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Secret Question") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = securityQuestionExpanded) },
                    modifier = Modifier.fillMaxWidth().menuAnchor(),
                    shape = RoundedCornerShape(12.dp)
                )
                ExposedDropdownMenu(
                    expanded = securityQuestionExpanded,
                    onDismissRequest = { securityQuestionExpanded = false }
                ) {
                    SecurityQuestions.list.forEach { q ->
                        DropdownMenuItem(
                            text = { Text(q) },
                            onClick = {
                                securityQuestion = q
                                securityQuestionExpanded = false
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = securityAnswer,
                onValueChange = { securityAnswer = it },
                label = { Text("Secret Answer") },
                placeholder = { Text("e.g. Bangalore, St. Xavier's") },
                supportingText = { Text("Used for password recovery without OTP (case-insensitive)") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Save / Update Button
            Button(
                onClick = {
                    val jwt = token
                    val uid = store.getUserId() ?: (if (!jwt.isNullOrBlank()) JwtUtils.getUserIdFromToken(jwt) else null)
                    if (uid.isNullOrBlank()) {
                        scope.launch { snackbarHostState.showSnackbar("Unable to identify user") }
                        return@Button
                    }

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
                        name = name.ifBlank { null },
                        age = age.toIntOrNull(),
                        height = height.ifBlank { null },
                        religion = religion.ifBlank { null },
                        caste = caste.ifBlank { null },
                        profession = profession.ifBlank { null },
                        location = location.ifBlank { null },
                        imageUrl = imageUrl.ifBlank { null },
                        about = encodedAbout,
                        gender = gender.ifBlank { null },
                        annualIncome = annualIncome.ifBlank { null },
                        phoneNumber = phoneNumber.ifBlank { null }
                    )

                    scope.launch {
                        // 1. Try authed upsert, or fallback to anon upsert
                        var success = false
                        if (!jwt.isNullOrBlank()) {
                            val authed = SupabaseRestApiClient.authedRetrofit(jwt)
                            val svc = authed.create(ProfilesService::class.java)
                            val res = runCatching { svc.upsertProfiles(listOf(upsert)) }
                            if (res.isSuccess && (res.getOrNull()?.isSuccessful == true)) {
                                success = true
                            }
                        }

                        if (!success) {
                            val anonSvc = SupabaseRestApiClient.retrofit.create(ProfilesService::class.java)
                            val resAnon = runCatching { anonSvc.upsertProfiles(listOf(upsert)) }
                            if (resAnon.isSuccess && (resAnon.getOrNull()?.isSuccessful == true)) {
                                success = true
                            }
                        }

                        // Always persist locally in CredentialStore
                        store.savePendingProfile(
                            name = name.ifBlank { null },
                            age = age.toIntOrNull(),
                            gender = gender.ifBlank { null },
                            avatarUri = imageUrl.ifBlank { null },
                            bio = bio.ifBlank { null },
                            lookingFor = lookingFor.ifBlank { null },
                            preferredAgeRange = preferredAgeRange.ifBlank { null },
                            preferredTopics = preferredTopics.ifBlank { null },
                            personalityType = personalityType.ifBlank { null },
                            height = height.ifBlank { null },
                            profession = profession.ifBlank { null },
                            conversationIntensity = conversationIntensity.ifBlank { null },
                            avoidedTopics = avoidedTopics.ifBlank { null },
                            location = location.ifBlank { null },
                            religion = religion.ifBlank { null },
                            caste = caste.ifBlank { null },
                            annualIncome = annualIncome.ifBlank { null },
                            phoneNumber = phoneNumber.ifBlank { null },
                            securityQuestion = securityQuestion,
                            securityAnswer = securityAnswer.trim().ifBlank { null }
                        )
                        if (gender.isNotBlank()) store.saveUserGender(gender)
                        val currEmail = store.getEmail()
                        if (!currEmail.isNullOrBlank() && securityAnswer.isNotBlank()) {
                            store.saveSecurityQuestion(currEmail, securityQuestion, securityAnswer.trim())
                        }

                        if (success) {
                            snackbarHostState.showSnackbar("Profile updated successfully ✨")
                        } else {
                            // Saved locally even if network has PostgREST permission limits
                            snackbarHostState.showSnackbar("Profile saved locally ✨")
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (colors.isDark) LuxuryGold else LuxuryRose,
                    contentColor = if (colors.isDark) Color(0xFF141416) else Color.White
                )
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.Check, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Update Profile", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(28.dp))
        }
    }
}

@Composable
private fun SectionHeader(title: String) {
    val colors = com.blyndchat.ui.theme.LocalAppColors.current
    Column(modifier = Modifier.fillMaxWidth().padding(top = 10.dp, bottom = 2.dp)) {
        Text(
            text = title,
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp,
            color = if (colors.isDark) LuxuryGold else Color(0xFF9A2D55)
        )
    }
}
