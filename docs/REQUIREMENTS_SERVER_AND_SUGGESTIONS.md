# 需求梳理：服务端 + 优化建议模块

## 一、PulseVita 服务端（Java）

### 1.1 目标

为 PulseVita APP 提供后端服务，支撑用户体系、数据同步、优化建议生成等联网功能。

### 1.2 技术选型

| 层 | 技术 | 理由 |
|---|---|---|
| 语言 | Java 17 | 与 Android 端统一语言生态 |
| 框架 | Spring Boot 3.x | 成熟稳定，社区大，快速开发 |
| 数据库 | MySQL 8.0 | 关系型数据（用户/记录/建议），成熟可靠 |
| 缓存 | Redis | 会话管理、每日建议缓存、投票计数 |
| ORM | MyBatis-Plus | 灵活 SQL 控制，适合复杂查询 |
| 认证 | JWT + Spring Security | 无状态认证，APP 友好 |
| API 文档 | Knife4j (Swagger) | 自动生成接口文档，方便联调 |
| 部署 | Docker + Docker Compose | 一键部署，环境隔离 |

### 1.3 核心模块

```
pulsevita-server/
├── 用户模块        注册/登录/个人资料同步
├── 数据同步模块    健康记录（饮水/运动/心情/饮食）上传与同步
├── 优化建议模块    每日生成建议 + 投票 + 采纳
├── 统计分析模块    周/月数据汇总、趋势分析
└── 系统管理        健康贴士管理、挑战管理、推送通知
```

### 1.4 数据库设计

```sql
-- 用户表
user (id, username, password_hash, nickname, gender, height, weight, 
      birth_date, occupation, avatar_url, created_at, updated_at)

-- 健康记录同步表
health_record (id, user_id, record_type, record_date, data_json, 
               synced_at, created_at)
-- record_type: WATER / EXERCISE / MOOD / DIET
-- data_json: 存储各类型详细数据的 JSON

-- 优化建议表
suggestion (id, title, description, category, target_metric, 
            difficulty, icon, priority, created_at)
-- category: DIET / EXERCISE / MENTAL / HABIT
-- difficulty: EASY / MEDIUM / HARD

-- 每日建议推送表
daily_suggestion (id, date, suggestion_id_1, suggestion_id_2, suggestion_id_3, 
                  total_votes, created_at)

-- 用户投票表
user_vote (id, user_id, daily_suggestion_id, suggestion_id, voted_at)
-- 唯一约束: (user_id, daily_suggestion_id)

-- 用户优化计划表
optimization_plan (id, user_id, suggestion_id, status, 
                   start_date, end_date, progress, created_at)
-- status: ACTIVE / COMPLETED / ABANDONED

-- 健康贴士表（服务端管理，同步到 APP）
health_tip (id, title, content, category, season, created_at)

-- 每日挑战表（服务端管理）
daily_challenge (id, date, title, description, icon, difficulty, created_at)
```

### 1.5 API 接口设计

| 模块 | 接口 | 方法 | 说明 |
|------|------|------|------|
| **用户** | `/api/auth/register` | POST | 注册 |
| | `/api/auth/login` | POST | 登录，返回 JWT |
| | `/api/auth/refresh` | POST | 刷新 token |
| | `/api/user/profile` | GET/PUT | 获取/更新个人资料 |
| **数据同步** | `/api/records/sync` | POST | 批量上传健康记录 |
| | `/api/records/pull` | GET | 拉取服务端记录（增量） |
| | `/api/records/stats` | GET | 周/月统计数据 |
| **优化建议** | `/api/suggestions/daily` | GET | 获取今日 3 条建议 |
| | `/api/suggestions/vote` | POST | 投票选择建议 |
| | `/api/suggestions/history` | GET | 历史建议列表 |
| **优化计划** | `/api/plan/active` | GET | 获取当前进行中的计划 |
| | `/api/plan/add` | POST | 将建议加入计划 |
| | `/api/plan/update` | PUT | 更新计划进度 |
| | `/api/plan/complete` | POST | 标记完成 |
| **内容管理** | `/api/tips/daily` | GET | 每日健康贴士 |
| | `/api/challenges/daily` | GET | 每日挑战 |

