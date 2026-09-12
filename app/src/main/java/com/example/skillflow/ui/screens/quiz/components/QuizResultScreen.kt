package com.example.skillflow.ui.screens.quiz.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.skillflow.R
import com.example.skillflow.ui.common.SkillflowTopAppBar
import com.example.skillflow.ui.screens.quiz.QuizState
import com.example.skillflow.ui.theme.*

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
                val userAnswer = state.userAnswers.getOrNull(index)
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
                                contentDescription = if (isCorrect) stringResource(R.string.status_mastered) else stringResource(R.string.deletion_failed),
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
