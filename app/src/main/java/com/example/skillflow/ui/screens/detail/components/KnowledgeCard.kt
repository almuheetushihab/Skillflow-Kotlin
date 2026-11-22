package com.example.skillflow.ui.screens.detail.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.QuestionMark
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.skillflow.R
import com.example.skillflow.domain.model.KnowledgeNugget
import com.example.skillflow.ui.common.components.FormattedMarkdownText
import com.example.skillflow.ui.common.components.stripMarkdownFormatting
import com.example.skillflow.ui.screens.profile.settings.components.LanguageToggleButton
import com.example.skillflow.ui.theme.GradientEnd
import com.example.skillflow.ui.theme.GradientStart
import com.example.skillflow.ui.theme.OnPrimary
import com.example.skillflow.ui.theme.SkillflowTheme
import com.example.skillflow.ui.theme.spacing

@Composable
fun KnowledgeCard(
    nugget: KnowledgeNugget,
    isFlipped: Boolean,
    rotation: Float,
    onFlip: () -> Unit,
    selectedLanguage: String = "en",
    translatedTitle: String? = null,
    translatedContent: String? = null,
    isTranslating: Boolean = false,
    onToggleLanguage: () -> Unit = {},
    isSpeaking: Boolean = false,
    onPlayAudio: (String, String) -> Unit = { _, _ -> },
    onStopAudio: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val revealText = stringResource(R.string.tap_to_reveal)

    val rawTitle = if (selectedLanguage == "bn" && !translatedTitle.isNullOrBlank()) {
        translatedTitle
    } else {
        nugget.title
    }

    val displayTitle = stripMarkdownFormatting(rawTitle)

    val displayContent = if (selectedLanguage == "bn" && !translatedContent.isNullOrBlank()) {
        translatedContent
    } else {
        nugget.content
    }

    val speechContent = stripMarkdownFormatting(displayContent)

    val frontScrollState = rememberScrollState()
    val backScrollState = rememberScrollState()

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
                    // Top Bar Actions
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .align(Alignment.TopCenter)
                            .padding(MaterialTheme.spacing.medium),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        LanguageToggleButton(
                            currentLanguage = selectedLanguage,
                            onToggle = onToggleLanguage
                        )

                        FilledIconButton(
                            onClick = {
                                if (isSpeaking) onStopAudio() else onPlayAudio("$displayTitle. $speechContent", selectedLanguage)
                            },
                            colors = IconButtonDefaults.filledIconButtonColors(
                                containerColor = OnPrimary.copy(alpha = 0.25f),
                                contentColor = OnPrimary
                            )
                        ) {
                            Icon(
                                imageVector = if (isSpeaking) Icons.Default.Stop else Icons.AutoMirrored.Filled.VolumeUp,
                                contentDescription = if (isSpeaking) stringResource(R.string.cd_stop) else stringResource(R.string.cd_play)
                            )
                        }
                    }

                    if (isTranslating) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            CircularProgressIndicator(
                                color = OnPrimary,
                                modifier = Modifier.size(36.dp)
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = stringResource(R.string.translating),
                                style = MaterialTheme.typography.bodyMedium,
                                color = OnPrimary
                            )
                        }
                    } else {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(top = 64.dp, bottom = 20.dp, start = 24.dp, end = 24.dp)
                                .verticalScroll(frontScrollState),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Surface(
                                shape = CircleShape,
                                color = OnPrimary.copy(alpha = 0.2f),
                                modifier = Modifier.size(56.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.QuestionMark,
                                    contentDescription = revealText,
                                    tint = OnPrimary,
                                    modifier = Modifier.padding(MaterialTheme.spacing.medium)
                                )
                            }
                            Spacer(modifier = Modifier.height(MaterialTheme.spacing.medium))
                            Text(
                                text = displayTitle,
                                style = if (displayTitle.length > 50) MaterialTheme.typography.headlineMedium else MaterialTheme.typography.displaySmall,
                                textAlign = TextAlign.Center,
                                fontWeight = FontWeight.Bold,
                                color = OnPrimary
                            )
                        }
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
                    // Top Bar Actions
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .align(Alignment.TopCenter)
                            .padding(MaterialTheme.spacing.medium),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        LanguageToggleButton(
                            currentLanguage = selectedLanguage,
                            onToggle = onToggleLanguage
                        )

                        FilledIconButton(
                            onClick = {
                                if (isSpeaking) onStopAudio() else onPlayAudio(speechContent, selectedLanguage)
                            },
                            colors = IconButtonDefaults.filledIconButtonColors(
                                containerColor = GradientStart.copy(alpha = 0.15f),
                                contentColor = GradientStart
                            )
                        ) {
                            Icon(
                                imageVector = if (isSpeaking) Icons.Default.Stop else Icons.AutoMirrored.Filled.VolumeUp,
                                contentDescription = if (isSpeaking) stringResource(R.string.cd_stop) else stringResource(R.string.cd_play)
                            )
                        }
                    }

                    if (isTranslating) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            CircularProgressIndicator(
                                color = GradientStart,
                                modifier = Modifier.size(36.dp)
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = stringResource(R.string.translating),
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    } else {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(top = 64.dp, bottom = 20.dp, start = 24.dp, end = 24.dp)
                                .verticalScroll(backScrollState),
                            horizontalAlignment = Alignment.Start,
                            verticalArrangement = Arrangement.Top
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(bottom = 12.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Lightbulb,
                                    contentDescription = displayTitle,
                                    tint = GradientStart,
                                    modifier = Modifier.size(32.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = displayTitle,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                            
                            FormattedMarkdownText(
                                markdownText = displayContent,
                                textColor = MaterialTheme.colorScheme.onSurface,
                                fontSize = if (displayContent.length > 250) 14.sp else 15.sp,
                                lineHeight = if (displayContent.length > 250) 20.sp else 22.sp,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun KnowledgeCardPreview() {
    SkillflowTheme {
        KnowledgeCard(
            nugget = KnowledgeNugget(
                id = "1",
                title = "Compose State Lifting",
                shortDescription = "Understanding state flow",
                content = "State lifting moves state to a common ancestor.",
                complexity = "Intermediate",
                careerPathId = "android",
                date = "2025-01-01"
            ),
            isFlipped = false,
            rotation = 0f,
            onFlip = {}
        )
    }
}


