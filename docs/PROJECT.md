# PulseVita — 健康助手

Android 健康追踪应用，融合中医养生理念，追踪饮水、运动、情绪、饮食并提供节气养生建议。

## 技术栈

| 层 | 技术 |
|---|---|
| UI | Jetpack Compose + Material 3 |
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
│   │   ├── HomeScreen.kt        # 仪表盘 (天气/水/运动/情绪/每日额外)
│   │   ├── HomeViewModel.kt     # 组合 5 个子状态
│   │   └── HomeUiState.kt       # WeatherState 等 5 个子 data class
│   ├── diet/                    # 水追踪 + 饮食记录
│   ├── exercise/                # 运动追踪 + 办公操
│   ├── mental/                  # 情绪日记 + 呼吸练习
│   ├── profile/                 # 用户资料 + BMI
│   ├── components/              # 公共组件
│   ├── DisplayMappings.kt       # 中文标签映射
│   └── theme/                   # 橙/黑主题
└── util/
    ├── BmiUtils.kt              # BMI 计算
    ├── DateUtils.kt             # 日期工具 (java.time)
    ├── SolarTermUtil.kt         # 24 节气
    ├── HealthChallengeProvider.kt# 每日健康挑战
    ├── FoodKnowledgeProvider.kt # 食物知识
    ├── HealthConnectManager.kt  # Health Connect API
    └── Constants.kt             # 全局常量
```

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

- `NetworkModule` 提供 Retrofit 实例 (Moshi + OkHttp)
- 天气 API: open-meteo.com + ip-api.com (HTTPS)
- 一言 API: v1.hitokoto.cn

### 日期处理

全部使用 `java.time.LocalDate` + `DateTimeFormatter`，无 `SimpleDateFormat`：
- Repository: `LocalDate.now().format(DATE_FORMATTER)`
- DAO: `@Query` 接收 `String` 参数, `Flow` 返回
- ViewModel: streak 计算从 `getAllDistinctDates()` 回溯连续天数

### Room 数据库

- 版本 2, schema 导出到 `schemas/` 目录
- `fallbackToDestructiveMigration()` 作为安全网
- `Migrations.kt` 预留 `MIGRATION_1_2` 模板

## 测试

| 测试类 | 覆盖范围 |
|--------|---------|
| `BmiUtilsTest` | BMI 计算、分类、体重范围 |
| `DateUtilsTest` | 日期格式化、季节检测、问候语 |
| `ConstantsTest` | 常量值验证 |
| `SolarTermUtilTest` | 节气计算 |

Android instrumented 测试 (需模拟器): `DatabaseTest`, `HomeScreenTest`, `NavigationTest`

## 改进记录

### P0 (已完成)
- [x] 安全: ip-api.com HTTP → HTTPS
- [x] 数据库: schema 导出 + fallback 迁移
- [x] 架构: Retrofit + OkHttp + Moshi 重写网络层
- [x] UX: ViewModel 加载/错误状态 + UI 反馈

### P1 (已完成)
- [x] HomeUiState 拆分为 5 个子状态 (Weather/Water/Exercise/Mood/DailyExtras)
- [x] 连续打卡天数修复: 从查单日记录改为 getAllDistinctDates() 回溯
- [x] SimpleDateFormat → java.time (4 个 Repository)

### P1 (待做)
- [ ] ViewModel/Repository 单元测试 (需 MockK)
- [ ] WorkManager 喝水/运动提醒通知

### P2 (待做)
- [ ] MPAndroidChart 图表展示
- [ ] Room 数据导出 (CSV)
- [ ] 睡眠追踪模块
- [ ] 目标自定义 (饮水/运动)
- [ ] App Widget 桌面小组件
- [ ] 深色模式
- [ ] 无障碍优化
- [ ] 个人中心头像

## 依赖版本

| 库 | 版本 |
|----|------|
| Kotlin | 1.9.22 |
| Compose | 1.5.8 (compiler) |
| Material 3 | BOM |
| Room | 2.6.1 |
| Hilt | 2.50 |
| Retrofit | 2.9.0 |
| OkHttp | 4.12.0 |
| Moshi | 1.15.0 |
| Health Connect | 1.1.0-alpha |
