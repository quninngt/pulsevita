#!/usr/bin/env python3
"""PulseVita v3.0 API 联调测试 - 修正版"""
import requests
import json
import sys

BASE_URL = "https://8fb0c8e02decca.lhr.life"
results = {"pass": 0, "fail": 0, "skip": 0, "details": []}

def test(tid, name, func):
    try:
        ok, msg = func()
        if ok:
            results["pass"] += 1
            status = "✅"
        elif ok is None:
            results["skip"] += 1
            status = "⚠️"
        else:
            results["fail"] += 1
            status = "❌"
        results["details"].append(f"{status} {tid}: {name} {msg if msg else ''}")
        print(f"{status} {tid}: {name} {msg if msg else ''}")
    except Exception as e:
        results["fail"] += 1
        results["details"].append(f"❌ {tid}: {name} 异常: {e}")
        print(f"❌ {tid}: {name} 异常: {e}")

# ==================== 1. 用户模块 ====================
print("\n--- 1. 用户模块 ---")

token = None
refresh_token = None

def t01_register():
    r = requests.post(f"{BASE_URL}/api/auth/register", json={
        "username": "test_v3_user", "password": "test123456", "nickname": "测试用户v3"
    })
    d = r.json()
    if d["code"] == 200:
        return True, f"(userId={d['data']['userId']})"
    if "已存在" in d.get("message", ""):
        return True, f"(用户已存在,跳过注册)"
    return False, f"code={d['code']}, msg={d['message']}"

def t02_duplicate():
    r = requests.post(f"{BASE_URL}/api/auth/register", json={
        "username": "test_v3_user", "password": "test123456"
    })
    d = r.json()
    if d["code"] != 200:
        return True, f"(code={d['code']}, 正确拒绝)"
    return False, "应该拒绝重复注册"

def t03_login():
    global token, refresh_token
    r = requests.post(f"{BASE_URL}/api/auth/login", json={
        "username": "test_v3_user", "password": "test123456"
    })
    d = r.json()
    if d["code"] == 200 and d["data"]["accessToken"]:
        token = d["data"]["accessToken"]
        refresh_token = d["data"]["refreshToken"]
        return True, "(token获取成功)"
    return False, f"code={d['code']}"

def t04_wrong_password():
    r = requests.post(f"{BASE_URL}/api/auth/login", json={
        "username": "test_v3_user", "password": "wrong_password"
    })
    d = r.json()
    if d["code"] != 200:
        return True, f"(code={d['code']}, 正确拒绝)"
    return False, "应该拒绝错误密码"

def t05_refresh():
    global token
    r = requests.post(f"{BASE_URL}/api/auth/refresh", params={"refreshToken": refresh_token})
    d = r.json()
    if d["code"] == 200:
        token = d["data"]["accessToken"]
        return True, "(新token获取成功)"
    return False, f"code={d['code']}, msg={d['message']}"

def t06_profile():
    r = requests.get(f"{BASE_URL}/api/user/profile", headers={"Authorization": f"Bearer {token}"})
    d = r.json()
    if d["code"] == 200 and d["data"]:
        return True, f"(nickname={d['data'].get('nickname', 'N/A')})"
    return False, f"code={d['code']}"

def t07_update_profile():
    r = requests.put(f"{BASE_URL}/api/user/profile",
        headers={"Authorization": f"Bearer {token}", "Content-Type": "application/json"},
        json={"nickname": "更新后昵称"})
    d = r.json()
    if d["code"] == 200:
        return True, f"(nickname={d['data'].get('nickname', 'N/A')})"
    return False, f"code={d['code']}, msg={d['message']}"

test("T01", "用户注册", t01_register)
test("T02", "重复注册拒绝", t02_duplicate)
test("T03", "用户登录", t03_login)
test("T04", "密码错误拒绝", t04_wrong_password)
test("T05", "Token刷新", t05_refresh)
test("T06", "获取用户资料", t06_profile)
test("T07", "更新用户资料", t07_update_profile)

# ==================== 2. 健康记录模块 ====================
print("\n--- 2. 健康记录模块 ---")

