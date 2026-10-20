package com.blyndchat.ui.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.blyndchat.BuildConfig
import com.blyndchat.data.auth.AuthRepository
import com.blyndchat.data.auth.CredentialStore
import com.blyndchat.data.auth.LocalAuthRepository
import com.blyndchat.data.auth.RemoteAuthRepository
import com.blyndchat.data.auth.SecurityQuestions
import com.blyndchat.data.auth.SupabaseAuthRepository
import com.blyndchat.ui.theme.LocalAppColors
import com.blyndchat.ui.theme.ShaadiGold
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ForgotPasswordScreen(navController: NavController) {
    val context = LocalContext.current
    val store = remember { CredentialStore(context.applicationContext) }
    val auth: AuthRepository = remember {
        when {
            BuildConfig.USE_SUPABASE_AUTH -> SupabaseAuthRepository.getInstance(context)
            BuildConfig.USE_REMOTE_AUTH -> RemoteAuthRepository.getInstance(context)
            else -> LocalAuthRepository.getInstance(context)
        }
    }
    val colors = LocalAppColors.current
    val scope = rememberCoroutineScope()

    var email by remember { mutableStateOf(store.getEmail() ?: "") }
    var selectedQuestion by remember {
        mutableStateOf(store.getSecurityQuestion(store.getEmail()) ?: SecurityQuestions.defaultQuestion)
    }
    var questionExpanded by remember { mutableStateOf(false) }
    var secretAnswer by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }

    var isResetting by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var successMessage by remember { mutableStateOf<String?>(null) }

    // Synchronize secret question when email changes
    LaunchedEffect(email) {
        val clean = email.trim()
        if (clean.isNotBlank()) {
            val q = store.getSecurityQuestion(clean)
            if (!q.isNullOrBlank()) {
                selectedQuestion = q
            }
        }
    }

    val brandGold = if (colors.isDark) Color(0xFFD4AF37) else ShaadiGold

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Reset Password",
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleMedium,
                        color = colors.topBarTitle
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = colors.topBarTitle
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = colors.topBarContainer,
                    titleContentColor = colors.topBarTitle
                )
            )
        },
        containerColor = Color.Transparent
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(colors.screenBackgroundBrush)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 24.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
            Spacer(modifier = Modifier.height(16.dp))

            // Security badge header
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(brandGold.copy(alpha = 0.15f))
                    .border(2.dp, brandGold.copy(alpha = 0.4f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Lock,
                    contentDescription = null,
                    tint = brandGold,
                    modifier = Modifier.size(38.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Account Security Recovery",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = colors.textPrimary
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Validate your secret question to reset your password. Answer matching is case-insensitive.",
                style = MaterialTheme.typography.bodyMedium,
                color = colors.textSecondary,
                modifier = Modifier.padding(horizontal = 8.dp),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(22.dp))

            // Error banner
            if (errorMessage != null) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = "Error",
                            tint = MaterialTheme.colorScheme.error
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = errorMessage!!,
                            color = MaterialTheme.colorScheme.onErrorContainer,
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            // Success banner
            if (successMessage != null) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = "Success",
                            tint = Color(0xFF2E7D32)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = successMessage!!,
                            color = Color(0xFF1B5E20),
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            // Field 1: Email Address
            OutlinedTextField(
                value = email,
                onValueChange = {
                    email = it
                    errorMessage = null
                },
                label = { Text("Registered Email") },
                placeholder = { Text("e.g. user@example.com") },
                leadingIcon = {
                    Icon(imageVector = Icons.Default.Email, contentDescription = null, tint = brandGold)
                },
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = colors.textPrimary,
                    unfocusedTextColor = colors.textPrimary,
                    focusedBorderColor = brandGold,
                    focusedLabelColor = brandGold,
                    cursorColor = brandGold
                ),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Field 2: Secret Question Dropdown
            ExposedDropdownMenuBox(
                expanded = questionExpanded,
                onExpandedChange = { questionExpanded = !questionExpanded },
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = selectedQuestion,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Secret Question") },
                    leadingIcon = {
                        Icon(imageVector = Icons.Default.Info, contentDescription = null, tint = brandGold)
                    },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = questionExpanded) },
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = colors.textPrimary,
                        unfocusedTextColor = colors.textPrimary,
                        focusedBorderColor = brandGold,
                        focusedLabelColor = brandGold
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor()
                )
                ExposedDropdownMenu(
                    expanded = questionExpanded,
                    onDismissRequest = { questionExpanded = false }
                ) {
                    SecurityQuestions.list.forEach { question ->
                        DropdownMenuItem(
                            text = { Text(question) },
                            onClick = {
                                selectedQuestion = question
                                questionExpanded = false
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Field 3: Secret Answer (Case-insensitive)
            OutlinedTextField(
                value = secretAnswer,
                onValueChange = {
                    secretAnswer = it
                    errorMessage = null
                },
                label = { Text("Secret Answer") },
                placeholder = { Text("Enter your secret answer") },
                supportingText = {
                    Text(
                        text = "Answers match irrespective of upper, lower, or camelCase",
                        color = colors.textSecondary,
                        fontSize = 11.sp
                    )
                },
                leadingIcon = {
                    Icon(imageVector = Icons.Default.Lock, contentDescription = null, tint = brandGold)
                },
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = colors.textPrimary,
                    unfocusedTextColor = colors.textPrimary,
                    focusedBorderColor = brandGold,
                    focusedLabelColor = brandGold,
                    cursorColor = brandGold
                ),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Field 4: New Password
            OutlinedTextField(
                value = newPassword,
                onValueChange = {
                    newPassword = it
                    errorMessage = null
                },
                label = { Text("New Password") },
                placeholder = { Text("Minimum 6 characters") },
                leadingIcon = {
                    Icon(imageVector = Icons.Default.Lock, contentDescription = null, tint = brandGold)
                },
                trailingIcon = {
                    TextButton(onClick = { passwordVisible = !passwordVisible }) {
                        Text(
                            text = if (passwordVisible) "Hide" else "Show",
                            color = brandGold,
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Bold
                        )
                    }
                },
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = colors.textPrimary,
                    unfocusedTextColor = colors.textPrimary,
                    focusedBorderColor = brandGold,
                    focusedLabelColor = brandGold,
                    cursorColor = brandGold
                ),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Field 5: Confirm Password
            OutlinedTextField(
                value = confirmPassword,
                onValueChange = {
                    confirmPassword = it
                    errorMessage = null
                },
                label = { Text("Confirm New Password") },
                placeholder = { Text("Re-enter new password") },
                leadingIcon = {
                    Icon(imageVector = Icons.Default.Lock, contentDescription = null, tint = brandGold)
                },
                trailingIcon = {
                    TextButton(onClick = { confirmPasswordVisible = !confirmPasswordVisible }) {
                        Text(
                            text = if (confirmPasswordVisible) "Hide" else "Show",
                            color = brandGold,
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Bold
                        )
                    }
                },
                visualTransformation = if (confirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = colors.textPrimary,
                    unfocusedTextColor = colors.textPrimary,
                    focusedBorderColor = brandGold,
                    focusedLabelColor = brandGold,
                    cursorColor = brandGold
                ),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(26.dp))

            // Submit Button: Reset Password
            Button(
                onClick = {
                    val cleanEmail = email.trim()
                    val cleanAnswer = secretAnswer.trim()
                    val cleanPass = newPassword.trim()
                    val cleanConfirm = confirmPassword.trim()

                    if (cleanEmail.isBlank()) {
                        errorMessage = "Please enter your registered email address."
                        return@Button
                    }
                    if (cleanAnswer.isBlank()) {
                        errorMessage = "Please enter your secret answer to verify identity."
                        return@Button
                    }
                    if (cleanPass.length < 6) {
                        errorMessage = "New password must be at least 6 characters long."
                        return@Button
                    }
                    if (cleanPass != cleanConfirm) {
                        errorMessage = "Passwords do not match. Please check and re-enter."
                        return@Button
                    }

                    errorMessage = null
                    isResetting = true
                    scope.launch {
                        val result = auth.resetPasswordWithSecurityAnswer(cleanEmail, cleanAnswer, cleanPass)
                        isResetting = false
                        if (result.isSuccess) {
                            successMessage = "Password reset successfully! Redirecting to login..."
                            delay(1600L)
                            navController.navigate("login") {
                                popUpTo("login") { inclusive = true }
                            }
                        } else {
                            val msg = result.exceptionOrNull()?.message ?: "Validation failed"
                            errorMessage = msg
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                enabled = !isResetting,
                colors = ButtonDefaults.buttonColors(
                    containerColor = brandGold,
                    contentColor = if (colors.isDark) Color(0xFF141416) else Color.White,
                    disabledContainerColor = brandGold.copy(alpha = 0.5f),
                    disabledContentColor = Color.White.copy(alpha = 0.8f)
                ),
                shape = RoundedCornerShape(14.dp)
            ) {
                if (isResetting) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(22.dp),
                        color = if (colors.isDark) Color(0xFF141416) else Color.White,
                        strokeWidth = 2.dp
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Verifying & Resetting...", fontWeight = FontWeight.Bold)
                } else {
                    Text("Reset Password", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Back to Login Link
            TextButton(
                onClick = { navController.popBackStack() },
                colors = ButtonDefaults.textButtonColors(contentColor = brandGold)
            ) {
                Text(
                    text = "Remember your password? Log In",
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
}

