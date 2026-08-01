package com.example.skillflow.ui.onboarding

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.skillflow.R
import com.example.skillflow.domain.model.CareerPath
import com.example.skillflow.presentation.onboarding.OnboardingState
import com.example.skillflow.presentation.onboarding.OnboardingViewModel
import com.example.skillflow.ui.common.LoadingView
import com.example.skillflow.ui.onboarding.components.CareerPathItem
import com.example.skillflow.ui.theme.GradientEnd
import com.example.skillflow.ui.theme.GradientStart
import com.example.skillflow.ui.theme.SkillflowTheme
import com.example.skillflow.ui.theme.spacing

@Composable
fun OnboardingScreen(
    onNavigateToHome: () -> Unit,
    viewModel: OnboardingViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()

    if (state.isOnboardingCompleted) {
        LaunchedEffect(Unit) {
            onNavigateToHome()
        }
    }

    OnboardingContent(
        state = state,
        onCareerPathSelected = viewModel::selectCareerPath,
        onCompleteOnboarding = viewModel::completeOnboarding
    )
}

@Composable
fun OnboardingContent(
    state: OnboardingState,
    onCareerPathSelected: (String) -> Unit,
    onCompleteOnboarding: () -> Unit
) {
    val backgroundGradient = Brush.verticalGradient(
        listOf(GradientStart.copy(alpha = 0.05f), MaterialTheme.colorScheme.background)
    )

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Box(modifier = Modifier.fillMaxSize().background(backgroundGradient)) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(MaterialTheme.spacing.large),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(MaterialTheme.spacing.extraExtraLarge))
                
                Text(
                    text = stringResource(R.string.app_name),
                    style = MaterialTheme.typography.displayLarge,
                    color = GradientStart,
                    fontWeight = FontWeight.ExtraBold
                )
                
                Spacer(modifier = Modifier.height(MaterialTheme.spacing.small))
                
                Text(
                    text = stringResource(R.string.micro_learning_subtitle),
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(48.dp))

                Text(
                    text = stringResource(R.string.select_career_goal),
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.align(Alignment.Start)
                )
                
                Spacer(modifier = Modifier.height(MaterialTheme.spacing.medium))

                if (state.isLoading) {
                    LoadingView(modifier = Modifier.weight(1f))
                } else {
                    LazyColumn(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.medium)
                    ) {
                        itemsIndexed(state.careerPaths) { _, path ->
                            CareerPathItem(
                                path = path,
                                isSelected = state.selectedCareerPathId == path.id,
                                onClick = { onCareerPathSelected(path.id) }
                            )
                        }
                    }
                }

                if (state.error != null) {
                    Text(
                        text = state.error,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(vertical = MaterialTheme.spacing.small)
                    )
                }

                Spacer(modifier = Modifier.height(MaterialTheme.spacing.large))
                
                Button(
                    onClick = onCompleteOnboarding,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .clip(RoundedCornerShape(28.dp))
                        .background(Brush.linearGradient(listOf(GradientStart, GradientEnd))),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent)
                ) {
                    Text(
                        text = stringResource(R.string.begin_journey),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
                Spacer(modifier = Modifier.height(MaterialTheme.spacing.medium))
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun OnboardingContentPreview() {
    SkillflowTheme {
        OnboardingContent(
            state = OnboardingState(
                careerPaths = listOf(
                    CareerPath("1", "Android Developer", "Build amazing mobile apps", ""),
                    CareerPath("2", "Backend Engineer", "Design scalable systems", "")
                ),
                selectedCareerPathId = "1"
            ),
            onCareerPathSelected = {},
            onCompleteOnboarding = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun OnboardingContentLoadingPreview() {
    SkillflowTheme {
        OnboardingContent(
            state = OnboardingState(isLoading = true),
            onCareerPathSelected = {},
            onCompleteOnboarding = {}
        )
    }
}
