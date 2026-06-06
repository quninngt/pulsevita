# 开发指南

## 环境要求

| 工具 | 版本 | 说明 |
|------|------|------|
| JDK | 17 | OpenJDK 或 Oracle JDK |
| Android SDK | 34 | compileSdk / targetSdk |
| Kotlin | 1.9.22 | 编译器版本 |
| Compose Compiler | 1.5.8 | 与 Kotlin 版本匹配 |
| Gradle | 8.5 | Wrapper 自带 |
| AGP | 8.2.2 | Android Gradle Plugin |

## 构建命令

```bash
# 构建 Debug APK
./gradlew assembleDebug

# 构建 Release APK
./gradlew assembleRelease

# 运行单元测试
./gradlew testDebugUnitTest

# 运行特定测试
./gradlew testDebugUnitTest --tests "com.healthapp.util.BmiUtilsTest"

# Clean 构建
./gradlew clean assembleDebug

# 查看测试报告
open app/build/reports/tests/testDebugUnitTest/index.html
```

## 项目结构

```
app/src/main/java/com/healthapp/
├── HealthApp.kt              # Application (Hilt 入口)
├── MainActivity.kt           # Activity (Compose 入口)
├── navigation/
│   ├── NavGraph.kt           # 导航图
│   └── Screen.kt             # 路由定义
├── data/
│   ├── local/
│   │   ├── AppDatabase.kt    # Room 数据库
│   │   ├── Migrations.kt     # 数据库迁移
│   │   ├── dao/              # 6 个 DAO
│   │   └── entity/           # 6 个 Entity
│   ├── remote/
│   │   ├── ApiModels.kt      # Moshi 模型
│   │   ├── ApiServices.kt    # Retrofit 接口
│   │   ├── NetworkResult.kt  # 网络结果封装
│   │   ├── WeatherRepository.kt
│   │   └── HitokotoRepository.kt
│   └── repository/           # 6 个 Repository
├── di/
│   ├── AppModule.kt          # 数据库 DI
│   └── NetworkModule.kt      # 网络 DI
├── ui/
│   ├── theme/                # 主题 (Color, Theme, Type)
│   ├── components/           # 共享组件
│   ├── DisplayMappings.kt    # 中文标签映射
│   ├── home/                 # 首页 (Screen + ViewModel)
│   ├── diet/                 # 饮食
│   ├── exercise/             # 运动
│   ├── mental/               # 心理
│   └── profile/              # 个人档案
└── util/                     # 工具类
```

## 依赖说明

### Compose BOM 版本

**重要**：使用 `2023.10.01`，不要升级到 `2024.01.00`！

原因：`2024.01.00` 的 `CircularProgressIndicator` 使用了 `KeyframesSpec.at()` 方法，
该方法在 Android 12 设备上不存在，会导致 `NoSuchMethodError` 崩溃。

```kotlin
// ✅ 正确
implementation(platform("androidx.compose:compose-bom:2023.10.01"))

// ❌ 错误 - 会导致崩溃
implementation(platform("androidx.compose:compose-bom:2024.01.00"))
```

### 图标兼容性

`AutoMirrored` 图标（如 `Icons.AutoMirrored.Filled.DirectionsWalk`）在 BOM 2023.10.01 中不存在。
使用标准图标替代：

```kotlin
// ✅ 正确
Icons.Default.DirectionsWalk
Icons.Default.KeyboardArrowRight

// ❌ 错误 - 编译失败
Icons.AutoMirrored.Filled.DirectionsWalk
Icons.AutoMirrored.Filled.KeyboardArrowRight
```

## 调试

### CrashLogger

应用内置崩溃日志捕获。崩溃时：
1. 日志保存到 `context.filesDir/crash_log.txt`
2. 下次启动自动显示日志
3. 用户可以复制日志发给开发者

### 日志查看

```bash
# 查看应用日志
adb logcat -s "com.healthapp.v2"

# 查看崩溃日志
adb shell cat /data/data/com.healthapp.v2/files/crash_log.txt
```

## 测试

### 单元测试

```bash
# 运行所有测试
./gradlew testDebugUnitTest

# 测试报告位置
app/build/reports/tests/testDebugUnitTest/index.html
```

当前覆盖：
- `BmiUtilsTest` — BMI 计算 (11 个用例)
- `DateUtilsTest` — 日期工具 (8 个用例)
- `ConstantsTest` — 常量验证 (8 个用例)
- `SolarTermUtilTest` — 节气计算

### Android 测试

需要设备或模拟器：
```bash
./gradlew connectedDebugAndroidTest
```

## 发版流程

1. 更新 `app/build.gradle.kts` 中的 `versionCode` 和 `versionName`
2. 构建：`./gradlew clean assembleDebug`
3. 运行测试：`./gradlew testDebugUnitTest`
4. APK 位置：`app/build/outputs/apk/debug/PulseVita_<version>.apk`

## 已知问题

| 问题 | 状态 | 说明 |
|------|------|------|
| Compose BOM 2024.01.00 崩溃 | ✅ 已修复 | 降级到 2023.10.01 |
| AutoMirrored 图标不兼容 | ✅ 已修复 | 使用标准图标 |
| Health Connect 权限 | ⚠️ 待处理 | 需要运行时权限请求 |
| 数据导出功能 | ⏳ 待开发 | P2 优先级 |
| 通知提醒 | ⏳ 待开发 | P2 优先级 |
