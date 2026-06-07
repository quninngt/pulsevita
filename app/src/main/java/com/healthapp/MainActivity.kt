package com.healthapp

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.healthapp.navigation.AuthNavHost
import com.healthapp.navigation.HealthNavHost
import com.healthapp.ui.auth.AuthViewModel
import com.healthapp.ui.theme.HealthAppTheme
import com.healthapp.ui.theme.ThemeViewModel
import com.healthapp.util.CrashLogger
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val savedCrashLog = CrashLogger.getCrashLog(this)
        setContent {
            val themeViewModel: ThemeViewModel = hiltViewModel()
            val currentScheme by themeViewModel.currentScheme.collectAsState()

            HealthAppTheme(colorScheme = currentScheme) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    if (savedCrashLog != null) {
                        CrashLogScreen(savedCrashLog)
                    } else {
                        AppContent()
                    }
                }
            }
        }
    }
}

@Composable
fun AppContent() {
    val authViewModel: AuthViewModel = hiltViewModel()
    val authState by authViewModel.uiState.collectAsState()

    if (authState.isLoggedIn) {
        HealthNavHost()
    } else {
        AuthNavHost(onLoginSuccess = { /* Auth state will update via flow */ })
    }
}

@Composable
fun CrashLogScreen(crashLog: String) {
    val context = LocalContext.current
    Box(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text("⚠️ 上次发生了崩溃", fontSize = 20.sp, fontWeight = FontWeight.Bold)
            Text("请截图或复制日志发给开发者：", fontSize = 14.sp)
            Card(modifier = Modifier.fillMaxWidth().weight(1f)) {
                Text(
                    text = crashLog,
                    modifier = Modifier.padding(12.dp).verticalScroll(rememberScrollState()),
                    fontSize = 11.sp,
                    fontFamily = FontFamily.Monospace,
                    lineHeight = 16.sp
                )
            }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(onClick = {
                    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                    clipboard.setPrimaryClip(ClipData.newPlainText("crash_log", crashLog))
                    Toast.makeText(context, "已复制到剪贴板", Toast.LENGTH_SHORT).show()
                }) {
                    Text("复制日志")
                }
                OutlinedButton(onClick = {
                    // Delete crash log and restart normally
                    context.filesDir.listFiles()?.forEach { f ->
                        if (f.name == "crash_log.txt") f.delete()
                    }
                    // Force restart
                    val intent = (context as ComponentActivity).intent
                    context.finish()
                    context.startActivity(intent)
                }) {
                    Text("重新打开")
                }
            }
        }
    }
}
