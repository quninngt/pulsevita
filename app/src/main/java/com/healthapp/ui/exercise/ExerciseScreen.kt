package com.healthapp.ui.exercise

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.healthapp.ui.DisplayMappings
import com.healthapp.ui.components.AnimatedNumber
import com.healthapp.ui.components.ExerciseTrendChart
import com.healthapp.ui.components.ExerciseTypeStatsCard
import com.healthapp.ui.components.ExerciseGoalDialog
import com.healthapp.ui.components.SectionHeader

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExerciseScreen(
    navController: NavController,
    viewModel: ExerciseViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        TopAppBar(
            title = { Text("运动") },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.primary,
                titleContentColor = MaterialTheme.colorScheme.onPrimary
            )
        )

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Exercise Overview Card with AnimatedNumber
            item {
                ExerciseOverviewCard(
                    todayDuration = uiState.todayDuration,
                    durationGoal = uiState.durationGoal,
                    todaySteps = uiState.todaySteps,
                    stepsGoal = uiState.stepsGoal,
                    onGoalClick = { viewModel.showGoalDialog() }
                )
            }

            // Exercise Goal Dialog
            if (uiState.showGoalDialog) {
                item {
                    ExerciseGoalDialog(
                        currentDurationGoal = uiState.durationGoal,
                        currentStepsGoal = uiState.stepsGoal,
                        onDurationGoalChange = { viewModel.updateDurationGoal(it) },
                        onStepsGoalChange = { viewModel.updateStepsGoal(it) },
                        onDismiss = { viewModel.hideGoalDialog() }
                    )
                }
            }

            // Weekly Exercise Trend Chart
            item {
                SectionHeader(
                    icon = Icons.Default.TrendingUp,
                    title = "本周运动趋势"
                )
                Spacer(modifier = Modifier.height(8.dp))
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    ExerciseTrendChart(
                        dailyMinutes = uiState.weeklyMinutes,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }

            // Exercise Type Stats Card
            item {
                ExerciseTypeStatsCard(
                    walkingCount = uiState.walkingCount,
                    runningCount = uiState.runningCount,
                    cyclingCount = uiState.cyclingCount,
                    yogaCount = uiState.yogaCount,
                    stretchingCount = uiState.stretchingCount,
                    strengthCount = uiState.strengthCount,
                    swimmingCount = uiState.swimmingCount,
                    officeExerciseCount = uiState.officeExerciseCount
                )
            }

            // Quick Add Buttons - 常用运动
            item {
                Text(
                    text = "快速记录",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                // 第一行：步行、跑步、骑行
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    ExerciseQuickButton(
                        icon = Icons.Default.DirectionsWalk,
                        label = "步行",
                        onClick = { viewModel.showAddDialog("walking") },
                        modifier = Modifier.weight(1f)
                    )
                    ExerciseQuickButton(
                        icon = Icons.Default.DirectionsRun,
                        label = "跑步",
                        onClick = { viewModel.showAddDialog("running") },
                        modifier = Modifier.weight(1f)
                    )
                    ExerciseQuickButton(
                        icon = Icons.Default.PedalBike,
                        label = "骑行",
                        onClick = { viewModel.showAddDialog("cycling") },
                        modifier = Modifier.weight(1f)
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                // 第二行：瑜伽、拉伸、力量
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    ExerciseQuickButton(
                        icon = Icons.Default.SelfImprovement,
                        label = "瑜伽",
                        onClick = { viewModel.showAddDialog("yoga") },
                        modifier = Modifier.weight(1f)
                    )
                    ExerciseQuickButton(
                        icon = Icons.Default.Accessible,
                        label = "拉伸",
                        onClick = { viewModel.showAddDialog("stretching") },
                        modifier = Modifier.weight(1f)
                    )
                    ExerciseQuickButton(
                        icon = Icons.Default.FitnessCenter,
                        label = "力量",
                        onClick = { viewModel.showAddDialog("strength") },
                        modifier = Modifier.weight(1f)
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                // 第三行：游泳、办公室运动、更多
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    ExerciseQuickButton(
                        icon = Icons.Default.Pool,
                        label = "游泳",
                        onClick = { viewModel.showAddDialog("swimming") },
                        modifier = Modifier.weight(1f)
                    )
                    ExerciseQuickButton(
                        icon = Icons.Default.Desk,
                        label = "办公室",
                        onClick = { viewModel.showAddDialog("office_exercise") },
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(modifier = Modifier.weight(1f))
                }
            }

            // Office Exercise Tutorials
            item {
                Text(
                    text = "办公室简易运动",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            uiState.officeExercises.forEach { exercise ->
                item {
                    OfficeExerciseCard(
                        title = exercise.title,
                        description = exercise.description,
                        duration = exercise.duration
                    )
                }
            }

            // Today's Records
            item {
                Text(
                    text = "今日运动记录",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            if (uiState.exerciseRecords.isEmpty()) {
                item {
                    Text(
                        text = "今天还没有运动记录，快去运动吧！",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                items(uiState.exerciseRecords) { record ->
                    ExerciseRecordItem(
                        record = record,
                        onDelete = { viewModel.deleteRecord(record) }
                    )
                }
            }
        }
    }

    // Add Exercise Dialog
    if (uiState.showAddDialog) {
        AddExerciseDialog(
            type = uiState.selectedType,
            duration = uiState.exerciseDuration,
            steps = uiState.exerciseSteps,
            note = uiState.exerciseNote,
            onDurationChange = { viewModel.updateDuration(it) },
            onStepsChange = { viewModel.updateSteps(it) },
            onNoteChange = { viewModel.updateNote(it) },
            onSave = { viewModel.saveExerciseRecord() },
            onDismiss = { viewModel.hideAddDialog() }
        )
    }
}

@Composable
fun ExerciseOverviewCard(
    todayDuration: Int,
    durationGoal: Int,
    todaySteps: Long,
    stepsGoal: Long,
    onGoalClick: () -> Unit = {}
) {
    val durationProgress = (todayDuration.toFloat() / durationGoal).coerceIn(0f, 1f)
    val stepsProgress = (todaySteps.toFloat() / stepsGoal).coerceIn(0f, 1f)

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "今日运动",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                IconButton(onClick = onGoalClick) {
                    Icon(
                        Icons.Default.Settings,
                        contentDescription = "设置目标",
                        tint = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }
            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    CircularProgressIndicator(
                        progress = durationProgress,
                        modifier = Modifier.size(80.dp),
                        color = MaterialTheme.colorScheme.primary,
                        trackColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f),
                        strokeWidth = 6.dp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    AnimatedNumber(
                        targetValue = todayDuration,
                        suffix = "分钟"
                    )
                    Text(
                        text = "目标${durationGoal}分钟",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                    )
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    CircularProgressIndicator(
                        progress = stepsProgress,
                        modifier = Modifier.size(80.dp),
                        color = MaterialTheme.colorScheme.secondary,
                        trackColor = MaterialTheme.colorScheme.secondary.copy(alpha = 0.2f),
                        strokeWidth = 6.dp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    AnimatedNumber(
                        targetValue = todaySteps.toInt(),
                        suffix = "步"
                    )
                    Text(
                        text = "目标${stepsGoal}步",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                    )
                }
            }
        }
    }
}

@Composable
fun ExerciseQuickButton(
    icon: ImageVector,
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        modifier = modifier.height(56.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(icon, contentDescription = label)
            Text(label, style = MaterialTheme.typography.bodySmall)
        }
    }
}

@Composable
fun OfficeExerciseCard(
    title: String,
    description: String,
    duration: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = duration,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = description,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Composable
fun ExerciseRecordItem(
    record: com.healthapp.data.local.entity.ExerciseRecord,
    onDelete: () -> Unit
) {
    val typeName = DisplayMappings.exerciseTypeName(record.type)

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = typeName,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "${record.duration}分钟${if (record.steps != null) " · ${record.steps}步" else ""}",
                    style = MaterialTheme.typography.bodyMedium
                )
                if (!record.note.isNullOrBlank()) {
                    Text(
                        text = record.note,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            IconButton(onClick = onDelete) {
                Icon(
                    Icons.Default.Delete,
                    contentDescription = "删除",
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}

@Composable
fun AddExerciseDialog(
    type: String,
    duration: String,
    steps: String,
    note: String,
    onDurationChange: (String) -> Unit,
    onStepsChange: (String) -> Unit,
    onNoteChange: (String) -> Unit,
    onSave: () -> Unit,
    onDismiss: () -> Unit
) {
    val typeName = DisplayMappings.exerciseTypeName(type)

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("记录$typeName") },
        text = {
            Column {
                OutlinedTextField(
                    value = duration,
                    onValueChange = onDurationChange,
                    label = { Text("运动时长（分钟）") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                if (type == "walking") {
                    OutlinedTextField(
                        value = steps,
                        onValueChange = onStepsChange,
                        label = { Text("步数（可选）") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }
                OutlinedTextField(
                    value = note,
                    onValueChange = onNoteChange,
                    label = { Text("备注（可选）") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            TextButton(onClick = onSave) {
                Text("保存")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("取消")
            }
        }
    )
}