### 1.6 认证流程

```
APP 登录 → POST /api/auth/login (username + password)
    ↓
服务端验证 → 返回 accessToken (30min) + refreshToken (7d)
    ↓
APP 存储 token → 后续请求 Header: Authorization: Bearer <token>
    ↓
token 过期 → POST /api/auth/refresh → 新 token
```

### 1.7 数据同步策略

```
APP 本地记录 → 定时/手动触发同步 → POST /api/records/sync
    ↓
服务端合并（按 record_date + record_type 去重）→ 存入 MySQL
    ↓
其他设备 → GET /api/records/pull?since=<timestamp> → 增量拉取
```

---

## 二、优化建议模块

### 2.1 目标

每天自动生成 3 条个性化健康优化建议，用户投票选择最想执行的，得票最高的自动加入优化计划。

### 2.2 建议生成逻辑

#### 数据来源

| 数据 | 来源 | 用途 |
|------|------|------|
| 饮水记录 | WaterRecord | 分析饮水习惯，发现不足 |
| 运动记录 | ExerciseRecord | 分析运动频率和时长 |
| 心情记录 | MoodRecord | 分析情绪趋势 |
| 饮食记录 | DietRecord | 分析饮食结构 |
| BMI | UserEntity | 体重相关建议 |
| 季节/节气 | SolarTermUtil | 时令养生建议 |
| 连续打卡 | Streak 计算 | 习惯养成建议 |

#### 生成方式（AI Agent）

服务端将用户近 7 天健康数据作为上下文，调用 LLM API 生成 3 条个性化建议。

```java
// 伪代码：AI 建议生成器
public List<Suggestion> generateDailySuggestions(Long userId) {
    // 1. 拉取用户近 7 天数据，组装 prompt
    UserStats stats = analyzeRecentRecords(userId, 7);
    String prompt = buildPrompt(stats);
    
    // 2. 调用 LLM API
    String response = llmClient.chat(prompt);
    
    // 3. 解析 JSON 返回 3 条建议
    return parseSuggestions(response);
}

private String buildPrompt(UserStats stats) {
    return String.format("""
        你是一个专业的健康顾问。根据以下用户健康数据，生成 3 条具体的、可执行的优化建议。
        
        用户数据（近 7 天）：
        - 饮水：日均 %d ml（目标 2000ml）
        - 运动：共 %d 天，平均 %d 分钟
        - 心情：平均 %.1f 分（1-5分）
        - 饮食：日均 %.1f 餐
        - BMI: %.1f
        - 当前节气：%s
        
        要求：
        1. 每条建议必须具体可执行，不要泛泛而谈
        2. 包含标题（10字内）、详细描述（50字内）、难度（简单/中等/困难）、类别（饮食/运动/心理/习惯）
        3. 根据用户数据中最薄弱的维度优先建议
        4. 返回 JSON 数组格式
        
        返回格式：
        [{"title":"...","description":"...","difficulty":"EASY","category":"DIET"},...]
        """, 
        stats.avgWaterIntake(), stats.exerciseDays(), stats.avgExerciseDuration(),
        stats.avgMoodLevel(), stats.mealsPerDay(), stats.bmi(), stats.solarTerm());
}
```

#### AI 生成的优势

| 对比 | 规则引擎 | AI Agent |
|------|----------|----------|
| 建议质量 | 固定模板，千篇一律 | 因人而异，自然语言 |
| 维护成本 | 每条规则需手动编写 | 只需维护 prompt |
| 扩展性 | 新增规则需改代码 | 调整 prompt 即可 |
| 个性化 | 基于阈值判断 | 理解上下文，综合分析 |

### 2.3 投票机制

```
每日 06:00 服务端生成当日 3 条建议 → 推送到 APP
    ↓
用户打开 APP → 看到 3 条建议卡片 → 点击投票（每人每天每条只能投 1 票）
    ↓
投票截止（当日 23:59）→ 得票最高的建议自动加入优化计划
    ↓
如果平票 → 优先级更高的入选
如果用户已投票的建议入选 → 标记为"你选择的"
```

### 2.4 APP 端交互设计

#### 首页集成

