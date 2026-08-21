package com.example.skillflow.ui.detail

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
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
import com.example.skillflow.ui.theme.GradientEnd
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

                // Notes Input
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)),
                    shape = RoundedCornerShape(spacing.medium)
                ) {
                    Column(modifier = Modifier.padding(spacing.medium)) {
                        Text(
                            text = if (state.editingNoteId == null) 
                                stringResource(R.string.add_study_note) 
                            else 
                                stringResource(R.string.edit_note), 
                            style = MaterialTheme.typography.titleSmall, 
                            color = GradientStart
                        )
                        Spacer(modifier = Modifier.height(spacing.small))
                        OutlinedTextField(
                            value = state.noteTitle,
                            onValueChange = onNoteTitleChange,
                            modifier = Modifier.fillMaxWidth(),
                            placeholder = { Text(text = stringResource(R.string.note_title_placeholder)) },
                            singleLine = true,
                            shape = RoundedCornerShape(spacing.small)
                        )
                        Spacer(modifier = Modifier.height(spacing.small))
                        OutlinedTextField(
                            value = state.noteDescription,
                            onValueChange = onNoteDescChange,
                            modifier = Modifier.fillMaxWidth().heightIn(min = 120.dp),
                            placeholder = { Text(text = stringResource(R.string.note_details_placeholder)) },
                            shape = RoundedCornerShape(spacing.small)
                        )
                        Spacer(modifier = Modifier.height(spacing.medium))
                        Button(
                            onClick = onSaveNote,
                            modifier = Modifier.align(Alignment.End),
                            shape = RoundedCornerShape(spacing.medium)
                        ) {
                            Icon(
                                imageVector = if (state.editingNoteId == null) Icons.Default.Save else Icons.Default.Update, 
                                contentDescription = null
                            )
                            Spacer(modifier = Modifier.width(spacing.small))
                            Text(
                                text = if (state.editingNoteId == null) 
                                    stringResource(R.string.save_note) 
                                else 
                                    stringResource(R.string.update_note)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(spacing.large))

                // Notes List
                if (state.notes.isNotEmpty()) {
                    Text(
                        text = stringResource(R.string.your_saved_notes), 
                        style = MaterialTheme.typography.titleMedium, 
                        fontWeight = FontWeight.Bold, 
                        modifier = Modifier.align(Alignment.Start)
                    )
                    Spacer(modifier = Modifier.height(spacing.small))
                    state.notes.forEach { note ->
                        NoteItem(
                            note = note, 
                            onEdit = { onEditNote(note) }, 
                            onDelete = { onDeleteNote(note) }
                        )
                        Spacer(modifier = Modifier.height(spacing.small))
                    }
                }

                Spacer(modifier = Modifier.height(spacing.extraLarge))
            }
        }
    }
}

@Composable
fun NoteItem(
    note: UserNote, 
    onEdit: () -> Unit, 
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }
    val spacing = MaterialTheme.spacing
    
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(spacing.medium))
            .clickable { expanded = !expanded },
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Column(modifier = Modifier.padding(spacing.medium)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = note.title, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                    if (!expanded) {
                        Text(
                            text = note.noteContent, 
                            style = MaterialTheme.typography.bodySmall, 
                            maxLines = 1, 
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
                IconButton(onClick = onEdit) { 
                    Icon(imageVector = Icons.Default.Edit, contentDescription = stringResource(R.string.edit_note), modifier = Modifier.size(18.dp)) 
                }
                IconButton(onClick = onDelete) { 
                    Icon(
                        imageVector = Icons.Default.Delete, 
                        contentDescription = null, 
                        modifier = Modifier.size(18.dp), 
                        tint = MaterialTheme.colorScheme.error
                    ) 
                }
            }
            if (expanded) {
                HorizontalDivider(modifier = Modifier.padding(vertical = spacing.small))
                Text(text = note.noteContent, style = MaterialTheme.typography.bodyMedium)
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
                notes = listOf(UserNote("1", "1", "Title", "Content", 123456789L))
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
