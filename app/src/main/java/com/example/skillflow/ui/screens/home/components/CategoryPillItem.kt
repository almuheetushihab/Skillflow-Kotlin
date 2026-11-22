package com.example.skillflow.ui.screens.home.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Today
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.skillflow.ui.theme.GradientStart
import com.example.skillflow.ui.theme.OnPrimary
import com.example.skillflow.ui.theme.SkillflowTheme
import com.example.skillflow.ui.theme.spacing

@Composable
fun CategoryPillItem(
    label: String,
    icon: ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val spacing = MaterialTheme.spacing
    val containerColor by animateColorAsState(
        targetValue = if (isSelected) GradientStart else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f),
        animationSpec = spring(),
        label = "PillContainer"
    )
    val contentColor by animateColorAsState(
        targetValue = if (isSelected) OnPrimary else MaterialTheme.colorScheme.onSurface,
        animationSpec = spring(),
        label = "PillContent"
    )

    Surface(
        modifier = modifier
            .height(42.dp)
            .clickable { onClick() },
        shape = CircleShape,
        color = containerColor,
        tonalElevation = if (isSelected) 4.dp else 0.dp,
        shadowElevation = if (isSelected) 6.dp else 0.dp
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.padding(horizontal = spacing.medium, vertical = spacing.extraSmall)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = if (isSelected) OnPrimary else GradientStart,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.labelLarge,
                color = contentColor,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                fontSize = 13.sp
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun CategoryPillItemPreview() {
    SkillflowTheme {
        CategoryPillItem(
            label = "Today's Lessons",
            icon = Icons.Default.Today,
            isSelected = true,
            onClick = {}
        )
    }
}

