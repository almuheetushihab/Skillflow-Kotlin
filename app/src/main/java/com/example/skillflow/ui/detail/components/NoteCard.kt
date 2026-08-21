package com.example.skillflow.ui.detail.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.PushPin
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.skillflow.R
import com.example.skillflow.domain.model.UserNote
import com.example.skillflow.ui.theme.GradientEnd
import com.example.skillflow.ui.theme.GradientStart
import com.example.skillflow.ui.theme.spacing

@Composable
fun NoteCard(
    note: UserNote,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    var isFlipped by remember { mutableStateOf(false) }
    val rotation by animateFloatAsState(
        targetValue = if (isFlipped) 180f else 0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "NoteCardRotation"
    )

    Card(
        modifier = modifier
            .width(165.dp) // Optimized for 2-column grid
            .height(200.dp)
            .graphicsLayer {
                rotationY = rotation
                cameraDistance = 15f * density
            }
            .clickable { isFlipped = !isFlipped },
        shape = RoundedCornerShape(32.dp), // Matched with main KnowledgeCard
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            if (rotation <= 90f) {
                // FRONT: Matching the KnowledgeCard Style
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Brush.linearGradient(listOf(GradientStart, GradientEnd))),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.PushPin,
                        contentDescription = null,
                        tint = Color.White.copy(alpha = 0.2f),
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .padding(16.dp)
                            .size(24.dp)
                    )
                    
                    Text(
                        text = note.title,
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.White,
                        fontWeight = FontWeight.Black,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(12.dp),
                        maxLines = 3,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            } else {
                // BACK: Shows the Description and Actions
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .graphicsLayer { rotationY = 180f }
                        .background(MaterialTheme.colorScheme.surface),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                            Text(
                                text = note.noteContent,
                                style = MaterialTheme.typography.bodySmall,
                                textAlign = TextAlign.Center,
                                color = MaterialTheme.colorScheme.onSurface,
                                maxLines = 6,
                                overflow = TextOverflow.Ellipsis,
                                lineHeight = 18.sp
                            )
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            IconButton(onClick = onEdit) {
                                Icon(
                                    imageVector = Icons.Default.Edit,
                                    contentDescription = stringResource(R.string.edit_note),
                                    tint = GradientStart,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            IconButton(onClick = onDelete) {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.error.copy(alpha = 0.7f),
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
