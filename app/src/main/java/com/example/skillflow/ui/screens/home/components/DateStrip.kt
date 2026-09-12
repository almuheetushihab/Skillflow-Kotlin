package com.example.skillflow.ui.screens.home.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.example.skillflow.R
import com.example.skillflow.ui.screens.home.DateModel
import com.example.skillflow.ui.theme.spacing

@Composable
fun DateStrip(
    dates: List<DateModel>,
    onDateSelected: (String?) -> Unit,
    modifier: Modifier = Modifier
) {
    val spacing = MaterialTheme.spacing
    val allText = stringResource(R.string.all_short)

    LazyRow(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(spacing.small),
        contentPadding = PaddingValues(horizontal = spacing.default)
    ) {
        item {
            DateItem(
                day = allText,
                date = "∞",
                isSelected = dates.none { it.isSelected },
                onClick = { onDateSelected(null) }
            )
        }
        
        items(dates.reversed()) { dateModel ->
            DateItem(
                day = dateModel.dayName,
                date = dateModel.dateDisplay,
                isSelected = dateModel.isSelected,
                onClick = { onDateSelected(dateModel.fullDate) }
            )
        }
    }
}
