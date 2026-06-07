# 更新日志 (CHANGELOG)

## v2.0.1 (2026-06-07) — UI 颜色调优

### 改进
- **全局颜色迁移**: 24 处硬编码高饱和色 → 莫兰迪低饱和主题色
  - WeeklyOverviewCard: WaterBlue/BrandGreen/GreenHealthy → scheme.chartBlue/chartGreen/success
  - MoodStatisticsCard: Apple 风格趋势色 → scheme.success/warning/error
  - NutritionOverviewCard: Material 标准色 → scheme.chartGreen/chartBlue/chartOrange
  - ExerciseTypeStatsCard: Keep 绿/Material 蓝紫 → scheme.chartGreen/chartBlue/chartPurple
  - HomeScreen: 环形图 + 快捷操作 + 食物属性色全部迁移至 MutedColorScheme
  - DietScreen: WaterBlue → scheme.chartBlue
  - WaterHistoryChart: 柱状图 + 目标线 → scheme.chartBlue/textSecondary
  - MentalScreen: 呼吸练习 Color.White → MaterialTheme.colorScheme.onPrimary/onSecondary/onTertiary
- **今日挑战卡片重设计**: 圆角 20dp、图标容器、双层标题、描述独立卡片、背景装饰圆、"开始挑战"引导
- **清理 Color.kt**: 删除所有未引用的旧 Apple 风格颜色常量（BrandGreen/WaterBlue/GreenHealthy 等 20+ 个）

### 技术
- 所有功能色通过 `PulseVitaTheme.currentScheme()` 获取，切换 4 套莫兰迪主题时全局色调自动统一
- WaterHistoryChart Canvas 内颜色提取到外部变量，避免 @Composable 上下文问题

---

## v2.0.0 (2026-06-07) — UI 全面重设计

### 新功能
- **主题切换系统**: 4 套莫兰迪配色方案（蓝绿/暖杏/雾紫/灰绿），DataStore 持久化
  - `MutedColorScheme` 枚举定义完整配色（主色/背景/文字/功能色/图表色/心情色）
  - `ThemeManager` + `ThemeViewModel` 管理主题状态
  - ProfileScreen 主题选择器入口
- **首页重设计**: 仪表盘环形图、周概览四宫格、打卡日历、健康趋势图、快速操作
- **饮食页优化**: 营养摄入概览卡片、饮水趋势图表
- **运动页优化**: 运动类型统计卡片、运动目标设定对话框
- **心理页优化**: 心情统计卡片

### 新增组件
- `WeeklyOverviewCard` — 周概览四宫格（饮水/运动/心情/饮食 + 趋势箭头）
- `NutritionOverviewCard` — 蛋白质/碳水/脂肪摄入进度
- `WaterHistoryChart` — 近 7 天饮水柱状图
- `ExerciseTypeStatsCard` — 步行/办公运动/瑜伽统计
- `ExerciseGoalDialog` — 运动目标设定
- `MoodStatisticsCard` — 本周心情统计（平均分/最好最差/趋势）

### 文档
- 新增 `REQUIREMENTS_v2.0.md` — 24 个功能需求文档

---

## v1.7.0 (2026-06-06) — 各页面增强

### 新功能
- **运动页面趋势图**: 本周运动时长柱状图
- **运动页面数字动画**: 运动时长和步数数字渐变动画
- **心理页面趋势图**: 最近 7 天心情趋势曲线图
- **呼吸练习脉冲动画**: 呼吸图标和练习圆圈添加脉冲动画效果

### 改进
- ExerciseViewModel 新增 weeklyMinutes 7 天数据
- MentalViewModel 新增 weeklyMoodLevels 7 天数据

---

## v1.6.0 (2026-06-06) — 功能界面增强

### 新功能
- **打卡日历**: 首页集成 kizitonwose/Calendar，显示当月打卡记录
- **健康趋势图**: 首页新增饮水/运动/心情三个趋势图表
- **健康贴士翻转卡片**: 点击翻转查看详细内容
- **脉冲动画**: 饮水页面推荐按钮脉冲呼吸动画
- **数字渐变动画**: 饮水量数字变化时平滑过渡

---

## v1.4.1 (2026-06-06) — 稳定版

### 修复
- **Critical**: 降级 Compose BOM 从 2024.01.00 到 2023.10.01，修复 Android 12 崩溃
- 替换 `AutoMirrored` 图标为标准图标

---

## v1.4.0 (2026-06-06) — 功能恢复版

### 功能
- 恢复所有真实页面：首页、饮食、运动、心理、个人档案
- 5 个 ViewModel + 8 个 Repository + 6 个 Room Entity 完整接入

---

## v1.2.2 (2026-06-05) — 网络层修复

### 修复
- 重写网络层: HttpURLConnection → Retrofit + OkHttp + Moshi
- ip-api.com HTTP → HTTPS
- Room schema 导出 + fallbackToDestructiveMigration

---

## v1.0.0 (2026-04-29) — 初始版本

### 功能
- 首页：健康概览、天气、打卡连续天数
- 饮食：饮水追踪、饮食记录
- 运动：运动时长/步数记录、办公室运动教程
- 心理：心情日记、4-7-8 呼吸练习
- 个人档案：BMI 计算、健康指标
