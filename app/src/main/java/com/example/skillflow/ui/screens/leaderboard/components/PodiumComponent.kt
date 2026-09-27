package com.example.skillflow.ui.screens.leaderboard.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.skillflow.domain.model.LeaderboardUser

val GoldColor = Color(0xFFFFD700)
val SilverColor = Color(0xFFC0C0C0)
val BronzeColor = Color(0xFFCD7F32)

/**
 * Component for displaying the top 3 users in a podium arrangement.
 */
@Composable
fun PodiumComponent(
    topThreeUsers: List<LeaderboardUser>,
    modifier: Modifier = Modifier
) {
    if (topThreeUsers.isEmpty()) return

    val firstPlace = topThreeUsers.getOrNull(0)
    val secondPlace = topThreeUsers.getOrNull(1)
    val thirdPlace = topThreeUsers.getOrNull(2)

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 20.dp, horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.Bottom
            ) {
                // 2nd Place (Left)
                if (secondPlace != null) {
                    PodiumPillar(
                        user = secondPlace,
                        rank = 2,
                        pillarHeight = 90.dp,
                        avatarSize = 58.dp,
                        accentColor = SilverColor,
                        modifier = Modifier.weight(1f)
                    )
                } else {
                    Spacer(modifier = Modifier.weight(1f))
                }

                Spacer(modifier = Modifier.width(8.dp))

                // 1st Place (Middle - Highest)
                if (firstPlace != null) {
                    PodiumPillar(
                        user = firstPlace,
                        rank = 1,
                        pillarHeight = 120.dp,
                        avatarSize = 72.dp,
                        accentColor = GoldColor,
                        isFirstPlace = true,
                        modifier = Modifier.weight(1.1f)
                    )
                } else {
                    Spacer(modifier = Modifier.weight(1.1f))
                }

                Spacer(modifier = Modifier.width(8.dp))

                // 3rd Place (Right)
                if (thirdPlace != null) {
                    PodiumPillar(
                        user = thirdPlace,
                        rank = 3,
                        pillarHeight = 70.dp,
                        avatarSize = 54.dp,
                        accentColor = BronzeColor,
                        modifier = Modifier.weight(1f)
                    )
                } else {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

@Composable
private fun PodiumPillar(
    user: LeaderboardUser,
    rank: Int,
    pillarHeight: Dp,
    avatarSize: Dp,
    accentColor: Color,
    modifier: Modifier = Modifier,
    isFirstPlace: Boolean = false
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Trophy Header for 1st Place
        if (isFirstPlace) {
            Icon(
                imageVector = Icons.Default.EmojiEvents,
                contentDescription = "First Place Trophy",
                tint = GoldColor,
                modifier = Modifier
                    .size(28.dp)
                    .padding(bottom = 4.dp)
            )
        }

        // User Avatar with Rank Badge
        Box(
            contentAlignment = Alignment.TopCenter,
            modifier = Modifier.padding(bottom = 6.dp)
        ) {
            Surface(
                modifier = Modifier
                    .size(avatarSize)
                    .border(3.dp, accentColor, CircleShape),
                shape = CircleShape,
                color = MaterialTheme.colorScheme.primaryContainer
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = user.name.take(2).uppercase(),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }

            // Rank Badge Overlay
            Surface(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .offset(y = 8.dp),
                shape = CircleShape,
                color = accentColor,
                shadowElevation = 4.dp
            ) {
                Text(
                    text = "#$rank",
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.Black
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Name
        Text(
            text = user.name,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = if (isFirstPlace) FontWeight.Bold else FontWeight.SemiBold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )

        // XP
        Text(
            text = "${user.xp} XP",
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Podium Block
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .height(pillarHeight),
            shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
            color = accentColor.copy(alpha = if (isFirstPlace) 0.25f else 0.15f),
            border = BorderStroke(1.dp, accentColor.copy(alpha = 0.5f))
        ) {
            Box(
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "$rank",
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.Black,
                    color = accentColor.copy(alpha = 0.8f)
                )
            }
        }
    }
}
