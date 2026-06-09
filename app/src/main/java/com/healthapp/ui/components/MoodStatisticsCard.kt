package com.healthapp.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.healthapp.ui.theme.*
import com.healthapp.ui.theme.PulseVitaTheme

/**
 * 心情统计卡片
 * 展示本周心情统计数据
 */
@Composable
fun MoodStatisticsCard(
    weeklyAverage: Float,
    bestDay: String,
    worstDay: String,
    trend: String, // "improving" / "stable" / "declining"
    monthlyAverage: Float = 0f,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "📊 心情统计",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // 平均心情
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "本周平均心情",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = String.format("%.1f 分", weeklyAverage),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = when {
                        weeklyAverage >= 4.0 -> PulseVitaTheme.currentScheme().success
                        weeklyAverage >= 3.0 -> PulseVitaTheme.currentScheme().warning
                        else -> PulseVitaTheme.currentScheme().error
                    }
                )
            }

            // 月平均心情（如果有数据）
            if (monthlyAverage > 0f) {
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "本月平均心情",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = String.format("%.1f 分", monthlyAverage),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = when {
                            monthlyAverage >= 4.0 -> PulseVitaTheme.currentScheme().success
                            monthlyAverage >= 3.0 -> PulseVitaTheme.currentScheme().warning
                            else -> PulseVitaTheme.currentScheme().error
                        }
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // 最好/最差的一天
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "😊 最好",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = bestDay,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "😔 最差",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = worstDay,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // 趋势
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = when (trend) {
                        "improving" -> Icons.Default.TrendingUp
                        "declining" -> Icons.Default.TrendingDown
                        else -> Icons.Default.TrendingFlat
                    },
                    contentDescription = trend,
                    tint = when (trend) {
                        "improving" -> PulseVitaTheme.currentScheme().success
                        "declining" -> PulseVitaTheme.currentScheme().error
                        else -> PulseVitaTheme.currentScheme().textSecondary
                    },
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = when (trend) {
                        "improving" -> "心情在改善 👍"
                        "declining" -> "心情在下降，注意调节"
                        else -> "心情稳定"
                    },
                    style = MaterialTheme.typography.bodyMedium,
                    color = when (trend) {
                        "improving" -> PulseVitaTheme.currentScheme().success
                        "declining" -> PulseVitaTheme.currentScheme().error
                        else -> PulseVitaTheme.currentScheme().textSecondary
                    }
                )
            }
        }
    }
}