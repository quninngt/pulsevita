package com.healthapp.util

data class HealthChallenge(
    val title: String,
    val description: String,
    val category: String,   // water/exercise/mental/diet
    val icon: String        // emoji hint for display
)

object HealthChallengeProvider {

    private val challenges = listOf(
        HealthChallenge("喝满8杯水", "今天的目标是喝满2000ml水，少量多次饮用", "water", "💧"),
        HealthChallenge("步行30分钟", "抽出30分钟散步，保持每分钟100步的速度", "exercise", "🚶"),
        HealthChallenge("记录今日心情", "花一分钟记录今天的心情和感受", "mental", "😊"),
        HealthChallenge("吃一份水果", "在早餐或午餐中加入一份新鲜水果", "diet", "🍎"),
        HealthChallenge("深呼吸5分钟", "找个安静的地方，做5分钟深呼吸放松", "mental", "🧘"),
        HealthChallenge("提前30分钟入睡", "比平时早30分钟上床休息", "mental", "😴"),
        HealthChallenge("做办公室拉伸", "每小时起身做一次颈部、肩部和腰部拉伸", "exercise", "🤸"),
        HealthChallenge("早餐吃好点", "今天的早餐包含蛋白质、碳水和蔬果", "diet", "🥚"),
        HealthChallenge("喝一杯温水", "晨起喝一杯温水，唤醒肠胃", "water", "☕"),
        HealthChallenge("写三件感恩的事", "在日记里记下今天值得感恩的三件事", "mental", "📝"),
        HealthChallenge("久坐提醒", "每坐45分钟起身活动5分钟", "exercise", "💺"),
        HealthChallenge("午餐加一份蔬菜", "今天的午餐确保有绿色蔬菜", "diet", "🥗"),
        HealthChallenge("傍晚散步", "晚饭后散步15-20分钟，帮助消化", "exercise", "🌆"),
        HealthChallenge("限制咖啡摄入", "今天最多喝一杯咖啡，多喝白水", "water", "☕"),
        HealthChallenge("主动联系朋友", "给许久未联系的朋友发个问候", "mental", "💬"),
        HealthChallenge("吃优质蛋白", "今天的每餐都包含优质蛋白质来源", "diet", "🥩"),
        HealthChallenge("做10分钟瑜伽", "跟着教程做10分钟简单的瑜伽拉伸", "exercise", "🧘"),
        HealthChallenge("喝足2000ml", "今天挑战喝满2000ml水，用app记录", "water", "💦"),
        HealthChallenge("放下手机1小时", "找一小时不看手机，专注当下", "mental", "📵"),
        HealthChallenge("尝试新食谱", "今天尝试一道没做过的健康菜式", "diet", "👨‍🍳"),
        HealthChallenge("爬楼梯代替电梯", "今天用楼梯代替电梯，增加运动量", "exercise", "🏃"),
        HealthChallenge("饭后不立即坐下", "饭后站立或慢走15分钟", "diet", "🚶"),
        HealthChallenge("和自己对话", "写下今天的一个成就和一个改进点", "mental", "✍️"),
        HealthChallenge("每小时喝水提醒", "设个闹钟，每小时喝几口水", "water", "⏰"),
        HealthChallenge("做眼部放松", "每用眼1小时，远眺或闭目养神5分钟", "exercise", "👁️"),
        HealthChallenge("晚餐七分饱", "今天的晚餐吃到七分饱即可", "diet", "🥣"),
        HealthChallenge("听音乐放松", "选几首喜欢的音乐，纯粹地听10分钟", "mental", "🎵"),
        HealthChallenge("步行代替短途乘车", "3公里内的出行尝试步行", "exercise", "🚶"),
        HealthChallenge("喝一杯花茶", "用花茶代替含糖饮料，养生又补水", "water", "🌸"),
        HealthChallenge("整理房间", "花15分钟整理房间，整洁环境改善心情", "mental", "🏠"),
        HealthChallenge("晒太阳15分钟", "在阳光下活动15分钟，促进维生素D合成", "exercise", "☀️"),
        HealthChallenge("午餐细嚼慢咽", "午餐每口咀嚼20次以上，慢慢吃", "diet", "🍽️")
    )

    fun getTodayChallenge(): HealthChallenge {
        val dayOfYear = java.util.Calendar.getInstance().get(java.util.Calendar.DAY_OF_YEAR)
        return challenges[dayOfYear % challenges.size]
    }
}
