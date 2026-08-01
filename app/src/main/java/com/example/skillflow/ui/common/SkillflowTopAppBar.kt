package com.example.skillflow.ui.common

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.skillflow.ui.theme.GradientEnd
import com.example.skillflow.ui.theme.GradientStart

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SkillflowTopAppBar(
    title: String,
    modifier: Modifier = Modifier,
    navigationIcon: ImageVector? = null,
    onNavigationClick: () -> Unit = {},
    actions: @Composable RowScope.() -> Unit = {},
    useGradient: Boolean = false
) {
    val containerColor = if (useGradient) Color.Transparent else MaterialTheme.colorScheme.background
    
    Box(
        modifier = modifier
            .fillMaxWidth()
            .then(
                if (useGradient) Modifier.background(Brush.horizontalGradient(listOf(GradientStart, GradientEnd)))
                else Modifier
            )
    ) {
        CenterAlignedTopAppBar(
            title = {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.ExtraBold,
                    color = if (useGradient) Color.White else MaterialTheme.colorScheme.primary
                )
            },
            navigationIcon = {
                if (navigationIcon != null) {
                    IconButton(onClick = onNavigationClick) {
                        Icon(
                            imageVector = navigationIcon,
                            contentDescription = "Back",
                            tint = if (useGradient) Color.White else MaterialTheme.colorScheme.primary
                        )
                    }
                }
            },
            actions = actions,
            colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                containerColor = containerColor
            )
        )
    }
}
