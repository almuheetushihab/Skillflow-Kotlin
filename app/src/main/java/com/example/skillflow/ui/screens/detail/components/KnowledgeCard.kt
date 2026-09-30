package com.example.skillflow.ui.screens.detail.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.skillflow.R
import com.example.skillflow.domain.model.KnowledgeNugget
import com.example.skillflow.ui.screens.profile.settings.components.LanguageToggleButton
import com.example.skillflow.ui.theme.GradientEnd
import com.example.skillflow.ui.theme.GradientStart
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

    val displayTitle = if (selectedLanguage == "bn" && !translatedTitle.isNullOrBlank()) {
        translatedTitle
    } else {
        nugget.title
    }

    val displayContent = if (selectedLanguage == "bn" && !translatedContent.isNullOrBlank()) {
        translatedContent
    } else {
        nugget.content
    }

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
                                if (isSpeaking) onStopAudio() else onPlayAudio("$displayTitle. $displayContent", selectedLanguage)
                            },
                            colors = IconButtonDefaults.filledIconButtonColors(
                                containerColor = Color.White.copy(alpha = 0.25f),
                                contentColor = Color.White
                            )
                        ) {
                            Icon(
                                imageVector = if (isSpeaking) Icons.Default.Stop else Icons.AutoMirrored.Filled.VolumeUp,
                                contentDescription = if (isSpeaking) "Stop Audio" else "Play Audio"
                            )
                        }
                    }

                    if (isTranslating) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            CircularProgressIndicator(
                                color = Color.White,
                                modifier = Modifier.size(36.dp)
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = "বাংলায় অনুবাদ করা হচ্ছে...",
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color.White
                            )
                        }
                    } else {
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
                                text = displayTitle,
                                style = MaterialTheme.typography.displaySmall,
                                textAlign = TextAlign.Center,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
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
                                if (isSpeaking) onStopAudio() else onPlayAudio(displayContent, selectedLanguage)
                            },
                            colors = IconButtonDefaults.filledIconButtonColors(
                                containerColor = GradientStart.copy(alpha = 0.15f),
                                contentColor = GradientStart
                            )
                        ) {
                            Icon(
                                imageVector = if (isSpeaking) Icons.Default.Stop else Icons.AutoMirrored.Filled.VolumeUp,
                                contentDescription = if (isSpeaking) "Stop Audio" else "Play Audio"
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
                                text = "বাংলায় অনুবাদ করা হচ্ছে...",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    } else {
                        Column(
                            modifier = Modifier.padding(MaterialTheme.spacing.large + 8.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = Icons.Default.Lightbulb,
                                contentDescription = displayTitle,
                                tint = GradientStart,
                                modifier = Modifier.size(48.dp)
                            )
                            Spacer(modifier = Modifier.height(MaterialTheme.spacing.large))
                            Text(
                                text = displayContent,
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
}

