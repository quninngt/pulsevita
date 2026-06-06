package com.healthapp.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * 成就数据类
 */
data class Achievement(
    val id: String,
    val icon: String,
    val title: String,
    val description: String,
    val unlocked: Boolean,
    val progress: Float = if (unlocked) 1f else 0f
)

/**
 * 成就系统 - 徽章卡片
 */
@Composable
fun AchievementBadge(
    achievement: Achievement,
    modifier: Modifier = Modifier
) {
    val alpha by animateFloatAsState(
        targetValue = if (achievement.unlocked) 1f else 0.4f,
        animationSpec = tween(500),
        label = "badgeAlpha"
    )

    Card(
        modifier = modifier.width(100.dp),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (achievement.unlocked) 4.dp else 1.dp
        ),
        colors = CardDefaults.cardColors(
            containerColor = if (achievement.unlocked)
                MaterialTheme.colorScheme.primaryContainer
            else
                MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier
                .padding(12.dp)
                .graphicsLayer { this.alpha = alpha },
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = achievement.icon,
                fontSize = 32.sp
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = achievement.title,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                maxLines = 1
            )
            Spacer(modifier = Modifier.height(4.dp))
            if (!achievement.unlocked) {
                LinearProgressIndicator(
                    progress = achievement.progress,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(4.dp),
                    color = MaterialTheme.colorScheme.primary,
                    trackColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = achievement.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    fontSize = 9.sp
                )
            } else {
                Text(
                    text = "已解锁",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

/**
 * 成就展示横向列表
 */
@Composable
fun AchievementRow(
    achievements: List<Achievement>,
    modifier: Modifier = Modifier
) {
    LazyRow(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(horizontal = 16.dp)
    ) {
        items(achievements) { achievement ->
            AchievementBadge(achievement = achievement)
        }
    }
}

/**
 * 根据用户的打卡数据生成成就列表
 */
fun generateAchievements(
    streakDays: Int,
    totalWaterDays: Int,
    totalExerciseDays: Int,
    totalMoodDays: Int
): List<Achievement> {
    return listOf(
        Achievement(
            id = "streak_3",
            icon = "🔥",
            title = "三天坚持",
            description = "连续打卡3天",
            unlocked = streakDays >= 3,
            progress = (streakDays.toFloat() / 3f).coerceAtMost(1f)
        ),
        Achievement(
            id = "streak_7",
            icon = "⭐",
            title = "一周达人",
            description = "连续打卡7天",
            unlocked = streakDays >= 7,
            progress = (streakDays.toFloat() / 7f).coerceAtMost(1f)
        ),
        Achievement(
            id = "streak_30",
            icon = "👑",
            title = "月度冠军",
            description = "连续打卡30天",
            unlocked = streakDays >= 30,
            progress = (streakDays.toFloat() / 30f).coerceAtMost(1f)
        ),
        Achievement(
            id = "water_7",
            icon = "💧",
            title = "水润达人",
            description = "饮水记录7天",
            unlocked = totalWaterDays >= 7,
            progress = (totalWaterDays.toFloat() / 7f).coerceAtMost(1f)
        ),
        Achievement(
            id = "exercise_7",
            icon = "🏃",
            title = "运动健将",
            description = "运动记录7天",
            unlocked = totalExerciseDays >= 7,
            progress = (totalExerciseDays.toFloat() / 7f).coerceAtMost(1f)
        ),
        Achievement(
            id = "mood_7",
            icon = "😊",
            title = "情绪达人",
            description = "心情记录7天",
            unlocked = totalMoodDays >= 7,
            progress = (totalMoodDays.toFloat() / 7f).coerceAtMost(1f)
        )
    )
}
