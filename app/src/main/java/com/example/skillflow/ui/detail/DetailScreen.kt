package com.example.skillflow.ui.detail

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.skillflow.R
import com.example.skillflow.domain.model.KnowledgeNugget
import com.example.skillflow.domain.model.UserNote
import com.example.skillflow.presentation.detail.DetailState
import com.example.skillflow.presentation.detail.DetailUiEvent
import com.example.skillflow.presentation.detail.DetailViewModel
import com.example.skillflow.ui.common.LoadingView
import com.example.skillflow.ui.common.SkillflowTopAppBar
import com.example.skillflow.ui.detail.components.KnowledgeCard
import com.example.skillflow.ui.detail.components.NoteCard
import com.example.skillflow.ui.detail.components.NoteInputCard
import com.example.skillflow.ui.theme.GradientStart
import com.example.skillflow.ui.theme.SkillflowTheme
import com.example.skillflow.ui.theme.spacing
import kotlinx.coroutines.flow.collectLatest

@Composable
fun DetailScreen(
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: DetailViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val scrollState = rememberScrollState()

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
        snackbarHostState = snackbarHostState,
        scrollState = scrollState,
        onNavigateBack = onNavigateBack,
        onToggleSave = viewModel::toggleSave,
        onFlipCard = viewModel::flipCard,
        onMarkAsMastered = viewModel::toggleMastered,
        onNoteTitleChange = viewModel::onNoteTitleChange,
        onNoteDescChange = viewModel::onNoteDescriptionChange,
        onSaveNote = viewModel::saveNote,
        onEditNote = viewModel::onEditNote,
        onDeleteNote = viewModel::deleteNote,
        modifier = modifier
    )
}

@Composable
fun DetailContent(
    state: DetailState,
    snackbarHostState: SnackbarHostState,
    scrollState: androidx.compose.foundation.ScrollState,
    onNavigateBack: () -> Unit,
    onToggleSave: () -> Unit,
    onFlipCard: () -> Unit,
    onMarkAsMastered: () -> Unit,
    onNoteTitleChange: (String) -> Unit,
    onNoteDescChange: (String) -> Unit,
    onSaveNote: () -> Unit,
    onEditNote: (UserNote) -> Unit,
    onDeleteNote: (UserNote) -> Unit,
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
                
                KnowledgeCard(nugget = nugget, isFlipped = state.isFlipped, rotation = rotation, onFlip = onFlipCard)

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

                Spacer(modifier = Modifier.height(spacing.large))

                // Premium Note Input Card
                NoteInputCard(
                    title = state.noteTitle,
                    onTitleChange = onNoteTitleChange,
                    description = state.noteDescription,
                    onDescriptionChange = onNoteDescChange,
                    onSave = onSaveNote,
                    isEditing = state.editingNoteId != null
                )

                Spacer(modifier = Modifier.height(spacing.large))

                // Saved Notes Section
                if (state.notes.isNotEmpty()) {
                    Text(
                        text = stringResource(R.string.your_saved_notes), 
                        style = MaterialTheme.typography.titleMedium, 
                        fontWeight = FontWeight.ExtraBold, 
                        modifier = Modifier.align(Alignment.Start),
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(spacing.medium))
                    state.notes.forEach { note ->
                        NoteCard(
                            note = note,
                            onEdit = { onEditNote(note) },
                            onDelete = { onDeleteNote(note) }
                        )
                        Spacer(modifier = Modifier.height(spacing.medium))
                    }
                }

                Spacer(modifier = Modifier.height(spacing.extraLarge))
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DetailContentPreview() {
    SkillflowTheme {
        DetailContent(
            state = DetailState(
                nugget = KnowledgeNugget("1", "Title", "Desc", "Content", "Intermediate", null, "android", false, false, true, null, 0, "2026-08-01"),
                notes = listOf(UserNote("1", "1", "Note Title", "Some content here...", 123456789L))
            ),
            snackbarHostState = SnackbarHostState(),
            scrollState = rememberScrollState(),
            onNavigateBack = {},
            onToggleSave = {},
            onFlipCard = {},
            onMarkAsMastered = {},
            onNoteTitleChange = {},
            onNoteDescChange = {},
            onSaveNote = {},
            onEditNote = {},
            onDeleteNote = {}
        )
    }
}
