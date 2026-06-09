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
 * 运动类型统计卡片
 * 展示各运动类型的今日统计（网格布局）
 */
@Composable
fun ExerciseTypeStatsCard(
    walkingCount: Int,
    runningCount: Int = 0,
    cyclingCount: Int = 0,
    yogaCount: Int,
    stretchingCount: Int = 0,
    strengthCount: Int = 0,
    swimmingCount: Int = 0,
    officeExerciseCount: Int,
    modifier: Modifier = Modifier
) {
    val scheme = PulseVitaTheme.currentScheme()

    // 只显示有记录的类型，最多显示6个
    data class TypeStat(val icon: ImageVector, val label: String, val count: Int, val color: Color)

    val allTypes = listOf(
        TypeStat(Icons.Default.DirectionsWalk, "步行", walkingCount, scheme.chartGreen),
        TypeStat(Icons.Default.DirectionsRun, "跑步", runningCount, scheme.chartOrange),
        TypeStat(Icons.Default.PedalBike, "骑行", cyclingCount, scheme.chartBlue),
        TypeStat(Icons.Default.SelfImprovement, "瑜伽", yogaCount, scheme.chartPurple),
        TypeStat(Icons.Default.Accessible, "拉伸", stretchingCount, scheme.chartTeal),
        TypeStat(Icons.Default.FitnessCenter, "力量", strengthCount, scheme.chartRed),
        TypeStat(Icons.Default.Pool, "游泳", swimmingCount, scheme.chartCyan),
        TypeStat(Icons.Default.Desk, "办公", officeExerciseCount, scheme.chartYellow)
    )

    // 过滤有记录的类型，如果没有则显示前3个
    val activeTypes = allTypes.filter { it.count > 0 }.ifEmpty { allTypes.take(3) }

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "📊 今日运动统计",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(12.dp))

            // 网格布局：每行3个
            activeTypes.chunked(3).forEach { rowTypes ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    rowTypes.forEach { type ->
                        ExerciseTypeItem(
                            icon = type.icon,
                            label = type.label,
                            count = type.count,
                            color = type.color,
                            modifier = Modifier.weight(1f)
                        )
                    }
                    // 填充空位
                    repeat(3 - rowTypes.size) {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

/**
 * 单个运动类型统计项
 */
@Composable
private fun ExerciseTypeItem(
    icon: ImageVector,
    label: String,
    count: Int,
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
                text = "${count}次",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = color
            )

            Text(
                text = label,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
