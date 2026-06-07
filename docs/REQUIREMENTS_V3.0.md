# PulseVita v3.0 — 综合需求规格说明书

> **版本**: 1.0  
> **日期**: 2026-06-07  
> **范围**: 服务端建设 + AI 智能功能 + APP 联网改造

---

## 目录

1. [项目概述](#一项目概述)
2. [现状分析](#二现状分析)
3. [目标架构](#三目标架构)
4. [服务端设计](#四服务端设计)
5. [功能模块规格](#五功能模块规格)
6. [APP 端改造](#六app-端改造)
7. [非功能性需求](#七非功能性需求)
8. [实施计划](#八实施计划)
9. [验收标准](#九验收标准)

---

## 一、项目概述

### 1.1 背景

PulseVita 是一款 Android 健康追踪应用，当前 v2.0.1 已实现完整的本地功能（饮水/运动/心理/饮食记录、莫兰迪主题系统、成就系统）。为进一步提升用户体验和产品价值，需要建设服务端，引入 AI 智能能力，实现从"单机工具"到"智能健康平台"的升级。

### 1.2 核心目标

| 目标 | 说明 |
|------|------|
| 数据安全 | 多设备同步 + 云端备份，用户数据不丢失 |
| 智能服务 | AI 生成个性化健康建议、健康报告、异常预警 |
| 社交激励 | 挑战、排行、好友系统，提升用户活跃度和留存 |
| 内容运营 | 动态贴士、节气专题、知识库，内容常看常新 |
| 可扩展 | 管理后台、监控告警，支撑产品持续迭代 |

### 1.3 已确认决策

| 项 | 决策 |
|----|------|
| 服务端部署 | 当前腾讯云服务器 (82.156.72.247) |
| 服务端语言 | Java 17 + Spring Boot 3 |
| 建议生成 | AI Agent 直接生成（非规则引擎） |
| 推送通知 | 暂不需要，预留接口 |
| 优化计划入口 | 集成到「我的」模块 |

---

## 二、现状分析

### 2.1 当前架构

```
┌─────────────────────────────────────────┐
│              APP (Android)               │
│                                          │
│  Jetpack Compose + Material 3 + Room     │
│  MVVM + Hilt + Retrofit                  │
│                                          │
│  ┌────────┐ ┌────────┐ ┌──────┐ ┌─────┐ │
│  │ 首页   │ │ 饮食   │ │ 运动 │ │心理 │ │
│  └───┬────┘ └───┬────┘ └──┬───┘ └──┬──┘ │
│      └──────────┼─────────┼────────┘    │
│                 │         │              │
│           Room (SQLite)   │              │
│           本地数据存储     │              │
│                          │              │
│           Retrofit ──────┼──→ 外部 API  │
│           (天气/语录/定位)               │
└─────────────────────────────────────────┘
```

### 2.2 当前能力清单

| 模块 | 能力 | 数据存储 |
|------|------|----------|
| 首页 | 仪表盘、周概览、打卡日历、趋势图、挑战、贴士 | Room |
| 饮食 | 饮水追踪、饮食记录、营养概览、饮水图表 | Room |
| 运动 | 运动记录、类型统计、目标设定、办公操教程 | Room |
| 心理 | 心情日记、心情统计、呼吸练习、趋势图 | Room |
| 我的 | 个人资料、BMI、主题切换、成就系统 | Room |
| 网络 | 天气查询、IP 定位、随机语录 | Retrofit |

### 2.3 当前痛点

| 痛点 | 影响 |
|------|------|
| 数据仅存本地 | 换手机数据丢失，无法多设备同步 |
| 无用户体系 | 无法识别用户，无法提供个性化服务 |
| 挑战/贴士固定 | 本地写死，内容不更新 |
| 成就系统有限 | 仅 6 个本地成就，缺乏长期目标 |
| 无数据分析 | 只有简单趋势图，缺少深度分析 |
| 无社交互动 | 独自使用，缺乏激励机制 |

---

## 三、目标架构

### 3.1 整体架构图

```
┌──────────────────────────────────────────────────────────┐
│                    PulseVita APP (Android)                 │
│                                                            │
│  ┌──────────┐ ┌──────────┐ ┌──────────┐ ┌──────────────┐ │
│  │ 首页     │ │ 饮食     │ │ 运动     │ │ 我的         │ │
│  │ +建议卡片│ │          │ │          │ │ +优化计划    │ │
│  │ +报告入口│ │          │ │          │ │ +成就升级    │ │
│  └────┬─────┘ └────┬─────┘ └────┬─────┘ └──────┬───────┘ │
│       └────────────┼────────────┼───────────────┘         │
│                    │            │                          │
│              ┌─────▼────────────▼─────┐                   │
│              │     Repository 层      │                   │
│              │  (本地优先+后台同步)    │                   │
│              └─────┬────────────┬─────┘                   │
│                    │            │                          │
│              ┌─────▼─────┐ ┌───▼──────────┐              │
│              │ Room DB   │ │ Retrofit     │              │
│              │ (本地缓存)│ │ (API 客户端) │              │
│              └───────────┘ └───┬──────────┘              │
└────────────────────────────────┼──────────────────────────┘
                                 │ HTTPS / JWT
                                 ▼
┌──────────────────────────────────────────────────────────┐
│              PulseVita Server (腾讯云)                     │
│                                                            │
│  ┌─────────────────────────────────────────────────────┐  │
│  │              Spring Boot 3 + Spring Security         │  │
│  │                                                     │  │
│  │  ┌──────────┐ ┌──────────┐ ┌──────────┐           │  │
│  │  │ 用户模块 │ │ 数据模块 │ │ AI 模块  │           │  │
│  │  │ 注册登录 │ │ 记录同步 │ │ 建议/报告│           │  │
│  │  │ JWT 认证 │ │ 统计聚合 │ │ 异常检测 │           │  │
│  │  └────┬─────┘ └────┬─────┘ └────┬─────┘           │  │
│  │       │            │            │                  │  │
│  │  ┌────▼────────────▼────────────▼────┐             │  │
│  │  │         MyBatis-Plus ORM          │             │  │
│  │  └────┬─────────────────────────┬───┘             │  │
│  │       │                         │                 │  │
│  │  ┌────▼─────┐            ┌──────▼──────┐         │  │
│  │  │ MySQL 8  │            │   Redis     │         │  │
│  │  │ 持久存储 │            │ 缓存/会话   │         │  │
│  │  └──────────┘            └─────────────┘         │  │
│  └─────────────────────────────────────────────────────┘  │
│                                                            │
│  ┌─────────────────────────────────────────────────────┐  │
│  │              管理后台 (Vue 3 + Element Plus)         │  │
│  │  用户管理 | 内容管理 | 数据看板 | AI 管理           │  │
│  └─────────────────────────────────────────────────────┘  │
└──────────────────────────────────────────────────────────┘
                                 │
                                 ▼
┌──────────────────────────────────────────────────────────┐
│                    外部服务                                │
│  LLM API (AI 建议/报告) | 天气 API | 语录 API            │
└──────────────────────────────────────────────────────────┘
```

### 3.2 数据流

```
记录数据流:
  APP 记录 → Room 本地存储 → 定时/手动同步 → 服务端 MySQL
  其他设备 ← 增量拉取 ← 服务端 API ← MySQL

AI 建议流:
  定时任务(06:00) → 汇总用户 7 天数据 → 组装 Prompt → LLM API
  → 解析 3 条建议 → 存入 MySQL → APP 拉取展示 → 用户投票
  → 投票截止(23:59) → 最高票加入优化计划

AI 报告流:
  每周日/每月 1 号 → 汇总周期数据 → 组装 Prompt → LLM API
  → 生成报告 → 存入 MySQL → APP 展示
```

---

## 四、服务端设计

### 4.1 技术栈

| 层 | 技术 | 版本 | 用途 |
|---|---|---|---|
| 语言 | Java | 17 | 主语言 |
| 框架 | Spring Boot | 3.2+ | Web 框架 |
| 安全 | Spring Security + JWT | — | 认证授权 |
| 数据库 | MySQL | 8.0 | 持久存储 |
| 缓存 | Redis | 7.x | 会话、缓存、投票计数 |
| ORM | MyBatis-Plus | 3.5+ | 数据访问 |
| API 文档 | Knife4j | 4.x | Swagger 增强 |
| 容器 | Docker + Docker Compose | — | 部署 |

### 4.2 项目结构

```
pulsevita-server/
├── docker-compose.yml              # MySQL + Redis + App 编排
├── Dockerfile
├── src/main/java/com/pulsevita/server/
│   ├── PulsevitaServerApplication.java
│   ├── config/
│   │   ├── SecurityConfig.java         # Spring Security 配置
│   │   ├── RedisConfig.java            # Redis 配置
│   │   ├── MyBatisPlusConfig.java      # ORM 配置
│   │   └── CorsConfig.java             # 跨域配置
│   ├── common/
│   │   ├── Result.java                 # 统一响应封装
│   │   ├── ErrorCode.java              # 错误码枚举
│   │   ├── GlobalExceptionHandler.java # 全局异常处理
│   │   └── JwtUtil.java                # JWT 工具类
│   ├── module/
│   │   ├── user/                       # 用户模块
│   │   │   ├── controller/UserController.java
│   │   │   ├── service/UserService.java
│   │   │   ├── mapper/UserMapper.java
│   │   │   └── entity/User.java
│   │   ├── record/                     # 健康记录模块
│   │   │   ├── controller/RecordController.java
│   │   │   ├── service/RecordService.java
│   │   │   ├── mapper/RecordMapper.java
│   │   │   └── entity/HealthRecord.java
│   │   ├── suggestion/                 # AI 建议模块
│   │   │   ├── controller/SuggestionController.java
│   │   │   ├── service/SuggestionService.java
│   │   │   ├── service/AIService.java
│   │   │   ├── mapper/SuggestionMapper.java
│   │   │   └── entity/...
│   │   ├── plan/                       # 优化计划模块
│   │   │   ├── controller/PlanController.java
│   │   │   ├── service/PlanService.java
│   │   │   ├── mapper/PlanMapper.java
│   │   │   └── entity/OptimizationPlan.java
│   │   ├── report/                     # 健康报告模块
│   │   │   ├── controller/ReportController.java
│   │   │   ├── service/ReportService.java
│   │   │   └── entity/HealthReport.java
│   │   ├── content/                    # 内容管理模块
│   │   │   ├── controller/ContentController.java
│   │   │   ├── service/ContentService.java
│   │   │   └── entity/...
│   │   ├── achievement/                # 成就模块
│   │   │   ├── controller/AchievementController.java
│   │   │   ├── service/AchievementService.java
│   │   │   └── entity/...
│   │   └── social/                     # 社交模块（P3）
│   │       ├── controller/...
│   │       └── entity/...
│   └── task/
│       ├── DailySuggestionTask.java    # 每日建议定时任务
│       └── WeeklyReportTask.java       # 周报定时任务
├── src/main/resources/
│   ├── application.yml
│   └── mapper/                         # MyBatis XML
└── pom.xml
```

### 4.3 数据库设计

#### 核心表

```sql
-- ============================================================
-- 1. 用户表
-- ============================================================
CREATE TABLE `user` (
    `id`            BIGINT AUTO_INCREMENT PRIMARY KEY,
    `username`      VARCHAR(50)  NOT NULL UNIQUE,
    `password_hash` VARCHAR(255) NOT NULL,
    `nickname`      VARCHAR(50)  DEFAULT '',
    `gender`        VARCHAR(10)  DEFAULT '',
    `height`        FLOAT        DEFAULT 0,
    `weight`        FLOAT        DEFAULT 0,
    `birth_date`    VARCHAR(20)  DEFAULT '',
    `occupation`    VARCHAR(50)  DEFAULT '',
    `avatar_url`    VARCHAR(500) DEFAULT '',
    `created_at`    DATETIME     DEFAULT CURRENT_TIMESTAMP,
    `updated_at`    DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ============================================================
-- 2. 健康记录表（统一存储 4 种记录类型）
-- ============================================================
CREATE TABLE `health_record` (
    `id`          BIGINT AUTO_INCREMENT PRIMARY KEY,
    `user_id`     BIGINT       NOT NULL,
    `record_type` VARCHAR(20)  NOT NULL COMMENT 'WATER/EXERCISE/MOOD/DIET',
    `record_date` VARCHAR(10)  NOT NULL COMMENT 'yyyy-MM-dd',
    `data_json`   JSON         NOT NULL COMMENT '各类型详细数据',
    `synced_at`   DATETIME     DEFAULT CURRENT_TIMESTAMP,
    `created_at`  DATETIME     DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY `uk_user_type_date` (`user_id`, `record_type`, `record_date`),
    INDEX `idx_user_date` (`user_id`, `record_date`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- data_json 示例:
-- WATER:   {"amount": 1500, "goal": 2000, "records": [{"time":"08:30","amount":250}, ...]}
-- EXERCISE: {"totalDuration": 45, "totalSteps": 6000, "records": [{"type":"WALKING","duration":30}, ...]}
-- MOOD:    {"level": 4, "icon": "😊", "note": "今天心情不错"}
-- DIET:    {"meals": [{"type":"BREAKFAST","records":[...]}, ...]}

-- ============================================================
-- 3. 优化建议表
-- ============================================================
CREATE TABLE `suggestion` (
    `id`          BIGINT AUTO_INCREMENT PRIMARY KEY,
    `user_id`     BIGINT       NOT NULL,
    `title`       VARCHAR(50)  NOT NULL,
    `description` VARCHAR(500) NOT NULL,
    `category`    VARCHAR(20)  NOT NULL COMMENT 'DIET/EXERCISE/MENTAL/HABIT',
    `difficulty`  VARCHAR(10)  NOT NULL COMMENT 'EASY/MEDIUM/HARD',
    `icon`        VARCHAR(20)  DEFAULT '',
    `created_at`  DATETIME     DEFAULT CURRENT_TIMESTAMP,
    INDEX `idx_user_created` (`user_id`, `created_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ============================================================
-- 4. 每日建议推送表
-- ============================================================
CREATE TABLE `daily_suggestion` (
    `id`              BIGINT AUTO_INCREMENT PRIMARY KEY,
    `user_id`         BIGINT      NOT NULL,
    `date`            VARCHAR(10) NOT NULL COMMENT 'yyyy-MM-dd',
    `suggestion_id_1` BIGINT      NOT NULL,
    `suggestion_id_2` BIGINT      NOT NULL,
    `suggestion_id_3` BIGINT      NOT NULL,
    `created_at`      DATETIME    DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY `uk_user_date` (`user_id`, `date`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ============================================================
-- 5. 用户投票表
-- ============================================================
CREATE TABLE `user_vote` (
    `id`                   BIGINT AUTO_INCREMENT PRIMARY KEY,
    `user_id`              BIGINT      NOT NULL,
    `daily_suggestion_id`  BIGINT      NOT NULL,
    `suggestion_id`        BIGINT      NOT NULL,
    `voted_at`             DATETIME    DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY `uk_user_daily` (`user_id`, `daily_suggestion_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ============================================================
-- 6. 优化计划表
-- ============================================================
CREATE TABLE `optimization_plan` (
    `id`            BIGINT AUTO_INCREMENT PRIMARY KEY,
    `user_id`       BIGINT       NOT NULL,
    `suggestion_id` BIGINT       NOT NULL,
    `status`        VARCHAR(20)  DEFAULT 'ACTIVE' COMMENT 'ACTIVE/COMPLETED/ABANDONED',
    `start_date`    VARCHAR(10)  NOT NULL,
    `end_date`      VARCHAR(10)  DEFAULT '',
    `progress`      INT          DEFAULT 0 COMMENT '0-100',
    `created_at`    DATETIME     DEFAULT CURRENT_TIMESTAMP,
    `updated_at`    DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX `idx_user_status` (`user_id`, `status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ============================================================
-- 7. 健康报告表
-- ============================================================
CREATE TABLE `health_report` (
    `id`          BIGINT AUTO_INCREMENT PRIMARY KEY,
    `user_id`     BIGINT       NOT NULL,
    `report_type` VARCHAR(10)  NOT NULL COMMENT 'WEEKLY/MONTHLY',
    `period`      VARCHAR(20)  NOT NULL COMMENT '2026-W23 / 2026-06',
    `content`     TEXT         NOT NULL COMMENT 'AI 生成的报告内容 (Markdown)',
    `summary`     VARCHAR(500) DEFAULT '',
    `created_at`  DATETIME     DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY `uk_user_type_period` (`user_id`, `report_type`, `period`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ============================================================
-- 8. 健康贴士表
-- ============================================================
CREATE TABLE `health_tip` (
    `id`         BIGINT AUTO_INCREMENT PRIMARY KEY,
    `title`      VARCHAR(100) NOT NULL,
    `content`    TEXT         NOT NULL,
    `category`   VARCHAR(20)  NOT NULL COMMENT 'TCM/DIET/EXERCISE/MENTAL',
    `season`     VARCHAR(10)  DEFAULT 'ALL' COMMENT 'SPRING/SUMMER/AUTUMN/WINTER/ALL',
    `solar_term` VARCHAR(20)  DEFAULT '' COMMENT '节气名称，空表示不限',
    `priority`   INT          DEFAULT 0,
    `created_at` DATETIME     DEFAULT CURRENT_TIMESTAMP,
    INDEX `idx_season_term` (`season`, `solar_term`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ============================================================
-- 9. 每日挑战表
-- ============================================================
CREATE TABLE `daily_challenge` (
    `id`          BIGINT AUTO_INCREMENT PRIMARY KEY,
    `date`        VARCHAR(10)  NOT NULL UNIQUE COMMENT 'yyyy-MM-dd',
    `title`       VARCHAR(100) NOT NULL,
    `description` VARCHAR(500) NOT NULL,
    `icon`        VARCHAR(20)  DEFAULT '',
    `difficulty`  VARCHAR(10)  DEFAULT 'EASY',
    `created_at`  DATETIME     DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ============================================================
-- 10. 成就表
-- ============================================================
CREATE TABLE `achievement` (
    `id`          BIGINT AUTO_INCREMENT PRIMARY KEY,
    `code`        VARCHAR(50)  NOT NULL UNIQUE COMMENT 'STREAK_7/STREAK_30/...',
    `name`        VARCHAR(50)  NOT NULL,
    `description` VARCHAR(200) NOT NULL,
    `icon`        VARCHAR(20)  DEFAULT '',
    `tier`        VARCHAR(10)  NOT NULL COMMENT 'BRONZE/SILVER/GOLD/RARE',
    `condition`   VARCHAR(200) NOT NULL COMMENT '达成条件描述',
    `created_at`  DATETIME     DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ============================================================
-- 11. 用户成就表
-- ============================================================
CREATE TABLE `user_achievement` (
    `id`             BIGINT AUTO_INCREMENT PRIMARY KEY,
    `user_id`        BIGINT   NOT NULL,
    `achievement_id` BIGINT   NOT NULL,
    `unlocked_at`    DATETIME DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY `uk_user_achievement` (`user_id`, `achievement_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
```

### 4.4 API 接口设计

#### 统一响应格式

```json
{
    "code": 200,
    "message": "success",
    "data": { ... }
}
```

#### 接口清单

**用户模块 `/api/auth`**

| 接口 | 方法 | 说明 | 认证 |
|------|------|------|------|
| `/api/auth/register` | POST | 注册 | ✕ |
| `/api/auth/login` | POST | 登录，返回 JWT | ✕ |
| `/api/auth/refresh` | POST | 刷新 token | ✕ |
| `/api/user/profile` | GET | 获取个人资料 | ✓ |
| `/api/user/profile` | PUT | 更新个人资料 | ✓ |

**数据同步模块 `/api/records`**

| 接口 | 方法 | 说明 | 认证 |
|------|------|------|------|
| `/api/records/sync` | POST | 批量上传记录 | ✓ |
| `/api/records/pull` | GET | 增量拉取 (?since=timestamp) | ✓ |
| `/api/records/stats/weekly` | GET | 周统计 | ✓ |
| `/api/records/stats/monthly` | GET | 月统计 | ✓ |

**AI 建议模块 `/api/suggestions`**

| 接口 | 方法 | 说明 | 认证 |
|------|------|------|------|
| `/api/suggestions/daily` | GET | 获取今日 3 条建议 | ✓ |
| `/api/suggestions/vote` | POST | 投票 | ✓ |
| `/api/suggestions/history` | GET | 历史建议列表 | ✓ |

**优化计划模块 `/api/plan`**

| 接口 | 方法 | 说明 | 认证 |
|------|------|------|------|
| `/api/plan/active` | GET | 当前进行中的计划 | ✓ |
| `/api/plan/all` | GET | 所有计划（含已完成） | ✓ |
| `/api/plan/add` | POST | 将建议加入计划 | ✓ |
| `/api/plan/{id}/progress` | PUT | 更新进度 | ✓ |
| `/api/plan/{id}/complete` | POST | 标记完成 | ✓ |
| `/api/plan/{id}/abandon` | POST | 放弃计划 | ✓ |

**健康报告模块 `/api/reports`**

| 接口 | 方法 | 说明 | 认证 |
|------|------|------|------|
| `/api/reports/latest` | GET | 最新报告 | ✓ |
| `/api/reports/weekly` | GET | 周报列表 | ✓ |
| `/api/reports/monthly` | GET | 月报列表 | ✓ |

**内容模块 `/api/content`**

| 接口 | 方法 | 说明 | 认证 |
|------|------|------|------|
| `/api/content/tips/daily` | GET | 每日健康贴士 | ✓ |
| `/api/content/challenges/daily` | GET | 每日挑战 | ✓ |
| `/api/content/tips/seasonal` | GET | 当季贴士列表 | ✓ |

**成就模块 `/api/achievements`**

| 接口 | 方法 | 说明 | 认证 |
|------|------|------|------|
| `/api/achievements/all` | GET | 所有成就定义 | ✓ |
| `/api/achievements/mine` | GET | 我已解锁的成就 | ✓ |

**数据导出 `/api/export`**

| 接口 | 方法 | 说明 | 认证 |
|------|------|------|------|
| `/api/export/csv` | GET | 导出 CSV | ✓ |
| `/api/export/pdf` | GET | 导出 PDF 报告 | ✓ |

---

## 五、功能模块规格

### 5.1 用户模块

**注册**
- 输入：用户名（3-20 字符）、密码（6-20 字符）、昵称
- 输出：用户 ID + JWT token
- 密码使用 BCrypt 加密存储

**登录**
- 输入：用户名 + 密码
- 输出：accessToken (30 分钟) + refreshToken (7 天)

**个人资料同步**
- APP 端登录后，首次同步本地 UserEntity 到服务端
- 后续修改双向同步（以最新 updated_at 为准）

### 5.2 数据同步模块

**同步策略：本地优先，后台同步**

```
APP 记录数据 → 立即存入 Room → 标记为"未同步"
    ↓
定时任务(每 6 小时) / 手动触发 / APP 启动时
    ↓
批量上传未同步记录 → POST /api/records/sync
    ↓
服务端按 (user_id, record_type, record_date) 去重合并
    ↓
其他设备 → GET /api/records/pull?since=<timestamp> → 增量拉取 → 存入 Room
```

**上传请求体**

```json
{
    "records": [
        {
            "type": "WATER",
            "date": "2026-06-07",
            "data": {
                "amount": 1500,
                "goal": 2000,
                "records": [
                    {"time": "08:30", "amount": 250},
                    {"time": "10:15", "amount": 500}
                ]
            },
            "syncedAt": "2026-06-07T10:30:00"
        }
    ]
}
```

### 5.3 AI 每日建议模块

**生成流程**

```
定时任务 (每日 06:00)
    ↓
遍历活跃用户（近 7 天有数据）
    ↓
汇总用户近 7 天数据 → 组装 Prompt
    ↓
调用 LLM API → 解析 JSON → 存入 suggestion 表
    ↓
创建 daily_suggestion 记录 → 缓存到 Redis
    ↓
APP 拉取 → 展示 3 条建议卡片 → 用户投票
    ↓
投票截止 (23:59) → 统计票数 → 最高票建议自动加入优化计划
```

**Prompt 模板**

```
你是专业的健康顾问。根据用户近 7 天健康数据，生成 3 条优化建议。

用户数据：
- 饮水：日均 {avgWater}ml（目标 2000ml）
- 运动：共 {exerciseDays} 天，平均 {avgDuration} 分钟
- 心情：平均 {avgMood}/5 分
- 饮食：日均 {avgMeals} 餐
- BMI：{bmi}
- 节气：{solarTerm}

要求：
1. 每条建议具体可执行，不要泛泛而谈
2. 优先关注最薄弱的维度
3. 返回 JSON 数组：

[{"title":"10字内标题","description":"50字内具体描述","difficulty":"EASY/MEDIUM/HARD","category":"DIET/EXERCISE/MENTAL/HABIT","icon":"emoji"}]
```

**投票规则**
- 每人每天对每组建议只能投 1 票
- 投票截止后，得票最高的建议自动加入优化计划
- 平票时按建议优先级决定

### 5.4 AI 健康报告模块

**周报 (每周日 08:00 生成)**

```
内容结构：
1. 本周总览
   - 饮水达标天数/7
   - 运动天数/7，总时长
   - 平均心情评分
   - 平均每日餐数

2. 趋势对比
   - 与上周对比：各维度进步/退步百分比
   - 连续打卡天数变化

3. AI 综合评价
   - 200 字以内的健康状况分析
   - 本周做得好的地方
   - 需要改进的地方

4. 下周建议
   - 3 条重点改进方向
```

**月报 (每月 1 号 08:00 生成)**
- 结构同周报，时间范围为整月
- 增加月度趋势图表数据

### 5.5 异常预警模块

| 触发条件 | 预警内容 | 类型 |
|----------|----------|------|
| 连续 3 天心情 < 2 分 | "最近心情不太好，要不要试试呼吸练习？" | 心理 |
| 连续 5 天未运动 | "好几天没运动了，今天走走路？" | 运动 |
| 饮水量骤降 50% | "最近喝水少了很多，注意补水" | 饮食 |
| 连续 7 天未记录 | "好久没来了，回来看看吧" | 习惯 |

**实现方式**：服务端定时任务检测，将预警作为"特殊建议"推送到每日建议中。

### 5.6 智能目标调整模块

| 条件 | 操作 |
|------|------|
| 连续 7 天达标率 > 90% | 建议提高目标 +10% |
| 连续 7 天达标率 < 30% | 建议降低目标 -10% |
| 连续 14 天达标率 > 95% | 解锁"超额完成"成就 |

### 5.7 动态健康贴士模块

- 服务端维护贴士库（100+ 条），按类别/季节/节气分类
- 每日推送 1 条，优先匹配当前季节和节气
- 已推送过的 7 天内不重复
- 运营可通过管理后台增删改贴士

### 5.8 成就系统（服务端升级）

| 等级 | 成就 | 条件 |
|------|------|------|
| 🥉 铜 | 初次打卡 | 首次记录 |
| 🥉 铜 | 饮水达人 | 单日饮水达标 |
| 🥉 铜 | 运动新手 | 首次运动记录 |
| 🥈 银 | 周周打卡 | 连续 7 天打卡 |
| 🥈 银 | 运动达人 | 本周运动 5 天 |
| 🥈 银 | 心情管理 | 连续 7 天记录心情 |
| 🥇 金 | 月度坚持 | 连续 30 天打卡 |
| 🥇 金 | 饮水大师 | 连续 30 天饮水达标 |
| 🥇 金 | 全勤运动 | 本月运动 25 天以上 |
| 💎 稀有 | 百日如一 | 连续 100 天打卡 |
| 💎 稀有 | 健康大使 | 所有维度连续 30 天达标 |

### 5.9 数据导出模块

**CSV 导出**
- 导出全部健康记录，格式：日期、类型、详细数据
- 支持按日期范围筛选

**PDF 报告**
- 包含：个人资料、数据总览、趋势图表、AI 综合评价
- 适合分享给医生或家人

### 5.10 管理后台（Web）

| 模块 | 功能 |
|------|------|
| 用户管理 | 查看用户列表、活跃度、数据量 |
| 内容管理 | 健康贴士 CRUD、每日挑战 CRUD、成就管理 |
| 数据看板 | DAU、留存率、功能使用率、同步成功率 |
| AI 管理 | 查看建议生成质量、手动调整、报告管理 |

**技术**：Vue 3 + Element Plus，独立前端项目，部署在同一服务器。

---

## 六、APP 端改造

### 6.1 新增页面

| 页面 | 入口 | 功能 |
|------|------|------|
| 登录页 | APP 启动时（未登录状态） | 用户名/密码登录 |
| 注册页 | 登录页跳转 | 用户名/密码/昵称注册 |
| 建议详情页 | 首页建议卡片 | 查看建议详情、投票 |
| 优化计划页 | 「我的」模块入口 | 查看/管理优化计划 |
| 健康报告页 | 「我的」模块入口 | 查看周报/月报 |
| 成就详情页 | 「我的」模块入口 | 查看所有成就和已解锁 |

### 6.2 页面改造

| 页面 | 改造内容 |
|------|----------|
| 首页 | 新增"今日建议"卡片区域（3 条建议+投票按钮） |
| 首页 | 新增"最新报告"入口卡片 |
| 我的 | 新增"优化计划"入口 |
| 我的 | 新增"健康报告"入口 |
| 我的 | 成就系统升级（铜/银/金/稀有四级） |
| 我的 | 新增"数据导出"功能 |

### 6.3 新增代码结构

```
app/src/main/java/com/healthapp/
├── data/
│   ├── remote/
│   │   ├── ApiService.kt              # 统一 API 接口定义
│   │   ├── AuthInterceptor.kt         # JWT token 拦截器
│   │   └── TokenManager.kt            # token 存储/刷新
│   └── repository/
│       ├── AuthRepository.kt          # 登录/注册
│       ├── SyncRepository.kt          # 数据同步
│       ├── SuggestionRepository.kt    # 建议相关
│       ├── PlanRepository.kt          # 计划相关
│       ├── ReportRepository.kt        # 报告相关
│       └── AchievementRepository.kt   # 成就相关
├── ui/
│   ├── auth/                          # 新增：登录/注册
│   │   ├── LoginScreen.kt
│   │   ├── RegisterScreen.kt
│   │   └── AuthViewModel.kt
│   ├── suggestion/                    # 新增：建议模块
│   │   ├── SuggestionCard.kt
│   │   ├── SuggestionDetailScreen.kt
│   │   └── SuggestionViewModel.kt
│   ├── plan/                          # 新增：优化计划
│   │   ├── PlanScreen.kt
│   │   ├── PlanProgressCard.kt
│   │   └── PlanViewModel.kt
│   ├── report/                        # 新增：健康报告
│   │   ├── ReportScreen.kt
│   │   ├── ReportDetailScreen.kt
│   │   └── ReportViewModel.kt
│   ├── home/HomeScreen.kt             # 改造：新增建议卡片
│   └── profile/ProfileScreen.kt       # 改造：新增入口卡片
└── navigation/
    └── NavGraph.kt                     # 改造：新增路由
```

---

## 七、非功能性需求

### 7.1 性能

| 指标 | 要求 |
|------|------|
| API 响应时间 | < 500ms (95th percentile) |
| AI 建议生成 | < 10s (单用户) |
| 数据同步 | 单次 < 5s (100 条记录) |
| 并发支持 | 100+ 同时在线用户 |

### 7.2 安全

| 措施 | 说明 |
|------|------|
| 密码加密 | BCrypt |
| JWT 认证 | accessToken 30min + refreshToken 7d |
| HTTPS | 全链路 HTTPS |
| 速率限制 | 登录接口 5 次/分钟，其他 60 次/分钟 |
| 数据隔离 | 用户只能访问自己的数据 |

### 7.3 可靠性

| 措施 | 说明 |
|------|------|
| 数据备份 | MySQL 每日自动备份 |
| 离线可用 | APP 无网络时正常使用本地功能，联网后自动同步 |
| 重试机制 | 同步失败自动重试 3 次 |
| 降级策略 | AI 服务不可用时，返回预置建议 |

### 7.4 部署

| 项 | 方案 |
|----|------|
| 服务器 | 腾讯云 82.156.72.247 |
| 容器化 | Docker Compose (MySQL + Redis + Server) |
| 端口 | 8080 (API)、3306 (MySQL)、6379 (Redis) |
| 域名 | 暂用 IP，后续绑定域名 |
| SSL | Let's Encrypt（绑定域名后配置） |

---

## 八、实施计划

### 第一阶段：服务端基础（3 天）

| 天 | 任务 | 产出 |
|----|------|------|
| D1 | Spring Boot 项目初始化、Docker 环境搭建、MySQL/Redis 配置 | 可运行的服务端骨架 |
| D2 | 用户注册/登录 API、JWT 认证、个人资料 API | 用户模块完成 |
| D3 | 健康记录同步 API、统计数据 API、Docker 部署 | 数据模块完成 |

### 第二阶段：AI 智能（2 天）

| 天 | 任务 | 产出 |
|----|------|------|
| D4 | AI 服务集成、每日建议生成、投票 API | 建议模块完成 |
| D5 | 优化计划 API、AI 周报生成、异常预警 | 计划+报告模块完成 |

### 第三阶段：APP 端改造（3 天）

| 天 | 任务 | 产出 |
|----|------|------|
| D6 | 登录/注册页面、JWT token 管理、AuthInterceptor | 登录流程完成 |
| D7 | 数据同步逻辑、建议卡片+投票 UI、首页集成 | 核心 UI 完成 |
| D8 | 优化计划页面（「我的」模块）、健康报告页面、成就升级 | 全部 UI 完成 |

### 第四阶段：内容与体验（2 天）

| 天 | 任务 | 产出 |
|----|------|------|
| D9 | 动态健康贴士、数据导出（CSV/PDF） | 内容模块完成 |
| D10 | 管理后台基础版（Vue 3）、联调测试 | 全部完成 |

### 总计：10 个工作日

---

## 九、验收标准

### 9.1 服务端验收

- [ ] 用户注册/登录正常，JWT 认证有效
- [ ] 健康记录上传/拉取正确，数据不丢失
- [ ] 每日 06:00 自动生成 3 条 AI 建议
- [ ] 投票功能正常，最高票建议自动加入计划
- [ ] 周报/月报自动生成，内容质量合格
- [ ] 成就解锁逻辑正确
- [ ] Docker 部署成功，服务稳定运行

### 9.2 APP 端验收

- [ ] 登录/注册流程完整
- [ ] 离线可用，联网后自动同步
- [ ] 建议卡片展示正常，投票交互流畅
- [ ] 优化计划页面功能完整
- [ ] 健康报告可查看
- [ ] 成就系统升级后正常工作
- [ ] 主题切换时新增页面颜色一致

### 9.3 性能验收

- [ ] API 响应 < 500ms
- [ ] 同步 100 条记录 < 5s
- [ ] 无内存泄漏，长时间运行稳定
