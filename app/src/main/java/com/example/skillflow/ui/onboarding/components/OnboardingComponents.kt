package com.example.skillflow.ui.onboarding.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.skillflow.R
import com.example.skillflow.presentation.onboarding.OnboardingState
import com.example.skillflow.ui.theme.GradientStart
import com.example.skillflow.ui.theme.SkillflowTheme
import com.example.skillflow.ui.theme.spacing

@Composable
fun OnboardingInfoPage(
    title: String,
    description: String,
    modifier: Modifier = Modifier
) {
    val spacing = MaterialTheme.spacing
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(spacing.extraLarge),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(200.dp)
                .clip(CircleShape)
                .background(GradientStart.copy(alpha = 0.1f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Info,
                contentDescription = null,
                modifier = Modifier.size(100.dp),
                tint = GradientStart
            )
        }
        
        Spacer(modifier = Modifier.height(spacing.extraLarge))
        
        Text(
            text = title,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onBackground
        )
        
        Spacer(modifier = Modifier.height(spacing.medium))
        
        Text(
            text = description,
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

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

@Composable
fun CareerPathItem(
    path: com.example.skillflow.domain.model.CareerPath,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val spacing = MaterialTheme.spacing
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(spacing.medium))
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(spacing.medium),
        border = androidx.compose.foundation.BorderStroke(
            1.dp, 
            if (isSelected) GradientStart else MaterialTheme.colorScheme.outlineVariant
        ),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.2f) else MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier.padding(spacing.medium),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                modifier = Modifier.size(48.dp),
                shape = CircleShape,
                color = if (isSelected) GradientStart else MaterialTheme.colorScheme.surfaceVariant
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = path.name.take(1),
                        style = MaterialTheme.typography.titleLarge,
                        color = if (isSelected) Color.White else MaterialTheme.colorScheme.primary
                    )
                }
            }
            
            Spacer(modifier = Modifier.width(spacing.medium))
            
            Column {
                Text(
                    text = path.name, 
                    style = MaterialTheme.typography.titleMedium, 
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = path.description, 
                    style = MaterialTheme.typography.bodySmall, 
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun OnboardingBottomBar(
    pagerState: PagerState,
    onNext: () -> Unit,
    onSkip: () -> Unit,
    isSelectionComplete: Boolean,
    modifier: Modifier = Modifier
) {
    val spacing = MaterialTheme.spacing
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(spacing.large),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        TextButton(onClick = onSkip) {
            Text(
                text = stringResource(R.string.skip),
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Row(horizontalArrangement = Arrangement.spacedBy(spacing.small)) {
            repeat(pagerState.pageCount) { index ->
                Box(
                    modifier = Modifier
                        .size(if (pagerState.currentPage == index) 12.dp else 8.dp)
                        .clip(CircleShape)
                        .background(
                            if (pagerState.currentPage == index) GradientStart else Color.LightGray
                        )
                )
            }
        }

        val canProceed = pagerState.currentPage != 2 || isSelectionComplete
        
        Button(
            onClick = onNext,
            enabled = canProceed,
            shape = RoundedCornerShape(24.dp),
            colors = ButtonDefaults.buttonColors(containerColor = GradientStart)
        ) {
            Text(
                text = if (pagerState.currentPage == pagerState.pageCount - 1) 
                    stringResource(R.string.get_started) else stringResource(R.string.next),
                color = Color.White
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun OnboardingInfoPagePreview() {
    SkillflowTheme {
        OnboardingInfoPage(title = "Welcome", description = "Learn something new today.")
    }
}

@Preview(showBackground = true)
@Composable
fun CareerPathItemPreview() {
    SkillflowTheme {
        Column(modifier = Modifier.padding(16.dp)) {
            CareerPathItem(
                path = com.example.skillflow.domain.model.CareerPath("1", "Android", "Learn Android", ""),
                isSelected = true,
                onClick = {}
            )
        }
    }
}
