package com.example.shaadi.ui.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.shaadi.data.model.Profile
import com.example.shaadi.data.profile.ProfilesRepository
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavController) {
    val profiles = remember { mutableStateListOf<Profile>() }
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val repo = remember { ProfilesRepository.getInstance(navController.context) }

    LaunchedEffect(Unit) {
        val result = repo.fetchProfiles(limit = 100)
        if (result.isSuccess) {
            profiles.clear()
            profiles.addAll(result.getOrDefault(emptyList()))
        } else {
            scope.launch {
                snackbarHostState.showSnackbar(
                    message = result.exceptionOrNull()?.message ?: "Failed to load profiles",
                    withDismissAction = true
                )
            }
        }
    }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Matches") }) },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { paddingValues ->
        LazyColumn(
            contentPadding = PaddingValues(16.dp),
            modifier = Modifier.padding(paddingValues)
        ) {
            items(profiles, key = { it.id }) { profile ->
                ProfileCard(
                    profile = profile,
                    onConnectClick = {
                        // Mark as liked, remove from list, and show snackbar with Undo
                        val index = profiles.indexOf(profile)
                        if (index != -1) {
                            profiles.removeAt(index)
                            scope.launch {
                                val result = snackbarHostState.showSnackbar(
                                    message = "Selected ${profile.name}",
                                    actionLabel = "Undo",
                                    withDismissAction = true,
                                    duration = SnackbarDuration.Short
                                )
                                if (result == SnackbarResult.ActionPerformed) {
                                    profiles.add(index.coerceAtMost(profiles.size), profile)
                                }
                            }
                        }
                    },
                    onSkipClick = {
                        // Mark as rejected, remove from list, and show snackbar with Undo
                        val index = profiles.indexOf(profile)
                        if (index != -1) {
                            profiles.removeAt(index)
                            scope.launch {
                                val result = snackbarHostState.showSnackbar(
                                    message = "Rejected ${profile.name}",
                                    actionLabel = "Undo",
                                    withDismissAction = true,
                                    duration = SnackbarDuration.Short
                                )
                                if (result == SnackbarResult.ActionPerformed) {
                                    profiles.add(index.coerceAtMost(profiles.size), profile)
                                }
                            }
                        }
                    },
                    modifier = Modifier.clickable { navController.navigate("profile/${profile.id}") }
                )
            }
        }
    }
}
