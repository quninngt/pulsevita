package com.healthapp.ui.diet

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.healthapp.ui.DisplayMappings
import com.healthapp.ui.components.AnimatedNumber
import com.healthapp.ui.components.PulseAnimation
import com.healthapp.ui.components.NutritionOverviewCard
import com.healthapp.ui.components.WaterHistoryChart
import com.healthapp.ui.theme.PulseVitaTheme
import com.healthapp.util.Constants
import com.healthapp.util.WaterReminderManager

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DietScreen(
    navController: NavController,
    viewModel: DietViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        TopAppBar(
            title = { Text("饮食") },
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
            // Water Tracking Card
            item {
                WaterTrackingCard(
                    currentAmount = uiState.waterAmount,
                    goalAmount = uiState.waterGoal,
                    onAddWater = { viewModel.addWater(it) }
                )
            }

            // Water Reminder Settings
            item {
                WaterReminderSettingsCard(context = androidx.compose.ui.platform.LocalContext.current)
            }

            // Nutrition Overview Card
            item {
                NutritionOverviewCard(
                    protein = uiState.protein,
                    proteinGoal = uiState.proteinGoal,
                    carbs = uiState.carbs,
                    carbsGoal = uiState.carbsGoal,
                    fat = uiState.fat,
                    fatGoal = uiState.fatGoal
                )
            }

            // Water History Chart
            item {
                WaterHistoryChart(
                    dailyAmounts = uiState.weeklyWaterAmounts,
                    goalAmount = uiState.waterGoal
                )
            }

            // Diet Records Section
            item {
                Text(
                    text = "今日饮食记录",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            // Meal Types
            DisplayMappings.mealTypes.forEach { mealType ->
                val mealRecords = uiState.dietRecords.filter { it.mealType == mealType.key }
                item {
                    MealSection(
                        mealType = mealType.label,
                        records = mealRecords,
                        onAddClick = { viewModel.showAddDietDialog(mealType.key) },
                        onDeleteClick = { viewModel.deleteDietRecord(it) }
                    )
                }
            }
        }
    }

    // Add Diet Dialog
    if (uiState.showAddDietDialog) {
        AddDietDialog(
            mealType = uiState.selectedMealType,
            description = uiState.dietDescription,
            calories = uiState.dietCalories,
            onDescriptionChange = { viewModel.updateDietDescription(it) },
            onCaloriesChange = { viewModel.updateDietCalories(it) },
            onSave = { viewModel.saveDietRecord() },
            onDismiss = { viewModel.hideAddDietDialog() }
        )
    }
}

@Composable
fun WaterTrackingCard(
    currentAmount: Int,
    goalAmount: Int,
    onAddWater: (Int) -> Unit
) {
    val progress = (currentAmount.toFloat() / goalAmount).coerceIn(0f, 1f)

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "饮水追踪",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
            Spacer(modifier = Modifier.height(12.dp))

            // Water Progress with AnimatedNumber
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.size(120.dp)
            ) {
                CircularProgressIndicator(
                    progress = progress,
                    modifier = Modifier.size(120.dp),
                    color = PulseVitaTheme.currentScheme().chartBlue,
                    trackColor = PulseVitaTheme.currentScheme().chartBlue.copy(alpha = 0.2f),
                    strokeWidth = 8.dp
                )
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    AnimatedNumber(
                        targetValue = currentAmount,
                        suffix = "ml"
                    )
                    Text(
                        text = "目标${goalAmount}ml",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Add Water Buttons with PulseAnimation on the recommended amount
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                WaterAddButton(amount = Constants.WATER_SMALL_AMOUNT, onClick = { onAddWater(Constants.WATER_SMALL_AMOUNT) })
                PulseAnimation {
                    WaterAddButton(amount = Constants.WATER_MEDIUM_AMOUNT, onClick = { onAddWater(Constants.WATER_MEDIUM_AMOUNT) })
                }
                WaterAddButton(amount = Constants.WATER_LARGE_AMOUNT, onClick = { onAddWater(Constants.WATER_LARGE_AMOUNT) })
            }
        }
    }
}

@Composable
fun WaterAddButton(amount: Int, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        shape = CircleShape,
        colors = ButtonDefaults.buttonColors(containerColor = PulseVitaTheme.currentScheme().chartBlue)
    ) {
        Text("+${amount}ml")
    }
}

