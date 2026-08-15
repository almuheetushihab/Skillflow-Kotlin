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
                                contentDescription = null,
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
                .padding(horizontal = MaterialTheme.spacing.large)
                .verticalScroll(scrollState),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (nugget == null) {
                if (state.isLoading) LoadingView(modifier = Modifier.fillMaxSize())
                else Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { Text(stringResource(R.string.nugget_not_found)) }
            } else {
                Spacer(modifier = Modifier.height(MaterialTheme.spacing.medium))
                
                KnowledgeCard(nugget = nugget, isFlipped = state.isFlipped, rotation = rotation, onFlip = onFlipCard)

                Spacer(modifier = Modifier.height(MaterialTheme.spacing.large))

                Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(text = "Mastered this topic?", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Switch(checked = nugget.isMastered, onCheckedChange = { onMarkAsMastered() })
                }

                Spacer(modifier = Modifier.height(MaterialTheme.spacing.large))

                // Notes Input
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
                ) {
                    Column(modifier = Modifier.padding(MaterialTheme.spacing.medium)) {
                        Text(text = if (state.editingNoteId == null) "Add Study Note" else "Edit Note", style = MaterialTheme.typography.titleSmall, color = GradientStart)
                        Spacer(modifier = Modifier.height(MaterialTheme.spacing.small))
                        OutlinedTextField(
                            value = state.noteTitle,
                            onValueChange = onNoteTitleChange,
                            modifier = Modifier.fillMaxWidth(),
                            placeholder = { Text("Note Title") },
                            singleLine = true
                        )
                        Spacer(modifier = Modifier.height(MaterialTheme.spacing.small))
                        OutlinedTextField(
                            value = state.noteDescription,
                            onValueChange = onNoteDescChange,
                            modifier = Modifier.fillMaxWidth().heightIn(min = 100.dp),
                            placeholder = { Text("Write details...") }
                        )
                        Spacer(modifier = Modifier.height(MaterialTheme.spacing.medium))
                        Button(
                            onClick = onSaveNote,
                            modifier = Modifier.align(Alignment.End),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(if (state.editingNoteId == null) Icons.Default.Save else Icons.Default.Update, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(if (state.editingNoteId == null) "Save Note" else "Update Note")
                        }
                    }
                }

                Spacer(modifier = Modifier.height(MaterialTheme.spacing.large))

                // Notes List
                if (state.notes.isNotEmpty()) {
                    Text(text = "Your Saved Notes", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, modifier = Modifier.align(Alignment.Start))
                    Spacer(modifier = Modifier.height(MaterialTheme.spacing.small))
                    state.notes.forEach { note ->
                        NoteItem(note = note, onEdit = { onEditNote(note) }, onDelete = { onDeleteNote(note) })
                        Spacer(modifier = Modifier.height(MaterialTheme.spacing.small))
                    }
                }

                Spacer(modifier = Modifier.height(MaterialTheme.spacing.extraLarge))
            }
        }
    }
}

@Composable
fun NoteItem(note: UserNote, onEdit: () -> Unit, onDelete: () -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    Card(
        modifier = Modifier.fillMaxWidth().clickable { expanded = !expanded },
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Column(modifier = Modifier.padding(MaterialTheme.spacing.medium)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = note.title, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                    if (!expanded) {
                        Text(text = note.noteContent, style = MaterialTheme.typography.bodySmall, maxLines = 1, overflow = TextOverflow.Ellipsis)
                    }
                }
                IconButton(onClick = onEdit) { Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(18.dp)) }
                IconButton(onClick = onDelete) { Icon(Icons.Default.Delete, contentDescription = null, modifier = Modifier.size(18.dp), tint = MaterialTheme.colorScheme.error) }
            }
            if (expanded) {
                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                Text(text = note.noteContent, style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}
