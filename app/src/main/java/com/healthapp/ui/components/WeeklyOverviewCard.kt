package com.healthapp.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.healthapp.ui.theme.*
import com.healthapp.ui.theme.PulseVitaTheme

/**
 * 周概览卡片组件
 * 四宫格展示本周各维度健康数据
 */
@Composable
fun WeeklyOverviewCard(
    waterDays: Int,
    exerciseDays: Int,
    moodDays: Int,
    dietDays: Int,
    waterTrend: String,  // "↑" or "↓" or "→"
    exerciseTrend: String,
    moodTrend: String,
    dietTrend: String,
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
                text = "📊 本周概览",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // 四宫格
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // 饮水
                WeeklyStatItem(
                    icon = Icons.Default.WaterDrop,
                    label = "饮水",
                    days = waterDays,
                    trend = waterTrend,
                    color = PulseVitaTheme.currentScheme().chartBlue,
                    modifier = Modifier.weight(1f)
                )
                
                // 运动
                WeeklyStatItem(
                    icon = Icons.Default.DirectionsWalk,
                    label = "运动",
                    days = exerciseDays,
                    trend = exerciseTrend,
                    color = PulseVitaTheme.currentScheme().chartGreen,
                    modifier = Modifier.weight(1f)
                )
                
                // 心情
                WeeklyStatItem(
                    icon = Icons.Default.Psychology,
                    label = "心情",
                    days = moodDays,
                    trend = moodTrend,
                    color = PulseVitaTheme.currentScheme().success,
                    modifier = Modifier.weight(1f)
                )
                
                // 饮食
                WeeklyStatItem(
                    icon = Icons.Default.Restaurant,
                    label = "饮食",
                    days = dietDays,
                    trend = dietTrend,
                    color = PulseVitaTheme.currentScheme().chartOrange,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

/**
 * 单个周统计项
 */
@Composable
private fun WeeklyStatItem(
    icon: ImageVector,
    label: String,
    days: Int,
    trend: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = color.copy(alpha = 0.1f)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = color,
                modifier = Modifier.size(24.dp)
            )
            
            Spacer(modifier = Modifier.height(4.dp))
            
            Text(
                text = "${days}天",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = color
            )
            
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = label,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = trend,
                    style = MaterialTheme.typography.bodySmall,
                    color = when (trend) {
                        "↑" -> PulseVitaTheme.currentScheme().success
                        "↓" -> PulseVitaTheme.currentScheme().error
                        else -> PulseVitaTheme.currentScheme().textSecondary
                    }
                )
            }
        }
    }
}