def t08_sync():
    """POST /api/records/sync - 上传记录"""
    r = requests.post(f"{BASE_URL}/api/records/sync",
        headers={"Authorization": f"Bearer {token}", "Content-Type": "application/json"},
        json={"records": [
            {"type": "water", "date": "2026-06-07", "data": json.dumps({"amount": 250})},
            {"type": "exercise", "date": "2026-06-07", "data": json.dumps({"type": "walking", "duration": 30, "steps": 3000})},
            {"type": "mood", "date": "2026-06-07", "data": json.dumps({"level": 4, "note": "心情不错"})},
            {"type": "diet", "date": "2026-06-07", "data": json.dumps({"mealType": "lunch", "description": "米饭+青菜"})}
        ]})
    d = r.json()
    if d["code"] == 200:
        return True, f"(同步{len(d['data'].get('syncedRecords', [])) if d['data'] else 0}条)"
    return False, f"code={d['code']}, msg={d['message']}"

def t09_pull():
    """GET /api/records/pull?since=xxx - 拉取记录"""
    r = requests.get(f"{BASE_URL}/api/records/pull",
        params={"since": "2026-06-01"},
        headers={"Authorization": f"Bearer {token}"})
    d = r.json()
    if d["code"] == 200:
        return True, f"({len(d['data']) if d['data'] else 0}条记录)"
    return False, f"code={d['code']}, msg={d['message']}"

def t10_weekly_stats():
    """GET /api/records/stats/weekly - 周统计"""
    r = requests.get(f"{BASE_URL}/api/records/stats/weekly",
        headers={"Authorization": f"Bearer {token}"})
    d = r.json()
    if d["code"] == 200:
        return True, "(统计获取成功)"
    return False, f"code={d['code']}, msg={d['message']}"

test("T08", "同步健康记录 POST /api/records/sync", t08_sync)
test("T09", "拉取记录 GET /api/records/pull", t09_pull)
test("T10", "周统计 GET /api/records/stats/weekly", t10_weekly_stats)

# ==================== 3. 内容模块 ====================
print("\n--- 3. 内容模块 ---")

def t11_daily_tip():
    r = requests.get(f"{BASE_URL}/api/content/tips/daily",
        headers={"Authorization": f"Bearer {token}"})
    d = r.json()
    if d["code"] == 200:
        tip = d["data"]
        return True, f"({tip.get('category', 'N/A')}: {tip.get('content', '')[:20]}...)" if tip else "(空)"
    return False, f"code={d['code']}, msg={d['message']}"

def t12_daily_challenge():
    r = requests.get(f"{BASE_URL}/api/content/challenges/daily",
        params={"date": "2026-06-07"},
        headers={"Authorization": f"Bearer {token}"})
    d = r.json()
    if d["code"] == 200:
        ch = d["data"]
        return True, f"({ch.get('title', 'N/A')[:20]}...)" if ch else "(空)"
    return False, f"code={d['code']}, msg={d['message']}"

test("T11", "每日贴士 GET /api/content/tips/daily", t11_daily_tip)
test("T12", "每日挑战 GET /api/content/challenges/daily", t12_daily_challenge)

# ==================== 4. 优化建议模块 ====================
print("\n--- 4. 优化建议模块 ---")

def t13_daily_suggestions():
    r = requests.get(f"{BASE_URL}/api/suggestions/daily",
        headers={"Authorization": f"Bearer {token}"})
    d = r.json()
    if d["code"] == 200:
        sug = d["data"]
        cnt = len(sug.get("suggestions", [])) if sug else 0
        return True, f"({cnt}条建议)"
    return False, f"code={d['code']}, msg={d['message']}"

def t14_suggestion_history():
    r = requests.get(f"{BASE_URL}/api/suggestions/history",
        params={"page": 1, "size": 10},
        headers={"Authorization": f"Bearer {token}"})
    d = r.json()
    if d["code"] == 200:
        return True, f"({len(d['data']) if d['data'] else 0}条历史)"
    return False, f"code={d['code']}, msg={d['message']}"

test("T13", "每日建议 GET /api/suggestions/daily", t13_daily_suggestions)
test("T14", "建议历史 GET /api/suggestions/history", t14_suggestion_history)
test("T15", "建议投票", lambda: (None, "跳过(需要有效建议ID)"))

# ==================== 5. 优化计划模块 ====================
print("\n--- 5. 优化计划模块 ---")

def t16_active_plans():
    r = requests.get(f"{BASE_URL}/api/plan/active",
        headers={"Authorization": f"Bearer {token}"})
    d = r.json()
    if d["code"] == 200:
        return True, f"({len(d['data']) if d['data'] else 0}个活跃计划)"
    return False, f"code={d['code']}, msg={d['message']}"

