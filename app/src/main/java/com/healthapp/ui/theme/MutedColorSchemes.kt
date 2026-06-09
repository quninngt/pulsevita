package com.healthapp.ui.theme

import androidx.compose.ui.graphics.Color

/**
 * 莫兰迪配色方案定义
 * 低饱和度、高级感、适合长期使用
 */
enum class MutedColorScheme(
    val displayName: String,
    val description: String,
    val icon: String,
    // 主色调
    val primary: Color,
    val primaryDark: Color,
    val primaryLight: Color,
    val primaryContainer: Color,
    // 背景色
    val background: Color,
    val surface: Color,
    val surfaceVariant: Color,
    // 文字色
    val textPrimary: Color,
    val textSecondary: Color,
    // 功能色
    val success: Color,
    val warning: Color,
    val error: Color,
    // 数据可视化色
    val chartBlue: Color,
    val chartGreen: Color,
    val chartOrange: Color,
    val chartPurple: Color,
    val chartTeal: Color,
    val chartRed: Color,
    val chartCyan: Color,
    val chartYellow: Color,
    // 心情色（5级）
    val mood1: Color, // 很差
    val mood2: Color, // 较差
    val mood3: Color, // 一般
    val mood4: Color, // 较好
    val mood5: Color  // 很好
) {
    /**
     * 方案1：莫兰迪蓝绿
     * 参考 Apple Health，清新专业
     */
    MORANDI_BLUE_GREEN(
        displayName = "莫兰迪蓝绿",
        description = "清新专业 · 参考Apple Health",
        icon = "💎",
        // 主色调
        primary = Color(0xFF7AADAD),
        primaryDark = Color(0xFF5A8A8A),
        primaryLight = Color(0xFF9CC5C5),
        primaryContainer = Color(0xFFD4E8E8),
        // 背景色
        background = Color(0xFFF5F9F9),
        surface = Color(0xFFFFFFFF),
        surfaceVariant = Color(0xFFE8F0F0),
        // 文字色
        textPrimary = Color(0xFF2C3E3E),
        textSecondary = Color(0xFF6B8080),
        // 功能色
        success = Color(0xFF7AADAD),
        warning = Color(0xFFC4A87A),
        error = Color(0xFFC47A7A),
        // 数据可视化色
        chartBlue = Color(0xFF7AADAD),
        chartGreen = Color(0xFF8AB88A),
        chartOrange = Color(0xFFC4A87A),
        chartPurple = Color(0xFF9A8AAD),
        chartTeal = Color(0xFF7AB0B0),
        chartRed = Color(0xFFC48888),
        chartCyan = Color(0xFF88C4C4),
        chartYellow = Color(0xFFC4C088),
        // 心情色
        mood1 = Color(0xFFD4A0A0),
        mood2 = Color(0xFFD4B8A0),
        mood3 = Color(0xFFD4C8A0),
        mood4 = Color(0xFFA0C8A0),
        mood5 = Color(0xFFA0C8D4)
    ),

    /**
     * 方案2：莫兰迪暖杏
     * 参考 Headspace，温暖治愈
     */
    MORANDI_WARM_APRICOT(
        displayName = "莫兰迪暖杏",
        description = "温暖治愈 · 参考Headspace",
        icon = "🌅",
        // 主色调
        primary = Color(0xFFC4A088),
        primaryDark = Color(0xFFA07860),
        primaryLight = Color(0xFFD8BCA8),
        primaryContainer = Color(0xFFF0E0D0),
        // 背景色
        background = Color(0xFFFAF6F2),
        surface = Color(0xFFFFFFFF),
        surfaceVariant = Color(0xFFF5EDE6),
        // 文字色
        textPrimary = Color(0xFF3C2E28),
        textSecondary = Color(0xFF8C7060),
        // 功能色
        success = Color(0xFF8AB88A),
        warning = Color(0xFFC4A87A),
        error = Color(0xFFC47A7A),
        // 数据可视化色
        chartBlue = Color(0xFF8AAAC4),
        chartGreen = Color(0xFF8AB88A),
        chartOrange = Color(0xFFC4A088),
        chartPurple = Color(0xFFAA88A8),
        chartTeal = Color(0xFF88B0A8),
        chartRed = Color(0xFFC49088),
        chartCyan = Color(0xFF88B8C4),
        chartYellow = Color(0xFFC4B888),
        // 心情色
        mood1 = Color(0xFFD4A8A0),
        mood2 = Color(0xFFD4BCA8),
        mood3 = Color(0xFFD4C8A8),
        mood4 = Color(0xFFA8C8A8),
        mood5 = Color(0xFFA0C4C4)
    ),

    /**
     * 方案3：莫兰迪雾紫
     * 参考 Calm，宁静深邃
     */
    MORANDI_MIST_PURPLE(
        displayName = "莫兰迪雾紫",
        description = "宁静深邃 · 参考Calm",
        icon = "🌙",
        // 主色调
        primary = Color(0xFFA898C0),
        primaryDark = Color(0xFF8578A0),
        primaryLight = Color(0xFFC4B8D8),
        primaryContainer = Color(0xFFE0D8F0),
        // 背景色
        background = Color(0xFFF8F5FA),
        surface = Color(0xFFFFFFFF),
        surfaceVariant = Color(0xFFEDE8F5),
        // 文字色
        textPrimary = Color(0xFF2E2838),
        textSecondary = Color(0xFF786888),
        // 功能色
        success = Color(0xFF88B0A0),
        warning = Color(0xFFC4A87A),
        error = Color(0xFFC47A7A),
        // 数据可视化色
        chartBlue = Color(0xFF88A0C4),
        chartGreen = Color(0xFF88B0A0),
        chartOrange = Color(0xFFC4A888),
        chartPurple = Color(0xFFA898C0),
        chartTeal = Color(0xFF88B0B0),
        chartRed = Color(0xFFC49098),
        chartCyan = Color(0xFF88B8C0),
        chartYellow = Color(0xFFC4B898),
        // 心情色
        mood1 = Color(0xFFD0A8B8),
        mood2 = Color(0xFFD0BCA8),
        mood3 = Color(0xFFCCC8A8),
        mood4 = Color(0xFFA8C8B8),
        mood5 = Color(0xFFA0C0D0)
    ),

    /**
     * 方案4：莫兰迪灰绿
     * 参考 Samsung Health，自然平衡
     */
    MORANDI_SAGE_GREEN(
        displayName = "莫兰迪灰绿",
        description = "自然平衡 · 参考Samsung Health",
        icon = "🌿",
        // 主色调
        primary = Color(0xFF8AA88A),
        primaryDark = Color(0xFF6A8A6A),
        primaryLight = Color(0xFFAAC8AA),
        primaryContainer = Color(0xFFD8ECD8),
        // 背景色
        background = Color(0xFFF5FAF5),
        surface = Color(0xFFFFFFFF),
        surfaceVariant = Color(0xFFE8F0E8),
        // 文字色
        textPrimary = Color(0xFF283028),
        textSecondary = Color(0xFF687868),
        // 功能色
        success = Color(0xFF8AA88A),
        warning = Color(0xFFC4B07A),
        error = Color(0xFFC47A7A),
        // 数据可视化色
        chartBlue = Color(0xFF88A8C4),
        chartGreen = Color(0xFF8AA88A),
        chartOrange = Color(0xFFC4A888),
        chartPurple = Color(0xFFA090B0),
        chartTeal = Color(0xFF88A8A0),
        chartRed = Color(0xFFC49088),
        chartCyan = Color(0xFF88B8A8),
        chartYellow = Color(0xFFC4B888),
        // 心情色
        mood1 = Color(0xFFD0A8A0),
        mood2 = Color(0xFFD0B8A0),
        mood3 = Color(0xFFCCC8A0),
        mood4 = Color(0xFFA0C8A8),
        mood5 = Color(0xFFA0C4C0)
    );

    companion object {
        /**
         * 获取默认方案
        */
        val default = MORANDI_BLUE_GREEN

        /**
         * 根据名称获取方案
         */
        fun fromName(name: String): MutedColorScheme {
            return entries.find { it.name == name } ?: default
        }
    }
}