package com.example.skillflow.ui.features.profile.profile

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Assignment
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.example.skillflow.R
import com.example.skillflow.ui.common.SkillflowTopAppBar
import com.example.skillflow.ui.features.profile.profile.components.StatCard
import com.example.skillflow.ui.theme.GradientStart
import com.example.skillflow.ui.theme.spacing
import java.util.Locale

@Composable
fun ProfileScreen(
    onResetOnboarding: () -> Unit,
    onNavigateToSettings: () -> Unit,
    onNavigateToQuiz: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { viewModel.setProfilePicture(it.toString()) }
    }

    ProfileContent(
        state = state,
        onToggleDarkMode = viewModel::toggleDarkMode,
        onNavigateToSettings = onNavigateToSettings,
        onNavigateToQuiz = onNavigateToQuiz,
        onResetOnboarding = {
            viewModel.resetOnboarding()
            onResetOnboarding()
        },
        onChangePhoto = { launcher.launch("image/*") },
        modifier = modifier
    )
}

@Composable
fun ProfileContent(
    state: ProfileState,
    onToggleDarkMode: () -> Unit,
    onNavigateToSettings: () -> Unit,
    onNavigateToQuiz: () -> Unit,
    onResetOnboarding: () -> Unit,
    onChangePhoto: () -> Unit,
    modifier: Modifier = Modifier
) {
    val spacing = MaterialTheme.spacing
    val backgroundGradient = Brush.verticalGradient(
        listOf(GradientStart.copy(alpha = 0.1f), MaterialTheme.colorScheme.background)
    )

    Scaffold(
        modifier = modifier.background(backgroundGradient),
        topBar = {
            SkillflowTopAppBar(
                title = stringResource(R.string.my_profile),
                actions = {
                    IconButton(onClick = onNavigateToSettings) {
                        Icon(
                            imageVector = Icons.Default.Settings, 
                            contentDescription = stringResource(R.string.settings)
                        )
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = spacing.large)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(spacing.medium))

            Box(
                modifier = Modifier.size(120.dp),
                contentAlignment = Alignment.BottomEnd
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.surface)
                        .border(3.dp, GradientStart, CircleShape)
                        .clickable { onChangePhoto() },
                    contentAlignment = Alignment.Center
                ) {
                    if (state.profilePictureUri != null) {
                        AsyncImage(
                            model = state.profilePictureUri,
                            contentDescription = stringResource(R.string.profile_photo),
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = null,
                            modifier = Modifier.size(64.dp),
                            tint = GradientStart.copy(alpha = 0.6f)
                        )
                    }
                }
                
                Surface(
                    modifier = Modifier
                        .size(36.dp)
                        .clickable { onChangePhoto() },
                    shape = CircleShape,
                    color = GradientStart,
                    tonalElevation = spacing.small,
                    shadowElevation = spacing.extraSmall
                ) {
                    Icon(
                        imageVector = Icons.Default.CameraAlt,
                        contentDescription = stringResource(R.string.change_photo),
                        modifier = Modifier.padding(spacing.small),
                        tint = Color.White
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(spacing.medium))
            
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = state.userName.ifEmpty { stringResource(R.string.learner_profile) },
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
                IconButton(onClick = onNavigateToSettings) {
                    Icon(
                        imageVector = Icons.Default.Edit, 
                        contentDescription = stringResource(R.string.edit_profile), 
                        modifier = Modifier.size(spacing.medium + 2.dp)
                    )
                }
            }

            if (state.userEmail.isNotEmpty()) {
                Text(
                    text = state.userEmail,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            Surface(
                shape = RoundedCornerShape(spacing.medium),
                color = GradientStart.copy(alpha = 0.1f),
                modifier = Modifier.padding(top = spacing.small)
            ) {
                val goal = state.careerPathId ?: stringResource(R.string.not_set)
                Text(
                    text = stringResource(R.string.career_goal, goal),
                    style = MaterialTheme.typography.labelLarge,
                    modifier = Modifier.padding(horizontal = spacing.medium, vertical = spacing.extraSmall),
                    color = GradientStart,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(spacing.large))

            Text(
                text = stringResource(R.string.quiz_stats),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(spacing.small))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(spacing.medium)
            ) {
                StatCard(
                    modifier = Modifier.weight(1f),
                    title = stringResource(R.string.quizzes_taken),
                    value = state.quizCount.toString(),
                    icon = Icons.AutoMirrored.Filled.Assignment,
                    color = MaterialTheme.colorScheme.primary
                )
                StatCard(
                    modifier = Modifier.weight(1f),
                    title = stringResource(R.string.avg_score),
                    value = String.format(Locale.getDefault(), "%.1f", state.averageScore),
                    icon = Icons.Default.Assessment,
                    color = MaterialTheme.colorScheme.secondary
                )
            }

            Spacer(modifier = Modifier.height(spacing.large))

            Text(
                text = stringResource(R.string.learning_progress),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(spacing.small))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(spacing.medium)
            ) {
                StatCard(
                    modifier = Modifier.weight(1f),
                    title = stringResource(R.string.profile_level),
                    value = stringResource(R.string.level_format, state.profileLevel),
                    icon = Icons.Default.Star,
                    color = MaterialTheme.colorScheme.tertiary
                )
                StatCard(
                    modifier = Modifier.weight(1f),
                    title = stringResource(R.string.daily_progress),
                    value = stringResource(R.string.daily_progress_format, state.todayLearned, state.todayTotal),
                    icon = Icons.AutoMirrored.Filled.TrendingUp,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            Spacer(modifier = Modifier.height(spacing.medium))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(spacing.medium),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
            ) {
                Column(modifier = Modifier.padding(spacing.medium)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.Book, contentDescription = null, tint = GradientStart)
                        Spacer(modifier = Modifier.width(spacing.small))
                        Text(
                            text = stringResource(R.string.todays_summary),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Spacer(modifier = Modifier.height(spacing.small))
                    if (state.learnedTopics.isEmpty()) {
                        Text(
                            text = stringResource(R.string.no_topics_learned),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    } else {
                        Text(
                            text = stringResource(R.string.learned_topics_format, state.learnedTopics.joinToString(", ")),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(spacing.medium))

            Button(
                onClick = onNavigateToQuiz,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(64.dp),
                shape = RoundedCornerShape(spacing.medium),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
            ) {
                Icon(
                    imageVector = Icons.Default.Quiz, 
                    contentDescription = null, 
                    tint = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Spacer(modifier = Modifier.width(spacing.medium))
                Column {
                    Text(
                        text = stringResource(R.string.daily_knowledge_quiz),
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Text(
                        text = stringResource(R.string.quiz_subtitle),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                    )
                }
                Spacer(modifier = Modifier.weight(1f))
                Icon(
                    imageVector = Icons.Default.ChevronRight, 
                    contentDescription = null, 
                    tint = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }

            Spacer(modifier = Modifier.height(spacing.large))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(spacing.large),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = spacing.default),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
            ) {
                Column(modifier = Modifier.padding(spacing.small)) {
                    ListItem(
                        headlineContent = { Text(text = stringResource(R.string.settings), fontWeight = FontWeight.Bold) },
                        leadingContent = { Icon(imageVector = Icons.Default.Settings, contentDescription = null) }
                    )
                    
                    ListItem(
                        headlineContent = { Text(text = stringResource(R.string.dark_mode)) },
                        trailingContent = {
                            Switch(
                                checked = state.isDarkMode,
                                onCheckedChange = { onToggleDarkMode() }
                            )
                        }
                    )

                    ListItem(
                        headlineContent = { Text(text = stringResource(R.string.daily_reminders)) },
                        leadingContent = { Icon(imageVector = Icons.Default.Notifications, contentDescription = null) },
                        trailingContent = { Switch(checked = true, onCheckedChange = {}) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(spacing.large))

            Button(
                onClick = onResetOnboarding,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(28.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
            ) {
                Text(
                    text = stringResource(R.string.reset_learning_goal),
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onError
                )
            }
            Spacer(modifier = Modifier.height(spacing.medium))
        }
    }
}
