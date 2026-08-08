package com.example.skillflow.ui.common

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.skillflow.R
import com.example.skillflow.ui.theme.GradientStart
import com.example.skillflow.ui.theme.SkillflowTheme
import com.example.skillflow.ui.theme.spacing

/**
 * A reusable loading view with a centered progress indicator.
 */
@Composable
fun LoadingView(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(color = GradientStart)
    }
}

/**
 * A reusable error view with a message and retry button.
 */
@Composable
fun ErrorView(
    message: String,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(MaterialTheme.spacing.large),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = message,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.error,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(MaterialTheme.spacing.medium))
        Button(onClick = onRetry) {
            Text(text = "Retry")
        }
    }
}

/**
 * A reusable empty state view with a descriptive message.
 */
@Composable
fun EmptyView(
    message: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = message,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(MaterialTheme.spacing.large)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun StateViewsPreview() {
    SkillflowTheme {
        Column {
            LoadingView(modifier = Modifier.height(100.dp))
            ErrorView(message = "Connection timeout", onRetry = {}, modifier = Modifier.height(100.dp))
            EmptyView(message = "No data found", modifier = Modifier.height(100.dp))
        }
    }
}
