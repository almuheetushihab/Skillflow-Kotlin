package com.example.skillflow.ui.common

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Stars
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.skillflow.domain.model.KnowledgeNugget
import com.example.skillflow.ui.theme.*

@Composable
fun NuggetCard(
    nugget: KnowledgeNugget,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val masteredColor = GradientStart
    val readColor = MaterialTheme.colorScheme.secondary
    val spacing = MaterialTheme.spacing
    
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(MaterialTheme.spacing.large))
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(MaterialTheme.spacing.large),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
        ),
        border = BorderStroke(
            width = if (nugget.isMastered) 2.dp else 1.dp,
            color = when {
                nugget.isMastered -> masteredColor.copy(alpha = 0.5f)
                nugget.isDone -> readColor.copy(alpha = 0.3f)
                else -> MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)
            }
        )
    ) {
        Row(
            modifier = Modifier
                .padding(spacing.medium)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Left Icon Status Indicator
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(RoundedCornerShape(spacing.medium))
                    .background(
                        when {
                            nugget.isMastered -> masteredColor.copy(alpha = 0.15f)
                            nugget.isDone -> readColor.copy(alpha = 0.1f)
                            else -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f)
                        }
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = when {
                        nugget.isMastered -> Icons.Default.Stars
                        nugget.isDone -> Icons.Default.Check
                        else -> Icons.Default.MenuBook
                    },
                    contentDescription = null,
                    tint = when {
                        nugget.isMastered -> masteredColor
                        nugget.isDone -> readColor
                        else -> MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                    },
                    modifier = Modifier.size(spacing.extraLarge)
                )
                
                if (nugget.isSaved) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(spacing.extraSmall)
                            .size(spacing.small)
                            .clip(CircleShape)
                            .background(masteredColor)
                    )
                }
            }
            
            Spacer(modifier = Modifier.width(spacing.medium))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = nugget.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                
                Spacer(modifier = Modifier.height(spacing.extraSmall))
                
                Row(verticalAlignment = Alignment.CenterVertically) {
                    ComplexityBadge(complexity = nugget.complexity)
                    
                    Spacer(modifier = Modifier.width(spacing.small))
                    
                    Text(
                        text = when {
                            nugget.isMastered -> "Mastered ✨"
                            nugget.isDone -> "Finished"
                            else -> "Start now"
                        },
                        style = MaterialTheme.typography.bodySmall,
                        color = when {
                            nugget.isMastered -> masteredColor
                            nugget.isDone -> readColor
                            else -> MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                        },
                        fontWeight = FontWeight.Medium
                    )
                }
            }
            
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
                contentDescription = null,
                modifier = Modifier.size(spacing.medium - 2.dp),
                tint = MaterialTheme.colorScheme.outline.copy(alpha = 0.6f)
            )
        }
    }
}

@Composable
fun ComplexityBadge(
    complexity: String,
    modifier: Modifier = Modifier
) {
    val (bgColor, textColor) = when (complexity.lowercase()) {
        "beginner" -> BeginnerGreen.copy(alpha = 0.1f) to BeginnerGreenDark
        "intermediate" -> IntermediateOrange.copy(alpha = 0.1f) to IntermediateOrangeDark
        else -> AdvancedRed.copy(alpha = 0.1f) to AdvancedRedDark
    }

    Surface(
        color = bgColor,
        shape = RoundedCornerShape(MaterialTheme.spacing.small),
        modifier = modifier
    ) {
        Text(
            text = complexity,
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
            style = MaterialTheme.typography.labelSmall,
            color = textColor,
            fontWeight = FontWeight.Bold
        )
    }
}

@Preview(showBackground = true)
@Composable
fun NuggetCardPreview() {
    SkillflowTheme {
        Column(modifier = Modifier.padding(16.dp)) {
            NuggetCard(
                nugget = KnowledgeNugget(
                    id = "1",
                    title = "Kotlin Coroutines",
                    shortDescription = "Learn async",
                    content = "Content",
                    complexity = "Intermediate",
                    careerPathId = "android",
                    isDone = false,
                    isSaved = true,
                    isMastered = false,
                    date = "2026-08-02"
                ),
                onClick = {}
            )
            Spacer(modifier = Modifier.height(8.dp))
            NuggetCard(
                nugget = KnowledgeNugget(
                    id = "2",
                    title = "Activity Lifecycle",
                    shortDescription = "Basics",
                    content = "Content",
                    complexity = "Beginner",
                    careerPathId = "android",
                    isDone = true,
                    isSaved = false,
                    isMastered = true,
                    date = "2026-08-02"
                ),
                onClick = {}
            )
        }
    }
}
