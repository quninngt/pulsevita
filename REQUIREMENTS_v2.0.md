# PulseVita v2.0 需求文档
## 首页 & 三大功能页面全面优化

**版本**: 2.0.0  
**日期**: 2026-06-07  
**作者**: Hermes AI Assistant  
**状态**: 待确认

---

## 一、项目概述

### 1.1 目标
对 PulseVita 的4个核心页面进行全面优化，提升布局美观度和功能实用性，打造专业级健康追踪应用体验。

### 1.2 设计原则
1. **简洁优先**：减少视觉噪音，突出核心信息
2. **数据驱动**：用数据指导用户行为
3. **正向激励**：成就系统、进度可视化
4. **一致性**：统一的视觉语言和交互模式

### 1.3 涉及页面
- 首页（HomeScreen）
- 饮食页面（DietScreen）
- 运动页面（ExerciseScreen）
- 心理页面（MentalScreen）

---

## 二、首页重新设计（HomeScreen）

### 2.1 功能需求

#### FR-HOME-01: 顶部问候区
- **描述**: 显示个性化问候语、用户名、连续打卡天数
- **优先级**: P0
- **验收标准**:
  - 根据时间段显示不同问候语（早上好/下午好/晚上好）
  - 显示用户姓名
  - 连续打卡天数徽章（🔥 图标 + 天数）
  - 点击头像跳转个人档案

#### FR-HOME-02: 环境信息条
- **描述**: 一行显示天气和节气信息
- **优先级**: P0
- **验收标准**:
  - 左侧：天气图标 + 温度 + 天气描述
  - 右侧：节气图标 + 节气名称 + 养生建议
  - 信息来源：Open-Meteo API + SolarTermUtil

#### FR-HOME-03: 核心数据卡片
- **描述**: 突出显示今日最重要的健康数据
- **优先级**: P0
- **验收标准**:
  - 饮水进度：环形进度条 + 当前值/目标值
  - 运动时长：环形进度条 + 分钟数
  - 今日心情：表情图标 + 评分
  - 点击卡片跳转对应功能页面

#### FR-HOME-04: 周概览卡片
- **描述**: 四宫格展示本周各维度健康数据
- **优先级**: P0
- **验收标准**:
  - 四个维度：💧饮水 | 🏃运动 | 😊心情 | 🍎饮食
  - 每个维度显示：达标天数、趋势箭头（↑↓→）
  - 点击可展开查看详情

#### FR-HOME-05: 健康贴士卡片
- **描述**: 展示今日健康建议
- **优先级**: P1
- **验收标准**:
  - 卡片式设计，支持翻转动画
  - 正面：贴士标题 + 摘要
  - 背面：详细内容 + 中医养生建议
  - 每日自动更新

#### FR-HOME-06: 成就徽章区
- **描述**: 横向滚动展示已获得的成就徽章
- **优先级**: P1
- **验收标准**:
  - 横向滚动列表
  - 徽章图标 + 名称 + 解锁日期
  - 未获得徽章显示为灰色锁定状态
  - 点击徽章查看详情

#### FR-HOME-07: 快速操作区
- **描述**: 底部快速操作入口
- **优先级**: P2
- **验收标准**:
  - 快速记录饮水
  - 快速记录运动
  - 快速记录心情
  - 浮动操作按钮（FAB）

### 2.2 技术实现

#### 数据流
```
HomeViewModel
├── weather: WeatherState (from WeatherRepository)
├── water: WaterState (from WaterRepository)
├── exercise: ExerciseState (from ExerciseRepository)
├── mood: MoodState (from MoodRepository)
├── streak: Int (from WaterRecordDao)
└── achievements: List<Achievement> (from AchievementRepository)
```

#### UI组件
- `GreetingSection`: 问候区组件
- `EnvironmentInfoBar`: 环境信息条组件
- `CoreDataCards`: 核心数据卡片组
- `WeeklyOverviewCard`: 周概览卡片
- `HealthTipCard`: 健康贴士卡片
- `AchievementRow`: 成就徽章行
- `QuickActionFAB`: 快速操作浮动按钮

#### 状态管理
```kotlin
data class HomeUiState(
    val greeting: String,
    val userName: String,
    val streakDays: Int,
    val weather: WeatherState,
    val solarTerm: SolarTermState,
    val waterProgress: Float,
    val exerciseProgress: Float,
    val moodScore: Int,
    val weeklyStats: WeeklyStats,
    val healthTip: HealthTip,
    val achievements: List<Achievement>,
    val isLoading: Boolean,
    val errorMessage: String?
)
```

---

## 三、饮食页面优化（DietScreen）

### 3.1 功能需求

