package com.example.skillflow.ui.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.skillflow.R
import com.example.skillflow.presentation.auth.AuthState
import com.example.skillflow.presentation.auth.AuthViewModel
import com.example.skillflow.ui.common.AuthButton
import com.example.skillflow.ui.common.AuthTextField
import com.example.skillflow.ui.theme.GradientStart
import com.example.skillflow.ui.theme.SkillflowTheme
import com.example.skillflow.ui.theme.spacing

@Composable
fun SignUpScreen(
    onNavigateToLogin: () -> Unit,
    onSignUpSuccess: () -> Unit,
    viewModel: AuthViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    if (state.isLoggedIn) {
        LaunchedEffect(Unit) {
            onSignUpSuccess()
        }
    }

    SignUpContent(
        state = state,
        name = name,
        email = email,
        password = password,
        onNameChange = { name = it },
        onEmailChange = { email = it },
        onPasswordChange = { password = it },
        onSignUpClick = { viewModel.signUp(name, email, password) },
        onNavigateToLogin = onNavigateToLogin
    )
}

@Composable
fun SignUpContent(
    state: AuthState,
    name: String,
    email: String,
    password: String,
    onNameChange: (String) -> Unit,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onSignUpClick: () -> Unit,
    onNavigateToLogin: () -> Unit
) {
    val backgroundGradient = Brush.verticalGradient(
        listOf(GradientStart.copy(alpha = 0.1f), MaterialTheme.colorScheme.background)
    )

    Box(modifier = Modifier.fillMaxSize().background(backgroundGradient)) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(MaterialTheme.spacing.large),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = stringResource(R.string.create_account),
                style = MaterialTheme.typography.displayLarge,
                color = GradientStart,
                fontWeight = FontWeight.ExtraBold
            )
            Spacer(modifier = Modifier.height(MaterialTheme.spacing.extraLarge))

            AuthTextField(
                value = name,
                onValueChange = onNameChange,
                label = stringResource(R.string.name)
            )
            Spacer(modifier = Modifier.height(MaterialTheme.spacing.medium))

            AuthTextField(
                value = email,
                onValueChange = onEmailChange,
                label = stringResource(R.string.email),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
            )
            Spacer(modifier = Modifier.height(MaterialTheme.spacing.medium))

            AuthTextField(
                value = password,
                onValueChange = onPasswordChange,
                label = stringResource(R.string.password),
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
            )

            Spacer(modifier = Modifier.height(MaterialTheme.spacing.extraLarge))

            AuthButton(
                text = stringResource(R.string.signup),
                onClick = onSignUpClick,
                isLoading = state.isLoading
            )

            Spacer(modifier = Modifier.height(MaterialTheme.spacing.medium))

            Text(
                text = stringResource(R.string.already_have_account),
                modifier = Modifier.clickable(onClick = onNavigateToLogin),
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            if (state.error != null) {
                Spacer(modifier = Modifier.height(MaterialTheme.spacing.medium))
                Text(
                    text = state.error,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SignUpContentPreview() {
    SkillflowTheme {
        SignUpContent(
            state = AuthState(),
            name = "",
            email = "",
            password = "",
            onNameChange = {},
            onEmailChange = {},
            onPasswordChange = {},
            onSignUpClick = {},
            onNavigateToLogin = {}
        )
    }
}
