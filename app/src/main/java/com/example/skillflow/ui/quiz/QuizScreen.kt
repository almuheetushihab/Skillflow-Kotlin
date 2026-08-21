package com.example.skillflow.ui.quiz

import android.app.Activity
import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.skillflow.R
import com.example.skillflow.domain.manager.PlayStoreManager
import com.example.skillflow.domain.model.QuizQuestion
import com.example.skillflow.presentation.quiz.QuizState
import com.example.skillflow.presentation.quiz.QuizUiEvent
import com.example.skillflow.presentation.quiz.QuizViewModel
import com.example.skillflow.ui.common.SkillflowTopAppBar
import com.example.skillflow.ui.theme.*

@Composable
fun QuizScreen(
    onFinish: () -> Unit,
    playStoreManager: PlayStoreManager,
    modifier: Modifier = Modifier,
    viewModel: QuizViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                is QuizUiEvent.RequestReview -> {
                    (context as? Activity)?.let { activity ->
                        playStoreManager.requestReview(activity)
                    }
                }
            }
        }
    }

    if (state.isFinished) {
        QuizResultScreen(state = state, onFinish = onFinish, modifier = modifier)
    } else if (state.questions.isNotEmpty()) {
        QuizContent(
            state = state,
            onOptionSelected = viewModel::onOptionSelected,
            onSubmit = viewModel::submitAnswer,
            onNext = viewModel::nextQuestion,
            onFinish = onFinish,
            modifier = modifier
        )
    } else {
        Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = GradientStart)
        }
    }
}

