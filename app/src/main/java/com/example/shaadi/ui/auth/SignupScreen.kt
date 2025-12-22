package com.example.shaadi.ui.auth

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import android.net.Uri

@Composable
fun SignupScreen(navController: NavController) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
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
    var error by remember { mutableStateOf<String?>(null) }
    var success by remember { mutableStateOf<String?>(null) }
    val context = LocalContext.current
    val store = remember { CredentialStore(context.applicationContext) }
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    val pickImageLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        selectedImageUri = uri
    }
    val auth = remember {
        when {
            BuildConfig.USE_SUPABASE_AUTH -> SupabaseAuthRepository.getInstance(context)
            BuildConfig.USE_REMOTE_AUTH -> RemoteAuthRepository.getInstance(context)
            else -> LocalAuthRepository.getInstance(context)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(32.dp))
        Text(text = "Create Profile", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(32.dp))

        OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Full Name") }, modifier = Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(value = email, onValueChange = { email = it }, label = { Text("Email") }, modifier = Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(value = password, onValueChange = { password = it }, label = { Text("Password") }, visualTransformation = PasswordVisualTransformation(), modifier = Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(value = age, onValueChange = { age = it }, label = { Text("Age") }, modifier = Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(value = height, onValueChange = { height = it }, label = { Text("Height (e.g. 5'8\"") }, modifier = Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(value = religion, onValueChange = { religion = it }, label = { Text("Religion") }, modifier = Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(value = caste, onValueChange = { caste = it }, label = { Text("Caste") }, modifier = Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(value = profession, onValueChange = { profession = it }, label = { Text("Profession") }, modifier = Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(value = location, onValueChange = { location = it }, label = { Text("Location") }, modifier = Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.height(16.dp))
        // Photo picker
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Button(onClick = { pickImageLauncher.launch("image/*") }) { Text("Select Photo") }
            Spacer(Modifier.width(12.dp))
            Text(text = selectedImageUri?.lastPathSegment ?: "No file selected")
        }
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(value = imageUrl, onValueChange = { imageUrl = it }, label = { Text("Photo URL (optional)") }, modifier = Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(value = about, onValueChange = { about = it }, label = { Text("About") }, modifier = Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(value = gender, onValueChange = { gender = it }, label = { Text("Gender") }, modifier = Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(value = annualIncome, onValueChange = { annualIncome = it }, label = { Text("Annual Income") }, modifier = Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(value = phoneNumber, onValueChange = { phoneNumber = it }, label = { Text("Phone Number") }, modifier = Modifier.fillMaxWidth())

        Spacer(modifier = Modifier.height(24.dp))

        if (error != null) {
            Text(text = error!!, color = MaterialTheme.colorScheme.error)
            Spacer(modifier = Modifier.height(12.dp))
        }
        if (success != null) {
            Text(text = success!!, color = MaterialTheme.colorScheme.primary)
            Spacer(modifier = Modifier.height(12.dp))
        }

        Button(
            onClick = {
                error = null
                success = null
                val trimmedEmail = email.trim()
                val trimmedName = name.trim()
                val ageInt = age.trim().toIntOrNull()

                if (BuildConfig.USE_SUPABASE_AUTH) {
                    try {
                        val service = com.example.shaadi.network.SupabaseApiClient.retrofit.create(com.example.shaadi.network.SupabaseAuthService::class.java)
                        val meta = com.example.shaadi.network.SupabaseUserMeta(
                            name = trimmedName.ifEmpty { null },
                            age = ageInt,
                            height = height.ifEmpty { null },
                            religion = religion.ifEmpty { null },
                            caste = caste.ifEmpty { null },
                            profession = profession.ifEmpty { null },
                            location = location.ifEmpty { null },
                            imageUrl = (imageUrl.ifEmpty { null }),
                            about = about.ifEmpty { null },
                            gender = gender.ifEmpty { null },
                            annualIncome = annualIncome.ifEmpty { null },
                            phoneNumber = phoneNumber.ifEmpty { null }
                        )
                        kotlinx.coroutines.runBlocking {
                            service.signup(
                                com.example.shaadi.network.SupabaseSignupRequest(
                                    email = trimmedEmail,
                                    password = password,
                                    data = meta
                                )
                            )
                        }
                        // Save picked image URI for upload after first login (when we have a token)
                        store.savePendingImageUri(selectedImageUri?.toString())
                        success = "Account created. Please check your email to verify, then login."
                        navController.popBackStack()
                    } catch (t: Throwable) {
                        error = t.message ?: "Signup failed"
                    }
                } else {
                    val result = auth.register(trimmedEmail, password)
                    if (result.isSuccess) {
                        success = "Account created. Please login."
                        navController.popBackStack()
                    } else {
                        error = result.exceptionOrNull()?.message ?: "Signup failed"
                    }
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) { Text("Sign Up") }

        Spacer(modifier = Modifier.height(16.dp))
        TextButton(onClick = { navController.popBackStack() }) { Text("Already have an account? Login") }
    }
}
