package com.healthapp.util

/**
 * 常见食物热量数据库
 * 数据来源：中国食物成分表（第6版）
 * 单位：卡路里 (kcal) / 每100g 或 每份
 */
object FoodDatabase {

    data class FoodItem(
        val name: String,
        val calories: Int,      // kcal per serving
        val serving: String,    // 份量描述
        val category: String    // 分类
    )

    /** 食物分类 */
    val categories = listOf("主食", "肉类", "蔬菜", "水果", "饮品", "零食", "蛋奶", "快餐")

    /** 常见食物列表 */
    val foods = listOf(
        // === 主食 ===
        FoodItem("米饭（一碗）", 230, "一碗(200g)", "主食"),
        FoodItem("馒头（一个）", 220, "一个(100g)", "主食"),
        FoodItem("面条（一碗）", 280, "一碗(200g)", "主食"),
        FoodItem("包子（一个）", 200, "一个(80g)", "主食"),
        FoodItem("饺子（10个）", 350, "10个", "主食"),
        FoodItem("油条（一根）", 310, "一根(80g)", "主食"),
        FoodItem("煎饼果子", 400, "一个", "主食"),
        FoodItem("红薯（一个）", 130, "一个(150g)", "主食"),
        FoodItem("玉米（一根）", 150, "一根(200g)", "主食"),
        FoodItem("全麦面包（两片）", 140, "两片(60g)", "主食"),

        // === 肉类 ===
        FoodItem("鸡胸肉", 165, "100g", "肉类"),
        FoodItem("鸡腿（一个）", 180, "一个(100g)", "肉类"),
        FoodItem("猪瘦肉", 143, "100g", "肉类"),
        FoodItem("红烧肉", 500, "一份(150g)", "肉类"),
        FoodItem("牛腱肉", 106, "100g", "肉类"),
        FoodItem("牛肉干", 550, "100g", "肉类"),
        FoodItem("鱼肉（清蒸）", 120, "100g", "肉类"),
        FoodItem("虾仁", 85, "100g", "肉类"),
        FoodItem("羊肉串（5串）", 350, "5串", "肉类"),

        // === 蔬菜 ===
        FoodItem("炒青菜", 60, "一盘(200g)", "蔬菜"),
        FoodItem("西红柿炒蛋", 180, "一盘", "蔬菜"),
        FoodItem("凉拌黄瓜", 30, "一盘", "蔬菜"),
        FoodItem("蒜蓉西兰花", 80, "一盘", "蔬菜"),
        FoodItem("酸辣土豆丝", 150, "一盘", "蔬菜"),
        FoodItem("干煸四季豆", 120, "一盘", "蔬菜"),
        FoodItem("麻婆豆腐", 180, "一盘", "蔬菜"),

        // === 水果 ===
        FoodItem("苹果（一个）", 95, "一个(200g)", "水果"),
        FoodItem("香蕉（一根）", 105, "一根(120g)", "水果"),
        FoodItem("橙子（一个）", 65, "一个(180g)", "水果"),
        FoodItem("葡萄（一碗）", 100, "一碗(150g)", "水果"),
        FoodItem("西瓜（一块）", 80, "一块(300g)", "水果"),
        FoodItem("草莓（一碗）", 50, "一碗(150g)", "水果"),
        FoodItem("芒果（一个）", 130, "一个(200g)", "水果"),
        FoodItem("猕猴桃（一个）", 60, "一个(100g)", "水果"),

        // === 饮品 ===
        FoodItem("牛奶（一杯）", 150, "一杯(250ml)", "饮品"),
        FoodItem("酸奶（一杯）", 180, "一杯(200ml)", "饮品"),
        FoodItem("豆浆（一杯）", 80, "一杯(300ml)", "饮品"),
        FoodItem("可乐（一罐）", 140, "一罐(330ml)", "饮品"),
        FoodItem("奶茶（一杯）", 350, "一杯(500ml)", "饮品"),
        FoodItem("咖啡（黑）", 5, "一杯(240ml)", "饮品"),
        FoodItem("咖啡（拿铁）", 190, "一杯(360ml)", "饮品"),
        FoodItem("果汁（一杯）", 120, "一杯(250ml)", "饮品"),
        FoodItem("啤酒（一瓶）", 150, "一瓶(330ml)", "饮品"),

        // === 零食 ===
        FoodItem("薯片（一小包）", 150, "一小包(30g)", "零食"),
        FoodItem("巧克力（一块）", 230, "一块(40g)", "零食"),
        FoodItem("坚果（一把）", 170, "一把(30g)", "零食"),
        FoodItem("饼干（5片）", 120, "5片", "零食"),
        FoodItem("蛋糕（一块）", 300, "一块(80g)", "零食"),
        FoodItem("冰淇淋（一个）", 200, "一个(100g)", "零食"),

        // === 蛋奶 ===
        FoodItem("鸡蛋（一个）", 78, "一个(60g)", "蛋奶"),
        FoodItem("煎蛋（一个）", 95, "一个", "蛋奶"),
        FoodItem("茶叶蛋（一个）", 85, "一个", "蛋奶"),
        FoodItem("奶酪（一片）", 110, "一片(30g)", "蛋奶"),

        // === 快餐 ===
        FoodItem("汉堡（一个）", 450, "一个", "快餐"),
        FoodItem("薯条（中份）", 380, "中份", "快餐"),
        FoodItem("炸鸡腿（一个）", 300, "一个", "快餐"),
        FoodItem("披萨（一块）", 280, "一块", "快餐"),
        FoodItem("麻辣烫（一碗）", 400, "一碗", "快餐"),
        FoodItem("螺蛳粉（一碗）", 450, "一碗", "快餐"),
        FoodItem("方便面（一碗）", 400, "一碗", "快餐"),
        FoodItem("盖浇饭（一份）", 550, "一份", "快餐"),
        FoodItem("炒饭（一份）", 500, "一份", "快餐"),
        FoodItem("炒面（一份）", 480, "一份", "快餐")
    )

    /** 按分类获取食物 */
    fun getFoodsByCategory(category: String): List<FoodItem> =
        foods.filter { it.category == category }

    /** 搜索食物 */
    fun searchFoods(query: String): List<FoodItem> =
        foods.filter { it.name.contains(query, ignoreCase = true) }

    /** 获取所有食物名称列表 */
    fun getAllFoodNames(): List<String> = foods.map { it.name }
}
