# 架构文档

## 整体架构

```
┌─────────────────────────────────────────────────────┐
│                    UI Layer                          │
│  ┌──────────┐ ┌──────────┐ ┌──────────┐ ┌────────┐ │
│  │HomeScreen│ │DietScreen│ │ExScreen  │ │Mental  │ │
│  └────┬─────┘ └────┬─────┘ └────┬─────┘ └───┬────┘ │
│       │             │            │           │      │
│  ┌────▼─────┐ ┌────▼─────┐ ┌───▼──────┐ ┌──▼────┐ │
│  │HomeVM    │ │DietVM    │ │ExVM      │ │MentalVM│ │
│  └────┬─────┘ └────┬─────┘ └───┬──────┘ └──┬────┘ │
├───────┼─────────────┼───────────┼────────────┼──────┤
│       │      Data Layer         │            │      │
│  ┌────▼─────────────▼───────────▼────────────▼────┐ │
│  │              Repositories                       │ │
│  │  WaterRepo  ExerciseRepo  MoodRepo  DietRepo   │ │
│  │  UserRepo   HealthTipRepo WeatherRepo Hitokoto │ │
│  └────┬─────────────┬───────────┬────────────┬────┘ │
│       │             │           │            │      │
│  ┌────▼─────┐ ┌────▼─────┐ ┌──▼───┐ ┌──────▼────┐ │
│  │Room DAO  │ │AppDatabase│ │Retrofit│ │OkHttp   │ │
│  │6个DAO    │ │6个Entity  │ │4个Service│ │Moshi   │ │
│  └──────────┘ └──────────┘ └──────┘ └──────────┘ │
└─────────────────────────────────────────────────────┘
```

## 依赖注入 (Hilt)

### Modules

| Module | 职责 | 提供 |
|--------|------|------|
| `AppModule` | 数据库层 | AppDatabase、6 个 DAO |
| `NetworkModule` | 网络层 | Moshi、OkHttpClient、4 个 Retrofit、4 个 Service |

### 依赖链路

```
HomeViewModel
├── WaterRepository → WaterRecordDao → AppDatabase
├── ExerciseRepository → ExerciseRecordDao → AppDatabase
├── MoodRepository → MoodRecordDao → AppDatabase
├── HealthTipRepository → HealthTipDao → AppDatabase
├── UserRepository → UserDao → AppDatabase
├── WeatherRepository → WeatherService → Retrofit("weather")
│                    → IpApiService → Retrofit("ipapi")
│                    → IpFallbackService → Retrofit("ipfallback")
└── HitokotoRepository → HitokotoService → Retrofit("hitokoto")
```

## 数据层

### Room 数据库

| Entity | 用途 | 主键 |
|--------|------|------|
| `UserEntity` | 用户信息 | id (自增) |
| `WaterRecord` | 饮水记录 | id (自增) |
| `ExerciseRecord` | 运动记录 | id (自增) |
| `MoodRecord` | 心情记录 | id (自增) |
| `DietRecord` | 饮食记录 | id (自增) |
| `HealthTip` | 健康贴士 | id (自增) |

- 数据库版本：1
- Schema 导出：`app/schemas/`
- 迁移策略：`fallbackToDestructiveMigration()`
- 初始化：`DatabaseCallback` 预填充 10 条健康贴士

### 网络层

| API | 用途 | URL |
|-----|------|-----|
| Open-Meteo | 天气查询 | `https://api.open-meteo.com/v1/forecast` |
| ip-api.com | IP 定位 | `https://ip-api.com/json/` |
| ip.useragentinfo.com | IP 定位备用 | `https://ip.useragentinfo.com/json` |
| Hitokoto | 随机语录 | `https://v1.hitokoto.cn/` |

错误处理：`NetworkResult<T>` sealed class (Success/Error/Loading)

## UI 层

### 状态管理

每个 ViewModel 使用 `MutableStateFlow<*UiState>` 模式：

```kotlin
private val _uiState = MutableStateFlow(HomeUiState())
val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()
```

Compose 中收集：`val uiState by viewModel.uiState.collectAsState()`

### HomeUiState 子状态

```kotlin
data class HomeUiState(
    val weather: WeatherState,      // 天气信息
    val water: WaterState,          // 今日饮水
    val exercise: ExerciseState,    // 今日运动
    val mood: MoodState,            // 今日心情
    val dailyExtras: DailyExtrasState, // 每日额外内容
    val greeting: String,           // 问候语
    val tip: String,                // 健康贴士
    val streak: Int                 // 连续打卡天数
)
```

### 导航

- 4 个底部标签：首页、饮食、运动、心理
- 个人档案从首页顶部进入
- 使用 `saveState`/`restoreState` 保持标签页状态

## 工具类

| 工具 | 功能 |
|------|------|
| `BmiUtils` | BMI 计算、分类、健康体重范围 |
| `DateUtils` | 日期格式化、季节检测、问候语、年龄计算 |
| `SolarTermUtil` | 24 节气计算 + 健康建议 |
| `HealthChallengeProvider` | 每日健康挑战 |
| `FoodKnowledgeProvider` | 食物营养知识 |
| `Constants` | 应用常量 |
| `CrashLogger` | 崩溃日志捕获 |
| `HealthConnectManager` | Health Connect 步数读取 |
