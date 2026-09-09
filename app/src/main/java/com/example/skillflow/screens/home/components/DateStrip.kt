package com.example.skillflow.screens.home.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.skillflow.R
import com.example.skillflow.viewModel.home.DateModel
import com.example.skillflow.screens.theme.GradientStart
import com.example.skillflow.screens.theme.SkillflowTheme
import com.example.skillflow.screens.theme.spacing

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
        // "All" Button
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

@Composable
fun DateItem(
    day: String,
    date: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val spacing = MaterialTheme.spacing
    val containerColor = if (isSelected) GradientStart else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
    val contentColor = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface

    Card(
        modifier = modifier
            .width(60.dp)
            .height(80.dp)
            .clip(RoundedCornerShape(spacing.medium))
            .clickable { onClick() },
        shape = RoundedCornerShape(spacing.medium),
        colors = CardDefaults.cardColors(containerColor = containerColor),
        elevation = CardDefaults.cardElevation(defaultElevation = if (isSelected) 4.dp else 0.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = day,
                style = MaterialTheme.typography.labelSmall,
                color = contentColor.copy(alpha = 0.7f),
                fontWeight = FontWeight.Medium
            )
            Spacer(modifier = Modifier.height(spacing.extraSmall))
            Text(
                text = date,
                style = MaterialTheme.typography.titleMedium,
                color = contentColor,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )
            if (isSelected) {
                Spacer(modifier = Modifier.height(4.dp))
                Box(modifier = Modifier.size(4.dp).clip(RoundedCornerShape(2.dp)).background(Color.White))
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DateStripPreview() {
    SkillflowTheme {
        Column(modifier = Modifier.padding(16.dp)) {
            DateStrip(
                dates = listOf(
                    DateModel("Mon", "12", "2026-08-12", true),
                    DateModel("Sun", "11", "2026-08-11", false)
                ),
                onDateSelected = {}
            )
        }
    }
}
