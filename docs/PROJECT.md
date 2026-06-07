# PulseVita — 健康助手

Android 健康追踪应用，融合中医养生理念，追踪饮水、运动、情绪、饮食并提供节气养生建议。

## 技术栈

| 层 | 技术 |
|---|---|
| UI | Jetpack Compose + Material 3 + 莫兰迪主题系统 |
| 架构 | MVVM + Repository |
| DI | Hilt (KSP) |
| 数据库 | Room (v2, 带 schema 导出 + 迁移) |
| 网络 | Retrofit 2.9 + OkHttp 4.12 + Moshi 1.15 |
| 异步 | Kotlin Coroutines + Flow |
| 健康 | Health Connect API (可选) |

## 模块结构

```
app/src/main/java/com/healthapp/
├── HealthApp.kt                 # Application (Hilt)
├── MainActivity.kt              # 单 Activity
├── data/
│   ├── local/
│   │   ├── AppDatabase.kt       # Room DB, v2, fallback destructive
│   │   ├── Migrations.kt        # 迁移模板 (MIGRATION_1_2)
│   │   ├── dao/                 # 6 个 DAO
│   │   └── entity/              # 6 个 Entity
│   ├── remote/
│   │   ├── NetworkResult.kt     # 成功/加载/错误 sealed class
│   │   ├── ApiModels.kt         # 天气 + Hitokoto JSON 模型
│   │   ├── ApiServices.kt       # Retrofit service 接口
│   │   ├── WeatherRepository.kt # 天气查询 (open-meteo + ip-api)
│   │   └── HitokotoRepository.kt# 一言 API
│   └── repository/              # 本地数据 Repository (5 个)
├── di/
│   ├── AppModule.kt             # DAO/Database 提供
│   └── NetworkModule.kt         # Retrofit/OkHttp/Moshi 提供
├── navigation/
│   ├── NavGraph.kt              # 底部导航 4 tab + 子路由
│   └── Screen.kt                # sealed class 路由定义
├── ui/
│   ├── home/
│   │   ├── HomeScreen.kt        # 仪表盘 (环形图/周概览/日历/趋势/挑战/贴士)
│   │   ├── HomeViewModel.kt     # 组合 5 个子状态
│   │   └── HomeUiState.kt       # WeatherState 等 5 个子 data class
│   ├── diet/                    # 水追踪 + 饮食记录 + 营养概览
│   ├── exercise/                # 运动追踪 + 办公操 + 类型统计
│   ├── mental/                  # 情绪日记 + 呼吸练习 + 心情统计
│   ├── profile/                 # 用户资料 + BMI + 主题切换
│   ├── components/              # 公共组件
│   │   ├── WeeklyOverviewCard.kt    # 周概览四宫格
│   │   ├── NutritionOverviewCard.kt # 营养摄入进度
│   │   ├── WaterHistoryChart.kt     # 饮水柱状图
│   │   ├── ExerciseTypeStatsCard.kt # 运动类型统计
│   │   ├── ExerciseGoalDialog.kt    # 运动目标设定
│   │   ├── MoodStatisticsCard.kt    # 心情统计
│   │   ├── StreakCalendar.kt        # 打卡日历
│   │   ├── HealthTipFlipCard.kt     # 翻转卡片
│   │   ├── QuickActionCard.kt       # 快速操作
│   │   ├── AchievementComponents.kt # 成就组件
│   │   └── ...
│   ├── DisplayMappings.kt       # 中文标签映射
│   └── theme/
│       ├── MutedColorSchemes.kt # 4 套莫兰迪配色方案
│       ├── ThemeManager.kt      # DataStore 主题持久化
│       ├── ThemeViewModel.kt    # 主题状态管理
│       └── Theme.kt             # Material 3 主题桥接
└── util/
    ├── BmiUtils.kt              # BMI 计算
    ├── DateUtils.kt             # 日期工具 (java.time)
    ├── SolarTermUtil.kt         # 24 节气
    ├── HealthChallengeProvider.kt# 每日健康挑战
    ├── FoodKnowledgeProvider.kt # 食物知识
    ├── HealthConnectManager.kt  # Health Connect API
    └── Constants.kt             # 全局常量
```

## 主题系统

4 套莫兰迪低饱和度配色方案，通过 `MutedColorScheme` 枚举定义：

```kotlin
enum class MutedColorScheme(
    val primary: Color,        // 主色调
    val primaryContainer: Color, // 容器背景
    val success: Color,        // 成功/正向
    val warning: Color,        // 警告
    val error: Color,          // 错误
    val chartBlue: Color,      // 图表蓝
    val chartGreen: Color,     // 图表绿
    val chartOrange: Color,    // 图表橙
    val chartPurple: Color,    // 图表紫
    val mood1~5: Color,        // 心情 5 级
    // ... 更多字段
)
```

所有 UI 组件通过 `PulseVitaTheme.currentScheme()` 获取当前方案颜色，确保主题切换时全局一致。

## 构建 & 测试

```bash
# 环境要求
# JDK 17, Android SDK 34, Gradle 8.5, AGP 8.2.2

# 构建 Debug APK
./gradlew assembleDebug

# 运行全部单元测试
./gradlew testDebugUnitTest

# 运行指定测试
./gradlew testDebugUnitTest --tests "com.healthapp.util.BmiUtilsTest"

# 测试报告
# → app/build/reports/tests/testDebugUnitTest/index.html
```

## 架构设计

### 状态管理

ViewModel 内部组合多个细粒度子状态，通过 `.map`/`.combine` 派生：

```kotlin
data class HomeUiState(
    val weather: WeatherState = WeatherState(),
    val water: WaterState = WaterState(),
    val exercise: ExerciseState = ExerciseState(),
    val mood: MoodState = MoodState(),
    val dailyExtras: DailyExtrasState = DailyExtrasState(),
    val greeting: String = "",
    val tip: String = "",
    val streak: Int = 0
)
```

每个子状态独立加载、独立错误处理，UI 局部刷新。

### 网络层

```
ApiServices (Retrofit) → NetworkResult<T> → WeatherRepository / HitokotoRepository
                          成功/加载/错误        ↓
                                          ViewModel (isLoading/errorMessage)
                                          ↓
                                       HomeScreen (指示器 + Snackbar)
```

### 日期处理

全部使用 `java.time.LocalDate` + `DateTimeFormatter`，无 `SimpleDateFormat`。

### Room 数据库

- 版本 2, schema 导出到 `schemas/` 目录
- `fallbackToDestructiveMigration()` 作为安全网

## 测试

| 测试类 | 用例数 | 覆盖范围 |
|--------|--------|----------|
| `BmiUtilsTest` | 11 | BMI 计算、分类、体重范围 |
| `DateUtilsTest` | 8 | 日期格式化、季节检测、问候语 |
| `ConstantsTest` | 8 | 常量值验证 |
| `SolarTermUtilTest` | 3 | 节气计算 |
| `ThemeTest` | 10 | 主题系统、配色方案验证 |

**总计: 40 个单元测试，100% 通过率**

## 依赖版本

| 库 | 版本 |
|----|------|
| Kotlin | 1.9.22 |
| Compose | 1.5.8 (compiler) |
| Material 3 | BOM 2023.10.01 |
| Room | 2.6.1 |
| Hilt | 2.50 |
| Retrofit | 2.9.0 |
| OkHttp | 4.12.0 |
| Moshi | 1.15.0 |
| Health Connect | 1.1.0-alpha |
