package com.example.skillflow.ui.screens.home.components

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun BentoGrid(
    completedCount: Int,
    totalCount: Int,
    streakCount: Int,
    savedCount: Int,
    onSavedTileClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(204.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        BentoProgressTile(
            completedCount = completedCount,
            totalCount = totalCount,
            modifier = Modifier
                .weight(1.3f)
                .fillMaxHeight()
        )

        Column(
            modifier = Modifier
                .weight(0.9f)
                .fillMaxHeight(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            BentoStreakTile(
                streakCount = streakCount,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            )

            BentoSavedTile(
                savedCount = savedCount,
                onClick = onSavedTileClick,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            )
        }
    }
}
