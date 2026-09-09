package com.example.skillflow.ui.common

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.skillflow.domain.model.ComplexityLevel
import com.example.skillflow.ui.theme.*

@Composable
fun ComplexityBadge(
    complexity: String,
    modifier: Modifier = Modifier
) {
    val level = ComplexityLevel.fromString(complexity)
    val (bgColor, textColor) = when (level) {
        ComplexityLevel.BEGINNER -> BeginnerGreen.copy(alpha = 0.1f) to BeginnerGreenDark
        ComplexityLevel.INTERMEDIATE -> IntermediateOrange.copy(alpha = 0.1f) to IntermediateOrangeDark
        ComplexityLevel.ADVANCED -> AdvancedRed.copy(alpha = 0.1f) to AdvancedRedDark
    }

    Surface(
        color = bgColor,
        shape = RoundedCornerShape(MaterialTheme.spacing.small),
        modifier = modifier
    ) {
        Text(
            text = level.levelName,
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
            style = MaterialTheme.typography.labelSmall,
            color = textColor,
            fontWeight = FontWeight.Bold
        )
    }
}