#### FR-DIET-01: 饮水追踪卡片
- **描述**: 独立的饮水追踪卡片，突出显示
- **优先级**: P0
- **验收标准**:
  - 环形进度条（大尺寸，视觉突出）
  - 当前饮水量/目标饮水量（ml）
  - 快速添加按钮：+100ml | +250ml | +500ml
  - 历史记录查看入口

#### FR-DIET-02: 营养摄入概览
- **描述**: 今日营养摄入统计
- **优先级**: P1
- **验收标准**:
  - 三个指标：蛋白质、碳水化合物、脂肪
  - 每个指标显示：当前值、目标值、进度条
  - 数据来源：饮食记录自动计算
  - 点击可查看详细营养报告

#### FR-DIET-03: 餐次记录列表
- **描述**: 按餐次分类展示饮食记录
- **优先级**: P0
- **验收标准**:
  - 时间线设计，按时间倒序
  - 餐次图标区分：☀️早餐 | 🌤️午餐 | 🌙晚餐 | 🍪加餐
  - 每餐显示：时间、食物列表、卡路里
  - 支持左滑删除、右滑编辑

#### FR-DIET-04: 添加饮食记录
- **描述**: 新增饮食记录的入口和表单
- **优先级**: P0
- **验收标准**:
  - 浮动操作按钮（+ 图标）
  - 表单字段：餐次、食物名称、数量、单位
  - 食物搜索和自动完成
  - 常用食物快速选择

#### FR-DIET-05: 饮水历史图表
- **描述**: 展示近7天饮水趋势
- **优先级**: P2
- **验收标准**:
  - 柱状图展示每日饮水量
  - 目标线标注
  - 点击柱子查看当日详情

### 3.2 技术实现

#### 数据模型扩展
```kotlin
// 新增实体
@Entity(tableName = "diet_records")
data class DietRecord(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val date: String,           // yyyy-MM-dd
    val mealType: String,       // breakfast/lunch/dinner/snack
    val foodName: String,
    val amount: Float,
    val unit: String,
    val calories: Float,
    val protein: Float,
    val carbs: Float,
    val fat: Float,
    val createdAt: Long
)
```

#### ViewModel扩展
```kotlin
data class DietUiState(
    val waterIntake: Int,
    val waterGoal: Int,
    val nutrition: NutritionState,
    val mealRecords: List<MealGroup>,
    val weeklyWaterTrend: List<DailyWater>,
    val isLoading: Boolean,
    val errorMessage: String?
)

data class NutritionState(
    val protein: Float,
    val proteinGoal: Float,
    val carbs: Float,
    val carbsGoal: Float,
    val fat: Float,
    val fatGoal: Float
)
```

---

## 四、运动页面优化（ExerciseScreen）

### 4.1 功能需求

#### FR-EX-01: 今日运动概览卡片
- **描述**: 突出显示今日运动数据
- **优先级**: P0
- **验收标准**:
  - 双环进度条：运动时长（外圈）+ 步数（内圈）
  - 当前值/目标值显示
  - 运动类型图标
  - 点击查看详情

#### FR-EX-02: 周运动趋势图
- **描述**: 展示近7天运动趋势
- **优先级**: P0
- **验收标准**:
  - 柱状图展示每日运动时长
  - 目标线标注
  - 支持切换：时长/步数/卡路里
  - 点击柱子查看当日详情

#### FR-EX-03: 运动类型统计
- **描述**: 展示各运动类型的统计
- **优先级**: P1
- **验收标准**:
  - 三宫格展示主要运动类型
  - 图标 + 名称 + 本周次数
  - 点击查看该类型详细记录

#### FR-EX-04: 运动记录列表
- **描述**: 展示运动记录历史
- **优先级**: P0
- **验收标准**:
  - 按时间倒序
  - 每条记录：时间、类型、时长、卡路里
  - 运动类型图标区分
  - 支持左滑删除

#### FR-EX-05: 添加运动记录
- **描述**: 新增运动记录的入口和表单
- **优先级**: P0
- **验收标准**:
  - 浮动操作按钮（+ 图标）
  - 表单字段：运动类型、时长、距离（可选）、卡路里（可选）
  - 运动类型选择：步行、跑步、瑜伽、游泳等
  - 计时器功能（可选）

#### FR-EX-06: 运动目标设定
- **描述**: 自定义运动目标
- **优先级**: P2
- **验收标准**:
  - 每日运动时长目标
  - 每日步数目标
  - 每周运动次数目标
  - 目标完成度统计

### 4.2 技术实现

