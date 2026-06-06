package com.healthapp.util

import java.util.Calendar

data class SolarTerm(
    val name: String,
    val description: String,
    val healthTip: String
)

object SolarTermUtil {

    private val solarTerms = listOf(
        SolarTerm(
            name = "立春",
            description = "春季开始，万物复苏",
            healthTip = "春季宜养肝，多吃绿色蔬菜，早睡早起，适当运动"
        ),
        SolarTerm(
            name = "雨水",
            description = "降雨增多，气温回升",
            healthTip = "注意祛湿健脾，多吃山药、薏米，适当保暖"
        ),
        SolarTerm(
            name = "惊蛰",
            description = "春雷始鸣，万物生长",
            healthTip = "阳气升发，宜早起运动，多吃清淡食物"
        ),
        SolarTerm(
            name = "春分",
            description = "昼夜平分，阴阳平衡",
            healthTip = "保持作息规律，饮食宜温补，多吃时令蔬菜"
        ),
        SolarTerm(
            name = "清明",
            description = "天气清明，万物洁净",
            healthTip = "适合户外踏青，饮食宜清淡，注意养肝"
        ),
        SolarTerm(
            name = "谷雨",
            description = "雨生百谷，播种时节",
            healthTip = "湿气较重，注意健脾祛湿，适当运动出汗"
        ),
        SolarTerm(
            name = "立夏",
            description = "夏季开始，万物繁茂",
            healthTip = "夏季宜养心，多吃苦味食物，避免过度出汗"
        ),
        SolarTerm(
            name = "小满",
            description = "麦类灌浆，小满未满",
            healthTip = "气温升高，注意防暑补水，饮食宜清淡"
        ),
        SolarTerm(
            name = "芒种",
            description = "麦收时节，忙种忙收",
            healthTip = "天气炎热，注意防暑降温，多饮水，适当午休"
        ),
        SolarTerm(
            name = "夏至",
            description = "白昼最长，阳气最盛",
            healthTip = "宜晚睡早起，饮食清淡，注意养心"
        ),
        SolarTerm(
            name = "小暑",
            description = "暑气渐浓，炎热开始",
            healthTip = "注意防暑，多吃瓜果蔬菜，避免暴晒"
        ),
        SolarTerm(
            name = "大暑",
            description = "一年中最热的时期",
            healthTip = "注意防暑降温，多喝绿豆汤，避免中午外出"
        ),
        SolarTerm(
            name = "立秋",
            description = "秋季开始，暑去凉来",
            healthTip = "秋季宜养肺，多吃白色食物，注意润燥"
        ),
        SolarTerm(
            name = "处暑",
            description = "暑气消退，秋意渐浓",
            healthTip = "注意滋阴润燥，多喝水，适当运动"
        ),
        SolarTerm(
            name = "白露",
            description = "天气转凉，露珠凝结",
            healthTip = "早晚温差大，注意保暖，饮食宜温润"
        ),
        SolarTerm(
            name = "秋分",
            description = "昼夜平分，秋高气爽",
            healthTip = "保持心情平和，饮食宜均衡，适当运动"
        ),
        SolarTerm(
            name = "寒露",
            description = "露水更凉，寒意渐浓",
            healthTip = "注意足部保暖，多喝温水，适当进补"
        ),
        SolarTerm(
            name = "霜降",
            description = "气温骤降，开始有霜",
            healthTip = "注意防寒保暖，多吃温补食物，适当锻炼"
        ),
        SolarTerm(
            name = "立冬",
            description = "冬季开始，万物收藏",
            healthTip = "冬季宜养肾，多吃黑色食物，早睡晚起"
        ),
        SolarTerm(
            name = "小雪",
            description = "开始下雪，寒气渐深",
            healthTip = "注意保暖防寒，适当进补，保持心情愉悦"
        ),
        SolarTerm(
            name = "大雪",
            description = "降雪增多，天寒地冻",
            healthTip = "注意头部和脚部保暖，多喝热汤，适度运动"
        ),
        SolarTerm(
            name = "冬至",
            description = "白昼最短，阴极阳生",
            healthTip = "宜进补养生，多吃温补食物，注意保暖"
        ),
        SolarTerm(
            name = "小寒",
            description = "一年中最冷的时期之一",
            healthTip = "注意防寒保暖，适当进补，早睡晚起"
        ),
        SolarTerm(
            name = "大寒",
            description = "一年中最后一个节气",
            healthTip = "加强保暖，饮食宜温热，为春季养生做准备"
        )
    )

    // Solar term approximate start dates (month, day)
    // Uses the earliest day in each term's typical range
    private val termDates = listOf(
        2 to 3,    // 立春 Feb 3-5
        2 to 18,   // 雨水 Feb 18-20
        3 to 5,    // 惊蛰 Mar 5-7
        3 to 20,   // 春分 Mar 20-22
        4 to 4,    // 清明 Apr 4-6
        4 to 19,   // 谷雨 Apr 19-21
        5 to 5,    // 立夏 May 5-7
        5 to 20,   // 小满 May 20-22
        6 to 5,    // 芒种 Jun 5-7
        6 to 21,   // 夏至 Jun 21-22
        7 to 6,    // 小暑 Jul 6-8
        7 to 22,   // 大暑 Jul 22-24
        8 to 7,    // 立秋 Aug 7-9
        8 to 22,   // 处暑 Aug 22-24
        9 to 7,    // 白露 Sep 7-9
        9 to 22,   // 秋分 Sep 22-24
        10 to 8,   // 寒露 Oct 8-9
        10 to 23,  // 霜降 Oct 23-24
        11 to 7,   // 立冬 Nov 7-8
        11 to 22,  // 小雪 Nov 22-23
        12 to 6,   // 大雪 Dec 6-8
        12 to 21,  // 冬至 Dec 21-23
        1 to 5,    // 小寒 Jan 5-7
        1 to 20    // 大寒 Jan 20-21
    )

    fun getCurrentSolarTerm(): SolarTerm? {
        val calendar = Calendar.getInstance()
        return getSolarTerm(calendar)
    }

    fun getSolarTerm(calendar: Calendar): SolarTerm? {
        val month = calendar.get(Calendar.MONTH)  // 0-based
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        // current date as (month * 100 + day) for comparison
        val currentValue = (month + 1) * 100 + day

        // January: handle specially since solar term year wraps around
        if (month == Calendar.JANUARY) {
            if (day >= termDates[23].second) return solarTerms[23]  // 大寒 (≥ Jan 20)
            if (day >= termDates[22].second) return solarTerms[22]  // 小寒 (≥ Jan 5)
            return solarTerms[21] // Before 小寒 → still 冬至 from previous year
        }

        // Main months Feb-Dec: only check Feb through Dec terms (indices 0-21)
        // January terms (22-23) are excluded because their values (105, 120)
        // would incorrectly match any date past January
        var bestIndex = -1
        for (i in 0..21) {
            val termValue = termDates[i].first * 100 + termDates[i].second
            if (termValue <= currentValue) {
                bestIndex = i
            }
        }

        return if (bestIndex >= 0) solarTerms[bestIndex]
        else solarTerms[23] // Before first term (Feb) → 大寒
    }
}
