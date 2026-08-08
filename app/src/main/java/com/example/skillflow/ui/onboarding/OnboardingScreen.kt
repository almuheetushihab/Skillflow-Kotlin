package com.example.skillflow.ui.onboarding

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.skillflow.R
import com.example.skillflow.domain.model.CareerPath
import com.example.skillflow.presentation.onboarding.OnboardingState
import com.example.skillflow.presentation.onboarding.OnboardingViewModel
import com.example.skillflow.ui.onboarding.components.*
import com.example.skillflow.ui.theme.GradientStart
import com.example.skillflow.ui.theme.SkillflowTheme
import kotlinx.coroutines.launch

/**
 * Screen that guides new users through the app's value proposition and career selection.
 */
@Composable
fun OnboardingScreen(
    onNavigateToHome: () -> Unit,
    modifier: Modifier = Modifier,
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
        onCompleteOnboarding = viewModel::completeOnboarding,
        modifier = modifier
    )
}

/**
 * The internal content of the Onboarding screen using a [HorizontalPager].
 */
@Composable
fun OnboardingContent(
    state: OnboardingState,
    onCareerPathSelected: (String) -> Unit,
    onCompleteOnboarding: () -> Unit,
    modifier: Modifier = Modifier
) {
    val pagerState = rememberPagerState(pageCount = { 4 })
    val scope = rememberCoroutineScope()
    
    val backgroundGradient = Brush.verticalGradient(
        listOf(GradientStart.copy(alpha = 0.05f), MaterialTheme.colorScheme.background)
    )

    Surface(
        modifier = modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Box(modifier = Modifier.fillMaxSize().background(backgroundGradient)) {
            Column(modifier = Modifier.fillMaxSize()) {
                HorizontalPager(
                    state = pagerState,
                    modifier = Modifier.weight(1f),
                    userScrollEnabled = true
                ) { page ->
                    when (page) {
                        0 -> OnboardingInfoPage(
                            title = stringResource(R.string.onboarding_welcome_title),
                            description = stringResource(R.string.onboarding_welcome_desc)
                        )
                        1 -> OnboardingInfoPage(
                            title = stringResource(R.string.onboarding_micro_title),
                            description = stringResource(R.string.onboarding_micro_desc)
                        )
                        2 -> OnboardingSelectionPage(
                            state = state,
                            onCareerPathSelected = onCareerPathSelected
                        )
                        3 -> OnboardingInfoPage(
                            title = stringResource(R.string.onboarding_ready_title),
                            description = stringResource(R.string.onboarding_ready_desc)
                        )
                    }
                }

                OnboardingBottomBar(
                    pagerState = pagerState,
                    onNext = {
                        if (pagerState.currentPage < 3) {
                            scope.launch { pagerState.animateScrollToPage(pagerState.currentPage + 1) }
                        } else {
                            onCompleteOnboarding()
                        }
                    },
                    onSkip = onCompleteOnboarding,
                    isSelectionComplete = state.selectedCareerPathId != null
                )
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
