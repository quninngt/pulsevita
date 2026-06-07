package com.healthapp.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import com.healthapp.ui.theme.PulseVitaTheme
import androidx.compose.ui.unit.dp

/**
 * 营养摄入概览卡片
 * 展示蛋白质、碳水化合物、脂肪的摄入情况
 */
@Composable
fun NutritionOverviewCard(
    protein: Float,
    proteinGoal: Float,
    carbs: Float,
    carbsGoal: Float,
    fat: Float,
    fatGoal: Float,
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
                text = "📊 营养摄入",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // 三个营养指标
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // 蛋白质
                NutritionItem(
                    label = "蛋白质",
                    current = protein,
                    goal = proteinGoal,
                    color = PulseVitaTheme.currentScheme().chartGreen,
                    modifier = Modifier.weight(1f)
                )
                
                // 碳水化合物
                NutritionItem(
                    label = "碳水",
                    current = carbs,
                    goal = carbsGoal,
                    color = PulseVitaTheme.currentScheme().chartBlue,
                    modifier = Modifier.weight(1f)
                )
                
                // 脂肪
                NutritionItem(
                    label = "脂肪",
                    current = fat,
                    goal = fatGoal,
                    color = PulseVitaTheme.currentScheme().chartOrange,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

/**
 * 单个营养指标项
 */
@Composable
private fun NutritionItem(
    label: String,
    current: Float,
    goal: Float,
    color: Color,
    modifier: Modifier = Modifier
) {
    val progress = if (goal > 0) (current / goal).coerceIn(0f, 1f) else 0f
    
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
            Text(
                text = label,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            Spacer(modifier = Modifier.height(4.dp))
            
            Text(
                text = "${current.toInt()}g",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = color
            )
            
            Spacer(modifier = Modifier.height(4.dp))
            
            // 进度条
            LinearProgressIndicator(
                progress = progress,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp),
                color = color,
                trackColor = color.copy(alpha = 0.2f)
            )
            
            Spacer(modifier = Modifier.height(4.dp))
            
            Text(
                text = "目标${goal.toInt()}g",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
            )
        }
    }
}