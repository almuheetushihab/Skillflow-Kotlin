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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.skillflow.BuildConfig
import com.example.skillflow.R
import com.example.skillflow.presentation.profile.SettingsState
import com.example.skillflow.presentation.profile.SettingsUiEvent
import com.example.skillflow.presentation.profile.SettingsViewModel
import com.example.skillflow.ui.common.AuthButton
import com.example.skillflow.ui.common.AuthTextField
import com.example.skillflow.ui.common.SkillflowTopAppBar
import com.example.skillflow.ui.profile.components.LanguageToggleButton
import com.example.skillflow.ui.profile.components.SettingsItem
import com.example.skillflow.ui.theme.SkillflowTheme
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

    SettingsContent(
        state = state,
        snackbarHostState = snackbarHostState,
        showDeleteDialog = showDeleteDialog,
        onNavigateBack = onNavigateBack,
        onUpdateName = viewModel::updateName,
        onUpdateEmail = viewModel::updateEmail,
        onSetLanguage = viewModel::setLanguage,
        onNavigateToPrivacy = onNavigateToPrivacy,
        onLogout = viewModel::logout,
        onDeleteAccount = viewModel::deleteAccount,
        onShowDeleteDialogChange = { showDeleteDialog = it },
        modifier = modifier
    )
}

@Composable
fun SettingsContent(
    state: SettingsState,
    snackbarHostState: SnackbarHostState,
    showDeleteDialog: Boolean,
    onNavigateBack: () -> Unit,
    onUpdateName: (String) -> Unit,
    onUpdateEmail: (String) -> Unit,
    onSetLanguage: (String) -> Unit,
    onNavigateToPrivacy: () -> Unit,
    onLogout: () -> Unit,
    onDeleteAccount: () -> Unit,
    onShowDeleteDialogChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    val spacing = MaterialTheme.spacing
    
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
                .padding(horizontal = spacing.large)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(spacing.medium))
            Text(
                text = stringResource(R.string.edit_profile),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(spacing.medium))

            AuthTextField(
                value = state.name,
                onValueChange = onUpdateName,
                label = stringResource(R.string.full_name)
            )
            Spacer(modifier = Modifier.height(spacing.medium))
            AuthTextField(
                value = state.email,
                onValueChange = onUpdateEmail,
                label = stringResource(R.string.email_address)
            )

            Spacer(modifier = Modifier.height(spacing.large))
            
            Text(
                text = stringResource(R.string.account_details),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(spacing.medium))

            SettingsItem(
                title = stringResource(R.string.language_toggle, if (state.language == "bn") stringResource(R.string.bengali) else stringResource(R.string.english)),
                icon = Icons.Default.Language,
                trailing = {
                    LanguageToggleButton(
                        currentLanguage = state.language,
                        onToggle = { 
                            val nextLang = if (state.language == "bn") "en" else "bn"
                            onSetLanguage(nextLang)
                        }
                    )
                }
            )

            SettingsItem(
                title = stringResource(R.string.privacy_policy),
                icon = Icons.Default.PrivacyTip,
                onClick = onNavigateToPrivacy
            )

            Spacer(modifier = Modifier.height(spacing.large))
            
            Text(
                text = stringResource(R.string.about_app),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(spacing.medium))
            
            Text(
                text = stringResource(R.string.app_version, "1.0"), // Ideally from BuildConfig
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

            Spacer(modifier = Modifier.height(spacing.extraLarge))
            
            AuthButton(
                text = stringResource(R.string.logout),
                onClick = onLogout,
                isLoading = state.isLoading && !showDeleteDialog
            )
            
            Spacer(modifier = Modifier.height(spacing.medium))
            
            TextButton(
                onClick = { onShowDeleteDialogChange(true) },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)
            ) {
                Text(
                    text = stringResource(R.string.delete_account),
                    fontWeight = FontWeight.Bold
                )
            }
            
            Spacer(modifier = Modifier.height(spacing.large))
        }
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { onShowDeleteDialogChange(false) },
            title = { Text(text = stringResource(R.string.delete_account_title)) },
            text = { Text(text = stringResource(R.string.delete_account_message)) },
            confirmButton = {
                TextButton(
                    onClick = {
                        onDeleteAccount()
                        onShowDeleteDialogChange(false)
                    },
                    colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)
                ) {
                    Text(text = stringResource(R.string.confirm_delete))
                }
            },
            dismissButton = {
                TextButton(onClick = { onShowDeleteDialogChange(false) }) {
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

@Preview(showBackground = true)
@Composable
fun SettingsContentPreview() {
    SkillflowTheme {
        SettingsContent(
            state = SettingsState(name = "User", email = "user@example.com"),
            snackbarHostState = SnackbarHostState(),
            showDeleteDialog = false,
            onNavigateBack = {},
            onUpdateName = {},
            onUpdateEmail = {},
            onSetLanguage = {},
            onNavigateToPrivacy = {},
            onLogout = {},
            onDeleteAccount = {},
            onShowDeleteDialogChange = {}
        )
    }
}
