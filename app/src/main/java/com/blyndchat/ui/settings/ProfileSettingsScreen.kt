package com.blyndchat.ui.settings

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.blyndchat.data.auth.CredentialStore
import com.blyndchat.data.auth.SecurityQuestions
import com.blyndchat.data.model.BiodataExtractor
import com.blyndchat.data.model.ProfileMetadataParser
import com.blyndchat.data.profile.AvatarCatalog
import com.blyndchat.network.ProfileDto
import com.blyndchat.network.ProfilesService
import com.blyndchat.network.SupabaseRestApiClient
import com.blyndchat.ui.theme.LocalAppColors
import com.blyndchat.ui.theme.LuxuryGold
import com.blyndchat.ui.theme.LuxuryRose
import com.blyndchat.util.JwtUtils
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun ProfileSettingsScreen(navController: NavController) {
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }
    val store = remember { CredentialStore(context.applicationContext) }
    val token = store.getToken()
    val scope = rememberCoroutineScope()
    val colors = LocalAppColors.current

    val defaultBio = BiodataExtractor.default

    // Basic Account Details - pre-filled from CredentialStore / Biodata
    var name by remember { mutableStateOf(store.getPendingName() ?: defaultBio.name) }
    var age by remember { mutableStateOf(store.getPendingAge()?.toString() ?: defaultBio.age.toString()) }
    var height by remember { mutableStateOf(store.getPendingHeight() ?: defaultBio.height) }
    var gender by remember { mutableStateOf(store.getUserGender() ?: defaultBio.gender) }
    var selectedAvatar by remember {
        val initialUrl = store.getPendingImageUri() ?: defaultBio.imageUrl
        val cleanName = initialUrl.substringAfterLast('/')
        val matched = if (AvatarCatalog.maleAvatars.contains(cleanName) || AvatarCatalog.femaleAvatars.contains(cleanName)) {
            cleanName
        } else {
            AvatarCatalog.defaultAvatarForGender(gender)
        }
        mutableStateOf(matched)
    }
    var showAvatarDialog by remember { mutableStateOf(false) }
    var dialogGenderTab by remember { mutableStateOf(if (gender.equals("Female", ignoreCase = true)) "Female" else "Male") }

    // Chat Persona & Preferences Details
    var bio by remember { mutableStateOf(store.getPendingBio() ?: defaultBio.bio) }
    var lookingFor by remember { mutableStateOf(store.getPendingLookingFor() ?: defaultBio.lookingFor) }
    var preferredAgeRange by remember { mutableStateOf(store.getPendingPreferredAgeRange() ?: defaultBio.preferredAgeRange) }
    val selectedTopics = remember { mutableStateListOf<String>() }
    var customTopicText by remember { mutableStateOf("") }
    var personalityType by remember { mutableStateOf(store.getPendingPersonalityType() ?: defaultBio.personalityType) }
    var profession by remember { mutableStateOf(store.getPendingProfession() ?: defaultBio.profession) }
    var professionExpanded by remember { mutableStateOf(false) }
    var conversationIntensity by remember { mutableStateOf(store.getPendingConversationIntensity() ?: defaultBio.conversationIntensity) }
    val selectedAvoidedTopics = remember { mutableStateListOf<String>() }
    var customAvoidedText by remember { mutableStateOf("") }

    // Security Question & Answer
    var securityQuestion by remember {
        mutableStateOf(store.getPendingSecurityQuestion() ?: store.getSecurityQuestion() ?: SecurityQuestions.defaultQuestion)
    }
    var securityQuestionExpanded by remember { mutableStateOf(false) }
    var securityAnswer by remember {
        mutableStateOf(store.getPendingSecurityAnswer() ?: store.getSecurityAnswer() ?: "")
    }

    var location by remember { mutableStateOf(store.getPendingLocation() ?: defaultBio.location) }
    var religion by remember { mutableStateOf(store.getPendingReligion() ?: defaultBio.religion) }
    var phoneNumber by remember { mutableStateOf(store.getPendingPhoneNumber() ?: defaultBio.phoneNumber) }
    var isLoading by remember { mutableStateOf(false) }

    // Quick Choice Constants - identical to SignupScreen
    val heightChoices = listOf("5'2\"", "5'4\"", "5'6\"", "5'8\"", "5'10\"", "6'0\"", "6'2\"")
    val lookingForOptions = listOf(
        "Friendship",
        "Casual chat",
        "Meaningful conversations",
        "Networking",
        "Dating"
    )
    val ageRangeOptions = listOf("18 - 24", "22 - 28", "25 - 32", "28 - 36", "30+")
    val topicChoices = listOf(
        "Music", "Travel", "Tech & AI", "Food & Cooking", "Books & Reading",
        "Fitness", "Movies & Shows", "Philosophy", "Art & Design", "Gaming", "Career & Startups"
    )
    val personalityOptions = listOf("Introvert", "Extrovert", "Ambivert")
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
    val intensityOptions = listOf("Lighthearted", "Deep")
    val avoidedChoices = listOf(
        "Politics",
        "Religion",
        "Exes & Past Relationships",
        "Financial Status",
        "Work Gossip",
        "Dark Humor"
    )

    fun onGenderSelected(newGender: String) {
        gender = newGender
        dialogGenderTab = newGender
        val isMale = newGender.equals("Male", ignoreCase = true)
        val isValidCurrent = if (isMale) {
            AvatarCatalog.isMaleAvatar(selectedAvatar)
        } else {
            AvatarCatalog.isFemaleAvatar(selectedAvatar)
        }
        if (!isValidCurrent) {
            selectedAvatar = AvatarCatalog.defaultAvatarForGender(newGender)
        }
    }

    val avatarList = remember(gender) {
        AvatarCatalog.getAvatarsForGender(gender)
    }

    fun applyProfileData(p: ProfileDto) {
        if (!p.name.isNullOrBlank()) name = p.name
        if (p.age != null && p.age > 0) age = p.age.toString()
        if (!p.height.isNullOrBlank()) height = p.height
        if (!p.gender.isNullOrBlank()) {
            gender = p.gender
            dialogGenderTab = p.gender
            store.saveUserGender(p.gender)
        }
        if (!p.imageUrl.isNullOrBlank()) {
            val clean = p.imageUrl.substringAfterLast('/')
            if (AvatarCatalog.maleAvatars.contains(clean) || AvatarCatalog.femaleAvatars.contains(clean)) {
                selectedAvatar = clean
            }
        }
        if (!p.profession.isNullOrBlank()) profession = p.profession
        if (!p.location.isNullOrBlank()) location = p.location
        if (!p.religion.isNullOrBlank()) religion = p.religion
        if (!p.phoneNumber.isNullOrBlank()) phoneNumber = p.phoneNumber

        val parsed = ProfileMetadataParser.parse(p.about)
        if (parsed.bio.isNotBlank()) bio = parsed.bio.ifBlank { p.about ?: "" }
        if (!parsed.lookingFor.isNullOrBlank()) lookingFor = parsed.lookingFor
        if (!parsed.preferredAgeRange.isNullOrBlank()) preferredAgeRange = parsed.preferredAgeRange
        if (!parsed.personalityType.isNullOrBlank()) personalityType = parsed.personalityType
        if (!parsed.conversationIntensity.isNullOrBlank()) conversationIntensity = parsed.conversationIntensity

        // Populate preferred topics
        val topicsStr = parsed.preferredTopics?.takeIf { it.isNotBlank() } ?: store.getPendingPreferredTopics()
        if (!topicsStr.isNullOrBlank()) {
            val allList = topicsStr.split(",").map { it.trim() }.filter { it.isNotBlank() }
            selectedTopics.clear()
            val customs = mutableListOf<String>()
            allList.forEach { t ->
                val matched = topicChoices.firstOrNull { it.equals(t, ignoreCase = true) }
                if (matched != null) {
                    if (!selectedTopics.contains(matched)) selectedTopics.add(matched)
                } else {
                    customs.add(t)
                }
            }
            customTopicText = customs.joinToString(", ")
        }

        // Populate avoided topics
        val avoidedStr = parsed.avoidedTopics?.takeIf { it.isNotBlank() } ?: store.getPendingAvoidedTopics()
        if (!avoidedStr.isNullOrBlank()) {
            val allList = avoidedStr.split(",").map { it.trim() }.filter { it.isNotBlank() }
            selectedAvoidedTopics.clear()
            val customs = mutableListOf<String>()
            allList.forEach { a ->
                val matched = avoidedChoices.firstOrNull { it.equals(a, ignoreCase = true) }
                if (matched != null) {
                    if (!selectedAvoidedTopics.contains(matched)) selectedAvoidedTopics.add(matched)
                } else {
                    customs.add(a)
                }
            }
            customAvoidedText = customs.joinToString(", ")
        }
    }

    fun autofillFromBiodata(overwriteAll: Boolean = false) {
        val b = BiodataExtractor.default
        if (overwriteAll || name.isBlank()) name = b.name
        if (overwriteAll || age.isBlank()) age = b.age.toString()
        if (overwriteAll || gender.isBlank()) onGenderSelected(b.gender)
        if (overwriteAll || height.isBlank()) height = b.height
        if (overwriteAll || profession.isBlank()) profession = b.profession
        if (overwriteAll || location.isBlank()) location = b.location
        if (overwriteAll || religion.isBlank()) religion = b.religion
        if (overwriteAll || phoneNumber.isBlank()) phoneNumber = b.phoneNumber
        if (overwriteAll || bio.isBlank()) bio = b.bio
        if (overwriteAll || lookingFor.isBlank()) lookingFor = b.lookingFor
        if (overwriteAll || preferredAgeRange.isBlank()) preferredAgeRange = b.preferredAgeRange
        if (overwriteAll || personalityType.isBlank()) personalityType = b.personalityType
        if (overwriteAll || conversationIntensity.isBlank()) conversationIntensity = b.conversationIntensity

        val topicsStr = b.preferredTopics
        if (overwriteAll || selectedTopics.isEmpty()) {
            selectedTopics.clear()
            val customs = mutableListOf<String>()
            topicsStr.split(",").map { it.trim() }.filter { it.isNotBlank() }.forEach { t ->
                val matched = topicChoices.firstOrNull { it.equals(t, ignoreCase = true) }
                if (matched != null) {
                    if (!selectedTopics.contains(matched)) selectedTopics.add(matched)
                } else {
                    customs.add(t)
                }
            }
            customTopicText = customs.joinToString(", ")
        }

        val avoidedStr = b.avoidedTopics
        if (overwriteAll || selectedAvoidedTopics.isEmpty()) {
            selectedAvoidedTopics.clear()
            val customs = mutableListOf<String>()
            avoidedStr.split(",").map { it.trim() }.filter { it.isNotBlank() }.forEach { a ->
                val matched = avoidedChoices.firstOrNull { it.equals(a, ignoreCase = true) }
                if (matched != null) {
                    if (!selectedAvoidedTopics.contains(matched)) selectedAvoidedTopics.add(matched)
                } else {
                    customs.add(a)
                }
            }
            customAvoidedText = customs.joinToString(", ")
        }
    }

    // Load initial profile data from Supabase, CredentialStore, or biodata
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

        // Fallback to local store values
        store.getPendingName()?.let { if (name.isBlank()) name = it }
        store.getPendingAge()?.let { if (age.isBlank()) age = it.toString() }
        store.getUserGender()?.let { if (gender.isBlank()) onGenderSelected(it) }
        store.getPendingHeight()?.let { if (height.isBlank()) height = it }
        store.getPendingProfession()?.let { if (profession.isBlank()) profession = it }
        store.getPendingBio()?.let { if (bio.isBlank()) bio = it }
        store.getPendingLookingFor()?.let { if (lookingFor.isBlank()) lookingFor = it }
        store.getPendingPreferredAgeRange()?.let { if (preferredAgeRange.isBlank()) preferredAgeRange = it }
        store.getPendingPersonalityType()?.let { if (personalityType.isBlank()) personalityType = it }
        store.getPendingConversationIntensity()?.let { if (conversationIntensity.isBlank()) conversationIntensity = it }
        store.getPendingLocation()?.let { if (location.isBlank()) location = it }
        store.getPendingReligion()?.let { if (religion.isBlank()) religion = it }
        store.getPendingPhoneNumber()?.let { if (phoneNumber.isBlank()) phoneNumber = it }

        if (selectedTopics.isEmpty()) {
            autofillFromBiodata(overwriteAll = false)
        }
    }

    // Modal Dialog: Choose AI Avatar (with Male (10) first, Female (10) second)
    if (showAvatarDialog) {
        Dialog(onDismissRequest = { showAvatarDialog = false }) {
            Surface(
                shape = RoundedCornerShape(24.dp),
                color = colors.cardContainer,
                border = BorderStroke(1.5.dp, colors.cardBorder),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Choose AI Avatar",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = colors.topBarTitle
                    )
                    Text(
                        text = "10 curated AI portraits for ${if (dialogGenderTab.equals("Male", ignoreCase = true)) "Male" else "Female"}",
                        style = MaterialTheme.typography.bodySmall,
                        color = colors.textSecondary
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf("Male" to "👨 Male (10)", "Female" to "👩 Female (10)").forEach { (gVal, gLabel) ->
                            val isTabSelected = dialogGenderTab.equals(gVal, ignoreCase = true)
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(38.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(
                                        if (isTabSelected) {
                                            if (colors.isDark) Color(0xFFD4AF37).copy(alpha = 0.25f)
                                            else MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                                        } else {
                                            if (colors.isDark) Color.White.copy(alpha = 0.05f)
                                            else Color.Black.copy(alpha = 0.04f)
                                        }
                                    )
                                    .border(
                                        width = if (isTabSelected) 2.dp else 1.dp,
                                        color = if (isTabSelected) {
                                            if (colors.isDark) Color(0xFFD4AF37) else MaterialTheme.colorScheme.primary
                                        } else {
                                            Color.Gray.copy(alpha = 0.25f)
                                        },
                                        shape = RoundedCornerShape(10.dp)
                                    )
                                    .clickable { dialogGenderTab = gVal },
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = gLabel,
                                    fontSize = 13.sp,
                                    fontWeight = if (isTabSelected) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isTabSelected) {
                                        if (colors.isDark) Color(0xFFD4AF37) else MaterialTheme.colorScheme.primary
                                    } else colors.textPrimary
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    val dialogAvatars = remember(dialogGenderTab) {
                        AvatarCatalog.getAvatarsForGender(dialogGenderTab)
                    }

                    LazyVerticalGrid(
                        columns = GridCells.Fixed(3),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 380.dp)
                    ) {
                        items(dialogAvatars) { avatarFile ->
                            val isSelected = selectedAvatar == avatarFile
                            val prefix = if (AvatarCatalog.isMaleAvatar(avatarFile)) "Male" else "Female"
                            val num = avatarFile.filter { it.isDigit() }
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.clickable {
                                    selectedAvatar = avatarFile
                                    gender = dialogGenderTab
                                    showAvatarDialog = false
                                }
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(76.dp)
                                        .clip(CircleShape)
                                        .border(
                                            width = if (isSelected) 3.dp else 1.5.dp,
                                            color = if (isSelected) {
                                                if (colors.isDark) Color(0xFFD4AF37) else MaterialTheme.colorScheme.primary
                                            } else {
                                                Color.Gray.copy(alpha = 0.35f)
                                            },
                                            shape = CircleShape
                                        )
                                ) {
                                    AsyncImage(
                                        model = ImageRequest.Builder(context)
                                            .data(AvatarCatalog.getAssetUri(avatarFile))
                                            .build(),
                                        contentDescription = "Avatar $avatarFile",
                                        modifier = Modifier.fillMaxSize(),
                                        contentScale = ContentScale.Crop
                                    )
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "#$num",
                                    fontSize = 11.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isSelected) {
                                        if (colors.isDark) Color(0xFFD4AF37) else MaterialTheme.colorScheme.primary
                                    } else colors.textSecondary
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    TextButton(
                        onClick = { showAvatarDialog = false },
                        modifier = Modifier.align(Alignment.End)
                    ) {
                        Text(
                            text = "Done",
                            fontWeight = FontWeight.Bold,
                            color = if (colors.isDark) Color(0xFFD4AF37) else MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        }
    }

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
                .padding(horizontal = 24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(10.dp))

            // Biodata Re-extraction Banner
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (colors.isDark) Color(0xFF1F1F24) else Color(0xFFF7F5FA)
                ),
                border = BorderStroke(1.dp, if (colors.isDark) LuxuryGold.copy(alpha = 0.4f) else LuxuryRose.copy(alpha = 0.4f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = null,
                            tint = if (colors.isDark) LuxuryGold else LuxuryRose,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = "Profile Details Pre-filled ✨",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = colors.textPrimary
                            )
                            Text(
                                text = "All fields are populated with your current settings.",
                                fontSize = 11.sp,
                                color = colors.textSecondary
                            )
                        }
                    }
                    FilledTonalButton(
                        onClick = {
                            autofillFromBiodata(overwriteAll = true)
                            scope.launch { snackbarHostState.showSnackbar("Re-extracted biodata values ✨") }
                        },
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Reset", fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Gender Selector: Male vs Female (10 AI photos each) - Male first!
            Text(
                text = "Gender",
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                color = colors.textPrimary,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 2.dp, bottom = 6.dp)
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                listOf("Male" to "👨 Male", "Female" to "👩 Female").forEach { (gVal, gLabel) ->
                    val isSelected = gender.equals(gVal, ignoreCase = true)
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(44.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(
                                if (isSelected) {
                                    if (colors.isDark) Color(0xFFD4AF37).copy(alpha = 0.22f)
                                    else MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                                } else {
                                    if (colors.isDark) Color.White.copy(alpha = 0.05f)
                                    else Color.Black.copy(alpha = 0.04f)
                                }
                            )
                            .border(
                                width = if (isSelected) 2.dp else 1.dp,
                                color = if (isSelected) {
                                    if (colors.isDark) Color(0xFFD4AF37) else MaterialTheme.colorScheme.primary
                                } else {
                                    Color.Gray.copy(alpha = 0.25f)
                                },
                                shape = RoundedCornerShape(12.dp)
                            )
                            .clickable { onGenderSelected(gVal) },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = gLabel,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                            ),
                            color = if (isSelected) {
                                if (colors.isDark) Color(0xFFD4AF37) else MaterialTheme.colorScheme.primary
                            } else colors.textPrimary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Selected Avatar Preview
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(105.dp)
                    .clip(CircleShape)
                    .border(3.dp, if (colors.isDark) Color(0xFFD4AF37) else MaterialTheme.colorScheme.primary, CircleShape)
                    .clickable {
                        dialogGenderTab = gender
                        showAvatarDialog = true
                    }
            ) {
                AsyncImage(
                    model = ImageRequest.Builder(context)
                        .data(AvatarCatalog.getAssetUri(selectedAvatar))
                        .build(),
                    contentDescription = "Selected Avatar",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "${if (gender.equals("Male", ignoreCase = true)) "Male" else "Female"} AI Avatar (${avatarList.size} styles)",
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                    color = colors.textPrimary
                )
                TextButton(onClick = {
                    dialogGenderTab = gender
                    showAvatarDialog = true
                }) {
                    Text("See all (10)")
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Horizontal Avatar carousel with 10 AI photos for current gender
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                contentPadding = PaddingValues(horizontal = 4.dp)
            ) {
                items(avatarList) { avatarFile ->
                    val isSelected = avatarFile == selectedAvatar
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .clip(CircleShape)
                            .border(
                                width = if (isSelected) 3.dp else 1.dp,
                                color = if (isSelected) (if (colors.isDark) Color(0xFFD4AF37) else MaterialTheme.colorScheme.primary) else Color.Gray.copy(alpha = 0.35f),
                                shape = CircleShape
                            )
                            .clickable { selectedAvatar = avatarFile }
                    ) {
                        AsyncImage(
                            model = ImageRequest.Builder(context)
                                .data(AvatarCatalog.getAssetUri(avatarFile))
                                .build(),
                            contentDescription = "Avatar thumbnail",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // SECTION 1: Account Details
            FormSectionHeader(title = "Account Details", subtitle = "Your basic profile information")

            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Full Name") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(
                    value = age,
                    onValueChange = { age = it },
                    label = { Text("Age") },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp)
                )

                OutlinedTextField(
                    value = height,
                    onValueChange = { height = it },
                    label = { Text("Height") },
                    placeholder = { Text("e.g. 5'8\"") },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp)
                )
            }

            Spacer(modifier = Modifier.height(6.dp))
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                heightChoices.forEach { h ->
                    SelectableChip(
                        text = h,
                        selected = height.trim() == h,
                        onClick = { height = h }
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = "Gender",
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                color = colors.textPrimary,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 2.dp, bottom = 6.dp)
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf("Male", "Female", "Non-binary").forEach { g ->
                    SelectableChip(
                        text = g,
                        selected = gender.equals(g, ignoreCase = true),
                        onClick = { onGenderSelected(g) },
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // SECTION 2: Bio
            FormSectionHeader(title = "Bio", subtitle = "Tell chat partners what makes you unique")

            OutlinedTextField(
                value = bio,
                onValueChange = { bio = it },
                label = { Text("About You") },
                placeholder = { Text("Share your passions, vibe, favorite things, or what brings you here...") },
                minLines = 3,
                maxLines = 5,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )

            // SECTION 3: Looking For
            FormSectionHeader(title = "Looking For", subtitle = "What kind of connection are you seeking?")

            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                lookingForOptions.forEach { option ->
                    SelectableChip(
                        text = option,
                        selected = lookingFor == option,
                        onClick = { lookingFor = option },
                        leadingIcon = "🎯"
                    )
                }
            }

            // SECTION 4: Preferred Age Range
            FormSectionHeader(title = "Preferred Age Range", subtitle = "Age range of your ideal chat partners")

            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                ageRangeOptions.forEach { option ->
                    SelectableChip(
                        text = option,
                        selected = preferredAgeRange == option,
                        onClick = { preferredAgeRange = option },
                        leadingIcon = "👥"
                    )
                }
            }

            // SECTION 5: Preferred Topics
            FormSectionHeader(title = "Preferred Topics", subtitle = "Topics you love talking about (select multiple)")

            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                topicChoices.forEach { topic ->
                    val isSelected = topic in selectedTopics
                    SelectableChip(
                        text = topic,
                        selected = isSelected,
                        onClick = {
                            if (isSelected) selectedTopics.remove(topic) else selectedTopics.add(topic)
                        },
                        leadingIcon = "💬"
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = customTopicText,
                onValueChange = { customTopicText = it },
                label = { Text("Other topics (optional, comma-separated)") },
                placeholder = { Text("e.g. Hiking, Anime, Photography") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )

            // SECTION 6: Personality Type
            FormSectionHeader(title = "Personality Type", subtitle = "Introvert, Extrovert, or Ambivert")

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                personalityOptions.forEach { p ->
                    SelectableChip(
                        text = p,
                        selected = personalityType == p,
                        onClick = { personalityType = p },
                        leadingIcon = "🧠",
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // SECTION 7: Profession Dropdown
            FormSectionHeader(title = "Profession", subtitle = "Select your field or profession")

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
                    placeholder = { Text("Select profession") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = professionExpanded) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor(),
                    shape = RoundedCornerShape(12.dp)
                )
                ExposedDropdownMenu(
                    expanded = professionExpanded,
                    onDismissRequest = { professionExpanded = false }
                ) {
                    professionOptions.forEach { option ->
                        DropdownMenuItem(
                            text = { Text(option) },
                            onClick = {
                                profession = option
                                professionExpanded = false
                            }
                        )
                    }
                }
            }

            // SECTION 8: Conversation Intensity
            FormSectionHeader(title = "Conversation Intensity", subtitle = "Lighthearted banter or deep, soulful talks?")

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                intensityOptions.forEach { item ->
                    SelectableChip(
                        text = item,
                        selected = conversationIntensity == item,
                        onClick = { conversationIntensity = item },
                        leadingIcon = if (item == "Deep") "🌊" else "🎈",
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // SECTION 9: Topics To Avoid
            FormSectionHeader(title = "Topics You Don't Want to Discuss", subtitle = "Boundaries and sensitive subjects (select to avoid)")

            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                avoidedChoices.forEach { topic ->
                    val isSelected = topic in selectedAvoidedTopics
                    SelectableChip(
                        text = topic,
                        selected = isSelected,
                        onClick = {
                            if (isSelected) selectedAvoidedTopics.remove(topic) else selectedAvoidedTopics.add(topic)
                        },
                        leadingIcon = "🚫",
                        isDestructive = true
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = customAvoidedText,
                onValueChange = { customAvoidedText = it },
                label = { Text("Other avoided topics (optional)") },
                placeholder = { Text("e.g. Spoilers, Horror") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )

            // SECTION 10: Security Question & Recovery
            FormSectionHeader(
                title = "Security Question (Password Recovery)",
                subtitle = "Update your secret question & answer to reset your password if forgotten"
            )

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
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor(),
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

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = securityAnswer,
                onValueChange = { securityAnswer = it },
                label = { Text("Secret Answer") },
                placeholder = { Text("e.g. Bangalore, St. Xavier's, etc.") },
                supportingText = { Text("Case-insensitive: uppercase/lowercase both work") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(28.dp))

            // Save / Update Profile Button
            Button(
                enabled = !isLoading,
                onClick = {
                    if (isLoading) return@Button
                    isLoading = true
                    scope.launch {
                        val jwt = token
                        val uid = store.getUserId() ?: (if (!jwt.isNullOrBlank()) JwtUtils.getUserIdFromToken(jwt) else null)
                        if (uid.isNullOrBlank()) {
                            isLoading = false
                            snackbarHostState.showSnackbar("Unable to identify account")
                            return@launch
                        }

                        val avatarAssetUri = AvatarCatalog.getAssetUri(selectedAvatar)

                        val allTopics = (selectedTopics + customTopicText.split(",").map { it.trim() }.filter { it.isNotBlank() })
                            .distinct()
                            .joinToString(", ")

                        val allAvoided = (selectedAvoidedTopics + customAvoidedText.split(",").map { it.trim() }.filter { it.isNotBlank() })
                            .distinct()
                            .joinToString(", ")

                        val encodedAbout = ProfileMetadataParser.encode(
                            bio = bio,
                            lookingFor = lookingFor.ifBlank { null },
                            preferredAgeRange = preferredAgeRange.ifBlank { null },
                            preferredTopics = allTopics.ifBlank { null },
                            personalityType = personalityType.ifBlank { null },
                            conversationIntensity = conversationIntensity.ifBlank { null },
                            avoidedTopics = allAvoided.ifBlank { null }
                        )

                        val upsert = ProfilesService.ProfileUpsertDto(
                            id = uid,
                            name = name.ifBlank { null },
                            age = age.toIntOrNull(),
                            height = height.ifBlank { null },
                            religion = religion.ifBlank { null },
                            caste = null,
                            profession = profession.ifBlank { null },
                            location = location.ifBlank { null },
                            imageUrl = avatarAssetUri,
                            about = encodedAbout,
                            gender = gender.ifBlank { null },
                            annualIncome = null,
                            phoneNumber = phoneNumber.ifBlank { null }
                        )

                        // 1. Save to Supabase (authed or fallback to anon)
                        var remoteSaved = false
                        if (!jwt.isNullOrBlank()) {
                            val authed = SupabaseRestApiClient.authedRetrofit(jwt)
                            val svc = authed.create(ProfilesService::class.java)
                            val res = runCatching { svc.upsertProfiles(listOf(upsert)) }
                            if (res.isSuccess && (res.getOrNull()?.isSuccessful == true)) {
                                remoteSaved = true
                            }
                        }

                        if (!remoteSaved) {
                            val anonSvc = SupabaseRestApiClient.retrofit.create(ProfilesService::class.java)
                            val resAnon = runCatching { anonSvc.upsertProfiles(listOf(upsert)) }
                            if (resAnon.isSuccess && (resAnon.getOrNull()?.isSuccessful == true)) {
                                remoteSaved = true
                            }
                        }

                        // 2. Persist locally to CredentialStore
                        store.savePendingProfile(
                            name = name,
                            age = age.toIntOrNull(),
                            gender = gender,
                            avatarUri = avatarAssetUri,
                            bio = bio,
                            lookingFor = lookingFor,
                            preferredAgeRange = preferredAgeRange,
                            preferredTopics = allTopics,
                            personalityType = personalityType,
                            height = height,
                            profession = profession,
                            conversationIntensity = conversationIntensity,
                            avoidedTopics = allAvoided,
                            location = location,
                            religion = religion,
                            phoneNumber = phoneNumber,
                            securityQuestion = securityQuestion,
                            securityAnswer = securityAnswer
                        )
                        store.saveUserGender(gender)

                        val email = store.getEmail() ?: ""
                        if (email.isNotBlank() && securityAnswer.isNotBlank()) {
                            store.saveSecurityQuestion(email, securityQuestion, securityAnswer)
                        }

                        isLoading = false
                        snackbarHostState.showSnackbar("Profile updated successfully ✨")
                        navController.popBackStack()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(14.dp)
            ) {
                if (isLoading) {
                    Row(
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            color = Color.White,
                            strokeWidth = 2.5.dp
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text("Saving Changes...", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    }
                } else {
                    Text("Save Changes", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
            }

            Spacer(modifier = Modifier.height(36.dp))
        }
    }
}

@Composable
private fun FormSectionHeader(title: String, subtitle: String? = null) {
    val colors = LocalAppColors.current
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 18.dp, bottom = 8.dp)
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp
            ),
            color = if (colors.isDark) Color(0xFFD4AF37) else Color(0xFF8B264E)
        )
        if (!subtitle.isNullOrBlank()) {
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = colors.textSecondary
            )
        }
    }
}

@Composable
private fun SelectableChip(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    leadingIcon: String? = null,
    isDestructive: Boolean = false
) {
    val colors = LocalAppColors.current
    val activeBg = if (isDestructive) {
        if (colors.isDark) Color(0x33FF4D4F) else Color(0x22D9363E)
    } else {
        if (colors.isDark) Color(0xFFD4AF37) else Color(0xFF8B264E)
    }
    val inactiveBg = if (colors.isDark) Color(0xFF221634) else Color(0xFFF1EBF5)
    val activeBorder = if (isDestructive) {
        if (colors.isDark) Color(0xFFFF4D4F) else Color(0xFFD9363E)
    } else {
        if (colors.isDark) Color(0xFFFFD700) else Color(0xFF7A1C41)
    }
    val inactiveBorder = if (colors.isDark) Color(0x33FFFFFF) else Color(0x22000000)

    val activeTextColor = if (isDestructive) {
        if (colors.isDark) Color(0xFFFF7875) else Color(0xFFCF1322)
    } else {
        if (colors.isDark) Color(0xFF150D24) else Color.White
    }

    Surface(
        shape = RoundedCornerShape(20.dp),
        color = if (selected) activeBg else inactiveBg,
        border = BorderStroke(
            width = if (selected) 1.5.dp else 1.dp,
            color = if (selected) activeBorder else inactiveBorder
        ),
        modifier = modifier.clickable { onClick() }
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 13.dp, vertical = 7.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            if (leadingIcon != null) {
                Text(leadingIcon, fontSize = 12.sp)
                Spacer(modifier = Modifier.width(5.dp))
            }
            Text(
                text = text,
                fontSize = 13.sp,
                fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
                color = if (selected) activeTextColor else colors.textPrimary
            )
        }
    }
}
