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

/**
 * 运动类型统计卡片
 * 展示各运动类型的本周统计
 */
@Composable
fun ExerciseTypeStatsCard(
    walkingCount: Int,
    officeExerciseCount: Int,
    yogaCount: Int,
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
                text = "📊 本周运动统计",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // 三个运动类型统计
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // 步行
                ExerciseTypeItem(
                    icon = Icons.Default.DirectionsWalk,
                    label = "步行",
                    count = walkingCount,
                    color = BrandGreen,
                    modifier = Modifier.weight(1f)
                )
                
                // 办公室运动
                ExerciseTypeItem(
                    icon = Icons.Default.Desk,
                    label = "办公运动",
                    count = officeExerciseCount,
                    color = Color(0xFF2196F3), // 蓝色
                    modifier = Modifier.weight(1f)
                )
                
                // 瑜伽
                ExerciseTypeItem(
                    icon = Icons.Default.SelfImprovement,
                    label = "瑜伽",
                    count = yogaCount,
                    color = Color(0xFF9C27B0), // 紫色
                    modifier = Modifier.weight(1f)
                )
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