package com.example.skillflow.ui.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
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
    modifier: Modifier = Modifier,
    viewModel: AuthViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isPasswordVisible by remember { mutableStateOf(false) }

    if (state.isLoggedIn) {
        LaunchedEffect(Unit) {
            onSignUpSuccess()
        }
    }

    SignUpContent(
        state = state,
        name = name,
        email = email,
        phone = phone,
        password = password,
        isPasswordVisible = isPasswordVisible,
        onNameChange = { name = it },
        onEmailChange = { email = it },
        onPhoneChange = { phone = it },
        onPasswordChange = { password = it },
        onTogglePasswordVisibility = { isPasswordVisible = !isPasswordVisible },
        onSignUpClick = { viewModel.signUp(name, email, phone, password) },
        onNavigateToLogin = onNavigateToLogin,
        modifier = modifier
    )
}

@Composable
fun SignUpContent(
    state: AuthState,
    name: String,
    email: String,
    phone: String,
    password: String,
    isPasswordVisible: Boolean,
    onNameChange: (String) -> Unit,
    onEmailChange: (String) -> Unit,
    onPhoneChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onTogglePasswordVisibility: () -> Unit,
    onSignUpClick: () -> Unit,
    onNavigateToLogin: () -> Unit,
    modifier: Modifier = Modifier
) {
    val backgroundGradient = Brush.verticalGradient(
        listOf(GradientStart.copy(alpha = 0.1f), MaterialTheme.colorScheme.background)
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(backgroundGradient)
            .windowInsetsPadding(WindowInsets.safeDrawing)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(MaterialTheme.spacing.large)
                .verticalScroll(rememberScrollState()),
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
                label = stringResource(R.string.name),
                error = state.nameError
            )
            Spacer(modifier = Modifier.height(MaterialTheme.spacing.medium))

            AuthTextField(
                value = email,
                onValueChange = onEmailChange,
                label = stringResource(R.string.email),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                error = state.emailError
            )
            Spacer(modifier = Modifier.height(MaterialTheme.spacing.medium))

            AuthTextField(
                value = phone,
                onValueChange = onPhoneChange,
                label = stringResource(R.string.phone_number),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                error = state.phoneError
            )
            Spacer(modifier = Modifier.height(MaterialTheme.spacing.medium))

            AuthTextField(
                value = password,
                onValueChange = onPasswordChange,
                label = stringResource(R.string.password),
                visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                error = state.passwordError,
                trailingIcon = {
                    IconButton(onClick = onTogglePasswordVisibility) {
                        Icon(
                            imageVector = if (isPasswordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                            contentDescription = stringResource(if (isPasswordVisible) R.string.hide_password else R.string.show_password),
                            tint = GradientStart
                        )
                    }
                }
            )

            Spacer(modifier = Modifier.height(MaterialTheme.spacing.extraLarge))

            AuthButton(
                text = stringResource(R.string.signup),
                onClick = onSignUpClick,
                isLoading = state.isLoading
            )

            Spacer(modifier = Modifier.height(MaterialTheme.spacing.large))

            Text(
                text = stringResource(R.string.already_have_account),
                modifier = Modifier.clickable(onClick = onNavigateToLogin),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.bodyLarge
            )

            if (state.error != null) {
                Spacer(modifier = Modifier.height(MaterialTheme.spacing.medium))
                Surface(
                    color = MaterialTheme.colorScheme.errorContainer,
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = state.error,
                        color = MaterialTheme.colorScheme.onErrorContainer,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    )
                }
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
            phone = "",
            password = "",
            isPasswordVisible = false,
            onNameChange = {},
            onEmailChange = {},
            onPhoneChange = {},
            onPasswordChange = {},
            onTogglePasswordVisibility = {},
            onSignUpClick = {},
            onNavigateToLogin = {}
        )
    }
}
