package com.healthapp.ui.auth

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.healthapp.ui.theme.PulseVitaTheme
import com.healthapp.util.ServerConfig

/**
 * 服务器地址设置对话框
 */
@Composable
fun ServerSettingsDialog(
    onDismiss: () -> Unit
) {
    val scheme = PulseVitaTheme.currentScheme()
    val context = LocalContext.current
    val serverConfig = remember { ServerConfig(context) }

    var editUrl by remember { mutableStateOf(serverConfig.getServerUrl()) }
    var isError by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "服务器设置",
                style = MaterialTheme.typography.titleLarge
            )
        },
        text = {
            Column {
                Text(
                    text = "修改后端服务器地址",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "格式: https://xxx.lhr.life/",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                    fontSize = 11.sp
                )
                Spacer(modifier = Modifier.height(16.dp))
                OutlinedTextField(
                    value = editUrl,
                    onValueChange = {
                        editUrl = it
                        isError = false
                    },
                    label = { Text("服务器地址") },
                    singleLine = true,
                    isError = isError,
                    supportingText = if (isError) {
                        { Text("请输入有效的 URL（http:// 或 https://）") }
                    } else null,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                // 当前状态提示
                if (!serverConfig.isCustomUrl()) {
                    Text(
                        text = "当前使用默认地址",
                        style = MaterialTheme.typography.bodySmall,
                        color = scheme.textSecondary.copy(alpha = 0.6f),
                        fontSize = 11.sp
                    )
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (editUrl.isNotBlank() &&
                        (editUrl.startsWith("http://") || editUrl.startsWith("https://"))
                    ) {
                        serverConfig.setServerUrl(editUrl)
                        onDismiss()
                    } else {
                        isError = true
                    }
                }
            ) {
                Text("保存")
            }
        },
        dismissButton = {
            Row {
                if (serverConfig.isCustomUrl()) {
                    TextButton(
                        onClick = {
                            serverConfig.resetToDefault()
                            onDismiss()
                        }
                    ) {
                        Text("恢复默认", color = scheme.error)
                    }
                    Spacer(modifier = Modifier.width(4.dp))
                }
                TextButton(onClick = onDismiss) {
                    Text("取消")
                }
            }
        }
    )
}

/**
 * 服务器设置图标按钮（用于登录/注册页面右上角）
 */
@Composable
fun ServerSettingsIconButton() {
    var showDialog by remember { mutableStateOf(false) }

    IconButton(onClick = { showDialog = true }) {
        Icon(
            imageVector = Icons.Default.Settings,
            contentDescription = "服务器设置",
            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
        )
    }

    if (showDialog) {
        ServerSettingsDialog(onDismiss = { showDialog = false })
    }
}
