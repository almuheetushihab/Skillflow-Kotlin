package com.example.skillflow.ui.home.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.skillflow.R
import com.example.skillflow.ui.theme.GradientEnd
import com.example.skillflow.ui.theme.GradientStart
import com.example.skillflow.ui.theme.SkillflowTheme
import com.example.skillflow.ui.theme.spacing

@Composable
fun DailyProgressCard(
    completedCount: Int,
    totalCount: Int,
    modifier: Modifier = Modifier
) {
    val spacing = MaterialTheme.spacing
    val progressPercentage = if (totalCount > 0) (completedCount.toFloat() / totalCount) else 0f
    val progress = animateFloatAsState(
        targetValue = progressPercentage,
        animationSpec = tween(1200, easing = FastOutSlowInEasing),
        label = "DailyProgress"
    )

    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(180.dp),
        shape = RoundedCornerShape(spacing.extraLarge),
        elevation = CardDefaults.cardElevation(defaultElevation = 12.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Brush.verticalGradient(listOf(GradientStart, GradientEnd)))
        ) {
            // Decorative background elements
            Box(
                modifier = Modifier
                    .size(160.dp)
                    .offset(x = (-50).dp, y = (-50).dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.07f))
            )
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .align(Alignment.BottomEnd)
                    .offset(x = 20.dp, y = 20.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.12f))
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(spacing.large),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Column {
                        Text(
                            text = stringResource(R.string.learning_progress),
                            style = MaterialTheme.typography.headlineSmall,
                            color = Color.White,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 0.5.sp
                        )
                        Text(
                            text = stringResource(R.string.keep_momentum),
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.White.copy(alpha = 0.8f)
                        )
                    }
                    
                    Surface(
                        color = Color.White.copy(alpha = 0.25f),
                        shape = RoundedCornerShape(spacing.medium)
                    ) {
                        Text(
                            text = "${(progress.value * 100).toInt()}%",
                            modifier = Modifier.padding(horizontal = spacing.small, vertical = spacing.extraSmall),
                            style = MaterialTheme.typography.labelLarge,
                            color = Color.White,
                            fontWeight = FontWeight.ExtraBold
                        )
                    }
                }

                Column {
                    LinearProgressIndicator(
                        progress = { progress.value },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(spacing.small)
                            .clip(CircleShape),
                        color = Color.White,
                        trackColor = Color.White.copy(alpha = 0.2f),
                        strokeCap = StrokeCap.Round
                    )
                    
                    Spacer(modifier = Modifier.height(spacing.medium))
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "$completedCount",
                                style = MaterialTheme.typography.titleLarge,
                                color = Color.White,
                                fontWeight = FontWeight.Black
                            )
                            Text(
                                text = " / $totalCount ${stringResource(R.string.mastered).lowercase()}",
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color.White.copy(alpha = 0.7f),
                                modifier = Modifier.padding(start = spacing.extraSmall)
                            )
                        }
                        
                        Text(
                            text = if (progress.value >= 1f) 
                                stringResource(R.string.achieved_emoji) 
                            else 
                                "${stringResource(R.string.goal)}: $totalCount",
                            style = MaterialTheme.typography.labelMedium,
                            color = Color.White.copy(alpha = 0.9f),
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

@Preview
@Composable
fun DailyProgressCardPreview() {
    SkillflowTheme {
        Column(modifier = Modifier.padding(16.dp)) {
            DailyProgressCard(completedCount = 5, totalCount = 10)
        }
    }
}
