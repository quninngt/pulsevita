package com.healthapp.ui.suggestion

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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.healthapp.data.remote.SuggestionData
import com.healthapp.ui.theme.PulseVitaTheme
import com.healthapp.ui.components.ErrorRetryCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SuggestionScreen(
    navController: NavController,
    viewModel: SuggestionViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    // Error handled inline with ErrorRetryCard

    val scheme = PulseVitaTheme.currentScheme()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("每日建议") },
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
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        if (uiState.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else if (uiState.error != null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                ErrorRetryCard(
                    message = uiState.error!!,
                    onRetry = { viewModel.loadDailySuggestions() },
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }
        } else if (uiState.suggestions.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        Icons.Default.Lightbulb,
                        contentDescription = null,
                        modifier = Modifier.size(64.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "暂无今日建议",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Header
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = scheme.primaryContainer.copy(alpha = 0.3f)
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "💡",
                                fontSize = 32.sp
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "今日健康建议",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = scheme.textPrimary
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "为你量身定制的健康小贴士，投票选出最想执行的建议",
                                style = MaterialTheme.typography.bodySmall,
                                color = scheme.textSecondary,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }

                // Suggestion cards
                items(uiState.suggestions) { suggestion ->
                    SuggestionDetailCard(
                        suggestion = suggestion,
                        isVoted = uiState.userVotedId == suggestion.id,
                        onVote = {
                            viewModel.vote(uiState.dailySuggestionId, suggestion.id)
                        }
                    )
                }

                // Bottom hint
                item {
                    Text(
                        text = "投票后，获得最多投票的建议将进入优化计划",
                        style = MaterialTheme.typography.bodySmall,
                        color = scheme.textSecondary.copy(alpha = 0.6f),
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun SuggestionDetailCard(
    suggestion: SuggestionData,
    isVoted: Boolean,
    onVote: () -> Unit
) {
    val scheme = PulseVitaTheme.currentScheme()

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isVoted)
                scheme.primaryContainer.copy(alpha = 0.4f)
            else
                MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (isVoted) 4.dp else 2.dp
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                // Icon
                Text(
                    text = suggestion.icon,
                    fontSize = 28.sp
                )
                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = suggestion.title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        if (suggestion.category.isNotEmpty()) {
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = scheme.primary.copy(alpha = 0.1f)
                            ) {
                                Text(
                                    text = getCategoryLabel(suggestion.category),
                                    style = MaterialTheme.typography.labelSmall,
                                    color = scheme.primary,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                        if (suggestion.difficulty.isNotEmpty()) {
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = getDifficultyColor(suggestion.difficulty).copy(alpha = 0.1f)
                            ) {
                                Text(
                                    text = getDifficultyLabel(suggestion.difficulty),
                                    style = MaterialTheme.typography.labelSmall,
                                    color = getDifficultyColor(suggestion.difficulty),
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = suggestion.description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Vote button
            Button(
                onClick = onVote,
                enabled = !isVoted,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = if (isVoted)
                    ButtonDefaults.buttonColors(
                        containerColor = scheme.success,
                        disabledContainerColor = scheme.success.copy(alpha = 0.6f)
                    )
                else
                    ButtonDefaults.buttonColors(containerColor = scheme.primary)
            ) {
                if (isVoted) {
                    Icon(
                        Icons.Default.Check,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("已投票", fontWeight = FontWeight.Medium)
                } else {
                    Icon(
                        Icons.Default.ThumbUp,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("投票", fontWeight = FontWeight.Medium)
                }
            }
        }
    }
}

private fun getCategoryLabel(category: String): String = when (category) {
    "DIET" -> "饮食"
    "EXERCISE" -> "运动"
    "MENTAL" -> "心理"
    "SLEEP" -> "睡眠"
    else -> category
}

private fun getDifficultyLabel(difficulty: String): String = when (difficulty) {
    "EASY" -> "简单"
    "MEDIUM" -> "中等"
    "HARD" -> "困难"
    else -> difficulty
}

@Composable
private fun getDifficultyColor(difficulty: String) = when (difficulty) {
    "EASY" -> PulseVitaTheme.currentScheme().success
    "MEDIUM" -> PulseVitaTheme.currentScheme().chartOrange
    "HARD" -> PulseVitaTheme.currentScheme().error
    else -> MaterialTheme.colorScheme.onSurfaceVariant
}
