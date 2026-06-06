package com.healthapp.data

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.healthapp.data.local.AppDatabase
import com.healthapp.data.local.dao.*
import com.healthapp.data.local.entity.*
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.Assert.*
import java.text.SimpleDateFormat
import java.util.*

@RunWith(AndroidJUnit4::class)
class DatabaseTest {

    private lateinit var database: AppDatabase
    private lateinit var userDao: UserDao
    private lateinit var waterRecordDao: WaterRecordDao
    private lateinit var exerciseRecordDao: ExerciseRecordDao
    private lateinit var moodRecordDao: MoodRecordDao
    private lateinit var dietRecordDao: DietRecordDao
    private lateinit var healthTipDao: HealthTipDao

    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    @Before
    fun setup() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            AppDatabase::class.java
        ).allowMainThreadQueries().build()

        userDao = database.userDao()
        waterRecordDao = database.waterRecordDao()
        exerciseRecordDao = database.exerciseRecordDao()
        moodRecordDao = database.moodRecordDao()
        dietRecordDao = database.dietRecordDao()
        healthTipDao = database.healthTipDao()
    }

    @After
    fun teardown() {
        database.close()
    }

    @Test
    fun insertAndRetrieveUser() = runBlocking {
        // Given
        val user = UserEntity(
            id = "test-user-1",
            name = "测试用户",
            gender = "male",
            birthDate = System.currentTimeMillis(),
            height = 175f,
            weight = 70f,
            occupation = "工程师"
        )

        // When
        userDao.insertUser(user)
        val retrieved = userDao.getCurrentUser().first()

        // Then
        assertNotNull(retrieved)
        assertEquals("测试用户", retrieved?.name)
        assertEquals("male", retrieved?.gender)
        assertEquals(175f, retrieved?.height)
        assertEquals(70f, retrieved?.weight)
    }

    @Test
    fun insertAndRetrieveWaterRecord() = runBlocking {
        // Given
        val today = dateFormat.format(Date())
        val record = WaterRecord(date = today, amount = 250)

        // When
        waterRecordDao.insertRecord(record)
        val records = waterRecordDao.getRecordsByDate(today).first()
        val total = waterRecordDao.getTotalAmountByDate(today).first()

        // Then
        assertEquals(1, records.size)
        assertEquals(250, records[0].amount)
        assertEquals(250, total)
    }

    @Test
    fun insertMultipleWaterRecords_calculatesTotal() = runBlocking {
        // Given
        val today = dateFormat.format(Date())
        val records = listOf(
            WaterRecord(date = today, amount = 250),
            WaterRecord(date = today, amount = 500),
            WaterRecord(date = today, amount = 100)
        )

        // When
        records.forEach { waterRecordDao.insertRecord(it) }
        val total = waterRecordDao.getTotalAmountByDate(today).first()

        // Then
        assertEquals(850, total)
    }

    @Test
    fun insertAndRetrieveExerciseRecord() = runBlocking {
        // Given
        val today = dateFormat.format(Date())
        val record = ExerciseRecord(
            date = today,
            type = "walking",
            duration = 30,
            steps = 5000
        )

        // When
        exerciseRecordDao.insertRecord(record)
        val records = exerciseRecordDao.getRecordsByDate(today).first()
        val totalDuration = exerciseRecordDao.getTotalDurationByDate(today).first()
        val totalSteps = exerciseRecordDao.getTotalStepsByDate(today).first()

        // Then
        assertEquals(1, records.size)
        assertEquals("walking", records[0].type)
        assertEquals(30, totalDuration)
        assertEquals(5000, totalSteps)
    }

    @Test
    fun insertAndRetrieveMoodRecord() = runBlocking {
        // Given
        val today = dateFormat.format(Date())
        val record = MoodRecord(
            date = today,
            moodLevel = 4,
            moodIcon = "😊",
            note = "今天心情不错"
        )

        // When
        moodRecordDao.insertRecord(record)
        val records = moodRecordDao.getRecordsByDate(today).first()

        // Then
        assertEquals(1, records.size)
        assertEquals(4, records[0].moodLevel)
        assertEquals("😊", records[0].moodIcon)
        assertEquals("今天心情不错", records[0].note)
    }

    @Test
    fun insertAndRetrieveDietRecord() = runBlocking {
        // Given
        val today = dateFormat.format(Date())
        val record = DietRecord(
            date = today,
            mealType = "breakfast",
            description = "牛奶+面包",
            calories = 350
        )

        // When
        dietRecordDao.insertRecord(record)
        val records = dietRecordDao.getRecordsByDate(today).first()

        // Then
        assertEquals(1, records.size)
        assertEquals("breakfast", records[0].mealType)
        assertEquals("牛奶+面包", records[0].description)
        assertEquals(350, records[0].calories)
    }

    @Test
    fun insertAndRetrieveHealthTip() = runBlocking {
        // Given
        val tip = HealthTip(
            category = "tcm",
            title = "春季养生",
            content = "春季宜养肝",
            season = "spring"
        )

        // When
        healthTipDao.insertTip(tip)
        val retrieved = healthTipDao.getRandomTipByCategory("tcm").first()

        // Then
        assertNotNull(retrieved)
        assertEquals("春季养生", retrieved?.title)
        assertEquals("tcm", retrieved?.category)
    }

    @Test
    fun deleteWaterRecord() = runBlocking {
        // Given
        val today = dateFormat.format(Date())
        val record = WaterRecord(date = today, amount = 250)
        waterRecordDao.insertRecord(record)

        // When
        waterRecordDao.deleteRecord(record)
        val records = waterRecordDao.getRecordsByDate(today).first()

        // Then
        assertEquals(0, records.size)
    }

    @Test
    fun updateUser() = runBlocking {
        // Given
        val user = UserEntity(
            id = "test-user-1",
            name = "原始名称",
            gender = "male",
            height = 170f,
            weight = 65f
        )
        userDao.insertUser(user)

        // When
        val updatedUser = user.copy(name = "更新后名称", weight = 70f)
        userDao.updateUser(updatedUser)
        val retrieved = userDao.getCurrentUser().first()

        // Then
        assertEquals("更新后名称", retrieved?.name)
        assertEquals(70f, retrieved?.weight)
    }
}