def t17_all_plans():
    r = requests.get(f"{BASE_URL}/api/plan/all",
        headers={"Authorization": f"Bearer {token}"})
    d = r.json()
    if d["code"] == 200:
        return True, f"({len(d['data']) if d['data'] else 0}个计划)"
    return False, f"code={d['code']}, msg={d['message']}"

test("T16", "活跃计划 GET /api/plan/active", t16_active_plans)
test("T17", "全部计划 GET /api/plan/all", t17_all_plans)

# ==================== 6. 健康报告模块 ====================
print("\n--- 6. 健康报告模块 ---")

def t18_weekly_report():
    r = requests.get(f"{BASE_URL}/api/reports/latest",
        params={"type": "weekly"},
        headers={"Authorization": f"Bearer {token}"})
    d = r.json()
    if d["code"] == 200:
        return True, f"(报告存在)" if d["data"] else "(暂无报告)"
    return False, f"code={d['code']}, msg={d['message']}"

def t19_report_list():
    r = requests.get(f"{BASE_URL}/api/reports/list",
        params={"type": "weekly", "page": 1, "size": 10},
        headers={"Authorization": f"Bearer {token}"})
    d = r.json()
    if d["code"] == 200:
        return True, f"({len(d['data']) if d['data'] else 0}份报告)"
    return False, f"code={d['code']}, msg={d['message']}"

test("T18", "最新周报 GET /api/reports/latest?type=weekly", t18_weekly_report)
test("T19", "报告列表 GET /api/reports/list", t19_report_list)

# ==================== 7. 成就系统模块 ====================
print("\n--- 7. 成就系统模块 ---")

def t20_achievements():
    r = requests.get(f"{BASE_URL}/api/achievements/all",
        headers={"Authorization": f"Bearer {token}"})
    d = r.json()
    if d["code"] == 200:
        return True, f"({len(d['data']) if d['data'] else 0}个成就)"
    return False, f"code={d['code']}, msg={d['message']}"

def t21_unlock():
    r = requests.post(f"{BASE_URL}/api/achievements/unlock",
        headers={"Authorization": f"Bearer {token}", "Content-Type": "application/json"},
        json={"code": "first_login"})
    d = r.json()
    if d["code"] == 200:
        return True, "(first_login解锁成功)"
    return False, f"code={d['code']}, msg={d['message']}"

test("T20", "成就列表 GET /api/achievements/all", t20_achievements)
test("T21", "解锁成就 POST /api/achievements/unlock", t21_unlock)

# ==================== 8. 数据安全测试 ====================
print("\n--- 8. 数据安全测试 ---")

def t22_no_auth():
    r = requests.get(f"{BASE_URL}/api/user/profile", timeout=10)
    # 隧道可能返回 503, 直接测本地
    r2 = requests.get("http://localhost:8080/api/user/profile", timeout=5)
    if r2.status_code == 401 or r2.status_code == 403:
        return True, f"(HTTP {r2.status_code} 正确拒绝)"
    d = r2.json()
    if d.get("code") == 401:
        return True, "(401正确)"
    return False, f"code={d.get('code')}, 应该返回401"

def t23_invalid_token():
    r = requests.get("http://localhost:8080/api/user/profile",
        headers={"Authorization": "Bearer invalid_token_xxx"}, timeout=5)
    if r.status_code == 401 or r.status_code == 403:
        return True, f"(HTTP {r.status_code} 正确拒绝)"
    d = r.json()
    if d.get("code") == 401:
        return True, "(401正确)"
    return False, f"code={d.get('code')}, 应该返回401"

def t24_https():
    r = requests.get(f"{BASE_URL}/api/auth/login", verify=True)
    return True, "(证书有效)"

test("T22", "未认证访问拒绝", t22_no_auth)
test("T23", "无效Token拒绝", t23_invalid_token)
test("T24", "HTTPS加密", t24_https)

# ==================== 汇总 ====================
print("\n" + "=" * 50)
total = results["pass"] + results["fail"] + results["skip"]
effective = total - results["skip"]
pct = results["pass"] * 100 // effective if effective > 0 else 0
print(f"总计: {total} | ✅通过: {results['pass']} | ❌失败: {results['fail']} | ⚠️跳过: {results['skip']}")
print(f"通过率: {results['pass']}/{effective} = {pct}%")
print("=" * 50)

# 输出失败详情
if results["fail"] > 0:
    print("\n❌ 失败项详情:")
    for d in results["details"]:
        if d.startswith("❌"):
            print(f"  {d}")

sys.exit(0 if results["fail"] == 0 else 1)
