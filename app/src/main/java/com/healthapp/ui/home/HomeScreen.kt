package com.healthapp.ui.home
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DirectionsWalk
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.healthapp.navigation.Screen
import com.healthapp.ui.DisplayMappings
import com.healthapp.ui.components.AnimatedCircularProgressRing
import com.healthapp.ui.components.AchievementRow
import com.healthapp.ui.components.HealthTipFlipCard
import com.healthapp.ui.components.MoodTrendChart
import com.healthapp.ui.components.QuickActionCard
import com.healthapp.ui.components.SectionHeader
import com.healthapp.ui.components.StreakCalendar
import com.healthapp.ui.components.WaterTrendChart
import com.healthapp.ui.components.ExerciseTrendChart
import com.healthapp.ui.components.WeeklyOverviewCard
import com.healthapp.ui.theme.PulseVitaTheme
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
navController: NavController,
viewModel: HomeViewModel = hiltViewModel()
) {
val uiState by viewModel.uiState.collectAsState()
val snackbarHostState = remember { SnackbarHostState() }
// Show error as snackbar
LaunchedEffect(uiState.errorMessage) {
uiState.errorMessage?.let {
snackbarHostState.showSnackbar(it)
viewModel.clearError()
}
}
Scaffold(
snackbarHost = { SnackbarHost(snackbarHostState) }
) { scaffoldPadding ->
Box(modifier = Modifier.padding(scaffoldPadding)) {
// Loading overlay
if (uiState.isLoading) {
CircularProgressIndicator(
modifier = Modifier
.align(Alignment.Center)
.size(48.dp)
)
}
Column(
modifier = Modifier
.fillMaxSize()
.verticalScroll(rememberScrollState())
) {
// === 1. Integrated Header (replaces TopAppBar) ===
Row(
modifier = Modifier
.fillMaxWidth()
.padding(start = 16.dp, end = 16.dp, top = 16.dp),
verticalAlignment = Alignment.CenterVertically
) {
// Avatar circle
Surface(
shape = CircleShape,
color = MaterialTheme.colorScheme.primaryContainer,
modifier = Modifier.size(48.dp),
onClick = { navController.navigate(Screen.Profile.route) }
) {
Box(contentAlignment = Alignment.Center) {
Icon(
Icons.Default.Person,
contentDescription = "个人档案",
tint = MaterialTheme.colorScheme.primary,
modifier = Modifier.size(28.dp)
)
}
}
Spacer(modifier = Modifier.width(12.dp))
Column(modifier = Modifier.weight(1f)) {
val displayName = if (uiState.userName.isNotEmpty()) {
"${uiState.greeting}, ${uiState.userName}"
} else {
uiState.greeting
}
Text(
text = displayName,
style = MaterialTheme.typography.titleLarge,
fontWeight = FontWeight.Bold
)
}
// Streak badge
if (uiState.streakDays > 0) {
Surface(
shape = RoundedCornerShape(20.dp),
color = MaterialTheme.colorScheme.tertiaryContainer
) {
Row(
modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
verticalAlignment = Alignment.CenterVertically
) {
Text(text = "🔥", style = MaterialTheme.typography.bodySmall)
Spacer(modifier = Modifier.width(3.dp))
Text(
text = "${uiState.streakDays} 天",
style = MaterialTheme.typography.labelSmall,
fontWeight = FontWeight.SemiBold,
color = MaterialTheme.colorScheme.onTertiaryContainer
)
}
}
}
}
Spacer(modifier = Modifier.height(12.dp))
// === 2. Weather + Solar Term compact row ===
Row(
modifier = Modifier
.fillMaxWidth()
.padding(horizontal = 16.dp),
verticalAlignment = Alignment.CenterVertically
) {
if (uiState.weather.temperature.isNotEmpty()) {
Surface(
shape = RoundedCornerShape(20.dp),
color = MaterialTheme.colorScheme.surfaceVariant
) {
Row(
modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
verticalAlignment = Alignment.CenterVertically
) {
Text(
text = uiState.weather.weatherEmoji,
style = MaterialTheme.typography.bodyMedium
)
Spacer(modifier = Modifier.width(4.dp))
Text(
text = uiState.weather.temperature,
style = MaterialTheme.typography.bodyMedium,
fontWeight = FontWeight.SemiBold
)
if (uiState.weather.city.isNotEmpty()) {
Text(
text = " · ${uiState.weather.city}",
style = MaterialTheme.typography.bodySmall,
color = MaterialTheme.colorScheme.onSurfaceVariant
)
}
}
}
}
Spacer(modifier = Modifier.width(8.dp))
// Solar term badge
if (uiState.weather.solarTerm.isNotEmpty()) {
Surface(
shape = RoundedCornerShape(20.dp),
color = MaterialTheme.colorScheme.tertiaryContainer
) {
Row(
modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
verticalAlignment = Alignment.CenterVertically
) {
Text(text = "🌿", style = MaterialTheme.typography.bodySmall)
Spacer(modifier = Modifier.width(3.dp))
Text(
text = uiState.weather.solarTerm,
style = MaterialTheme.typography.bodySmall,
fontWeight = FontWeight.Medium,
color = MaterialTheme.colorScheme.onTertiaryContainer
)
}
}
if (uiState.weather.solarTermTip.isNotEmpty()) {
Spacer(modifier = Modifier.width(6.dp))
Text(
text = uiState.weather.solarTermTip,
style = MaterialTheme.typography.bodySmall,
color = MaterialTheme.colorScheme.onSurfaceVariant,
maxLines = 1,
overflow = TextOverflow.Ellipsis,
modifier = Modifier.weight(1f)
)
}
}
}
Spacer(modifier = Modifier.height(16.dp))
// === 3. Dashboard Rings ===
Card(
modifier = Modifier
.fillMaxWidth()
.padding(horizontal = 16.dp),
shape = RoundedCornerShape(20.dp),
elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
) {
Row(
modifier = Modifier
.fillMaxWidth()
.padding(vertical = 20.dp),
horizontalArrangement = Arrangement.SpaceEvenly
) {
// Water ring
RingMetric(
progress = (uiState.water.amount.toFloat() / uiState.water.goal).coerceIn(0f, 1f),
icon = Icons.Default.WaterDrop,
progressColor = PulseVitaTheme.currentScheme().chartBlue,
trackColor = PulseVitaTheme.currentScheme().chartBlue.copy(alpha = 0.15f),
iconTint = PulseVitaTheme.currentScheme().chartBlue,
value = "${uiState.water.amount}ml",
label = "饮水",
subtitle = "目标${uiState.water.goal}ml"
)
// Exercise ring
RingMetric(
progress = (uiState.exercise.duration.toFloat() / uiState.exercise.goal).coerceIn(0f, 1f),
icon = Icons.Default.DirectionsWalk,
progressColor = PulseVitaTheme.currentScheme().chartGreen,
trackColor = PulseVitaTheme.currentScheme().chartGreen.copy(alpha = 0.15f),
iconTint = PulseVitaTheme.currentScheme().chartGreen,
value = "${uiState.exercise.duration}分钟",
label = "运动",
subtitle = "目标${uiState.exercise.goal}分钟"
)
// Mood ring
val moodProgress = if (uiState.mood.level != null) {
uiState.mood.level!! / 5f
} else 0f
RingMetric(
progress = moodProgress,
icon = Icons.Default.Psychology,
progressColor = PulseVitaTheme.currentScheme().success,
trackColor = MaterialTheme.colorScheme.tertiaryContainer,
iconTint = PulseVitaTheme.currentScheme().success,
value = if (uiState.mood.level != null) {
DisplayMappings.moodLevelName(uiState.mood.level!!)
} else "未记录",
label = "心情",
subtitle = uiState.mood.icon
)
}
}
Spacer(modifier = Modifier.height(20.dp))
// === 4. 周概览卡片 ===
WeeklyOverviewCard(
waterDays = uiState.weeklyWaterDays,
exerciseDays = uiState.weeklyExerciseDays,
moodDays = uiState.weeklyMoodDays,
dietDays = uiState.weeklyDietDays,
waterTrend = uiState.weeklyWaterTrend,
exerciseTrend = uiState.weeklyExerciseTrend,
moodTrend = uiState.weeklyMoodTrend,
dietTrend = uiState.weeklyDietTrend,
modifier = Modifier.padding(horizontal = 16.dp)
)
Spacer(modifier = Modifier.height(20.dp))
// === 5. 打卡日历 ===
SectionHeader(
icon = Icons.Default.CalendarMonth,
title = "打卡日历"
)
Spacer(modifier = Modifier.height(8.dp))
Card(
modifier = Modifier
.fillMaxWidth()
.padding(horizontal = 16.dp),
shape = RoundedCornerShape(16.dp),
elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
) {
StreakCalendar(
streakDates = uiState.streakDates,
modifier = Modifier.padding(12.dp)
)
}
Spacer(modifier = Modifier.height(20.dp))
// === 6. 健康趋势图 ===
SectionHeader(
icon = Icons.Default.TrendingUp,
title = "健康趋势"
)
Spacer(modifier = Modifier.height(8.dp))
// 饮水趋势
Card(
modifier = Modifier
.fillMaxWidth()
.padding(horizontal = 16.dp),
shape = RoundedCornerShape(16.dp),
elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
) {
WaterTrendChart(
dailyAmounts = uiState.weeklyWaterAmounts.map { it.toFloat() },
modifier = Modifier.padding(16.dp)
)
}
Spacer(modifier = Modifier.height(12.dp))
// 运动趋势
Card(
modifier = Modifier
.fillMaxWidth()
.padding(horizontal = 16.dp),
shape = RoundedCornerShape(16.dp),
elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
) {
ExerciseTrendChart(
dailyMinutes = uiState.weeklyExerciseMinutes,
modifier = Modifier.padding(16.dp)
)
}
Spacer(modifier = Modifier.height(12.dp))
// 心情趋势
Card(
modifier = Modifier
.fillMaxWidth()
.padding(horizontal = 16.dp),
shape = RoundedCornerShape(16.dp),
elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
) {
MoodTrendChart(
moodLevels = uiState.weeklyMoodLevels.map { (it ?: 0).toFloat() },
modifier = Modifier.padding(16.dp)
)
}
Spacer(modifier = Modifier.height(20.dp))
// === 6. 成就徽章 ===
if (uiState.achievements.isNotEmpty()) {
SectionHeader(
icon = Icons.Default.EmojiEvents,
title = "我的成就"
)
Spacer(modifier = Modifier.height(8.dp))
AchievementRow(
achievements = uiState.achievements
)
Spacer(modifier = Modifier.height(20.dp))
}
// === 7. Quick Actions ===
SectionHeader(
icon = Icons.Default.EditNote,
title = "快速记录"
)
Spacer(modifier = Modifier.height(8.dp))
Row(
modifier = Modifier
.fillMaxWidth()
.padding(horizontal = 16.dp),
horizontalArrangement = Arrangement.spacedBy(12.dp)
) {
QuickActionCard(
icon = Icons.Default.WaterDrop,
label = "喝水",
iconBackground = PulseVitaTheme.currentScheme().chartBlue.copy(alpha = 0.15f),
iconTint = PulseVitaTheme.currentScheme().chartBlue,
onClick = { navController.navigate(Screen.Diet.route) },
modifier = Modifier.weight(1f)
)
QuickActionCard(
icon = Icons.Default.FitnessCenter,
label = "运动",
iconBackground = PulseVitaTheme.currentScheme().chartGreen.copy(alpha = 0.15f),
iconTint = PulseVitaTheme.currentScheme().chartGreen,
onClick = { navController.navigate(Screen.Exercise.route) },
modifier = Modifier.weight(1f)
)
QuickActionCard(
icon = Icons.Default.Mood,
label = "心情",
iconBackground = PulseVitaTheme.currentScheme().success.copy(alpha = 0.15f),
iconTint = PulseVitaTheme.currentScheme().success,
onClick = { navController.navigate(Screen.Mental.route) },
modifier = Modifier.weight(1f)
)
}
Spacer(modifier = Modifier.height(20.dp))
// === 7. Daily Challenge ===
val displayChallengeTitle = uiState.serverChallengeTitle ?: uiState.extras.challengeTitle
val displayChallengeDesc = uiState.serverChallenge ?: uiState.extras.challengeDesc
val displayChallengeIcon = uiState.serverChallengeIcon ?: uiState.extras.challengeIcon
if (displayChallengeTitle.isNotEmpty()) {
SectionHeader(
icon = Icons.Default.EmojiEvents,
title = "今日挑战"
)
Spacer(modifier = Modifier.height(8.dp))
val challengeScheme = PulseVitaTheme.currentScheme()
Card(
modifier = Modifier
.fillMaxWidth()
.padding(horizontal = 16.dp),
shape = RoundedCornerShape(20.dp),
elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
colors = CardDefaults.cardColors(
containerColor = challengeScheme.primaryContainer
)
) {
Box(modifier = Modifier.fillMaxWidth()) {
// 背景装饰圆
Box(
modifier = Modifier
.size(120.dp)
.align(Alignment.TopEnd)
.offset(x = 30.dp, y = (-20).dp)
) {
Surface(
modifier = Modifier.fillMaxSize(),
shape = CircleShape,
color = challengeScheme.primary.copy(alpha = 0.08f)
) {}
}
// 内容
Column(
modifier = Modifier
.fillMaxWidth()
.padding(20.dp)
) {
Row(
verticalAlignment = Alignment.CenterVertically
) {
// 图标容器
Surface(
shape = RoundedCornerShape(14.dp),
color = challengeScheme.primary.copy(alpha = 0.15f),
modifier = Modifier.size(52.dp)
) {
Box(contentAlignment = Alignment.Center) {
Text(
text = displayChallengeIcon,
style = MaterialTheme.typography.headlineMedium
)
}
}
Spacer(modifier = Modifier.width(16.dp))
Column(modifier = Modifier.weight(1f)) {
Text(
text = "今日挑战",
style = MaterialTheme.typography.labelMedium,
color = challengeScheme.primary,
fontWeight = FontWeight.SemiBold
)
Spacer(modifier = Modifier.height(2.dp))
Text(
text = displayChallengeTitle,
style = MaterialTheme.typography.titleMedium,
fontWeight = FontWeight.Bold,
color = challengeScheme.textPrimary
)
}
}
Spacer(modifier = Modifier.height(14.dp))
// 描述区域
Surface(
shape = RoundedCornerShape(12.dp),
color = challengeScheme.surface.copy(alpha = 0.6f),
modifier = Modifier.fillMaxWidth()
) {
Text(
text = displayChallengeDesc,
style = MaterialTheme.typography.bodyMedium,
color = challengeScheme.textSecondary,
modifier = Modifier.padding(14.dp),
lineHeight = MaterialTheme.typography.bodyMedium.lineHeight
)
}
Spacer(modifier = Modifier.height(14.dp))
// 底部行动提示
Row(
modifier = Modifier.fillMaxWidth(),
horizontalArrangement = Arrangement.End,
verticalAlignment = Alignment.CenterVertically
) {
Text(
text = "开始挑战",
style = MaterialTheme.typography.labelLarge,
color = challengeScheme.primary,
fontWeight = FontWeight.SemiBold
)
Spacer(modifier = Modifier.width(4.dp))
Icon(
imageVector = Icons.Default.KeyboardArrowRight,
contentDescription = null,
tint = challengeScheme.primary,
modifier = Modifier.size(18.dp)
)
}
}
}
}
Spacer(modifier = Modifier.height(20.dp))
}
// === 8. Health Tip — 翻转卡片 ===
val tipTitle = uiState.serverTipTitle ?: uiState.healthTip?.title
val tipContent = uiState.serverTip ?: uiState.healthTip?.content
if (tipTitle != null && tipContent != null) {
SectionHeader(
icon = Icons.Default.Lightbulb,
title = "健康贴士"
)
Spacer(modifier = Modifier.height(8.dp))
HealthTipFlipCard(
tipTitle = tipTitle,
tipContent = tipContent,
modifier = Modifier.padding(horizontal = 16.dp)
)
Spacer(modifier = Modifier.height(20.dp))
}
// === 9. Quote Card ===
if (uiState.extras.quote.isNotEmpty()) {
Card(
modifier = Modifier
.fillMaxWidth()
.padding(horizontal = 16.dp),
shape = RoundedCornerShape(12.dp),
elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
) {
Column(modifier = Modifier.padding(16.dp)) {
Text(
text = "\u201C${uiState.extras.quote}\u201D",
style = MaterialTheme.typography.bodyMedium,
color = MaterialTheme.colorScheme.onSurfaceVariant
)
if (uiState.extras.quoteFrom.isNotEmpty()) {
Spacer(modifier = Modifier.height(4.dp))
Text(
text = "—— ${uiState.extras.quoteFrom}",
style = MaterialTheme.typography.bodySmall,
color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
)
}
}
}
Spacer(modifier = Modifier.height(20.dp))
}
// === 10. Food Knowledge Card ===
if (uiState.extras.foodName.isNotEmpty()) {
SectionHeader(
icon = Icons.Default.Spa,
title = "今日食材"
)
Spacer(modifier = Modifier.height(8.dp))
Card(
modifier = Modifier
.fillMaxWidth()
.padding(horizontal = 16.dp),
shape = RoundedCornerShape(16.dp),
elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
) {
Box(
modifier = Modifier
.fillMaxWidth()
.height(3.dp)
) {
Surface(
color = PulseVitaTheme.currentScheme().success,
modifier = Modifier.fillMaxSize()
) {}
}
Column(modifier = Modifier.padding(16.dp)) {
Row(verticalAlignment = Alignment.CenterVertically) {
Text(
text = uiState.extras.foodName,
style = MaterialTheme.typography.headlineSmall,
fontWeight = FontWeight.Bold,
color = MaterialTheme.colorScheme.onSurface
)
Spacer(modifier = Modifier.width(10.dp))
Surface(
shape = RoundedCornerShape(6.dp),
color = foodPropertyColor(uiState.extras.foodProperty, PulseVitaTheme.currentScheme())
) {
Text(
text = uiState.extras.foodProperty,
modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
style = MaterialTheme.typography.labelSmall,
color = MaterialTheme.colorScheme.onPrimary,
fontWeight = FontWeight.Medium
)
}
}
Spacer(modifier = Modifier.height(8.dp))
Text(
text = uiState.extras.foodPropertyDesc,
style = MaterialTheme.typography.titleSmall,
fontWeight = FontWeight.SemiBold,
color = MaterialTheme.colorScheme.primary
)
Spacer(modifier = Modifier.height(8.dp))
Text(
text = uiState.extras.foodBenefits,
style = MaterialTheme.typography.bodyMedium,
color = MaterialTheme.colorScheme.onSurfaceVariant,
maxLines = 4,
overflow = TextOverflow.Ellipsis
)
Spacer(modifier = Modifier.height(10.dp))
Surface(
shape = RoundedCornerShape(8.dp),
color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f)
) {
Text(
text = uiState.extras.foodHowToEat,
modifier = Modifier.padding(10.dp),
style = MaterialTheme.typography.bodySmall,
color = MaterialTheme.colorScheme.onSecondaryContainer
)
}
}
}
Spacer(modifier = Modifier.height(16.dp))
}
Spacer(modifier = Modifier.height(16.dp))
}
} // Box
} // Scaffold
}
private fun foodPropertyColor(property: String, scheme: com.healthapp.ui.theme.MutedColorScheme): Color {
return when (property) {
"热性" -> scheme.error
"温性" -> scheme.chartOrange
"平性" -> scheme.success
"凉性" -> scheme.chartBlue
"寒性" -> scheme.primaryDark
else -> scheme.textSecondary
}
}
@Composable
private fun RingMetric(
progress: Float,
icon: ImageVector,
progressColor: Color,
trackColor: Color,
iconTint: Color,
value: String,
label: String,
subtitle: String
) {
Column(
horizontalAlignment = Alignment.CenterHorizontally
) {
AnimatedCircularProgressRing(
progress = progress,
size = 72.dp,
strokeWidth = 6.dp,
progressColor = progressColor,
trackColor = trackColor,
icon = icon,
iconTint = iconTint
)
Spacer(modifier = Modifier.height(6.dp))
Text(
text = value,
style = MaterialTheme.typography.titleSmall,
fontWeight = FontWeight.Bold,
color = MaterialTheme.colorScheme.onPrimaryContainer
)
Text(
text = label,
style = MaterialTheme.typography.bodySmall,
color = MaterialTheme.colorScheme.onPrimaryContainer
)
Text(
text = subtitle,
style = MaterialTheme.typography.bodySmall,
color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.6f)
)
}
}
