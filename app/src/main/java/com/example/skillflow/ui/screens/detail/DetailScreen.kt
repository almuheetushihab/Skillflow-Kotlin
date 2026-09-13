package com.example.skillflow.ui.screens.detail

import androidx.compose.animation.core.*
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.skillflow.R
import com.example.skillflow.domain.model.ComplexityLevel
import com.example.skillflow.domain.model.UserNote
import com.example.skillflow.ui.common.LoadingView
import com.example.skillflow.ui.common.SkillflowTopAppBar
import com.example.skillflow.ui.common.components.VideoPlayerComponent
import com.example.skillflow.ui.screens.detail.components.AiChatBottomSheet
import com.example.skillflow.ui.screens.detail.components.KnowledgeCard
import com.example.skillflow.ui.screens.detail.components.NoteCard
import com.example.skillflow.ui.screens.detail.components.NoteInputCard
import com.example.skillflow.ui.theme.*
import kotlinx.coroutines.flow.collectLatest

@Composable
fun DetailScreen(
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: DetailViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val isSpeaking by viewModel.isSpeaking.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val scrollState = rememberScrollState()

    DisposableEffect(Unit) {
        onDispose {
            viewModel.onStopAudioClicked()
        }
    }

    LaunchedEffect(Unit) {
        viewModel.eventFlow.collectLatest { event ->
            when (event) {
                is DetailUiEvent.ShowSnackbar -> snackbarHostState.showSnackbar(event.message)
                DetailUiEvent.ScrollToTop -> scrollState.animateScrollTo(0)
            }
        }
    }

    DetailContent(
        state = state,
        isSpeaking = isSpeaking,
        snackbarHostState = snackbarHostState,
        scrollState = scrollState,
        onNavigateBack = onNavigateBack,
        onToggleSave = viewModel::toggleSave,
        onFlipCard = viewModel::flipCard,
        onMarkAsMastered = viewModel::toggleMastered,
        onPlayAudio = viewModel::onPlayAudioClicked,
        onStopAudio = viewModel::onStopAudioClicked,
        onNoteTitleChange = viewModel::onNoteTitleChange,
        onNoteDescChange = viewModel::onNoteDescriptionChange,
        onSaveNote = viewModel::saveNote,
        onEditNote = viewModel::onEditNote,
        onDeleteNote = viewModel::deleteNote,
        onToggleAiSheet = viewModel::toggleAiBottomSheet,
        onAiQuestionChange = viewModel::onAiQuestionChange,
        onSendAiQuestion = viewModel::askAI,
        modifier = modifier
    )
}

