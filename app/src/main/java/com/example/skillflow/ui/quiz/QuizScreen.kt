package com.example.skillflow.ui.quiz

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuizScreen(onFinish: () -> Unit) {
    // Mock quiz data
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

    val question = questions[currentQuestionIndex]

    Scaffold(
        topBar = { TopAppBar(title = { Text("Daily Quiz") }) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Question ${currentQuestionIndex + 1}",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = question.text,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(32.dp))

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
                    onClick = { if (!showFeedback) selectedOption = index },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp),
                    colors = CardDefaults.outlinedCardColors(containerColor = color)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = isSelected,
                            onClick = null
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = option)
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            if (!showFeedback) {
                Button(
                    onClick = { showFeedback = true },
                    enabled = selectedOption != null,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Submit")
                }
            } else {
                Button(
                    onClick = onFinish,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Finish Quiz")
                }
            }
        }
    }
}

data class QuizQuestion(
    val text: String,
    val options: List<String>,
    val correctAnswerIndex: Int
)
