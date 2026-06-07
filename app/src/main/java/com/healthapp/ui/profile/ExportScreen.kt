package com.healthapp.ui.profile

import android.content.Context
import android.content.Intent
import android.os.Environment
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.healthapp.ui.theme.PulseVitaTheme
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.BufferedWriter
import java.io.File
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.inject.Inject

/** 导出类型 */
enum class ExportType(val label: String, val icon: String) {
    ALL("全部数据", "📊"),
    WATER("饮水记录", "💧"),
    EXERCISE("运动记录", "🏃"),
    MOOD("心情记录", "😊"),
    DIET("饮食记录", "🍽️")
}

/** 导出状态 */
sealed class ExportState {
    data object Idle : ExportState()
    data object Exporting : ExportState()
    data class Success(val filePath: String, val recordCount: Int) : ExportState()
    data class Error(val message: String) : ExportState()
}

@HiltViewModel
class ExportViewModel @Inject constructor(
    private val database: com.healthapp.data.local.AppDatabase
) : ViewModel() {

    var exportState by mutableStateOf<ExportState>(ExportState.Idle)
        private set
    var selectedType by mutableStateOf(ExportType.ALL)
        private set

    fun selectType(type: ExportType) {
        selectedType = type
    }

    fun exportData(context: Context) {
        if (exportState is ExportState.Exporting) return

        exportState = ExportState.Exporting
        val exportType = selectedType

        viewModelScope.launch {
            try {
                val result = withContext(Dispatchers.IO) {
                    performExport(context, exportType)
                }
                exportState = result
            } catch (e: Exception) {
                exportState = ExportState.Error(e.message ?: "导出失败")
            }
        }
    }

    private suspend fun performExport(context: Context, type: ExportType): ExportState {
        val dateStr = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"))
        val fileName = "health_${type.name.lowercase()}_$dateStr.csv"

        val dir = context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)
            ?: return ExportState.Error("无法访问存储目录")
        if (!dir.exists()) dir.mkdirs()

        val file = File(dir, fileName)
        var totalRecords = 0

        file.bufferedWriter().use { writer ->
            when (type) {
                ExportType.ALL -> {
                    totalRecords += writeWaterCsv(writer)
                    writer.newLine()
                    totalRecords += writeExerciseCsv(writer)
                    writer.newLine()
                    totalRecords += writeMoodCsv(writer)
                    writer.newLine()
                    totalRecords += writeDietCsv(writer)
                }
                ExportType.WATER -> {
                    totalRecords += writeWaterCsv(writer)
                }
                ExportType.EXERCISE -> {
                    totalRecords += writeExerciseCsv(writer)
                }
                ExportType.MOOD -> {
                    totalRecords += writeMoodCsv(writer)
                }
                ExportType.DIET -> {
                    totalRecords += writeDietCsv(writer)
                }
            }
        }

        return ExportState.Success(file.absolutePath, totalRecords)
    }

    private suspend fun writeWaterCsv(writer: BufferedWriter): Int {
        val records = database.waterRecordDao().getAllRecords()
        if (records.isEmpty()) return 0
        writer.write("--- 饮水记录 ---")
        writer.newLine()
        writer.write("日期,饮水量(ml),备注,时间戳")
        writer.newLine()
        records.forEach { r ->
            writer.write("${r.date},${r.amount},${r.note ?: ""},${r.timestamp}")
            writer.newLine()
        }
        return records.size
    }

    private suspend fun writeExerciseCsv(writer: BufferedWriter): Int {
        val records = database.exerciseRecordDao().getAllRecords()
        if (records.isEmpty()) return 0
        writer.write("--- 运动记录 ---")
        writer.newLine()
        writer.write("日期,运动类型,时长(分钟),步数,备注,时间戳")
        writer.newLine()
        records.forEach { r ->
            writer.write("${r.date},${r.type},${r.duration},${r.steps ?: ""},${r.note ?: ""},${r.timestamp}")
            writer.newLine()
        }
        return records.size
    }

    private suspend fun writeMoodCsv(writer: BufferedWriter): Int {
        val records = database.moodRecordDao().getAllRecords()
        if (records.isEmpty()) return 0
        writer.write("--- 心情记录 ---")
        writer.newLine()
        writer.write("日期,心情等级(1-5),心情图标,备注,时间戳")
        writer.newLine()
        records.forEach { r ->
            writer.write("${r.date},${r.moodLevel},${r.moodIcon},${r.note ?: ""},${r.timestamp}")
            writer.newLine()
        }
        return records.size
    }

    private suspend fun writeDietCsv(writer: BufferedWriter): Int {
        val records = database.dietRecordDao().getAllRecords()
        if (records.isEmpty()) return 0
        writer.write("--- 饮食记录 ---")
        writer.newLine()
        writer.write("日期,餐次,描述,卡路里,时间戳")
        writer.newLine()
        records.forEach { r ->
            writer.write("${r.date},${r.mealType},${r.description},${r.calories ?: ""},${r.timestamp}")
            writer.newLine()
        }
        return records.size
    }

    fun resetState() {
        exportState = ExportState.Idle
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExportScreen(
    navController: NavController,
    viewModel: ExportViewModel = hiltViewModel()
) {
    val scheme = PulseVitaTheme.currentScheme()
    val context = LocalContext.current
    val exportState = viewModel.exportState

    Column(modifier = Modifier.fillMaxSize()) {
        TopAppBar(
            title = { Text("数据导出") },
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
            // 说明卡片
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = scheme.primaryContainer)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.Info,
                            contentDescription = null,
                            tint = scheme.primary,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "导出说明",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = scheme.textPrimary
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "将您的健康数据导出为 CSV 文件，可用于数据分析或备份。导出文件保存在应用专属存储目录中。",
                        style = MaterialTheme.typography.bodyMedium,
                        color = scheme.textSecondary
                    )
                }
            }

            // 导出类型选择
            Text(
                text = "选择导出类型",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            ExportType.entries.forEach { type ->
                val isSelected = viewModel.selectedType == type
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isSelected)
                            scheme.primary.copy(alpha = 0.12f)
                        else
                            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                    ),
                    border = if (isSelected)
                        BorderStroke(2.dp, scheme.primary)
                    else
                        null,
                    onClick = { viewModel.selectType(type) }
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = type.icon,
                            style = MaterialTheme.typography.headlineSmall
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = type.label,
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.weight(1f)
                        )
                        if (isSelected) {
                            Icon(
                                Icons.Default.CheckCircle,
                                contentDescription = "已选中",
                                tint = scheme.primary,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // 导出按钮
            Button(
                onClick = { viewModel.exportData(context) },
                modifier = Modifier.fillMaxWidth(),
                enabled = exportState !is ExportState.Exporting,
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = scheme.primary)
            ) {
                if (exportState is ExportState.Exporting) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = MaterialTheme.colorScheme.onPrimary,
                        strokeWidth = 2.dp
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("正在导出...")
                } else {
                    Icon(Icons.Default.FileDownload, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("导出 CSV 文件", fontWeight = FontWeight.SemiBold)
                }
            }

            // 结果显示
            when (val state = exportState) {
                is ExportState.Success -> {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = scheme.success.copy(alpha = 0.1f)
                        )
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    Icons.Default.CheckCircle,
                                    contentDescription = null,
                                    tint = scheme.success,
                                    modifier = Modifier.size(24.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "导出成功",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = scheme.success
                                )
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "共导出 ${state.recordCount} 条记录",
                                style = MaterialTheme.typography.bodyMedium
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "文件路径：${state.filePath}",
                                style = MaterialTheme.typography.bodySmall,
                                color = scheme.textSecondary
                            )
                            Spacer(modifier = Modifier.height(12.dp))

                            // 分享按钮
                            OutlinedButton(
                                onClick = {
                                    shareFile(context, state.filePath)
                                },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Icon(Icons.Default.Share, contentDescription = null)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("分享文件")
                            }
                        }
                    }
                }
                is ExportState.Error -> {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = scheme.error.copy(alpha = 0.1f)
                        )
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.Error,
                                contentDescription = null,
                                tint = scheme.error,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "导出失败：${state.message}",
                                style = MaterialTheme.typography.bodyMedium,
                                color = scheme.error
                            )
                        }
                    }
                }
                else -> {}
            }
        }
    }
}

private fun shareFile(context: Context, filePath: String) {
    try {
        val file = File(filePath)
        val uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            file
        )
        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/csv"
            putExtra(Intent.EXTRA_STREAM, uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        context.startActivity(Intent.createChooser(shareIntent, "分享健康数据"))
    } catch (e: Exception) {
        e.printStackTrace()
    }
}
