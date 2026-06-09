package com.healthapp.ui.report

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.clickable
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.healthapp.data.remote.HealthReport
import com.healthapp.ui.theme.PulseVitaTheme
import com.healthapp.ui.components.ErrorRetryCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportScreen(
    navController: NavController,
    viewModel: ReportViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    var selectedTab by remember { mutableIntStateOf(0) }

    // Error handled inline with ErrorRetryCard

    // Load report when tab changes (only in non-history mode)
    LaunchedEffect(selectedTab) {
        val type = if (selectedTab == 0) "weekly" else "monthly"
        viewModel.loadLatestReport(type)
    }

    val scheme = PulseVitaTheme.currentScheme()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        when {
                            uiState.showHistory && uiState.selectedReportIndex >= 0 -> "报告详情"
                            uiState.showHistory -> "历史报告"
                            else -> "健康报告"
                        }
                    )
                },
                navigationIcon = {
                    IconButton(onClick = {
                        when {
                            uiState.showHistory && uiState.selectedReportIndex >= 0 -> {
                                viewModel.backToHistoryList()
                            }
                            uiState.showHistory -> {
                                viewModel.toggleHistory()
                            }
                            else -> {
                                navController.popBackStack()
                            }
                        }
                    }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "返回")
                    }
                },
                actions = {
                    if (!uiState.showHistory) {
                        IconButton(onClick = {
                            viewModel.loadHistory(
                                if (selectedTab == 0) "weekly" else "monthly"
                            )
                        }) {
                            Icon(
                                Icons.Default.History,
                                contentDescription = "历史报告"
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary,
                    actionIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Tabs: 周报 / 月报
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.primary
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = {
                        selectedTab = 0
                        if (uiState.showHistory) {
                            viewModel.loadHistory("weekly")
                        }
                    },
                    text = { Text("周报") },
                    icon = {
                        Icon(
                            Icons.Default.DateRange,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = {
                        selectedTab = 1
                        if (uiState.showHistory) {
                            viewModel.loadHistory("monthly")
                        }
                    },
                    text = { Text("月报") },
                    icon = {
                        Icon(
                            Icons.Default.CalendarMonth,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                )
            }

            // Content area
            when {
                uiState.isLoading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
                uiState.error != null -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        ErrorRetryCard(
                            message = uiState.error!!,
                            onRetry = {
                                val type = if (selectedTab == 0) "weekly" else "monthly"
                                viewModel.loadLatestReport(type)
                            },
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )
                    }
                }
                uiState.showHistory && uiState.selectedReportIndex >= 0 -> {
                    // History detail with prev/next navigation
                    val currentIndex = uiState.selectedReportIndex
                    val total = uiState.reportList.size
                    uiState.currentReport?.let { report ->
                        HistoryDetailContent(
                            report = report,
                            currentIndex = currentIndex,
                            totalCount = total,
                            onPrevious = { viewModel.navigateToPrevious() },
                            onNext = { viewModel.navigateToNext() }
                        )
                    } ?: ReportEmptyState()
                }
                uiState.showHistory -> {
                    // History list view
                    if (uiState.reportList.isEmpty()) {
                        HistoryEmptyState()
                    } else {
                        HistoryListContent(
                            reports = uiState.reportList,
                            onReportClick = { index -> viewModel.selectReport(index) }
                        )
                    }
                }
                uiState.currentReport == null -> {
                    ReportEmptyState()
                }
                else -> {
                    ReportContent(report = uiState.currentReport!!)
                }
            }
        }
    }
}

@Composable
private fun HistoryListContent(
    reports: List<HealthReport>,
    onReportClick: (Int) -> Unit
) {
    val scheme = PulseVitaTheme.currentScheme()

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        itemsIndexed(reports) { index, report ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onReportClick(index) },
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = scheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    // 标题行
                    Text(
                        text = report.title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = scheme.primaryDark,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    // 日期范围
                    if (report.startDate.isNotEmpty() && report.endDate.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Default.DateRange,
                                contentDescription = null,
                                modifier = Modifier.size(14.dp),
                                tint = scheme.textSecondary
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "${report.startDate} ~ ${report.endDate}",
                                style = MaterialTheme.typography.bodySmall,
                                color = scheme.textSecondary
                            )
                        }
                    }

                    // 摘要（前50字）
                    if (report.summary.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = if (report.summary.length > 50) {
                                report.summary.take(50) + "…"
                            } else {
                                report.summary
                            },
                            style = MaterialTheme.typography.bodyMedium,
                            color = scheme.textSecondary,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    // 右下角箭头
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        Icon(
                            Icons.Default.ChevronRight,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp),
                            tint = scheme.primary.copy(alpha = 0.5f)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun HistoryDetailContent(
    report: HealthReport,
    currentIndex: Int,
    totalCount: Int,
    onPrevious: () -> Unit,
    onNext: () -> Unit
) {
    val scheme = PulseVitaTheme.currentScheme()

    Column(modifier = Modifier.fillMaxSize()) {
        // 导航栏
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextButton(
                onClick = onPrevious,
                enabled = currentIndex > 0
            ) {
                Icon(
                    Icons.Default.ChevronLeft,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text("上一篇")
            }

            Text(
                text = "${currentIndex + 1} / $totalCount",
                style = MaterialTheme.typography.bodyMedium,
                color = scheme.textSecondary
            )

            TextButton(
                onClick = onNext,
                enabled = currentIndex < totalCount - 1
            ) {
                Text("下一篇")
                Spacer(modifier = Modifier.width(4.dp))
                Icon(
                    Icons.Default.ChevronRight,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
            }
        }

        Divider(
            modifier = Modifier.padding(horizontal = 16.dp),
            color = scheme.surfaceVariant
        )

        // 报告内容（复用已有组件）
        ReportContent(report = report)
    }
}

@Composable
private fun HistoryEmptyState() {
    val scheme = PulseVitaTheme.currentScheme()

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                Icons.Default.History,
                contentDescription = null,
                modifier = Modifier.size(64.dp),
                tint = scheme.textSecondary.copy(alpha = 0.4f)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "暂无历史报告",
                style = MaterialTheme.typography.bodyLarge,
                color = scheme.textSecondary
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "持续记录健康数据后将生成历史报告",
                style = MaterialTheme.typography.bodyMedium,
                color = scheme.textSecondary.copy(alpha = 0.6f)
            )
        }
    }
}

