package com.example.skillflow.ui.profile

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.BiasAlignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.skillflow.R
import com.example.skillflow.presentation.profile.SettingsState
import com.example.skillflow.presentation.profile.SettingsViewModel
import com.example.skillflow.ui.common.AuthButton
import com.example.skillflow.ui.common.AuthTextField
import com.example.skillflow.ui.common.SkillflowTopAppBar
import com.example.skillflow.ui.theme.GradientEnd
import com.example.skillflow.ui.theme.GradientStart
import com.example.skillflow.ui.theme.spacing

@Composable
fun SettingsScreen(
    onNavigateBack: () -> Unit,
    onLogout: () -> Unit,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()

    Scaffold(
        topBar = {
            SkillflowTopAppBar(
                title = stringResource(R.string.settings),
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
            Text(
                text = stringResource(R.string.account_details),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(MaterialTheme.spacing.medium))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Language, contentDescription = null, tint = GradientStart)
                    Spacer(modifier = Modifier.width(MaterialTheme.spacing.medium))
                    Text(
                        text = stringResource(R.string.language_toggle, if (state.language == "bn") stringResource(R.string.bengali) else stringResource(R.string.english)),
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.SemiBold
                    )
                }
                
                LanguageToggleButton(
                    currentLanguage = state.language,
                    onToggle = { 
                        val nextLang = if (state.language == "bn") "en" else "bn"
                        viewModel.setLanguage(nextLang)
                    }
                )
            }

            Spacer(modifier = Modifier.weight(1f))
            
            AuthButton(
                text = stringResource(R.string.logout),
                onClick = {
                    viewModel.logout()
                    onLogout()
                },
                modifier = Modifier.padding(vertical = MaterialTheme.spacing.large)
            )
        }
    }
}

@Composable
fun LanguageToggleButton(
    currentLanguage: String,
    onToggle: () -> Unit
) {
    val horizontalBias by animateFloatAsState(
        targetValue = if (currentLanguage == "en") -1f else 1f,
        label = "LanguageThumbBias"
    )

    Surface(
        onClick = onToggle,
        shape = RoundedCornerShape(24.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
        modifier = Modifier
            .height(40.dp)
            .width(100.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(4.dp),
            contentAlignment = Alignment.Center
        ) {
            Row(
                modifier = Modifier.fillMaxSize(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                Text(
                    text = "EN",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                )
                Text(
                    text = "BN",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                )
            }

            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .width(46.dp)
                    .align(BiasAlignment(horizontalBias, 0f))
                    .clip(RoundedCornerShape(20.dp))
                    .background(
                        Brush.linearGradient(listOf(GradientStart, GradientEnd))
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (currentLanguage == "en") "EN" else "BN",
                    color = Color.White,
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.ExtraBold
                )
            }
        }
    }
}
