package com.healthapp.ui.profile

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.healthapp.navigation.Screen
import com.healthapp.ui.components.ThemeSelectorCard
import com.healthapp.ui.theme.PulseVitaTheme
import com.healthapp.ui.theme.ThemeViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    navController: NavController,
    viewModel: ProfileViewModel = hiltViewModel(),
    themeViewModel: ThemeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val currentScheme by themeViewModel.currentScheme.collectAsState()

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        TopAppBar(
            title = { Text("个人档案") },
            navigationIcon = {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "返回")
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.primary,
                titleContentColor = MaterialTheme.colorScheme.onPrimary,
                navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
            )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            if (uiState.isEditing) {
                // Edit Mode
                ProfileEditSection(
                    name = uiState.editName,
                    gender = uiState.editGender,
                    height = uiState.editHeight,
                    weight = uiState.editWeight,
                    occupation = uiState.editOccupation,
                    onNameChange = { viewModel.updateName(it) },
                    onGenderChange = { viewModel.updateGender(it) },
                    onHeightChange = { viewModel.updateHeight(it) },
                    onWeightChange = { viewModel.updateWeight(it) },
                    onOccupationChange = { viewModel.updateOccupation(it) },
                    onSave = { viewModel.saveProfile() },
                    onCancel = { viewModel.cancelEditing() }
                )
            } else {
                // View Mode
                ProfileViewSection(
                    user = uiState.user,
                    bmi = uiState.bmi,
                    onEdit = { viewModel.startEditing() }
                )
            }

            // BMI Explanation
            if (uiState.bmi != null) {
                BmiExplanationCard(bmi = uiState.bmi!!)
            }

            // Health Indicators
            HealthIndicatorsCard(
                bmi = uiState.bmi,
                height = uiState.user?.height,
                weight = uiState.user?.weight
            )

            // Theme Selector
            ThemeSelectorCard(
                currentScheme = currentScheme,
                onSchemeSelected = { scheme ->
                    themeViewModel.setScheme(scheme)
                }
            )

            // Feature entry cards
            FeatureEntryCards(navController = navController)
        }
    }
}

/**
 * 功能入口卡片：优化计划、健康报告、成就系统
 */
@Composable
private fun FeatureEntryCards(navController: NavController) {
    val scheme = PulseVitaTheme.currentScheme()

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "更多功能",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(12.dp))

            FeatureEntryItem(
                icon = Icons.Default.Assignment,
                title = "优化计划",
                description = "查看和管理你的健康优化计划",
                iconTint = scheme.primary,
                onClick = { navController.navigate(Screen.Plan.route) }
            )
            Spacer(modifier = Modifier.height(8.dp))

            FeatureEntryItem(
                icon = Icons.Default.Assessment,
                title = "健康报告",
                description = "查看每周和每月的健康分析报告",
                iconTint = scheme.chartGreen,
                onClick = { navController.navigate(Screen.Report.route) }
            )
            Spacer(modifier = Modifier.height(8.dp))

            FeatureEntryItem(
                icon = Icons.Default.EmojiEvents,
                title = "成就系统",
                description = "查看已解锁的成就和徽章",
                iconTint = scheme.chartOrange,
                onClick = { navController.navigate(Screen.AchievementDetail.route) }
            )
        }
    }
}

@Composable
private fun FeatureEntryItem(
    icon: ImageVector,
    title: String,
    description: String,
    iconTint: Color,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = iconTint.copy(alpha = 0.12f),
                modifier = Modifier.size(40.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        icon,
                        contentDescription = title,
                        tint = iconTint,
                        modifier = Modifier.size(22.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Icon(
                Icons.Default.KeyboardArrowRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
            )
        }
    }
}