@Composable
fun QuizContent(
    state: QuizState,
    onOptionSelected: (Int) -> Unit,
    onSubmit: () -> Unit,
    onNext: () -> Unit,
    onFinish: () -> Unit,
    modifier: Modifier = Modifier
) {
    val spacing = MaterialTheme.spacing
    val question = state.questions[state.currentIndex]

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            SkillflowTopAppBar(
                title = stringResource(R.string.daily_quiz),
                actions = {
                    Text(
                        text = stringResource(R.string.daily_progress_format, state.currentIndex + 1, state.questions.size),
                        modifier = Modifier.padding(end = spacing.medium),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(spacing.large)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            LinearProgressIndicator(
                progress = { (state.currentIndex + 1).toFloat() / state.questions.size },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(spacing.small)
                    .clip(RoundedCornerShape(spacing.extraSmall)),
                color = GradientStart,
                trackColor = GradientStart.copy(alpha = 0.1f)
            )
            
            Spacer(modifier = Modifier.height(spacing.extraLarge))

            Text(
                text = question.text,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
            
            Spacer(modifier = Modifier.height(spacing.extraLarge))

            question.options.forEachIndexed { index, option ->
                val isSelected = state.selectedOption == index
                val isCorrect = index == question.correctAnswerIndex
                
                val containerColor = when {
                    state.showFeedback && isCorrect -> BeginnerGreen.copy(alpha = 0.1f)
                    state.showFeedback && isSelected && !isCorrect -> AdvancedRed.copy(alpha = 0.1f)
                    isSelected -> MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
                    else -> MaterialTheme.colorScheme.surface
                }
                
                val borderColor = when {
                    state.showFeedback && isCorrect -> BeginnerGreen
                    state.showFeedback && isSelected && !isCorrect -> AdvancedRed
                    isSelected -> GradientStart
                    else -> MaterialTheme.colorScheme.outlineVariant
                }

                OutlinedCard(
                    onClick = { onOptionSelected(index) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = spacing.medium),
                    shape = RoundedCornerShape(spacing.medium),
                    colors = CardDefaults.outlinedCardColors(containerColor = containerColor),
                    border = BorderStroke(2.dp, borderColor)
                ) {
                    Row(
                        modifier = Modifier.padding(spacing.medium),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = isSelected,
                            onClick = null,
                            colors = RadioButtonDefaults.colors(selectedColor = GradientStart)
                        )
                        Spacer(modifier = Modifier.width(spacing.small))
                        Text(
                            text = option,
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                        )
                        
                        if (state.showFeedback) {
                            Spacer(modifier = Modifier.weight(1f))
                            if (isCorrect) {
                                Icon(imageVector = Icons.Default.CheckCircle, contentDescription = null, tint = BeginnerGreen)
                            } else if (isSelected) {
                                Icon(imageVector = Icons.Default.Cancel, contentDescription = null, tint = AdvancedRed)
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(spacing.large))

            AnimatedVisibility(
                visible = state.showFeedback,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                    shape = RoundedCornerShape(spacing.medium)
                ) {
                    Column(modifier = Modifier.padding(spacing.medium)) {
                        Text(
                            text = stringResource(R.string.explanation_label),
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold,
                            color = GradientStart
                        )
                        Text(
                            text = question.explanation,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(spacing.extraLarge))

            if (!state.showFeedback) {
                Button(
                    onClick = onSubmit,
                    enabled = state.selectedOption != null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(28.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = GradientStart)
                ) {
                    Text(text = stringResource(R.string.submit), fontWeight = FontWeight.Bold)
                }
            } else {
                Button(
                    onClick = onNext,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(28.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = GradientStart)
                ) {
                    Text(
                        text = if (state.currentIndex == state.questions.size - 1) 
                            stringResource(R.string.finish_quiz) 
                        else 
                            stringResource(R.string.next_question),
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
fun QuizResultScreen(
    state: QuizState,
    onFinish: () -> Unit,
    modifier: Modifier = Modifier
) {
    val spacing = MaterialTheme.spacing
    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = { SkillflowTopAppBar(title = stringResource(R.string.quiz_results)) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(spacing.large)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            val percentage = (state.score.toFloat() / state.questions.size * 100).toInt()
            
            Box(contentAlignment = Alignment.Center, modifier = Modifier.size(200.dp)) {
                CircularProgressIndicator(
                    progress = { state.score.toFloat() / state.questions.size },
                    modifier = Modifier.fillMaxSize(),
                    strokeWidth = 12.dp,
                    color = if (percentage >= 70) BeginnerGreen else GradientStart,
                    trackColor = MaterialTheme.colorScheme.surfaceVariant
                )
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "$percentage%",
                        style = MaterialTheme.typography.displayMedium,
                        fontWeight = FontWeight.ExtraBold
                    )
                    Text(
                        text = stringResource(R.string.correct_count, state.score, state.questions.size),
                        style = MaterialTheme.typography.titleMedium
                    )
                }
            }

            Spacer(modifier = Modifier.height(spacing.extraLarge))

            Text(
                text = if (percentage >= 70) stringResource(R.string.great_job) else stringResource(R.string.keep_learning),
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(spacing.extraLarge))

            Text(
                text = stringResource(R.string.detailed_feedback),
                modifier = Modifier.fillMaxWidth(),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(spacing.medium))

            state.questions.forEachIndexed { index, question ->
                val userAnswer = state.userAnswers[index]
                val isCorrect = userAnswer == question.correctAnswerIndex
                
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = spacing.medium),
                    shape = RoundedCornerShape(spacing.medium),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isCorrect) BeginnerGreen.copy(alpha = 0.1f) else AdvancedRed.copy(alpha = 0.1f)
                    )
                ) {
                    Column(modifier = Modifier.padding(spacing.medium)) {
                        Row(verticalAlignment = Alignment.Top) {
                            Text(text = "${index + 1}. ", fontWeight = FontWeight.Bold)
                            Text(text = question.text, modifier = Modifier.weight(1f))
                            Icon(
                                imageVector = if (isCorrect) Icons.Default.CheckCircle else Icons.Default.Cancel,
                                contentDescription = null,
                                tint = if (isCorrect) BeginnerGreen else AdvancedRed
                            )
                        }
                        Spacer(modifier = Modifier.height(spacing.small))
                        val answerText = if (userAnswer != null) question.options[userAnswer] else stringResource(R.string.skipped)
                        Text(
                            text = stringResource(R.string.your_answer, answerText),
                            style = MaterialTheme.typography.bodySmall,
                            color = if (isCorrect) BeginnerGreenDark else AdvancedRedDark
                        )
                        if (!isCorrect) {
                            Text(
                                text = stringResource(R.string.correct_answer, question.options[question.correctAnswerIndex]),
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.Bold,
                                color = BeginnerGreenDark
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(spacing.extraLarge))

            Button(
                onClick = onFinish,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(28.dp),
                colors = ButtonDefaults.buttonColors(containerColor = GradientStart)
            ) {
                Text(text = stringResource(R.string.back_to_profile), fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun QuizContentPreview() {
    SkillflowTheme {
        QuizContent(
            state = QuizState(
                questions = listOf(
                    QuizQuestion("1", "1", "Question Text", listOf("Option 1", "Option 2"), 0, "Explanation")
                )
            ),
            onOptionSelected = {},
            onSubmit = {},
            onNext = {},
            onFinish = {}
        )
    }
}
