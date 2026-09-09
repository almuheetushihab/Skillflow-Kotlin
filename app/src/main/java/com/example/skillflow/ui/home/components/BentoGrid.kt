package com.example.skillflow.ui.home.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.TrendingUp
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
import com.example.skillflow.ui.theme.SunsetEnd

/**
 * Clean, modern Bento Grid layout for the Home Screen header section.
 * Balanced grid height, soft grey/light backgrounds, 24dp rounded corners, and clear visual hierarchy.
 */
@Composable
fun BentoGrid(
    completedCount: Int,
    totalCount: Int,
    streakCount: Int,
    savedCount: Int,
    onSavedTileClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(204.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Large Tile: Daily Progress (Asymmetrical - 1.3f weight)
        BentoProgressTile(
            completedCount = completedCount,
            totalCount = totalCount,
            modifier = Modifier
                .weight(1.3f)
                .fillMaxHeight()
        )

        // Column with 2 Smaller Stacked Tiles (0.9f weight, height exactly equals large progress card)
        Column(
            modifier = Modifier
                .weight(0.9f)
                .fillMaxHeight(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Smaller Tile 1: Streak Counter
            BentoStreakTile(
                streakCount = streakCount,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            )

            // Smaller Tile 2: Saved Items / Bookmarks
            BentoSavedTile(
                savedCount = savedCount,
                onClick = onSavedTileClick,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            )
        }
    }
}

/**
 * Large Bento Tile displaying learning progress with vibrant blue gradient and 24dp rounded corners.
 */
@Composable
fun BentoProgressTile(
    completedCount: Int,
    totalCount: Int,
    modifier: Modifier = Modifier
) {
    val progressPercentage = if (totalCount > 0) (completedCount.toFloat() / totalCount) else 0f
    val progress = animateFloatAsState(
        targetValue = progressPercentage,
        animationSpec = tween(1200, easing = FastOutSlowInEasing),
        label = "BentoProgress"
    )

    Card(
        modifier = modifier,
        shape = RoundedCornerShape(24.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.linearGradient(
                        colors = listOf(GradientStart, GradientEnd)
                    )
                )
        ) {
            // Decorative background translucent shapes
            Box(
                modifier = Modifier
                    .size(130.dp)
                    .offset(x = (-40).dp, y = (-40).dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.08f))
            )
            Box(
                modifier = Modifier
                    .size(90.dp)
                    .align(Alignment.BottomEnd)
                    .offset(x = 25.dp, y = 25.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.12f))
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        color = Color.White.copy(alpha = 0.2f),
                        shape = CircleShape
                    ) {
                        Icon(
                            imageVector = Icons.Default.TrendingUp,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier
                                .padding(6.dp)
                                .size(18.dp)
                        )
                    }

                    Surface(
                        color = Color.White.copy(alpha = 0.25f),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = "${(progress.value * 100).toInt()}%",
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                            style = MaterialTheme.typography.labelMedium,
                            color = Color.White,
                            fontWeight = FontWeight.ExtraBold
                        )
                    }
                }

                // Title & Stats
                Column {
                    Text(
                        text = stringResource(R.string.learning_progress),
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.White,
                        fontWeight = FontWeight.Black,
                        fontSize = 16.sp,
                        letterSpacing = 0.3.sp
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "$completedCount / $totalCount ${stringResource(R.string.mastered).lowercase()}",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White.copy(alpha = 0.85f),
                        fontWeight = FontWeight.Medium
                    )
                }

                // Progress Bar
                Column {
                    LinearProgressIndicator(
                        progress = { progress.value },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .clip(CircleShape),
                        color = Color.White,
                        trackColor = Color.White.copy(alpha = 0.25f),
                        strokeCap = StrokeCap.Round
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = if (progress.value >= 1f)
                            stringResource(R.string.achieved_emoji)
                        else
                            stringResource(R.string.keep_momentum),
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.White.copy(alpha = 0.9f),
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp
                    )
                }
            }
        }
    }
}

/**
 * Smaller Bento Tile for Streak with soft grey background, subtle border, and top-right flame icon.
 */
@Composable
fun BentoStreakTile(
    streakCount: Int,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(24.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.35f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // Accent Flame Icon in Top-Right Corner
            Surface(
                color = SunsetEnd.copy(alpha = 0.12f),
                shape = CircleShape,
                modifier = Modifier.align(Alignment.TopEnd)
            ) {
                Icon(
                    imageVector = Icons.Default.LocalFireDepartment,
                    contentDescription = stringResource(R.string.streak),
                    tint = SunsetEnd,
                    modifier = Modifier
                        .padding(6.dp)
                        .size(18.dp)
                )
            }

            // Bold Number & Subtitle
            Column(
                modifier = Modifier.align(Alignment.CenterStart)
            ) {
                Text(
                    text = streakCount.toString(),
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = stringResource(R.string.streak),
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

/**
 * Smaller Bento Tile for Saved items with soft grey background, subtle border, and top-right bookmark icon.
 */
@Composable
fun BentoSavedTile(
    savedCount: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.clickable { onClick() },
        shape = RoundedCornerShape(24.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.35f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // Accent Bookmark Icon in Top-Right Corner
            Surface(
                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f),
                shape = CircleShape,
                modifier = Modifier.align(Alignment.TopEnd)
            ) {
                Icon(
                    imageVector = Icons.Default.Bookmark,
                    contentDescription = stringResource(R.string.saved_nuggets),
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier
                        .padding(6.dp)
                        .size(18.dp)
                )
            }

            // Bold Number & Subtitle
            Column(
                modifier = Modifier.align(Alignment.CenterStart)
            ) {
                Text(
                    text = savedCount.toString(),
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = stringResource(R.string.nav_saved),
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun BentoGridPreview() {
    SkillflowTheme {
        Box(modifier = Modifier.padding(16.dp)) {
            BentoGrid(
                completedCount = 8,
                totalCount = 12,
                streakCount = 5,
                savedCount = 14,
                onSavedTileClick = {}
            )
        }
    }
}
