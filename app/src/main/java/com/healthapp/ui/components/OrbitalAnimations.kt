package com.healthapp.ui.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * 页面切换动画包装器
 * 为页面切换提供流畅的过渡动画
 */
@Composable
fun AnimatedPageTransition(
    visible: Boolean,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    AnimatedVisibility(
        visible = visible,
        enter = fadeIn() + slideInVertically(
            initialOffsetY = { it / 20 },
            animationSpec = tween(300, easing = FastOutSlowInEasing)
        ),
        exit = fadeOut() + slideOutVertically(
            targetOffsetY = { -it / 20 },
            animationSpec = tween(200, easing = FastOutLinearInEasing)
        ),
        modifier = modifier
    ) {
        content()
    }
}

/**
 * 卡片翻转动画
 * 用于展示健康贴士的翻转效果
 */
@Composable
fun FlipCard(
    front: @Composable () -> Unit,
    back: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    flipped: Boolean = false
) {
    val rotation by animateFloatAsState(
        targetValue = if (flipped) 180f else 0f,
        animationSpec = tween(500, easing = FastOutSlowInEasing),
        label = "flip"
    )

    Box(
        modifier = modifier
            .graphicsLayer {
                rotationY = rotation
                cameraDistance = 12f * density
            }
    ) {
        if (rotation <= 90f) {
            front()
        } else {
            Box(
                modifier = Modifier.graphicsLayer {
                    rotationY = 180f
                }
            ) {
                back()
            }
        }
    }
}

/**
 * 脉冲动画
 * 用于饮水按钮等需要强调的元素
 */
@Composable
fun PulseAnimation(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.1f,
        animationSpec = infiniteRepeatable(
            animation = tween(600, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_scale"
    )

    Box(
        modifier = modifier.graphicsLayer {
            scaleX = scale
            scaleY = scale
        }
    ) {
        content()
    }
}

/**
 * 渐变数字动画
 * 用于数字变化时的平滑过渡
 */
@Composable
fun AnimatedNumber(
    targetValue: Int,
    modifier: Modifier = Modifier,
    prefix: String = "",
    suffix: String = ""
) {
    var currentValue by remember { mutableIntStateOf(0) }

    LaunchedEffect(targetValue) {
        animate(
            initialValue = currentValue.toFloat(),
            targetValue = targetValue.toFloat(),
            animationSpec = tween(500, easing = FastOutSlowInEasing)
        ) { value, _ ->
            currentValue = value.toInt()
        }
    }

    Text(
        text = "$prefix$currentValue$suffix",
        modifier = modifier
    )
}

/**
 * 健康贴士翻转卡片
 */
@Composable
fun HealthTipFlipCard(
    tipTitle: String,
    tipContent: String,
    modifier: Modifier = Modifier
) {
    var flipped by remember { mutableStateOf(false) }

    FlipCard(
        front = {
            Card(
                modifier = modifier
                    .fillMaxWidth()
                    .height(120.dp)
                    .clickable { flipped = true }
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "💡",
                            fontSize = 32.sp
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = tipTitle,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                        Text(
                            text = "点击查看详情",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        },
        back = {
            Card(
                modifier = modifier
                    .fillMaxWidth()
                    .height(120.dp)
                    .clickable { flipped = false }
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = tipContent,
                        fontSize = 14.sp,
                        lineHeight = 20.sp
                    )
                }
            }
        },
        flipped = flipped
    )
}
