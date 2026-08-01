package com.example.skillflow.ui.profile

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Mail
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.skillflow.R
import com.example.skillflow.presentation.profile.SettingsViewModel
import com.example.skillflow.ui.common.AuthButton
import com.example.skillflow.ui.common.AuthTextField
import com.example.skillflow.ui.common.SkillflowTopAppBar
import com.example.skillflow.ui.theme.spacing

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onNavigateBack: () -> Unit,
    onLogout: () -> Unit,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    var showEmailDialog by remember { mutableStateOf(false) }
    var newEmail by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            SkillflowTopAppBar(
                title = "Settings",
                navigationIcon = Icons.AutoMirrored.Filled.ArrowBack,
                onNavigationClick = onNavigateBack
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(MaterialTheme.spacing.large)
                .verticalScroll(rememberScrollState())
        ) {
            Text(
                text = "Account Details",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(MaterialTheme.spacing.medium))

            SettingsItem(
                icon = Icons.Default.Person,
                title = "Name",
                subtitle = state.name.ifEmpty { "Not set" },
                onClick = {}
            )
            SettingsItem(
                icon = Icons.Default.Mail,
                title = "Email",
                subtitle = state.email.ifEmpty { "Not set" },
                onClick = { 
                    newEmail = state.email
                    showEmailDialog = true 
                }
            )

            Spacer(modifier = Modifier.height(MaterialTheme.spacing.large))
            Text(
                text = "App Settings",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(MaterialTheme.spacing.medium))

            SettingsItem(
                icon = Icons.Default.Language,
                title = "Language",
                subtitle = if (state.language == "bn") "Bengali" else "English",
                onClick = {
                    viewModel.setLanguage(if (state.language == "bn") "en" else "bn")
                }
            )
            
            SettingsItem(
                icon = Icons.Default.Timer,
                title = "Total Learning Time",
                subtitle = "${state.learningTime} minutes",
                onClick = {}
            )

            Spacer(modifier = Modifier.weight(1f))
            
            AuthButton(
                text = "Logout",
                onClick = {
                    viewModel.logout()
                    onLogout()
                },
                modifier = Modifier.padding(vertical = MaterialTheme.spacing.large)
            )
        }
    }

    if (showEmailDialog) {
        AlertDialog(
            onDismissRequest = { showEmailDialog = false },
            title = { Text("Update Email") },
            text = {
                AuthTextField(
                    value = newEmail,
                    onValueChange = { newEmail = it },
                    label = "New Email"
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.updateEmail(newEmail)
                    showEmailDialog = false
                }) {
                    Text("Save")
                }
            },
            dismissButton = {
                TextButton(onClick = { showEmailDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun SettingsItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    ListItem(
        modifier = Modifier.padding(vertical = 4.dp),
        headlineContent = { Text(title, fontWeight = FontWeight.Bold) },
        supportingContent = { Text(subtitle) },
        leadingContent = { Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary) },
        trailingContent = {
            if (onClick != {}) {
                TextButton(onClick = onClick) {
                    Text("Edit")
                }
            }
        }
    )
}
