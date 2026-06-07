# PulseVita (健康助手)

一款简洁实用的 Android 健康应用，融合现代科技与传统中医养生智慧。

## 功能特性

### 🏠 首页
- 今日健康概览（饮水、运动、心情环形图）
- 天气信息（自动定位）
- 连续打卡天数 + 打卡日历
- 周概览四宫格（各维度天数 + 趋势箭头）
- 健康趋势图（近 7 天饮水/运动/心情）
- 快速记录入口（喝水/运动/心情）
- 每日健康挑战（优化卡片设计）
- 每日健康贴士（翻转卡片）
- 二十四节气养生建议

### 🍎 饮食模块
- 饮水追踪（100/250/500ml 快速记录）
- 饮水趋势柱状图（近 7 天）
- 饮食记录（早/午/晚/加餐分类）
- 营养摄入概览（蛋白质/碳水/脂肪进度）
- 中医食疗建议

### 💪 运动模块
- 运动时长记录 + 数字动画
- 运动类型统计（步行/办公运动/瑜伽）
- 运动目标设定
- 办公室简易运动教程
- Health Connect 步数集成（可选）

### 🧠 心理健康模块
- 心情日记（1-5 级，emoji 图标）
- 心情统计卡片（平均分/最好最差/趋势）
- 心情趋势曲线图（近 7 天）
- 4-7-8 呼吸练习（带计时器 + 脉冲动画）
- 随机语录（Hitokoto API）

### 👤 个人档案
- 基本信息管理（姓名、性别、身高、体重、职业）
- BMI 自动计算与分类
- 主题切换（4 套莫兰迪配色方案）

## 主题系统

4 套低饱和度莫兰迪配色方案，支持运行时切换：

| 方案 | 风格 | 参考 |
|------|------|------|
| 💎 莫兰迪蓝绿 | 清新专业 | Apple Health |
| 🌅 莫兰迪暖杏 | 温暖治愈 | Headspace |
| 🌙 莫兰迪雾紫 | 宁静深邃 | Calm |
| 🌿 莫兰迪灰绿 | 自然平衡 | Samsung Health |

所有颜色通过 `PulseVitaTheme.currentScheme()` 统一管理，切换主题时全局色调自动协调。

## 技术栈

| 技术 | 版本 | 用途 |
|------|------|------|
| Kotlin | 1.9.22 | 主语言 |
| Jetpack Compose | BOM 2023.10.01 | UI 框架 |
| Material 3 | — | 设计系统 |
| Room | 2.6.1 | 本地数据库 |
| Hilt | 2.50 | 依赖注入 |
| Retrofit | 2.9.0 | 网络请求 |
| OkHttp | 4.12.0 | HTTP 客户端 |
| Moshi | 1.15.0 | JSON 解析 |
| Coroutines | 1.7.3 | 异步处理 |

## 架构

MVVM + Repository 模式：

```
UI (Compose) → ViewModel → Repository → Room DAO / Retrofit
```

详见 [docs/ARCHITECTURE.md](docs/ARCHITECTURE.md)

## 开发环境

- JDK 17
- Android SDK 34 (compileSdk/targetSdk)
- Min SDK 26 (Android 8.0)
- Gradle 8.5

## 构建

```bash
# 构建 Debug APK
./gradlew assembleDebug

# 运行单元测试
./gradlew testDebugUnitTest

# Clean 构建
./gradlew clean assembleDebug
```

详见 [docs/DEVELOPMENT.md](docs/DEVELOPMENT.md)

## 测试

40 个单元测试，100% 通过率：

| 测试类 | 用例数 | 覆盖范围 |
|--------|--------|----------|
| BmiUtilsTest | 11 | BMI 计算、分类、健康体重范围 |
| DateUtilsTest | 8 | 日期格式化、季节检测、问候语 |
| ConstantsTest | 8 | 常量验证 |
| SolarTermUtilTest | 3 | 二十四节气计算 |
| ThemeTest | 10 | 主题系统、配色方案验证 |

## 文档

- [更新日志](docs/CHANGELOG.md) — 版本历史
- [架构文档](docs/ARCHITECTURE.md) — 技术架构详解
- [开发指南](docs/DEVELOPMENT.md) — 开发环境、构建、调试
- [需求文档](REQUIREMENTS_v2.0.md) — v2.0 功能需求

## 已知问题

| 问题 | 状态 |
|------|------|
| Compose BOM 2024.01.00 兼容性 | ✅ 已修复 (降级到 2023.10.01) |
| Health Connect 权限请求 | ⚠️ 待处理 |
| 数据导出功能 | ⏳ 待开发 |
| 通知提醒 | ⏳ 待开发 |

## License

MIT License