#### 数据模型
```kotlin
// 现有 ExerciseRecord 已满足需求
// 新增运动目标配置
@Entity(tableName = "exercise_goals")
data class ExerciseGoal(
    @PrimaryKey val id: String = "default",
    val dailyDuration: Int = 60,    // 分钟
    val dailySteps: Int = 6000,
    val weeklySessions: Int = 5,
    val updatedAt: Long
)
```

#### ViewModel扩展
```kotlin
data class ExerciseUiState(
    val todayDuration: Int,
    val todaySteps: Int,
    val durationGoal: Int,
    val stepsGoal: Int,
    val weeklyTrend: List<DailyExercise>,
    val exerciseTypeStats: List<ExerciseTypeStat>,
    val recentRecords: List<ExerciseRecord>,
    val isLoading: Boolean,
    val errorMessage: String?
)
```

---

## 五、心理页面优化（MentalScreen）

### 5.1 功能需求

#### FR-MENTAL-01: 今日心情卡片
- **描述**: 快速记录和展示今日心情
- **优先级**: P0
- **验收标准**:
  - 5个表情图标：😫(1分) | 😔(2分) | 😐(3分) | 😊(4分) | 😄(5分)
  - 点击表情记录心情
  - 已选择的表情高亮显示
  - 支持修改今日心情

#### FR-MENTAL-02: 心情趋势图
- **描述**: 展示近7天心情变化趋势
- **优先级**: P0
- **验收标准**:
  - 折线图展示每日心情评分
  - Y轴：1-5分
  - X轴：周一到周日
  - 点击数据点查看详情

#### FR-MENTAL-03: 呼吸练习卡片
- **描述**: 4-7-8呼吸练习引导
- **优先级**: P1
- **验收标准**:
  - 动画引导：吸气(4秒) → 屏息(7秒) → 呼气(8秒)
  - 脉冲动画效果
  - 练习计时器
  - 练习完成统计

#### FR-MENTAL-04: 心情日记列表
- **描述**: 展示心情记录历史
- **优先级**: P0
- **验收标准**:
  - 按时间倒序
  - 每条记录：时间、表情、评分、文字描述
  - 支持左滑删除
  - 点击查看详情

#### FR-MENTAL-05: 记录心情对话框
- **描述**: 新增心情记录的对话框
- **优先级**: P0
- **验收标准**:
  - 对话框形式
  - 心情评分：滑动条或表情选择
  - 文字描述：可选输入框
  - 保存按钮

#### FR-MENTAL-06: 心情统计
- **描述**: 心情数据分析
- **优先级**: P2
- **验收标准**:
  - 本周平均心情
  - 心情最好/最差的一天
  - 心情波动分析
  - 改善建议

### 5.2 技术实现

#### 数据模型
```kotlin
// 现有 MoodRecord 已满足需求
// 新增心情统计
data class MoodStatistics(
    val weeklyAverage: Float,
    val bestDay: String,
    val worstDay: String,
    val trend: String  // improving/stable/declining
)
```

#### ViewModel扩展
```kotlin
data class MentalUiState(
    val todayMood: Int?,
    val todayNote: String,
    val weeklyTrend: List<DailyMood>,
    val recentRecords: List<MoodRecord>,
    val statistics: MoodStatistics,
    val isBreathing: Boolean,
    val breathingPhase: String,
    val breathingProgress: Float,
    val isLoading: Boolean,
    val errorMessage: String?
)
```

---

## 六、UI组件库扩展

### 6.1 新增组件

| 组件名 | 描述 | 使用页面 |
|--------|------|----------|
| `CircularProgressRing` | 环形进度条 | 首页、饮食、运动 |
| `DualProgressRing` | 双环进度条 | 运动概览 |
| `WeeklyBarChart` | 周柱状图 | 首页、运动 |
| `TrendLineChart` | 趋势折线图 | 心理 |
| `MealTimeline` | 餐次时间线 | 饮食 |
| `ExerciseTimeline` | 运动时间线 | 运动 |
| `MoodSelector` | 心情选择器 | 心理 |
| `BreathingAnimation` | 呼吸动画 | 心理 |
| `AchievementBadge` | 成就徽章 | 首页 |
| `StatCard` | 统计卡片 | 首页、个人档案 |
| `QuickActionButton` | 快速操作按钮 | 全局 |

### 6.2 组件规范

#### 尺寸规范
- 卡片圆角：12dp
- 卡片间距：12dp
- 内边距：16dp
- 图标尺寸：24dp（标准）、28dp（强调）

#### 颜色规范
- 使用 MutedColorScheme 中定义的颜色
- 进度条：primary 色
- 成功状态：success 色
- 警告状态：warning 色
- 错误状态：error 色

#### 动画规范
- 动画时长：300ms（标准）、500ms（强调）
- 缓动曲线：FastOutSlowIn
- 进度动画：LinearOutSlowIn

