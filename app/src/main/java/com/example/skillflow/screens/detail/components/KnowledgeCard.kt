package com.example.skillflow.screens.detail.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.QuestionMark
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.skillflow.R
import com.example.skillflow.domain.model.KnowledgeNugget
import com.example.skillflow.screens.theme.GradientEnd
import com.example.skillflow.screens.theme.GradientStart
import com.example.skillflow.screens.theme.SkillflowTheme
import com.example.skillflow.screens.theme.spacing

@Composable
fun KnowledgeCard(
    nugget: KnowledgeNugget,
    isFlipped: Boolean,
    rotation: Float,
    onFlip: () -> Unit,
    modifier: Modifier = Modifier
) {
    val revealText = stringResource(R.string.tap_to_reveal)

    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(420.dp)
            .graphicsLayer {
                rotationY = rotation
                cameraDistance = 15f * density
            }
            .clickable(onClick = onFlip),
        shape = RoundedCornerShape(32.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 12.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            if (rotation <= 90f) {
                // Front
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Brush.linearGradient(listOf(GradientStart, GradientEnd))),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        modifier = Modifier.padding(MaterialTheme.spacing.large + 8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = Color.White.copy(alpha = 0.2f),
                            modifier = Modifier.size(64.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.QuestionMark,
                                contentDescription = revealText,
                                tint = Color.White,
                                modifier = Modifier.padding(MaterialTheme.spacing.medium)
                            )
                        }
                        Spacer(modifier = Modifier.height(MaterialTheme.spacing.large))
                        Text(
                            text = nugget.title,
                            style = MaterialTheme.typography.displaySmall,
                            textAlign = TextAlign.Center,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            } else {
                // Back
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .graphicsLayer { rotationY = 180f }
                        .background(MaterialTheme.colorScheme.surface),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        modifier = Modifier.padding(MaterialTheme.spacing.large + 8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.Lightbulb,
                            contentDescription = nugget.title,
                            tint = GradientStart,
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(MaterialTheme.spacing.large))
                        Text(
                            text = nugget.content,
                            style = MaterialTheme.typography.headlineSmall,
                            lineHeight = 32.sp,
                            textAlign = TextAlign.Center,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun KnowledgeCardPreview() {
    SkillflowTheme {
        Box(modifier = Modifier.padding(16.dp)) {
            KnowledgeCard(
                nugget = KnowledgeNugget(
                    id = "1",
                    title = "Kotlin Coroutines",
                    shortDescription = "Learn async programming",
                    content = "Coroutines simplify async code execution on Android.",
                    complexity = "Intermediate",
                    careerPathId = "android",
                    date = "2026-08-02"
                ),
                isFlipped = false,
                rotation = 0f,
                onFlip = {}
            )
        }
    }
}
