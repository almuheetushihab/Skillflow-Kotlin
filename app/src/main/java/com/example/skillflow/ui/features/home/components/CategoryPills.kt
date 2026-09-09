package com.example.skillflow.ui.features.home.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.EventAvailable
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Today
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import com.example.skillflow.R
import com.example.skillflow.ui.features.home.DateModel
import com.example.skillflow.ui.theme.spacing
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

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
        item {
            CategoryPillItem(
                label = stringResource(R.string.all_lessons),
                icon = Icons.Default.Dashboard,
                isSelected = selectedDate == null,
                onClick = { onDateSelected(null) }
            )
        }

        item {
            CategoryPillItem(
                label = stringResource(R.string.todays_nuggets),
                icon = Icons.Default.Today,
                isSelected = selectedDate == todayDateStr,
                onClick = { onDateSelected(todayDateStr) }
            )
        }

        items(availableDates.filter { it.fullDate != todayDateStr }) { dateModel ->
            val icon = getIconForDate(dateModel.fullDate, todayDateStr)
            CategoryPillItem(
                label = "${dateModel.dayName} ${dateModel.dateDisplay}",
                icon = icon,
                isSelected = selectedDate == dateModel.fullDate,
                onClick = { onDateSelected(dateModel.fullDate) }
            )
        }

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