---

## 七、数据层扩展

### 7.1 新增 Repository

| Repository | 描述 | 数据源 |
|------------|------|--------|
| `NutritionRepository` | 营养数据 | DietRecord |
| `AchievementRepository` | 成就数据 | 各Record表 |
| `GoalRepository` | 目标配置 | 本地存储 |

### 7.2 DAO 扩展

#### WaterRecordDao 新增
```kotlin
@Query("SELECT date, SUM(amount) as total FROM water_records WHERE date BETWEEN :startDate AND :endDate GROUP BY date ORDER BY date")
fun getDailyWaterTrend(startDate: String, endDate: String): Flow<List<DailyWater>>
```

#### ExerciseRecordDao 新增
```kotlin
@Query("SELECT date, SUM(duration) as total FROM exercise_records WHERE date BETWEEN :startDate AND :endDate GROUP BY date ORDER BY date")
fun getDailyExerciseTrend(startDate: String, endDate: String): Flow<List<DailyExercise>>
```

#### MoodRecordDao 新增
```kotlin
@Query("SELECT date, AVG(score) as average FROM mood_records WHERE date BETWEEN :startDate AND :endDate GROUP BY date ORDER BY date")
fun getDailyMoodTrend(startDate: String, endDate: String): Flow<List<DailyMood>>
```

---

## 八、实施计划

### 8.1 阶段划分

#### 阶段一：首页重新设计（P0）
- **时间**: 2-3小时
- **内容**: FR-HOME-01 ~ FR-HOME-04
- **产出**: 新版首页，核心数据展示优化

#### 阶段二：饮食页面优化（P0）
- **时间**: 2-3小时
- **内容**: FR-DIET-01 ~ FR-DIET-04
- **产出**: 独立饮水卡片、营养概览、餐次记录

#### 阶段三：运动页面优化（P0）
- **时间**: 2-3小时
- **内容**: FR-EX-01 ~ FR-EX-05
- **产出**: 双环进度、趋势图、运动记录

#### 阶段四：心理页面优化（P0）
- **时间**: 2-3小时
- **内容**: FR-MENTAL-01 ~ FR-MENTAL-05
- **产出**: 心情选择、趋势图、呼吸练习

#### 阶段五：收尾优化（P1-P2）
- **时间**: 2-3小时
- **内容**: P1/P2功能、UI细节优化、测试
- **产出**: 完整功能、测试报告

### 8.2 里程碑

| 里程碑 | 预计完成 | 交付物 |
|--------|----------|--------|
| M1: 首页优化 | Day 1 | 新版首页 + APK |
| M2: 饮食优化 | Day 2 | 新版饮食页 + APK |
| M3: 运动优化 | Day 3 | 新版运动页 + APK |
| M4: 心理优化 | Day 4 | 新版心理页 + APK |
| M5: 项目完成 | Day 5 | 完整APP + 文档 |

---

## 九、风险评估

### 9.1 技术风险

| 风险 | 概率 | 影响 | 应对措施 |
|------|------|------|----------|
| 图表库兼容性 | 中 | 高 | 使用Canvas自绘图表 |
| 动画性能 | 低 | 中 | 优化动画实现，减少过度绘制 |
| 数据库迁移 | 低 | 高 | 使用fallbackToDestructiveMigration |

### 9.2 进度风险

| 风险 | 概率 | 影响 | 应对措施 |
|------|------|------|----------|
| 功能范围蔓延 | 中 | 高 | 严格按需求文档执行 |
| 测试时间不足 | 中 | 中 | 自动化测试 + 手动测试 |

---

## 十、验收标准

### 10.1 功能验收
- [ ] 所有P0功能实现并测试通过
- [ ] 所有P1功能实现并测试通过
- [ ] P2功能至少实现50%
- [ ] 单元测试通过率100%
- [ ] 无P0/P1级别bug

### 10.2 性能验收
- [ ] 页面加载时间 < 1秒
- [ ] 动画流畅度 > 60fps
- [ ] 内存占用 < 100MB
- [ ] 无内存泄漏

### 10.3 用户体验验收
- [ ] 界面布局符合设计稿
- [ ] 交互反馈及时准确
- [ ] 错误提示友好清晰
- [ ] 主题切换功能正常

---

## 附录

### A. 参考资料
- Material Design 3 规范
- Jetpack Compose 最佳实践
- 健康类APP设计趋势

### B. 相关文档
- `MutedColorSchemes.kt` - 配色方案定义
- `ThemeManager.kt` - 主题管理器
- `CLAUDE.md` - 项目架构文档

---

**文档状态**: 待确认  
**下一步**: 用户确认后开始实施阶段一
