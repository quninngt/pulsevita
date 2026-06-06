package com.healthapp.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.healthapp.data.local.dao.*
import com.healthapp.data.local.entity.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        UserEntity::class,
        WaterRecord::class,
        ExerciseRecord::class,
        MoodRecord::class,
        DietRecord::class,
        HealthTip::class
    ],
    version = 1,
    exportSchema = true
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun waterRecordDao(): WaterRecordDao
    abstract fun exerciseRecordDao(): ExerciseRecordDao
    abstract fun moodRecordDao(): MoodRecordDao
    abstract fun dietRecordDao(): DietRecordDao
    abstract fun healthTipDao(): HealthTipDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "health_app_database"
                )
                    .addCallback(DatabaseCallback())
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }

    private class DatabaseCallback : Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            INSTANCE?.let { database ->
                CoroutineScope(Dispatchers.IO).launch {
                    populateDatabase(database.healthTipDao())
                }
            }
        }

        suspend fun populateDatabase(healthTipDao: HealthTipDao) {
            val tips = listOf(
                HealthTip(
                    category = "tcm",
                    title = "春季养生",
                    content = "春季宜养肝，多吃绿色蔬菜，如菠菜、芹菜。早睡早起，适当户外运动，保持心情舒畅。",
                    season = "spring"
                ),
                HealthTip(
                    category = "tcm",
                    title = "夏季养生",
                    content = "夏季宜养心，多吃苦味食物，如苦瓜、莲子。避免过度出汗，午休有助于恢复精力。",
                    season = "summer"
                ),
                HealthTip(
                    category = "tcm",
                    title = "秋季养生",
                    content = "秋季宜养肺，多吃白色食物，如梨、银耳、百合。早睡早起，注意保暖，防止秋燥。",
                    season = "autumn"
                ),
                HealthTip(
                    category = "tcm",
                    title = "冬季养生",
                    content = "冬季宜养肾，多吃黑色食物，如黑豆、黑芝麻。早睡晚起，注意保暖，适当进补。",
                    season = "winter"
                ),
                HealthTip(
                    category = "diet",
                    title = "早餐的重要性",
                    content = "早餐是一天中最重要的一餐，建议包含蛋白质、碳水化合物和膳食纤维。一碗粥配鸡蛋和蔬菜是不错的选择。",
                    season = null
                ),
                HealthTip(
                    category = "diet",
                    title = "饮水小贴士",
                    content = "每天饮水量建议1500-2000ml，少量多次饮用。晨起一杯温水有助于肠胃蠕动，餐前30分钟饮水有助于消化。",
                    season = null
                ),
                HealthTip(
                    category = "exercise",
                    title = "办公室久坐提醒",
                    content = "每坐1小时，起身活动5分钟。可以做颈部旋转、肩部耸肩、腰部扭转等简单动作，预防颈椎和腰椎问题。",
                    season = null
                ),
                HealthTip(
                    category = "exercise",
                    title = "步行的好处",
                    content = "每天步行30分钟，可以促进血液循环，增强心肺功能。建议保持每分钟100-120步的频率，微微出汗为宜。",
                    season = null
                ),
                HealthTip(
                    category = "mental",
                    title = "深呼吸放松法",
                    content = "感到压力时，试试4-7-8呼吸法：吸气4秒，屏息7秒，呼气8秒。重复3-5次，有助于快速平静心情。",
                    season = null
                ),
                HealthTip(
                    category = "mental",
                    title = "情绪管理小技巧",
                    content = "当情绪低落时，不要压抑，可以写日记记录感受，或者找朋友倾诉。适当运动也能释放压力，改善心情。",
                    season = null
                )
            )
            healthTipDao.insertTips(tips)
        }
    }
}