@Composable
fun MealSection(
    mealType: String,
    records: List<com.healthapp.data.local.entity.DietRecord>,
    onAddClick: () -> Unit,
    onDeleteClick: (com.healthapp.data.local.entity.DietRecord) -> Unit
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
                    text = mealType,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
                IconButton(onClick = onAddClick) {
                    Icon(Icons.Default.Add, contentDescription = "添加")
                }
            }

            if (records.isEmpty()) {
                Text(
                    text = "暂无记录",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            } else {
                records.forEach { record ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = record.description,
                                style = MaterialTheme.typography.bodyMedium
                            )
                            if (record.calories != null) {
                                Text(
                                    text = "${record.calories}千卡",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                        IconButton(onClick = { onDeleteClick(record) }) {
                            Icon(
                                Icons.Default.Delete,
                                contentDescription = "删除",
                                tint = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddDietDialog(
    mealType: String,
    description: String,
    calories: String,
    onDescriptionChange: (String) -> Unit,
    onCaloriesChange: (String) -> Unit,
    onSave: () -> Unit,
    onDismiss: () -> Unit
) {
    val mealTypeName = DisplayMappings.mealTypeName(mealType)
    var showFoodSearch by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }

    val searchResults = remember(searchQuery) {
        if (searchQuery.isNotBlank()) {
            com.healthapp.util.FoodDatabase.searchFoods(searchQuery).take(5)
        } else {
            emptyList()
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("添加$mealTypeName") },
        text = {
            Column {
                // 食物搜索入口
                OutlinedCard(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = { showFoodSearch = !showFoodSearch }
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Search, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("🔍 从常见食物中选择", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.primary)
                    }
                }

                if (showFoodSearch) {
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        label = { Text("搜索食物") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    if (searchResults.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Column {
                            searchResults.forEach { food ->
                                Card(
                                    modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp),
                                    onClick = {
                                        onDescriptionChange(food.name)
                                        onCaloriesChange(food.calories.toString())
                                        showFoodSearch = false
                                        searchQuery = ""
                                    }
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth().padding(8.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column {
                                            Text(food.name, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium)
                                            Text(food.serving, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                        }
                                        Text("${food.calories}千卡", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = description,
                    onValueChange = onDescriptionChange,
                    label = { Text("吃了什么") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = calories,
                    onValueChange = onCaloriesChange,
                    label = { Text("热量（千卡，可选）") },
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

/**
 * 饮水提醒设置卡片
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WaterReminderSettingsCard(context: android.content.Context) {
    val scheme = PulseVitaTheme.currentScheme()
    var isEnabled by remember { mutableStateOf(WaterReminderManager.isEnabled(context)) }
    var intervalHours by remember { mutableIntStateOf(WaterReminderManager.getIntervalHours(context)) }
    var startHour by remember { mutableIntStateOf(WaterReminderManager.getStartHour(context)) }
    var endHour by remember { mutableIntStateOf(WaterReminderManager.getEndHour(context)) }
    var showSettings by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.Notifications,
                        contentDescription = null,
                        tint = scheme.primary,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "饮水提醒",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
                Switch(
                    checked = isEnabled,
                    onCheckedChange = { enabled ->
                        isEnabled = enabled
                        WaterReminderManager.saveSettings(context, enabled, intervalHours, startHour, endHour)
                    }
                )
            }

            if (isEnabled) {
                Spacer(modifier = Modifier.height(8.dp))
                TextButton(onClick = { showSettings = !showSettings }) {
                    Text(if (showSettings) "收起设置" else "展开设置")
                }

                if (showSettings) {
                    Spacer(modifier = Modifier.height(8.dp))

                    // 提醒间隔
                    Text("提醒间隔", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium)
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        listOf(1, 2, 3, 4).forEach { hours ->
                            FilterChip(
                                selected = intervalHours == hours,
                                onClick = {
                                    intervalHours = hours
                                    WaterReminderManager.saveSettings(context, true, hours, startHour, endHour)
                                },
                                label = { Text("${hours}小时") }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // 提醒时间范围
                    Text("提醒时间范围", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium)
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("从 ${startHour}:00", style = MaterialTheme.typography.bodyMedium)
                        Slider(
                            value = startHour.toFloat(),
                            onValueChange = { startHour = it.toInt() },
                            onValueChangeFinished = {
                                WaterReminderManager.saveSettings(context, true, intervalHours, startHour, endHour)
                            },
                            valueRange = 6f..12f,
                            steps = 6,
                            modifier = Modifier.weight(1f).padding(horizontal = 8.dp)
                        )
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("到 ${endHour}:00", style = MaterialTheme.typography.bodyMedium)
                        Slider(
                            value = endHour.toFloat(),
                            onValueChange = { endHour = it.toInt() },
                            onValueChangeFinished = {
                                WaterReminderManager.saveSettings(context, true, intervalHours, startHour, endHour)
                            },
                            valueRange = 18f..23f,
                            steps = 5,
                            modifier = Modifier.weight(1f).padding(horizontal = 8.dp)
                        )
                    }
                }
            }
        }
    }
}
