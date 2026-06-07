package com.healthapp.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.healthapp.data.remote.SuggestionData
import com.healthapp.ui.theme.PulseVitaTheme

/**
 * 首页集成的建议卡片组件
 * 展示 3 条建议，每条有图标+标题+描述+投票按钮
 * 用户已投票的建议高亮显示
 */
@Composable
fun SuggestionCard(
    suggestions: List<SuggestionData>,
    userVotedId: Long?,
    onVote: (Long, Long) -> Unit,
    modifier: Modifier = Modifier
) {
    val scheme = PulseVitaTheme.currentScheme()

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "今日建议",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(12.dp))

            suggestions.forEach { suggestion ->
                val isVoted = userVotedId == suggestion.id
                SuggestionItem(
                    suggestion = suggestion,
                    isVoted = isVoted,
                    onVote = { onVote(suggestion.id, suggestion.id) },
                    scheme = scheme
                )
                if (suggestion != suggestions.last()) {
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }
}

@Composable
private fun SuggestionItem(
    suggestion: SuggestionData,
    isVoted: Boolean,
    onVote: () -> Unit,
    scheme: com.healthapp.ui.theme.MutedColorScheme
) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = if (isVoted) scheme.primaryContainer else scheme.surfaceVariant,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icon
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = if (isVoted) scheme.primary.copy(alpha = 0.15f) else scheme.primary.copy(alpha = 0.08f),
                modifier = Modifier.size(40.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = suggestion.icon.ifEmpty { "💡" },
                        style = MaterialTheme.typography.titleMedium
                    )
                }
            }
            Spacer(modifier = Modifier.width(12.dp))

            // Title + Description
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = suggestion.title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = if (isVoted) scheme.primary else MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = suggestion.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = if (isVoted) scheme.primary.copy(alpha = 0.7f) else MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
            Spacer(modifier = Modifier.width(8.dp))

            // Vote button
            IconButton(
                onClick = onVote,
                enabled = !isVoted
            ) {
                Icon(
                    Icons.Default.ThumbUp,
                    contentDescription = "投票",
                    tint = if (isVoted) scheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}