```
┌─────────────────────────────────┐
│ 📊 今日优化建议                  │
│ ┌─────────────────────────────┐ │
│ │ 💧 每天喝够 2000ml 水       │ │
│ │ 近7天日均饮水仅1200ml...    │ │
│ │ [👍 投票]  已有 23 人投票    │ │
│ └─────────────────────────────┘ │
│ ┌─────────────────────────────┐ │
│ │ 🚶 增加运动频率             │ │
│ │ 本周仅运动1天，建议...      │ │
│ │ [👍 投票]  已有 15 人投票    │ │
│ └─────────────────────────────┘ │
│ ┌─────────────────────────────┐ │
│ │ 🧘 尝试 4-7-8 呼吸练习      │ │
│ │ 近期心情偏低，建议...       │ │
│ │ [👍 投票]  已有 8 人投票     │ │
│ └─────────────────────────────┘ │
└─────────────────────────────────┘
```

#### 优化计划页面（集成到「我的」模块）

在 ProfileScreen 中新增「优化计划」入口卡片，点击进入计划详情页。

```
┌─────────────────────────────────┐
│ 📋 我的优化计划                  │
│                                 │
│ 进行中 (2)                      │
│ ┌─────────────────────────────┐ │
│ │ 💧 每天喝够 2000ml 水       │ │
│ │ 开始: 06-05  进度: ████░░ 60%│ │
│ │ [更新进度] [标记完成]        │ │
│ └─────────────────────────────┘ │
│ ┌─────────────────────────────┐ │
│ │ 🚶 增加运动频率             │ │
│ │ 开始: 06-06  进度: ██░░░░ 30%│ │
│ │ [更新进度] [标记完成]        │ │
│ └─────────────────────────────┘ │
│                                 │
│ 已完成 (3)                      │
│ ┌─────────────────────────────┐ │
│ │ ✅ 规律三餐                  │ │
│ │ 完成于 06-04                 │ │
│ └─────────────────────────────┘ │
└─────────────────────────────────┘
```

### 2.5 APP 端新增代码结构

```
ui/
├── suggestion/                  # 新增：优化建议模块
│   ├── SuggestionScreen.kt      # 建议列表页面
│   ├── SuggestionViewModel.kt   # 建议数据管理
│   ├── SuggestionUiState.kt     # UI 状态
│   ├── SuggestionCard.kt        # 建议卡片组件
│   └── PlanScreen.kt            # 优化计划页面（从「我的」进入）
├── profile/
│   └── ProfileScreen.kt         # 新增优化计划入口卡片
├── components/
│   ├── SuggestionCard.kt        # 建议卡片（首页集成）
│   └── PlanProgressCard.kt      # 计划进度卡片
data/
├── remote/
│   ├── SuggestionApiService.kt  # 建议 API 接口
│   └── SuggestionRepository.kt  # 建议数据仓库
├── local/
│   └── entity/
│       └── OptimizationPlan.kt  # 本地计划缓存
navigation/
└── Screen.kt                    # 新增 Suggestion / Plan 路由
```

---

## 三、实施计划

### 阶段一：服务端基础（1-2 天）

1. Spring Boot 项目初始化
2. 数据库建表
3. 用户注册/登录 API（JWT）
4. 健康记录同步 API
5. Docker 部署配置

### 阶段二：优化建议服务端（1 天）

1. 建议规则引擎
2. 每日建议生成定时任务
3. 投票 API
4. 优化计划 CRUD API

### 阶段三：APP 端联网改造（2-3 天）

1. APP 登录/注册页面
2. Retrofit 接入服务端 API
3. 数据同步逻辑（本地优先，后台同步）
4. 优化建议页面
5. 优化计划页面
6. 首页集成建议卡片

### 阶段四：联调测试（1 天）

1. API 联调
2. 数据同步测试
3. 边界情况处理（网络断开、token 过期等）

---

## 四、已确认决策

| 问题 | 决策 |
|------|------|
| 服务端部署 | 当前腾讯云服务器 (82.156.72.247) |
| 建议来源 | AI Agent 直接生成（非规则引擎），需要调用 LLM API |
| 推送通知 | 暂不需要，后续再加 |
| 优化计划入口 | 集成到「我的」(Profile) 模块中 |
