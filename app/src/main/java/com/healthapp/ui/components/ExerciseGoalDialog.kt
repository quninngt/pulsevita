package com.healthapp.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog

/**
 * 运动目标设定对话框
 */
@Composable
fun ExerciseGoalDialog(
    currentDurationGoal: Int,
    currentStepsGoal: Long,
    onDurationGoalChange: (Int) -> Unit,
    onStepsGoalChange: (Long) -> Unit,
    onDismiss: () -> Unit
) {
    var durationGoalText by remember { mutableStateOf(currentDurationGoal.toString()) }
    var stepsGoalText by remember { mutableStateOf(currentStepsGoal.toString()) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(24.dp)
            ) {
                Text(
                    text = "🎯 运动目标设定",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.height(24.dp))

                // 运动时长目标
                Text(
                    text = "每日运动时长目标（分钟）",
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = durationGoalText,
                    onValueChange = { durationGoalText = it },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    label = { Text("运动时长") },
                    suffix = { Text("分钟") }
                )

                Spacer(modifier = Modifier.height(16.dp))

                // 步数目标
                Text(
                    text = "每日步数目标",
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = stepsGoalText,
                    onValueChange = { stepsGoalText = it },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    label = { Text("步数目标") },
                    suffix = { Text("步") }
                )

                Spacer(modifier = Modifier.height(24.dp))

                // 按钮
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("取消")
                    }
                    Button(
                        onClick = {
                            val durationGoal = durationGoalText.toIntOrNull() ?: currentDurationGoal
                            val stepsGoal = stepsGoalText.toLongOrNull() ?: currentStepsGoal
                            onDurationGoalChange(durationGoal)
                            onStepsGoalChange(stepsGoal)
                            onDismiss()
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("保存")
                    }
                }
            }
        }
    }
}