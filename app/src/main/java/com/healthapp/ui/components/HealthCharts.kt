package com.healthapp.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * 饮水趋势图（柱状图）
 * 显示最近 7 天的饮水量
 */
@Composable
fun WaterTrendChart(
    dailyAmounts: List<Float>,  // 最近 7 天的饮水量 (ml)
    modifier: Modifier = Modifier
) {
    val primaryColor = MaterialTheme.colorScheme.primary

    Column(modifier = modifier) {
        Text(
            text = "最近 7 天饮水趋势",
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(modifier = Modifier.height(8.dp))

        if (dailyAmounts.isNotEmpty()) {
            val maxAmount = dailyAmounts.maxOrNull() ?: 2000f
            Canvas(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp)
            ) {
                val barWidth = size.width / (dailyAmounts.size * 2)
                val spacing = barWidth

                dailyAmounts.forEachIndexed { index, amount ->
                    val barHeight = (amount / maxAmount) * size.height * 0.8f
                    val x = (index * (barWidth + spacing)) + spacing / 2
                    val y = size.height - barHeight

                    drawRoundRect(
                        color = primaryColor,
                        topLeft = Offset(x, y),
                        size = Size(barWidth, barHeight),
                        cornerRadius = CornerRadius(4.dp.toPx(), 4.dp.toPx())
                    )
                }
            }
        } else {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "暂无数据",
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

/**
 * 心情趋势图（曲线图）
 * 支持 7天/30天 切换
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MoodTrendChart(
    moodLevels: List<Float>,  // 心情等级 (1-5)
    period: Int = 7,          // 7 or 30
    modifier: Modifier = Modifier,
    onPeriodChange: ((Int) -> Unit)? = null
) {
    val tertiaryColor = MaterialTheme.colorScheme.tertiary
    val surfaceVariant = MaterialTheme.colorScheme.surfaceVariant
    val onSurfaceVariant = MaterialTheme.colorScheme.onSurfaceVariant

    Column(modifier = modifier) {
        // 标题行 + 时间范围切换
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "心情趋势",
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface
            )

            if (onPeriodChange != null) {
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    FilterChip(
                        selected = period == 7,
                        onClick = { onPeriodChange(7) },
                        label = { Text("7天", fontSize = 11.sp) },
                        modifier = Modifier.height(28.dp)
                    )
                    FilterChip(
                        selected = period == 30,
                        onClick = { onPeriodChange(30) },
                        label = { Text("30天", fontSize = 11.sp) },
                        modifier = Modifier.height(28.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        if (moodLevels.isNotEmpty()) {
            Canvas(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp)
            ) {
                val points = moodLevels.mapIndexed { index, level ->
                    val x = if (moodLevels.size > 1)
                        (index.toFloat() / (moodLevels.size - 1)) * size.width
                    else size.width / 2
                    val y = size.height - ((level / 5f) * size.height * 0.8f)
                    Offset(x, y)
                }

                // Draw area fill
                val areaPath = Path().apply {
                    moveTo(points.first().x, size.height)
                    lineTo(points.first().x, points.first().y)
                    for (i in 1 until points.size) {
                        val prev = points[i - 1]
                        val curr = points[i]
                        val controlX = (prev.x + curr.x) / 2
                        cubicTo(controlX, prev.y, controlX, curr.y, curr.x, curr.y)
                    }
                    lineTo(points.last().x, size.height)
                    close()
                }
                drawPath(
                    path = areaPath,
                    color = tertiaryColor.copy(alpha = 0.1f)
                )

                // Draw line
                val path = Path().apply {
                    moveTo(points.first().x, points.first().y)
                    for (i in 1 until points.size) {
                        val prev = points[i - 1]
                        val curr = points[i]
                        val controlX = (prev.x + curr.x) / 2
                        cubicTo(controlX, prev.y, controlX, curr.y, curr.x, curr.y)
                    }
                }
                drawPath(
                    path = path,
                    color = tertiaryColor,
                    style = Stroke(width = if (period == 30) 2.dp.toPx() else 3.dp.toPx())
                )

                // Draw dots (skip some for 30-day view)
                val dotStep = if (period == 30) 3 else 1
                points.forEachIndexed { index, point ->
                    if (index % dotStep == 0 || index == points.size - 1) {
                        drawCircle(
                            color = tertiaryColor,
                            radius = if (period == 30) 3.dp.toPx() else 4.dp.toPx(),
                            center = point
                        )
                    }
                }
            }

            // X轴标签
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = if (period == 30) "30天前" else "7天前",
                    fontSize = 10.sp,
                    color = onSurfaceVariant
                )
                Text(
                    text = "今天",
                    fontSize = 10.sp,
                    color = onSurfaceVariant
                )
            }
        } else {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "暂无数据",
                    color = onSurfaceVariant
                )
            }
        }
    }
}

/**
 * 运动统计图（柱状图）
 * 显示最近 7 天的运动时长
 */
@Composable
fun ExerciseTrendChart(
    dailyMinutes: List<Int>,  // 最近 7 天的运动时长 (分钟)
    modifier: Modifier = Modifier
) {
    val secondaryColor = MaterialTheme.colorScheme.secondary

    Column(modifier = modifier) {
        Text(
            text = "最近 7 天运动趋势",
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(modifier = Modifier.height(8.dp))

        if (dailyMinutes.isNotEmpty()) {
            val maxMinutes = dailyMinutes.maxOrNull()?.toFloat() ?: 60f
            Canvas(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp)
            ) {
                val barWidth = size.width / (dailyMinutes.size * 2)
                val spacing = barWidth

                dailyMinutes.forEachIndexed { index, minutes ->
                    val barHeight = (minutes.toFloat() / maxMinutes) * size.height * 0.8f
                    val x = (index * (barWidth + spacing)) + spacing / 2
                    val y = size.height - barHeight

                    drawRoundRect(
                        color = secondaryColor,
                        topLeft = Offset(x, y),
                        size = Size(barWidth, barHeight),
                        cornerRadius = CornerRadius(4.dp.toPx(), 4.dp.toPx())
                    )
                }
            }
        } else {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "暂无数据",
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
