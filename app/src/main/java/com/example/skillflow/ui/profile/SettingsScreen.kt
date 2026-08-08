package com.example.skillflow.ui.profile

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.skillflow.R
import com.example.skillflow.presentation.profile.SettingsState
import com.example.skillflow.presentation.profile.SettingsUiEvent
import com.example.skillflow.presentation.profile.SettingsViewModel
import com.example.skillflow.ui.common.AuthButton
import com.example.skillflow.ui.common.AuthTextField
import com.example.skillflow.ui.common.SkillflowTopAppBar
import com.example.skillflow.ui.profile.components.LanguageToggleButton
import com.example.skillflow.ui.profile.components.SettingsItem
import com.example.skillflow.ui.theme.spacing
import kotlinx.coroutines.flow.collectLatest

@Composable
fun SettingsScreen(
    onNavigateBack: () -> Unit,
    onLogout: () -> Unit,
    onNavigateToPrivacy: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    var showDeleteDialog by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.eventFlow.collectLatest { event ->
            when (event) {
                is SettingsUiEvent.ShowSnackbar -> {
                    snackbarHostState.showSnackbar(event.message)
                }
                is SettingsUiEvent.LogoutSuccess -> {
                    onLogout()
                }
            }
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            SkillflowTopAppBar(
                title = stringResource(R.string.settings),
                navigationIcon = Icons.AutoMirrored.Filled.ArrowBack,
                onNavigationClick = onNavigateBack
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(MaterialTheme.spacing.large)
                .verticalScroll(rememberScrollState())
        ) {
            Text(
                text = stringResource(R.string.edit_profile),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(MaterialTheme.spacing.medium))

            AuthTextField(
                value = state.name,
                onValueChange = { viewModel.updateName(it) },
                label = stringResource(R.string.full_name)
            )
            Spacer(modifier = Modifier.height(MaterialTheme.spacing.medium))
            AuthTextField(
                value = state.email,
                onValueChange = { viewModel.updateEmail(it) },
                label = stringResource(R.string.email_address)
            )

            Spacer(modifier = Modifier.height(MaterialTheme.spacing.large))
            
            // Preferences Section
            Text(
                text = stringResource(R.string.account_details),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(MaterialTheme.spacing.medium))

            SettingsItem(
                title = stringResource(R.string.language_toggle, if (state.language == "bn") stringResource(R.string.bengali) else stringResource(R.string.english)),
                icon = Icons.Default.Language,
                trailing = {
                    LanguageToggleButton(
                        currentLanguage = state.language,
                        onToggle = { 
                            val nextLang = if (state.language == "bn") "en" else "bn"
                            viewModel.setLanguage(nextLang)
                        }
                    )
                }
            )

            SettingsItem(
                title = stringResource(R.string.privacy_policy),
                icon = Icons.Default.PrivacyTip,
                onClick = onNavigateToPrivacy
            )

            Spacer(modifier = Modifier.height(MaterialTheme.spacing.large))
            
            // About Section
            Text(
                text = stringResource(R.string.about_app),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(MaterialTheme.spacing.medium))
            
            Text(
                text = stringResource(R.string.app_version, "1.0.0"),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = stringResource(R.string.developer_info),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            TextButton(
                onClick = { /* Handle support click */ },
                contentPadding = PaddingValues(0.dp)
            ) {
                Text(text = stringResource(R.string.contact_support))
            }

            Spacer(modifier = Modifier.height(MaterialTheme.spacing.extraLarge))
            
            AuthButton(
                text = stringResource(R.string.logout),
                onClick = {
                    viewModel.logout()
                },
                isLoading = state.isLoading && !showDeleteDialog
            )
            
            Spacer(modifier = Modifier.height(MaterialTheme.spacing.medium))
            
            TextButton(
                onClick = { showDeleteDialog = true },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)
            ) {
                Text(
                    text = stringResource(R.string.delete_account),
                    fontWeight = FontWeight.Bold
                )
            }
            
            Spacer(modifier = Modifier.height(MaterialTheme.spacing.large))
        }
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text(text = stringResource(R.string.delete_account_title)) },
            text = { Text(text = stringResource(R.string.delete_account_message)) },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.deleteAccount()
                        showDeleteDialog = false
                    },
                    colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)
                ) {
                    Text(text = stringResource(R.string.confirm_delete))
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text(text = stringResource(R.string.cancel))
                }
            }
        )
    }

    if (state.isLoading) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    }
}
