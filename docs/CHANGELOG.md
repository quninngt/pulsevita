# 更新日志 (CHANGELOG)

## v1.4.1 (2026-06-06) — 稳定版

### 修复
- **Critical**: 降级 Compose BOM 从 2024.01.00 到 2023.10.01，修复 `CircularProgressIndicator` 在 Android 12 上的 `NoSuchMethodError` 崩溃
- 替换 `AutoMirrored` 图标为标准图标（`KeyboardArrowRight`、`DirectionsWalk`），兼容旧版 Compose

### 改进
- 添加 CrashLogger：全局未捕获异常处理器，崩溃日志保存到应用私有目录
- 下次启动时自动显示崩溃日志，方便调试

---

## v1.4.0 (2026-06-06) — 功能恢复版

### 功能
- 恢复所有真实页面：首页、饮食、运动、心理、个人档案
- 5 个 ViewModel + 8 个 Repository + 6 个 Room Entity 完整接入

---

## v1.3.1 (2026-06-06) — 框架验证版

### 功能
- SimpleScreen 占位页面，验证 Hilt + Compose + Navigation 基础框架
- 确认基础框架在 Android 12 上正常工作

---

## v1.3.0 (2026-06-05) — 调试版

### 修复
- 移除 CrashCatcher（死代码，引用未声明的 CrashActivity）
- 简化 HealthApp 和 MainActivity

---

## v1.2.2 (2026-06-05) — 网络层修复

### 修复
- **P0-1**: 重写网络层，从 HttpURLConnection 迁移到 Retrofit + OkHttp + Moshi
- **P0-2**: ip-api.com 从 HTTP 迁移到 HTTPS
- **P0-3**: 添加 Room schema 导出和 fallbackToDestructiveMigration
- **P0-4**: HomeViewModel/MentalViewModel 添加 isLoading/errorMessage 状态

### 新增
- `NetworkResult<T>` sealed class 统一 API 响应处理
- `ApiModels.kt` — Moshi JSON 模型
- `ApiServices.kt` — Retrofit Service 接口
- `NetworkModule.kt` — Hilt 网络模块
- `WeatherRepository` — open-meteo.com 天气 + ip-api.com 定位
- `HitokotoRepository` — v1.hitokoto.cn 随机语录

---

## v1.2.1 (2026-06-05) — 代码质量改进

### 改进
- **P1-5**: HomeUiState 拆分为 5 个子状态（WeatherState, WaterState, ExerciseState, MoodState, DailyExtrasState）
- **P1-6**: 连续打卡天数修复 — DAO 加 `getAllDistinctDates()`，ViewModel 从今天往回数连续天数
- **P1-7**: 四个 Repository 全部从 `SimpleDateFormat` 迁移到 `java.time.LocalDate` + `DateTimeFormatter`

---

## v1.0.0 (2026-04-29) — 初始版本

### 功能
- 首页：健康概览、天气、打卡连续天数
- 饮食：饮水追踪（100/250/500ml）、饮食记录（早/午/晚/加餐）
- 运动：运动时长/步数记录、办公室运动教程
- 心理：心情日记（1-5 级）、4-7-8 呼吸练习
- 个人档案：BMI 计算、健康指标

### 技术栈
- Kotlin + Jetpack Compose + Material 3
- Room + Hilt + MVVM
- 30 个单元测试