@Composable
fun ProfileViewSection(
    user: com.healthapp.data.local.entity.UserEntity?,
    bmi: Float?,
    onEdit: () -> Unit
) {
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
                    text = "基本信息",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                IconButton(onClick = onEdit) {
                    Icon(
                        Icons.Default.Edit,
                        contentDescription = "编辑",
                        tint = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            if (user == null) {
                Text(
                    text = "点击编辑按钮添加个人信息",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            } else {
                ProfileInfoItem(label = "姓名", value = user.name.ifBlank { "未设置" })
                ProfileInfoItem(
                    label = "性别",
                    value = when (user.gender) {
                        "male" -> "男"
                        "female" -> "女"
                        else -> "未设置"
                    }
                )
                ProfileInfoItem(label = "身高", value = if (user.height > 0) "${user.height}cm" else "未设置")
                ProfileInfoItem(label = "体重", value = if (user.weight > 0) "${user.weight}kg" else "未设置")
                ProfileInfoItem(label = "职业", value = user.occupation.ifBlank { "未设置" })
                if (bmi != null) {
                    ProfileInfoItem(label = "BMI", value = String.format("%.1f", bmi))
                }
            }
        }
    }
}

@Composable
fun ProfileInfoItem(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onPrimaryContainer
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileEditSection(
    name: String,
    gender: String,
    height: String,
    weight: String,
    occupation: String,
    onNameChange: (String) -> Unit,
    onGenderChange: (String) -> Unit,
    onHeightChange: (String) -> Unit,
    onWeightChange: (String) -> Unit,
    onOccupationChange: (String) -> Unit,
    onSave: () -> Unit,
    onCancel: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "编辑个人信息",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = name,
                onValueChange = onNameChange,
                label = { Text("姓名") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))

            // Gender Selection
            Text(
                text = "性别",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(bottom = 4.dp)
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = gender == "male",
                    onClick = { onGenderChange("male") },
                    label = { Text("男") },
                    modifier = Modifier.weight(1f)
                )
                FilterChip(
                    selected = gender == "female",
                    onClick = { onGenderChange("female") },
                    label = { Text("女") },
                    modifier = Modifier.weight(1f)
                )
            }
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = height,
                onValueChange = onHeightChange,
                label = { Text("身高（cm）") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = weight,
                onValueChange = onWeightChange,
                label = { Text("体重（kg）") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = occupation,
                onValueChange = onOccupationChange,
                label = { Text("职业") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = onCancel,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("取消")
                }
                Button(
                    onClick = onSave,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("保存")
                }
            }
        }
    }
}

@Composable
fun BmiExplanationCard(bmi: Float) {
    val (category, color) = when {
        bmi < 18.5 -> "偏瘦" to MaterialTheme.colorScheme.tertiary
        bmi < 24 -> "正常" to MaterialTheme.colorScheme.primary
        bmi < 28 -> "偏胖" to MaterialTheme.colorScheme.secondary
        else -> "肥胖" to MaterialTheme.colorScheme.error
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.1f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "BMI 指数",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = color
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "您的BMI为 ${String.format("%.1f", bmi)}，属于${category}范围",
                style = MaterialTheme.typography.bodyMedium
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "BMI参考范围：偏瘦 <18.5 | 正常 18.5-24 | 偏胖 24-28 | 肥胖 >28",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun HealthIndicatorsCard(
    bmi: Float?,
    height: Float?,
    weight: Float?
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "健康指标",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                HealthIndicatorItem(
                    label = "身高",
                    value = if (height != null && height > 0) "${height}cm" else "--",
                    icon = Icons.Default.Height
                )
                HealthIndicatorItem(
                    label = "体重",
                    value = if (weight != null && weight > 0) "${weight}kg" else "--",
                    icon = Icons.Default.MonitorWeight
                )
                HealthIndicatorItem(
                    label = "BMI",
                    value = if (bmi != null) String.format("%.1f", bmi) else "--",
                    icon = Icons.Default.Calculate
                )
            }
        }
    }
}

@Composable
fun HealthIndicatorItem(
    label: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            icon,
            contentDescription = label,
            tint = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
