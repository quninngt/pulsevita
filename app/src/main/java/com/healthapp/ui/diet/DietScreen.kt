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
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.healthapp.ui.DisplayMappings
import com.healthapp.ui.theme.WaterBlue
import com.healthapp.util.Constants

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

            // Water Progress
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.size(120.dp)
            ) {
                CircularProgressIndicator(
                    progress = progress,
                    modifier = Modifier.size(120.dp),
                    color = WaterBlue,
                    trackColor = WaterBlue.copy(alpha = 0.2f),
                    strokeWidth = 8.dp
                )
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "${currentAmount}ml",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Text(
                        text = "目标${goalAmount}ml",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Add Water Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                WaterAddButton(amount = Constants.WATER_SMALL_AMOUNT, onClick = { onAddWater(Constants.WATER_SMALL_AMOUNT) })
                WaterAddButton(amount = Constants.WATER_MEDIUM_AMOUNT, onClick = { onAddWater(Constants.WATER_MEDIUM_AMOUNT) })
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
        colors = ButtonDefaults.buttonColors(containerColor = WaterBlue)
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

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("添加$mealTypeName") },
        text = {
            Column {
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