@Composable
private fun ReportContent(report: HealthReport) {
    val scheme = PulseVitaTheme.currentScheme()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        // Report header card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = scheme.primaryContainer)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = report.title,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = scheme.primaryDark
                )
                if (report.startDate.isNotEmpty() && report.endDate.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "${report.startDate} ~ ${report.endDate}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = scheme.primary.copy(alpha = 0.7f)
                    )
                }
                if (report.summary.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = report.summary,
                        style = MaterialTheme.typography.bodyMedium,
                        color = scheme.primaryDark.copy(alpha = 0.8f)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Report content (Markdown-style rendering)
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                MarkdownContent(content = report.content)
            }
        }
    }
}

/**
 * 简单的 Markdown 风格渲染
 * 支持标题(#)、加粗(**)、列表(-)、分隔线(---)
 */
@Composable
private fun MarkdownContent(content: String) {
    val lines = content.split("\n")

    lines.forEach { line ->
        val trimmed = line.trim()
        when {
            trimmed.startsWith("# ") -> {
                Text(
                    text = trimmed.removePrefix("# "),
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }
            trimmed.startsWith("## ") -> {
                Text(
                    text = trimmed.removePrefix("## "),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(vertical = 6.dp)
                )
            }
            trimmed.startsWith("### ") -> {
                Text(
                    text = trimmed.removePrefix("### "),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(vertical = 4.dp)
                )
            }
            trimmed == "---" || trimmed == "***" -> {
                Divider(
                    modifier = Modifier.padding(vertical = 8.dp),
                    color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                )
            }
            trimmed.startsWith("- ") || trimmed.startsWith("* ") -> {
                Row(modifier = Modifier.padding(vertical = 2.dp)) {
                    Text(
                        text = "•",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(end = 8.dp)
                    )
                    Text(
                        text = trimmed.removePrefix("- ").removePrefix("* "),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
            trimmed.isNotEmpty() -> {
                // Handle bold text (**text**)
                val displayText = trimmed.replace("**", "")
                Text(
                    text = displayText,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(vertical = 2.dp)
                )
            }
        }
    }
}

@Composable
private fun ReportEmptyState() {
    val scheme = PulseVitaTheme.currentScheme()

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                Icons.Default.Assessment,
                contentDescription = null,
                modifier = Modifier.size(64.dp),
                tint = scheme.textSecondary.copy(alpha = 0.4f)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "暂无报告",
                style = MaterialTheme.typography.bodyLarge,
                color = scheme.textSecondary
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "持续记录健康数据后将生成报告",
                style = MaterialTheme.typography.bodyMedium,
                color = scheme.textSecondary.copy(alpha = 0.6f)
            )
        }
    }
}
