package com.healthapp.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.healthapp.ui.theme.PulseVitaTheme

/**
 * 将常见英文错误消息转换为中文友好提示
 */
fun mapErrorToChinese(rawMessage: String?): String {
    if (rawMessage.isNullOrBlank()) return "出了点问题，请稍后再试"
    val msg = rawMessage.lowercase()
    return when {
        "timeout" in msg -> "网络超时，请检查网络连接"
        "unable to resolve host" in msg -> "无法连接服务器"
        "sockettimeoutexception" in msg -> "网络超时"
        "ioexception" in msg -> "网络异常"
        "http 401" in msg || "401" in msg && "unauthorized" in msg -> "登录已过期，请重新登录"
        "http 403" in msg || "403" in msg && "forbidden" in msg -> "没有权限访问"
        "http 404" in msg || "404" in msg && "not found" in msg -> "请求的内容不存在"
        "http 500" in msg || "500" in msg -> "服务器繁忙，请稍后再试"
        "http 503" in msg || "503" in msg -> "服务暂时不可用"
        "connect" in msg && "refused" in msg -> "无法连接服务器"
        "network" in msg -> "网络异常，请检查网络连接"
        else -> "出了点问题，请稍后再试"
    }
}

/**
 * 统一错误显示卡片组件
 * 展示中文友好错误提示，并提供重试按钮
 */
@Composable
fun ErrorRetryCard(
    message: String,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scheme = PulseVitaTheme.currentScheme()
    val chineseMessage = mapErrorToChinese(message)

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = scheme.error.copy(alpha = 0.12f)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Default.ErrorOutline,
                contentDescription = null,
                modifier = Modifier.size(48.dp),
                tint = scheme.error
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = chineseMessage,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium,
                color = scheme.textPrimary,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = onRetry,
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = scheme.primary
                )
            ) {
                Text("重试", fontWeight = FontWeight.SemiBold)
            }
        }
    }
}
