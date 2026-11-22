package com.example.skillflow.ui.screens.detail.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.AutoStories
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
import com.example.skillflow.ui.common.components.FormattedMarkdownText
import com.example.skillflow.ui.common.components.stripMarkdownFormatting
import com.example.skillflow.ui.screens.profile.settings.components.LanguageToggleButton
import com.example.skillflow.ui.theme.GradientEnd
import com.example.skillflow.ui.theme.GradientStart
import com.example.skillflow.ui.theme.OnPrimary
import com.example.skillflow.ui.theme.RoyalBlueEnd
import com.example.skillflow.ui.theme.RoyalBlueMid
import com.example.skillflow.ui.theme.RoyalBlueStart
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
            .heightIn(min = 450.dp, max = 520.dp)
            .graphicsLayer {
                rotationY = rotation
                cameraDistance = 15f * density
            }
            .clickable(onClick = onFlip),
        shape = RoundedCornerShape(24.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        border = BorderStroke(
            width = 1.dp,
            color = if (rotation <= 90f) {
                Color.White.copy(alpha = 0.2f)
            } else {
                MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.35f)
            }
        ),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            if (rotation <= 90f) {
                // ==========================================
                // FRONT SIDE: BOOK COVER / QUESTION FRONT
                // ==========================================
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.linearGradient(
                                listOf(RoyalBlueStart, RoyalBlueMid, RoyalBlueEnd)
                            )
                        )
                ) {
                    // Decorative Background Accent Rings (Book Theme Watermark)
                    Box(
                        modifier = Modifier
                            .size(240.dp)
                            .align(Alignment.TopEnd)
                            .offset(x = 60.dp, y = (-50).dp)
                            .clip(CircleShape)
                            .background(OnPrimary.copy(alpha = 0.05f))
                    )
                    Box(
                        modifier = Modifier
                            .size(180.dp)
                            .align(Alignment.BottomStart)
                            .offset(x = (-40).dp, y = 40.dp)
                            .clip(CircleShape)
                            .background(OnPrimary.copy(alpha = 0.04f))
                    )

                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(MaterialTheme.spacing.medium),
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        // Top Action Bar
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            LanguageToggleButton(
                                currentLanguage = selectedLanguage,
                                onToggle = onToggleLanguage
                            )

                            // Page/Book Badge
                            Surface(
                                shape = RoundedCornerShape(16.dp),
                                color = OnPrimary.copy(alpha = 0.18f),
                                contentColor = OnPrimary
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.MenuBook,
                                        contentDescription = null,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "BOOK COVER",
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold,
                                        letterSpacing = 1.sp
                                    )
                                }
                            }

                            FilledIconButton(
                                onClick = {
                                    if (isSpeaking) onStopAudio() else onPlayAudio("$displayTitle. $speechContent", selectedLanguage)
                                },
                                colors = IconButtonDefaults.filledIconButtonColors(
                                    containerColor = OnPrimary.copy(alpha = 0.22f),
                                    contentColor = OnPrimary
                                ),
                                modifier = Modifier.size(40.dp)
                            ) {
                                Icon(
                                    imageVector = if (isSpeaking) Icons.Default.Stop else Icons.AutoMirrored.Filled.VolumeUp,
                                    contentDescription = if (isSpeaking) stringResource(R.string.cd_stop) else stringResource(R.string.cd_play),
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }

                        // Center Content
                        if (isTranslating) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .weight(1f),
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
                                    .fillMaxWidth()
                                    .weight(1f)
                                    .verticalScroll(frontScrollState)
                                    .padding(horizontal = 16.dp, vertical = 8.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                // Emblem Icon Container
                                Surface(
                                    shape = CircleShape,
                                    color = OnPrimary.copy(alpha = 0.15f),
                                    modifier = Modifier
                                        .size(68.dp)
                                        .border(2.dp, OnPrimary.copy(alpha = 0.3f), CircleShape)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(
                                            imageVector = Icons.AutoMirrored.Filled.MenuBook,
                                            contentDescription = revealText,
                                            tint = OnPrimary,
                                            modifier = Modifier.size(32.dp)
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(16.dp))

                                Text(
                                    text = "STUDY TOPIC",
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = OnPrimary.copy(alpha = 0.8f),
                                    letterSpacing = 2.sp
                                )

                                Spacer(modifier = Modifier.height(8.dp))

                                Text(
                                    text = displayTitle,
                                    style = if (displayTitle.length > 40) MaterialTheme.typography.headlineMedium else MaterialTheme.typography.displaySmall,
                                    textAlign = TextAlign.Center,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = OnPrimary,
                                    lineHeight = 34.sp
                                )
                            }
                        }

                        // Bottom Flip Affordance Pill
                        Surface(
                            shape = RoundedCornerShape(20.dp),
                            color = OnPrimary.copy(alpha = 0.2f),
                            contentColor = OnPrimary,
                            modifier = Modifier.align(Alignment.CenterHorizontally)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Sync,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Tap page to flip & read 📖",
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            } else {
                // ==========================================
                // BACK SIDE: BOOK PAGE / EDITORIAL READING
                // ==========================================
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .graphicsLayer { rotationY = 180f }
                        .background(MaterialTheme.colorScheme.surface)
                ) {
                    // Left Book Spine Accent Line
                    Box(
                        modifier = Modifier
                            .width(5.dp)
                            .fillMaxHeight()
                            .align(Alignment.CenterStart)
                            .background(
                                Brush.verticalGradient(
                                    listOf(GradientStart, GradientEnd)
                                )
                            )
                    )

                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(start = 14.dp, end = 16.dp, top = 14.dp, bottom = 14.dp)
                    ) {
                        // Top Header Actions Bar
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            LanguageToggleButton(
                                currentLanguage = selectedLanguage,
                                onToggle = onToggleLanguage
                            )

                            // Audio TTS Player Button
                            Button(
                                onClick = {
                                    if (isSpeaking) onStopAudio() else onPlayAudio(speechContent, selectedLanguage)
                                },
                                shape = RoundedCornerShape(20.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (isSpeaking) MaterialTheme.colorScheme.errorContainer else GradientStart.copy(alpha = 0.12f),
                                    contentColor = if (isSpeaking) MaterialTheme.colorScheme.error else GradientStart
                                ),
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                                modifier = Modifier.height(36.dp)
                            ) {
                                Icon(
                                    imageVector = if (isSpeaking) Icons.Default.Stop else Icons.AutoMirrored.Filled.VolumeUp,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = if (isSpeaking) "Stop" else "Listen",
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        if (isTranslating) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .weight(1f),
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
                            // Main Editorial Reading Scrollable Column
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .weight(1f)
                                    .padding(horizontal = 8.dp)
                                    .verticalScroll(backScrollState)
                            ) {
                                // Chapter Header Row
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.padding(bottom = 6.dp)
                                ) {
                                    Surface(
                                        shape = RoundedCornerShape(8.dp),
                                        color = GradientStart.copy(alpha = 0.12f),
                                        modifier = Modifier.size(32.dp)
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Icon(
                                                imageVector = Icons.Default.Lightbulb,
                                                contentDescription = displayTitle,
                                                tint = GradientStart,
                                                modifier = Modifier.size(18.dp)
                                            )
                                        }
                                    }

                                    Spacer(modifier = Modifier.width(10.dp))

                                    Text(
                                        text = displayTitle,
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = MaterialTheme.colorScheme.onSurface,
                                        modifier = Modifier.weight(1f)
                                    )
                                }

                                // Book Page Decorative Horizontal Rule
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(2.dp)
                                        .background(
                                            Brush.horizontalGradient(
                                                listOf(
                                                    GradientStart,
                                                    GradientStart.copy(alpha = 0.3f),
                                                    Color.Transparent
                                                )
                                            )
                                        )
                                )

                                Spacer(modifier = Modifier.height(12.dp))

                                // Editorial Reading Body
                                FormattedMarkdownText(
                                    markdownText = displayContent,
                                    textColor = MaterialTheme.colorScheme.onSurface,
                                    fontSize = if (displayContent.length > 250) 14.sp else 15.sp,
                                    lineHeight = if (displayContent.length > 250) 22.sp else 24.sp,
                                    modifier = Modifier.fillMaxWidth()
                                )

                                Spacer(modifier = Modifier.height(16.dp))
                            }
                        }

                        // Bottom Page Footer
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(start = 8.dp, end = 8.dp, top = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.AutoStories,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.outline.copy(alpha = 0.6f),
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "LESSON PAGE",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.outline.copy(alpha = 0.6f),
                                    letterSpacing = 0.5.sp
                                )
                            }

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Sync,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "Tap to flip back",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun KnowledgeCardFrontPreview() {
    SkillflowTheme {
        KnowledgeCard(
            nugget = KnowledgeNugget(
                id = "1",
                title = "String Templates in Kotlin",
                shortDescription = "Learn string interpolation",
                content = "Kotlin allows embedding variables and expressions directly into string literals using the $ symbol. For example:\n\n```kotlin\nval name = \"Alex\"\nprintln(\"Hello \$name! 2+2 is \${2+2}\")\n```\n\n- Simplified syntax\n- Supports expressions",
                complexity = "Beginner",
                careerPathId = "android",
                date = "2025-01-01"
            ),
            isFlipped = false,
            rotation = 0f,
            onFlip = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun KnowledgeCardBackPreview() {
    SkillflowTheme {
        KnowledgeCard(
            nugget = KnowledgeNugget(
                id = "1",
                title = "String Templates in Kotlin",
                shortDescription = "Learn string interpolation",
                content = "Kotlin allows embedding variables and expressions directly into string literals using the $ symbol. For example:\n\n```kotlin\nval name = \"Alex\"\nprintln(\"Hello \$name! 2+2 is \${2+2}\")\n```\n\n- Simplified syntax\n- Supports expressions",
                complexity = "Beginner",
                careerPathId = "android",
                date = "2025-01-01"
            ),
            isFlipped = true,
            rotation = 180f,
            onFlip = {}
        )
    }
}
