package com.example.skillflow.screens.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import com.example.skillflow.R
import com.example.skillflow.screens.commonComponents.AuthButton
import com.example.skillflow.screens.commonComponents.AuthTextField
import com.example.skillflow.screens.theme.GradientStart
import com.example.skillflow.screens.theme.SkillflowTheme
import com.example.skillflow.screens.theme.spacing

@Composable
fun ForgotPasswordScreen(
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    var email by remember { mutableStateOf("") }
    var isSubmitted by remember { mutableStateOf(false) }

    ForgotPasswordContent(
        email = email,
        isSubmitted = isSubmitted,
        onEmailChange = { email = it },
        onSubmit = { isSubmitted = true },
        onNavigateBack = onNavigateBack,
        modifier = modifier
    )
}

@Composable
fun ForgotPasswordContent(
    email: String,
    isSubmitted: Boolean,
    onEmailChange: (String) -> Unit,
    onSubmit: () -> Unit,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val spacing = MaterialTheme.spacing
    val backgroundGradient = Brush.verticalGradient(
        listOf(GradientStart.copy(alpha = 0.1f), MaterialTheme.colorScheme.background)
    )

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = Color.Transparent
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(backgroundGradient)
                .padding(padding)
                .windowInsetsPadding(WindowInsets.safeDrawing)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(spacing.large)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = stringResource(R.string.reset_password),
                    style = MaterialTheme.typography.headlineLarge,
                    color = GradientStart,
                    fontWeight = FontWeight.ExtraBold
                )
                Spacer(modifier = Modifier.height(spacing.medium))
                Text(
                    text = stringResource(R.string.reset_password_hint),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(spacing.extraLarge + spacing.small))

                if (!isSubmitted) {
                    AuthTextField(
                        value = email,
                        onValueChange = onEmailChange,
                        label = stringResource(R.string.email),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
                    )
                    Spacer(modifier = Modifier.height(spacing.extraLarge + spacing.small))

                    AuthButton(
                        text = stringResource(R.string.send_reset_link),
                        onClick = onSubmit,
                        enabled = email.isNotEmpty()
                    )
                } else {
                    Text(
                        text = stringResource(R.string.reset_link_sent, email),
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(spacing.extraLarge + spacing.small))
                    AuthButton(
                        text = stringResource(R.string.back_to_login),
                        onClick = onNavigateBack
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ForgotPasswordContentPreview() {
    SkillflowTheme {
        ForgotPasswordContent(
            email = "user@example.com",
            isSubmitted = false,
            onEmailChange = {},
            onSubmit = {},
            onNavigateBack = {}
        )
    }
}
