package com.blyndchat.ui.auth

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
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.blyndchat.BuildConfig
import com.blyndchat.data.auth.CredentialStore
import com.blyndchat.data.auth.LocalAuthRepository
import com.blyndchat.data.auth.RemoteAuthRepository
import com.blyndchat.data.auth.SecurityQuestions
import com.blyndchat.data.auth.SupabaseAuthRepository
import com.blyndchat.data.profile.AvatarCatalog
import com.blyndchat.ui.theme.LocalAppColors
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun SignupScreen(navController: NavController) {
    val colors = LocalAppColors.current

    // Basic Account Details
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var age by remember { mutableStateOf("") }
    var gender by remember { mutableStateOf("Male") }
    var selectedAvatar by remember { mutableStateOf(AvatarCatalog.defaultAvatarForGender("Male")) }
    var showAvatarDialog by remember { mutableStateOf(false) }
    var dialogGenderTab by remember { mutableStateOf("Male") }

    // Security Question & Answer for password recovery
    var securityQuestion by remember { mutableStateOf(SecurityQuestions.defaultQuestion) }
    var securityQuestionExpanded by remember { mutableStateOf(false) }
    var securityAnswer by remember { mutableStateOf("") }

    // Chat Persona & Preferences Details
    var bio by remember { mutableStateOf("") }
    var lookingFor by remember { mutableStateOf("Meaningful conversations") }
    var preferredAgeRange by remember { mutableStateOf("22 - 28") }
    val selectedTopics = remember { mutableStateListOf("Music", "Travel", "Tech & AI") }
    var customTopicText by remember { mutableStateOf("") }
    var personalityType by remember { mutableStateOf("Ambivert") }
    var height by remember { mutableStateOf("5'6\"") }
    var profession by remember { mutableStateOf("Software Engineer") }
    var professionExpanded by remember { mutableStateOf(false) }
    var conversationIntensity by remember { mutableStateOf("Deep") }
    val selectedAvoidedTopics = remember { mutableStateListOf("Politics") }
    var customAvoidedText by remember { mutableStateOf("") }

    var error by remember { mutableStateOf<String?>(null) }
    var success by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    val context = LocalContext.current
    val store = remember { CredentialStore(context.applicationContext) }
    val auth = remember {
        when {
            BuildConfig.USE_SUPABASE_AUTH -> SupabaseAuthRepository.getInstance(context)
            BuildConfig.USE_REMOTE_AUTH -> RemoteAuthRepository.getInstance(context)
            else -> LocalAuthRepository.getInstance(context)
        }
    }

    val avatarList = remember(gender) {
        AvatarCatalog.getAvatarsForGender(gender)
    }

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

    val lookingForOptions = listOf(
        "Friendship",
        "Casual chat",
        "Meaningful conversations",
        "Networking",
        "Dating"
    )

    val ageRangeOptions = listOf(
        "18 - 24",
        "22 - 28",
        "25 - 32",
        "30 - 40",
        "Any age"
    )

    val topicChoices = listOf(
        "Tech & AI", "Music", "Movies & TV", "Books", "Travel",
        "Fitness", "Philosophy", "Gaming", "Art & Design",
        "Food & Cooking", "Science", "Psychology"
    )

    val personalityOptions = listOf("Introvert", "Extrovert", "Ambivert")

    val heightChoices = listOf("5'2\"", "5'4\"", "5'6\"", "5'8\"", "5'10\"", "6'0\"", "6'2\"")

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
        "Politics", "Past Relationships", "Money / Salary",
        "Office Drama", "Religion", "Gossip"
    )

    if (showAvatarDialog) {
        val currentGenderAvatars = remember(dialogGenderTab) {
            AvatarCatalog.getAvatarsForGender(dialogGenderTab)
        }

        AlertDialog(
            onDismissRequest = { showAvatarDialog = false },
            title = {
                Column {
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
                                        } else Color.Gray.copy(alpha = 0.25f),
                                        shape = RoundedCornerShape(10.dp)
                                    )
                                    .clickable {
                                        dialogGenderTab = gVal
                                        onGenderSelected(gVal)
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = gLabel,
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        fontWeight = if (isTabSelected) FontWeight.Bold else FontWeight.Medium
                                    ),
                                    color = if (isTabSelected) {
                                        if (colors.isDark) Color(0xFFD4AF37) else MaterialTheme.colorScheme.primary
                                    } else colors.textPrimary
                                )
                            }
                        }
                    }
                }
            },
            text = {
                Box(modifier = Modifier.fillMaxWidth().heightIn(max = 420.dp)) {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(3),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(currentGenderAvatars) { avatarFile ->
                            val isSelected = avatarFile == selectedAvatar
                            val avatarNumber = avatarFile.filter { it.isDigit() }
                            val prefix = if (AvatarCatalog.isMaleAvatar(avatarFile)) "Male" else "Female"

                            Card(
                                shape = RoundedCornerShape(14.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = if (isSelected) {
                                        if (colors.isDark) Color(0xFF2A2415) else Color(0xFFFFF8E7)
                                    } else {
                                        if (colors.isDark) Color(0xFF1E1E1E) else Color(0xFFF6F6F6)
                                    }
                                ),
                                border = BorderStroke(
                                    width = if (isSelected) 2.dp else 1.dp,
                                    color = if (isSelected) {
                                        if (colors.isDark) Color(0xFFD4AF37) else MaterialTheme.colorScheme.primary
                                    } else Color.Gray.copy(alpha = 0.25f)
                                ),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        selectedAvatar = avatarFile
                                        if (!gender.equals(dialogGenderTab, ignoreCase = true)) {
                                            onGenderSelected(dialogGenderTab)
                                        }
                                        showAvatarDialog = false
                                    }
                            ) {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    modifier = Modifier.padding(6.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(72.dp)
                                            .clip(CircleShape)
                                            .border(
                                                width = if (isSelected) 2.5.dp else 1.dp,
                                                color = if (isSelected) {
                                                    if (colors.isDark) Color(0xFFD4AF37) else MaterialTheme.colorScheme.primary
                                                } else Color.Gray.copy(alpha = 0.3f),
                                                shape = CircleShape
                                            )
                                    ) {
                                        AsyncImage(
                                            model = ImageRequest.Builder(context)
                                                .data(AvatarCatalog.getAssetUri(avatarFile))
                                                .build(),
                                            contentDescription = "$prefix Avatar $avatarNumber",
                                            modifier = Modifier.fillMaxSize(),
                                            contentScale = ContentScale.Crop
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "#$avatarNumber",
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                        color = if (isSelected) {
                                            if (colors.isDark) Color(0xFFD4AF37) else MaterialTheme.colorScheme.primary
                                        } else colors.textPrimary
                                    )
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showAvatarDialog = false }) {
                    Text("Done", fontWeight = FontWeight.Bold)
                }
            }
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.screenBackgroundBrush)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(28.dp))

            Text(
                text = "Create Profile",
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.5.sp
                ),
                color = colors.topBarTitle
            )

            Text(
                text = "Set up your persona and select your AI avatar",
                style = MaterialTheme.typography.bodyMedium,
                color = colors.textSecondary,
                modifier = Modifier.padding(top = 4.dp)
            )

            Spacer(modifier = Modifier.height(18.dp))

            // Gender Selector: Male vs Female (10 AI photos each)
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

            // SECTION 1: Account Essentials
            FormSectionHeader(title = "Account Details", subtitle = "Your basic login credentials")

            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Full Name") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password") },
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Security Question Section for Password Recovery
            FormSectionHeader(
                title = "Security Question (Password Recovery)",
                subtitle = "Select a secret question to reset your password if you ever forget it"
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

                // Height quick input / dropdown
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
                        selected = gender == g,
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

            Spacer(modifier = Modifier.height(28.dp))

            if (error != null) {
                Text(
                    text = error!!,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium
                )
                Spacer(modifier = Modifier.height(12.dp))
            }
            if (success != null) {
                Text(
                    text = success!!,
                    color = MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.bodyMedium
                )
                Spacer(modifier = Modifier.height(12.dp))
            }

            Button(
                enabled = !isLoading,
                onClick = {
                    if (isLoading) return@Button
                    error = null
                    success = null
                    val avatarAssetUri = AvatarCatalog.getAssetUri(selectedAvatar)

                    val allTopics = (selectedTopics + customTopicText.split(",").map { it.trim() }.filter { it.isNotBlank() })
                        .distinct()
                        .joinToString(", ")

                    val allAvoided = (selectedAvoidedTopics + customAvoidedText.split(",").map { it.trim() }.filter { it.isNotBlank() })
                        .distinct()
                        .joinToString(", ")

                    if (email.isBlank()) {
                        error = "Email is required"
                        return@Button
                    }
                    if (password.length < 6) {
                        error = "Password must be at least 6 characters"
                        return@Button
                    }
                    if (securityAnswer.trim().isBlank()) {
                        error = "Please provide an answer to your secret question"
                        return@Button
                    }

                    isLoading = true

                    scope.launch {
                        val result = withContext(Dispatchers.IO) {
                            store.savePendingProfile(
                                name = name.trim().ifEmpty { null },
                                age = age.trim().toIntOrNull(),
                                gender = gender.ifEmpty { null },
                                avatarUri = avatarAssetUri,
                                bio = bio.trim().ifEmpty { null },
                                lookingFor = lookingFor.ifEmpty { null },
                                preferredAgeRange = preferredAgeRange.ifEmpty { null },
                                preferredTopics = allTopics.ifEmpty { null },
                                personalityType = personalityType.ifEmpty { null },
                                height = height.trim().ifEmpty { null },
                                profession = profession.trim().ifEmpty { null },
                                conversationIntensity = conversationIntensity.ifEmpty { null },
                                avoidedTopics = allAvoided.ifEmpty { null },
                                securityQuestion = securityQuestion,
                                securityAnswer = securityAnswer.trim().ifEmpty { null }
                            )

                            if (gender.isNotBlank()) {
                                store.saveUserGender(gender)
                            }

                            store.saveSecurityQuestion(email.trim(), securityQuestion, securityAnswer.trim())

                            auth.register(email.trim(), password, securityQuestion, securityAnswer.trim())
                        }

                        if (result.isSuccess) {
                            success = "Account created. Please login."
                            navController.popBackStack()
                        } else {
                            isLoading = false
                            error = result.exceptionOrNull()?.message ?: "Signup failed"
                        }
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
                        Text("Creating Profile...", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    }
                } else {
                    Text("Create Profile & Sign Up", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            TextButton(
                enabled = !isLoading,
                onClick = { navController.popBackStack() }
            ) {
                Text(
                    text = "Already have an account? Login",
                    color = if (colors.isDark) Color(0xFFD4AF37) else MaterialTheme.colorScheme.primary
                )
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
        border = androidx.compose.foundation.BorderStroke(
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
