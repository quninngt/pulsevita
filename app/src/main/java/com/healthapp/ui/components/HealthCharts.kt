package com.healthapp.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
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
 * 显示最近 7 天的心情指数
 */
@Composable
fun MoodTrendChart(
    moodLevels: List<Float>,  // 最近 7 天的心情等级 (1-5)
    modifier: Modifier = Modifier
) {
    val tertiaryColor = MaterialTheme.colorScheme.tertiary

    Column(modifier = modifier) {
        Text(
            text = "最近 7 天心情趋势",
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(modifier = Modifier.height(8.dp))

        if (moodLevels.isNotEmpty()) {
            Canvas(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp)
            ) {
                val points = moodLevels.mapIndexed { index, level ->
                    val x = (index.toFloat() / (moodLevels.size - 1)) * size.width
                    val y = size.height - ((level / 5f) * size.height * 0.8f)
                    Offset(x, y)
                }

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
                    style = Stroke(width = 3.dp.toPx())
                )

                // Draw dots
                points.forEach { point ->
                    drawCircle(
                        color = tertiaryColor,
                        radius = 4.dp.toPx(),
                        center = point
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