@Composable
fun DetailContent(
    state: DetailState,
    isSpeaking: Boolean,
    snackbarHostState: SnackbarHostState,
    scrollState: ScrollState,
    onNavigateBack: () -> Unit,
    onToggleSave: () -> Unit,
    onFlipCard: () -> Unit,
    onMarkAsMastered: () -> Unit,
    onPlayAudio: (String) -> Unit,
    onStopAudio: () -> Unit,
    onNoteTitleChange: (String) -> Unit,
    onNoteDescChange: (String) -> Unit,
    onSaveNote: () -> Unit,
    onEditNote: (UserNote) -> Unit,
    onDeleteNote: (UserNote) -> Unit,
    onToggleAiSheet: (Boolean) -> Unit,
    onAiQuestionChange: (String) -> Unit,
    onSendAiQuestion: () -> Unit,
    modifier: Modifier = Modifier
) {
    val nugget = state.nugget
    val spacing = MaterialTheme.spacing

    val rotation by animateFloatAsState(
        targetValue = if (state.isFlipped) 180f else 0f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow),
        label = "CardRotation"
    )

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            SkillflowTopAppBar(
                title = stringResource(R.string.knowledge_nugget),
                navigationIcon = Icons.AutoMirrored.Filled.ArrowBack,
                onNavigationClick = onNavigateBack,
                actions = {
                    if (nugget != null) {
                        IconButton(onClick = onToggleSave) {
                            Icon(
                                imageVector = if (nugget.isSaved) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                                contentDescription = stringResource(R.string.save_nugget),
                                tint = if (nugget.isSaved) GradientStart else MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            )
        },
        floatingActionButton = {
            if (nugget != null) {
                ExtendedFloatingActionButton(
                    onClick = { onToggleAiSheet(true) },
                    icon = {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = "Ask AI Assistant",
                            tint = Color.White
                        )
                    },
                    text = {
                        Text(
                            text = "Ask AI Assistant",
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    },
                    containerColor = GradientStart
                )
            }
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = spacing.large)
                .verticalScroll(scrollState),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (nugget == null) {
                if (state.isLoading) LoadingView(modifier = Modifier.fillMaxSize())
                else Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { 
                    Text(text = stringResource(R.string.nugget_not_found)) 
                }
            } else {
                Spacer(modifier = Modifier.height(spacing.medium))

                if (!nugget.videoUrl.isNullOrEmpty()) {
                    VideoPlayerComponent(
                        videoUrl = nugget.videoUrl,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = spacing.medium)
                    )
                }

                KnowledgeCard(
                    nugget = nugget,
                    isFlipped = state.isFlipped,
                    rotation = rotation,
                    onFlip = onFlipCard,
                    isSpeaking = isSpeaking,
                    onPlayAudio = onPlayAudio,
                    onStopAudio = onStopAudio
                )

                Spacer(modifier = Modifier.height(spacing.large))

                Row(
                    modifier = Modifier.fillMaxWidth(), 
                    verticalAlignment = Alignment.CenterVertically, 
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = stringResource(R.string.mastered_question), 
                        style = MaterialTheme.typography.titleMedium, 
                        fontWeight = FontWeight.Bold
                    )
                    Switch(checked = nugget.isMastered, onCheckedChange = { onMarkAsMastered() })
                }

                Spacer(modifier = Modifier.height(spacing.extraLarge + spacing.medium))

                NoteInputCard(
                    title = state.noteTitle,
                    onTitleChange = onNoteTitleChange,
                    description = state.noteDescription,
                    onDescriptionChange = onNoteDescChange,
                    onSave = onSaveNote,
                    isEditing = state.editingNoteId != null
                )

                if (state.notes.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(spacing.extraLarge))

                    Text(
                        text = stringResource(R.string.your_saved_notes), 
                        style = MaterialTheme.typography.titleMedium, 
                        fontWeight = FontWeight.Bold, 
                        modifier = Modifier.align(Alignment.Start),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    
                    Spacer(modifier = Modifier.height(spacing.medium))
                    
                    val level = ComplexityLevel.fromString(nugget.complexity)
                    val accentColor = when (level) {
                        ComplexityLevel.BEGINNER -> BeginnerGreen
                        ComplexityLevel.INTERMEDIATE -> IntermediateOrange
                        ComplexityLevel.ADVANCED -> AdvancedRed
                    }

                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(spacing.medium)
                    ) {
                        state.notes.forEach { note ->
                            NoteCard(
                                note = note,
                                onEdit = { onEditNote(note) },
                                onDelete = { onDeleteNote(note) },
                                accentColor = accentColor,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }

                // AI Assistant Banner Button below Study Notes
                Spacer(modifier = Modifier.height(spacing.extraLarge))

                Card(
                    onClick = { onToggleAiSheet(true) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f)
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(spacing.medium),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(44.dp)
                                    .clip(CircleShape)
                                    .background(
                                        Brush.horizontalGradient(
                                            listOf(GradientStart, GradientEnd)
                                        )
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.AutoAwesome,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(22.dp)
                                )
                            }

                            Spacer(modifier = Modifier.width(spacing.medium))

                            Column {
                                Text(
                                    text = "Need help understanding?",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "Ask SkillFlow AI Tutor for instant insights",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        Surface(
                            shape = RoundedCornerShape(20.dp),
                            color = GradientStart,
                            contentColor = Color.White
                        ) {
                            Text(
                                text = "Ask AI",
                                modifier = Modifier.padding(horizontal = spacing.medium, vertical = spacing.small),
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(spacing.extraLarge * 2))
            }
        }

        // ModalBottomSheet for AI Chat
        AiChatBottomSheet(
            isSheetOpen = state.isAiBottomSheetOpen,
            messages = state.aiMessages,
            questionInput = state.aiQuestionInput,
            isLoading = state.isAiLoading,
            onQuestionChange = onAiQuestionChange,
            onSendQuestion = onSendAiQuestion,
            onDismiss = { onToggleAiSheet(false) }
        )
    }
}
