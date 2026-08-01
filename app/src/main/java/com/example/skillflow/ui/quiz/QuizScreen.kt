package com.example.skillflow.ui.quiz

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import com.example.skillflow.R
import com.example.skillflow.ui.common.SkillflowTopAppBar
import com.example.skillflow.ui.theme.SkillflowTheme
import com.example.skillflow.ui.theme.spacing

@Composable
fun QuizScreen(onFinish: () -> Unit) {
    // In a real app, this would be in a ViewModel
    val questions = listOf(
        QuizQuestion(
            "What is Jetpack Compose?",
            listOf("A UI Toolkit", "A Database", "A Network Library"),
            0
        )
    )
    
    var currentQuestionIndex by remember { mutableIntStateOf(0) }
    var selectedOption by remember { mutableStateOf<Int?>(null) }
    var showFeedback by remember { mutableStateOf(false) }

    QuizContent(
        question = questions[currentQuestionIndex],
        currentQuestionIndex = currentQuestionIndex,
        selectedOption = selectedOption,
        showFeedback = showFeedback,
        onOptionSelected = { if (!showFeedback) selectedOption = it },
        onSubmit = { showFeedback = true },
        onFinish = onFinish
    )
}

@Composable
fun QuizContent(
    question: QuizQuestion,
    currentQuestionIndex: Int,
    selectedOption: Int?,
    showFeedback: Boolean,
    onOptionSelected: (Int) -> Unit,
    onSubmit: () -> Unit,
    onFinish: () -> Unit
) {
    Scaffold(
        topBar = {
            SkillflowTopAppBar(title = stringResource(R.string.daily_quiz))
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(MaterialTheme.spacing.large),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = stringResource(R.string.question_count, currentQuestionIndex + 1),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(MaterialTheme.spacing.medium))
            Text(
                text = question.text,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(MaterialTheme.spacing.extraLarge))

            question.options.forEachIndexed { index, option ->
                val isSelected = selectedOption == index
                val color = if (showFeedback) {
                    if (index == question.correctAnswerIndex) MaterialTheme.colorScheme.primary
                    else if (isSelected) MaterialTheme.colorScheme.error
                    else MaterialTheme.colorScheme.surface
                } else {
                    if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface
                }

                OutlinedCard(
                    onClick = { onOptionSelected(index) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = MaterialTheme.spacing.small),
                    colors = CardDefaults.outlinedCardColors(containerColor = color)
                ) {
                    Row(
                        modifier = Modifier.padding(MaterialTheme.spacing.medium),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = isSelected,
                            onClick = null
                        )
                        Spacer(modifier = Modifier.width(MaterialTheme.spacing.small))
                        Text(text = option)
                    }
                }
            }

            Spacer(modifier = Modifier.height(MaterialTheme.spacing.extraLarge))

            if (!showFeedback) {
                Button(
                    onClick = onSubmit,
                    enabled = selectedOption != null,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(stringResource(R.string.submit))
                }
            } else {
                Button(
                    onClick = onFinish,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(stringResource(R.string.finish_quiz))
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun QuizContentPreview() {
    SkillflowTheme {
        QuizContent(
            question = QuizQuestion(
                "What is Jetpack Compose?",
                listOf("A UI Toolkit", "A Database", "A Network Library"),
                0
            ),
            currentQuestionIndex = 0,
            selectedOption = null,
            showFeedback = false,
            onOptionSelected = {},
            onSubmit = {},
            onFinish = {}
        )
    }
}

data class QuizQuestion(
    val text: String,
    val options: List<String>,
    val correctAnswerIndex: Int
)
