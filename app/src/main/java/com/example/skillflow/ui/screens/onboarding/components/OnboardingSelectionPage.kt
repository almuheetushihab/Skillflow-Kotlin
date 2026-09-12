package com.example.skillflow.ui.screens.onboarding.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import com.example.skillflow.R
import com.example.skillflow.ui.screens.onboarding.OnboardingState
import com.example.skillflow.ui.theme.spacing

@Composable
fun OnboardingSelectionPage(
    state: OnboardingState,
    onCareerPathSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val spacing = MaterialTheme.spacing
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(spacing.large),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(spacing.large))
        
        Text(
            text = stringResource(R.string.onboarding_goal_title),
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.align(Alignment.Start)
        )
        
        Spacer(modifier = Modifier.height(spacing.small))
        
        Text(
            text = stringResource(R.string.onboarding_goal_desc),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.align(Alignment.Start)
        )

        Spacer(modifier = Modifier.height(spacing.large))

        if (state.isLoading) {
            Column(verticalArrangement = Arrangement.spacedBy(spacing.medium)) {
                repeat(4) {
                    CareerPathSkeleton()
                }
            }
        } else if (state.careerPaths.isEmpty()) {
            Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                Text(
                    text = stringResource(R.string.loading_career_paths), 
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(spacing.medium)
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
                modifier = Modifier.padding(vertical = spacing.small)
            )
        }
    }
}
