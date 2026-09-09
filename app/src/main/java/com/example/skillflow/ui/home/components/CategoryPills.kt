package com.example.skillflow.ui.home.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.EventAvailable
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Today
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.skillflow.R
import com.example.skillflow.presentation.home.DateModel
import com.example.skillflow.ui.theme.GradientStart
import com.example.skillflow.ui.theme.SkillflowTheme
import com.example.skillflow.ui.theme.spacing
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Simplified Category / Date filter pills with distinct icons.
 */
@Composable
fun CategoryPills(
    selectedDate: String?,
    availableDates: List<DateModel>,
    onDateSelected: (String?) -> Unit,
    onOpenDatePicker: () -> Unit,
    modifier: Modifier = Modifier
) {
    val spacing = MaterialTheme.spacing
    val todayDateStr = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

    LazyRow(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(spacing.small),
        contentPadding = PaddingValues(horizontal = spacing.extraSmall)
    ) {
        // Pill 1: "All"
        item {
            CategoryPillItem(
                label = stringResource(R.string.all_lessons),
                icon = Icons.Default.Dashboard,
                isSelected = selectedDate == null,
                onClick = { onDateSelected(null) }
            )
        }

        // Pill 2: "Today"
        item {
            CategoryPillItem(
                label = stringResource(R.string.todays_nuggets),
                icon = Icons.Default.Today,
                isSelected = selectedDate == todayDateStr,
                onClick = { onDateSelected(todayDateStr) }
            )
        }

        // Pill 3..N: Day pills with distinct icons
        items(availableDates.filter { it.fullDate != todayDateStr }) { dateModel ->
            val icon = getIconForDate(dateModel.fullDate, todayDateStr)
            CategoryPillItem(
                label = "${dateModel.dayName} ${dateModel.dateDisplay}",
                icon = icon,
                isSelected = selectedDate == dateModel.fullDate,
                onClick = { onDateSelected(dateModel.fullDate) }
            )
        }

        // Pill N+1: "Calendar / Custom Date"
        item {
            val isCustomDateSelected = selectedDate != null &&
                    selectedDate != todayDateStr &&
                    availableDates.none { it.fullDate == selectedDate }

            CategoryPillItem(
                label = if (selectedDate != null && isCustomDateSelected) selectedDate else stringResource(R.string.select_date),
                icon = Icons.Default.CalendarMonth,
                isSelected = isCustomDateSelected,
                onClick = onOpenDatePicker
            )
        }
    }
}

/**
 * Returns a distinct icon based on how recent the date is relative to today.
 */
private fun getIconForDate(fullDate: String, todayDateStr: String): ImageVector {
    return when {
        fullDate == todayDateStr -> Icons.Default.Today
        isYesterday(fullDate) -> Icons.Default.History
        else -> Icons.Default.EventAvailable
    }
}

private fun isYesterday(fullDate: String): Boolean {
    val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    return try {
        val date = sdf.parse(fullDate)
        val today = Date()
        if (date != null) {
            val diff = (today.time - date.time) / (1000 * 60 * 60 * 24)
            diff == 1L
        } else false
    } catch (_: Exception) {
        false
    }
}

/**
 * Single Category Pill item with a distinct icon and smooth animated state transition.
 */
@Composable
fun CategoryPillItem(
    label: String,
    icon: ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val spacing = MaterialTheme.spacing
    val containerColor by animateColorAsState(
        targetValue = if (isSelected) GradientStart else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f),
        animationSpec = spring(),
        label = "PillContainer"
    )
    val contentColor by animateColorAsState(
        targetValue = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface,
        animationSpec = spring(),
        label = "PillContent"
    )

    Surface(
        modifier = Modifier
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
                contentDescription = null,
                tint = if (isSelected) Color.White else GradientStart,
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
fun CategoryPillsPreview() {
    SkillflowTheme {
        Column(modifier = Modifier.padding(16.dp)) {
            CategoryPills(
                selectedDate = null,
                availableDates = listOf(
                    DateModel("Mon", "12", "2026-08-12", false),
                    DateModel("Sun", "11", "2026-08-11", false)
                ),
                onDateSelected = {},
                onOpenDatePicker = {}
            )
        }
    }
}
