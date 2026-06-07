package com.healthapp.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.healthapp.ui.theme.PulseVitaTheme

/**
 * 饮水历史图表组件
 * 展示近7天饮水趋势
 */
@Composable
fun WaterHistoryChart(
    dailyAmounts: List<Int>,
    goalAmount: Int,
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
                text = "💧 饮水趋势（近7天）",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            if (dailyAmounts.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "暂无数据",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                // 图表
                val scheme = PulseVitaTheme.currentScheme()
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp)
                ) {
                    Canvas(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        val width = size.width
                        val height = size.height
                        val maxValue = (dailyAmounts.maxOrNull() ?: goalAmount).toFloat()
                        val barWidth = width / (dailyAmounts.size * 2 + 1)
                        
                        // 绘制目标线
                        val goalY = height * (1 - goalAmount / maxValue)
                        drawLine(
                            color = scheme.textSecondary.copy(alpha = 0.5f),
                            start = Offset(0f, goalY),
                            end = Offset(width, goalY),
                            strokeWidth = 2f
                        )
                        
                        // 绘制柱状图
                        dailyAmounts.forEachIndexed { index, amount ->
                            val barHeight = (amount / maxValue) * height
                            val x = barWidth * (index * 2 + 1)
                            val y = height - barHeight
                            
                            drawRect(
                                color = scheme.chartBlue,
                                topLeft = Offset(x, y),
                                size = Size(barWidth, barHeight)
                            )
                        }
                    }
                    
                    // 目标线标注
                    Text(
                        text = "目标${goalAmount}ml",
                        style = MaterialTheme.typography.bodySmall,
                        color = scheme.textSecondary,
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(top = 4.dp, end = 4.dp)
                    )
                }
                
                Spacer(modifier = Modifier.height(8.dp))
                
                // 日期标签
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    val days = listOf("一", "二", "三", "四", "五", "六", "日")
                    days.forEach { day ->
                        Text(
                            text = day,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}