package com.example.skillflow.ui.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.skillflow.R
import com.example.skillflow.presentation.profile.ProfileViewModel
import com.example.skillflow.ui.theme.GradientEnd
import com.example.skillflow.ui.theme.GradientStart

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    onResetOnboarding: () -> Unit,
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()

    val backgroundGradient = Brush.verticalGradient(
        listOf(GradientStart.copy(alpha = 0.1f), MaterialTheme.colorScheme.background)
    )

    Scaffold(
        modifier = Modifier.background(backgroundGradient),
        topBar = { 
            CenterAlignedTopAppBar(
                title = { 
                    Text(
                        stringResource(R.string.my_profile), 
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    ) 
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Surface(
                shape = CircleShape,
                modifier = Modifier.size(100.dp),
                color = MaterialTheme.colorScheme.surface,
                tonalElevation = 4.dp,
                border = androidx.compose.foundation.BorderStroke(2.dp, GradientStart)
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = null,
                    modifier = Modifier.padding(20.dp),
                    tint = GradientStart
                )
            }
            
            Spacer(modifier = Modifier.height(20.dp))
            
            Text(
                text = stringResource(R.string.learner_profile),
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
            
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = GradientStart.copy(alpha = 0.1f),
                modifier = Modifier.padding(top = 8.dp)
            ) {
                Text(
                    text = stringResource(R.string.career_goal, state.careerPathId ?: "Not set"),
                    style = MaterialTheme.typography.labelLarge,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                    color = GradientStart,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(40.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
            ) {
                Column(modifier = Modifier.padding(8.dp)) {
                    ListItem(
                        headlineContent = { Text(stringResource(R.string.settings), fontWeight = FontWeight.Bold) },
                        leadingContent = { Icon(Icons.Default.Settings, contentDescription = null) }
                    )
                    
                    ListItem(
                        headlineContent = { Text(stringResource(R.string.dark_mode)) },
                        trailingContent = {
                            Switch(
                                checked = state.isDarkMode,
                                onCheckedChange = { viewModel.toggleDarkMode() }
                            )
                        }
                    )

                    ListItem(
                        headlineContent = { Text(stringResource(R.string.daily_reminders)) },
                        leadingContent = { Icon(Icons.Default.Notifications, contentDescription = null) },
                        trailingContent = { Switch(checked = true, onCheckedChange = {}) }
                    )
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = { 
                    viewModel.resetOnboarding()
                    onResetOnboarding()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .clip(RoundedCornerShape(28.dp))
                    .background(Brush.linearGradient(listOf(Color(0xFFFF5252), Color(0xFFFF1744)))),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent)
            ) {
                Text(stringResource(R.string.reset_learning_goal), fontWeight = FontWeight.Bold, color = Color.White)
            }
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
