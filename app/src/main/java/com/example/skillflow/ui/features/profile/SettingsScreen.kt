package com.example.skillflow.ui.features.profile

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.skillflow.R
import com.example.skillflow.ui.common.AuthButton
import com.example.skillflow.ui.common.AuthTextField
import com.example.skillflow.ui.common.SkillflowTopAppBar
import com.example.skillflow.ui.features.profile.components.LanguageToggleButton
import com.example.skillflow.ui.features.profile.components.SettingsItem
import com.example.skillflow.ui.theme.spacing
import kotlinx.coroutines.flow.collectLatest
import java.util.Locale

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
        onSetNotificationEnabled = viewModel::setNotificationEnabled,
        onSetReminderTime = viewModel::setReminderTime,
        onNavigateToPrivacy = onNavigateToPrivacy,
        onLogout = viewModel::logout,
        onDeleteAccount = viewModel::deleteAccount,
        onShowDeleteDialogChange = { showDeleteDialog = it },
        modifier = modifier
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsContent(
    state: SettingsState,
    snackbarHostState: SnackbarHostState,
    showDeleteDialog: Boolean,
    onNavigateBack: () -> Unit,
    onUpdateName: (String) -> Unit,
    onUpdateEmail: (String) -> Unit,
    onSetLanguage: (String) -> Unit,
    onSetNotificationEnabled: (Boolean) -> Unit,
    onSetReminderTime: (Long) -> Unit,
    onNavigateToPrivacy: () -> Unit,
    onLogout: () -> Unit,
    onDeleteAccount: () -> Unit,
    onShowDeleteDialogChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    val spacing = MaterialTheme.spacing
    val context = LocalContext.current
    var showTimePicker by remember { mutableStateOf(false) }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            onSetNotificationEnabled(true)
        } else {
            onSetNotificationEnabled(false)
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
                text = stringResource(R.string.daily_reminders),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(spacing.medium))

            SettingsItem(
                title = stringResource(R.string.daily_reminders),
                icon = Icons.Default.Notifications,
                trailing = {
                    Switch(
                        checked = state.isNotificationEnabled,
                        onCheckedChange = { checked ->
                            if (checked) {
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                    val hasPermission = ContextCompat.checkSelfPermission(
                                        context,
                                        Manifest.permission.POST_NOTIFICATIONS
                                    ) == PackageManager.PERMISSION_GRANTED

                                    if (hasPermission) {
                                        onSetNotificationEnabled(true)
                                    } else {
                                        permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                                    }
                                } else {
                                    onSetNotificationEnabled(true)
                                }
                            } else {
                                onSetNotificationEnabled(false)
                            }
                        }
                    )
                }
            )

            if (state.isNotificationEnabled) {
                SettingsItem(
                    title = stringResource(R.string.reminder_time),
                    subtitle = formatReminderTime(state.reminderTime),
                    icon = Icons.Default.Schedule,
                    onClick = { showTimePicker = true }
                )
            }

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
                text = stringResource(R.string.app_version, "1.0"),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = stringResource(R.string.developer_info),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            TextButton(
                onClick = { },
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

    if (showTimePicker) {
        val initialHours = (state.reminderTime / (1000 * 60 * 60)).toInt().coerceIn(0, 23)
        val initialMinutes = ((state.reminderTime / (1000 * 60)) % 60).toInt().coerceIn(0, 59)

        val timePickerState = rememberTimePickerState(
            initialHour = initialHours,
            initialMinute = initialMinutes,
            is24Hour = false
        )

        AlertDialog(
            onDismissRequest = { showTimePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        val newTimeMillis = (timePickerState.hour * 60 + timePickerState.minute) * 60 * 1000L
                        onSetReminderTime(newTimeMillis)
                        showTimePicker = false
                    }
                ) {
                    Text(text = stringResource(R.string.save))
                }
            },
            dismissButton = {
                TextButton(onClick = { showTimePicker = false }) {
                    Text(text = stringResource(R.string.cancel))
                }
            },
            text = {
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    TimePicker(state = timePickerState)
                }
            }
        )
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

private fun formatReminderTime(timeInMillisFromMidnight: Long): String {
    val hours = (timeInMillisFromMidnight / (1000 * 60 * 60)).toInt().coerceIn(0, 23)
    val minutes = ((timeInMillisFromMidnight / (1000 * 60)) % 60).toInt().coerceIn(0, 59)
    val amPm = if (hours >= 12) "PM" else "AM"
    val hour12 = if (hours % 12 == 0) 12 else hours % 12
    return String.format(Locale.getDefault(), "%02d:%02d %s", hour12, minutes, amPm)
}
