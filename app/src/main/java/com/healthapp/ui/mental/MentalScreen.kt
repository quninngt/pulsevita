package com.healthapp.ui.mental

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.healthapp.data.local.entity.MoodRecord
import com.healthapp.ui.DisplayMappings
import com.healthapp.ui.components.MoodTrendChart
import com.healthapp.ui.components.MoodStatisticsCard
import com.healthapp.ui.components.PulseAnimation
import com.healthapp.ui.components.SectionHeader
import com.healthapp.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MentalScreen(
    navController: NavController,
    viewModel: MentalViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        TopAppBar(
            title = { Text("心理健康") },
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
            // Mood Entry Button
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { viewModel.showAddMoodDialog() },
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            Icons.Default.Add,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "记录今天的心情",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }
            }

            // Mood Trend Chart
            item {
                SectionHeader(
                    icon = Icons.Default.TrendingUp,
                    title = "心情趋势"
                )
                Spacer(modifier = Modifier.height(8.dp))
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    MoodTrendChart(
                        moodLevels = uiState.weeklyMoodLevels.map { (it ?: 0).toFloat() },
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }

            // Mood Statistics Card
            item {
                MoodStatisticsCard(
                    weeklyAverage = uiState.weeklyAverage,
                    bestDay = uiState.bestDay,
                    worstDay = uiState.worstDay,
                    trend = uiState.trend
                )
            }

            // Recent Moods
            item {
                Text(
                    text = "最近心情",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            item {
                if (uiState.recentMoods.isEmpty()) {
                    Text(
                        text = "还没有心情记录，点击上方按钮记录吧！",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                } else {
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(uiState.recentMoods) { mood ->
                            MoodCard(mood = mood, onDelete = { viewModel.deleteMoodRecord(mood) })
                        }
                    }
                }
            }

            // Breathing Exercise with PulseAnimation
            item {
                Text(
                    text = "呼吸练习",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { viewModel.showBreathingExercise() },
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.tertiaryContainer)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        PulseAnimation {
                            Icon(
                                Icons.Default.Air,
                                contentDescription = null,
                                modifier = Modifier.size(48.dp),
                                tint = MaterialTheme.colorScheme.onTertiaryContainer
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "4-7-8 呼吸法",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onTertiaryContainer
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "吸气4秒，屏息7秒，呼气8秒",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onTertiaryContainer
                        )
                    }
                }
            }

            // Daily Quote
            if (uiState.quote.isNotEmpty()) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "💬 今日一句",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "\u201C${uiState.quote}\u201D",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                            if (uiState.quoteFrom.isNotEmpty()) {
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "—— ${uiState.quoteFrom}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.6f)
                                )
                            }
                        }
                    }
                }
            }

            // Emotional Management Tips
            item {
                Text(
                    text = "情绪管理建议",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            uiState.emotionTips.forEach { tipGroup ->
                item {
                    EmotionTipCard(
                        title = tipGroup.title,
                        tips = tipGroup.tips
                    )
                }
            }
        }
    }

    // Add Mood Dialog
    if (uiState.showAddMoodDialog) {
        AddMoodDialog(
            selectedLevel = uiState.selectedMoodLevel,
            selectedIcon = uiState.selectedMoodIcon,
            note = uiState.moodNote,
            onSelectMood = { level, icon -> viewModel.selectMood(level, icon) },
            onNoteChange = { viewModel.updateMoodNote(it) },
            onSave = { viewModel.saveMoodRecord() },
            onDismiss = { viewModel.hideAddMoodDialog() }
        )
    }

    // Breathing Exercise Dialog
    if (uiState.showBreathingExercise) {
        BreathingExerciseDialog(
            isBreathing = uiState.isBreathing,
            phase = uiState.breathingPhase,
            count = uiState.breathingCount,
            onStart = { viewModel.startBreathing() },
            onStop = { viewModel.stopBreathing() },
            onDismiss = { viewModel.hideBreathingExercise() }
        )
    }
}

@Composable
fun MoodCard(mood: MoodRecord, onDelete: () -> Unit) {
    Card(
        modifier = Modifier.width(100.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = mood.moodIcon,
                fontSize = 32.sp
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = DisplayMappings.moodLevelName(mood.moodLevel),
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.Bold
            )
            if (!mood.note.isNullOrBlank()) {
                Text(
                    text = mood.note,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2
                )
            }
            IconButton(
                onClick = onDelete,
                modifier = Modifier.size(24.dp)
            ) {
                Icon(
                    Icons.Default.Close,
                    contentDescription = "删除",
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}

@Composable
fun AddMoodDialog(
    selectedLevel: Int,
    selectedIcon: String,
    note: String,
    onSelectMood: (Int, String) -> Unit,
    onNoteChange: (String) -> Unit,
    onSave: () -> Unit,
    onDismiss: () -> Unit
) {
    val moods = DisplayMappings.moodOptions

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("记录心情") },
        text = {
            Column {
                Text(
                    text = "今天心情怎么样？",
                    style = MaterialTheme.typography.bodyLarge
                )
                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    moods.forEach { mood ->
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(
                                    if (selectedLevel == mood.level)
                                        MaterialTheme.colorScheme.primaryContainer
                                    else
                                        Color.Transparent
                                )
                                .clickable { onSelectMood(mood.level, mood.icon) },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = mood.icon, fontSize = 24.sp)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = note,
                    onValueChange = onNoteChange,
                    label = { Text("想说点什么？（可选）") },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 3
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

@Composable
fun BreathingExerciseDialog(
    isBreathing: Boolean,
    phase: String,
    count: Int,
    onStart: () -> Unit,
    onStop: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = {
            if (!isBreathing) onDismiss()
        },
        title = { Text("4-7-8 呼吸法") },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "吸气4秒，屏息7秒，呼气8秒",
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(24.dp))

                if (isBreathing) {
                    // Breathing Animation with PulseAnimation
                    PulseAnimation {
                        Box(
                            modifier = Modifier
                                .size(120.dp)
                                .clip(CircleShape)
                                .background(
                                    when (phase) {
                                        "inhale" -> MaterialTheme.colorScheme.primary
                                        "hold" -> MaterialTheme.colorScheme.secondary
                                        "exhale" -> MaterialTheme.colorScheme.tertiary
                                        else -> MaterialTheme.colorScheme.surfaceVariant
                                    }
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = when (phase) {
                                        "inhale" -> "吸气"
                                        "hold" -> "屏息"
                                        "exhale" -> "呼气"
                                        else -> ""
                                    },
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                Text(
                                    text = "${count}秒",
                                    style = MaterialTheme.typography.headlineMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = onStop,
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                    ) {
                        Text("停止")
                    }
                } else {
                    Text(
                        text = "点击开始，跟随引导进行呼吸练习",
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(onClick = onStart) {
                        Text("开始练习")
                    }
                }
            }
        },
        confirmButton = {
            if (!isBreathing) {
                TextButton(onClick = onDismiss) {
                    Text("关闭")
                }
            }
        }
    )
}

@Composable
fun EmotionTipCard(
    title: String,
    tips: List<String>
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            tips.forEach { tip ->
                Row(
                    modifier = Modifier.padding(vertical = 2.dp)
                ) {
                    Text(
                        text = "• ",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = tip,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }
}
