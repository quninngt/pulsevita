package com.healthapp.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.healthapp.data.remote.HitokotoRepository
import com.healthapp.data.remote.NetworkResult
import com.healthapp.data.remote.WeatherRepository
import com.healthapp.data.repository.*
import com.healthapp.util.DateUtils
import com.healthapp.util.FoodKnowledgeProvider
import com.healthapp.util.HealthChallengeProvider
import com.healthapp.util.SolarTermUtil
import com.healthapp.ui.components.generateAchievements
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val waterRepository: WaterRepository,
    private val exerciseRepository: ExerciseRepository,
    private val moodRepository: MoodRepository,
    private val healthTipRepository: HealthTipRepository,
    private val userRepository: UserRepository,
    private val weatherRepository: WeatherRepository,
    private val hitokotoRepository: HitokotoRepository,
    private val serverRepository: ServerRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()
    private val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

    init {
        loadData()
    }

    fun clearError() {
        _uiState.update { it.copy(errorMessage = null) }
    }

    private fun loadData() {
        _uiState.update { it.copy(greeting = DateUtils.getGreeting()) }

        loadQuote()
        loadWeather()
        loadSolarTerm()
        loadDailyChallenge()
        loadDailyFood()
        loadWeeklyData()
        loadServerTip()
        loadServerChallenge()

        // Reactive local data
        waterRepository.getTodayTotalAmount().onEach { amount ->
            _uiState.update {
                it.copy(water = WaterState(amount, it.water.goal), isLoading = false)
            }
        }.launchIn(viewModelScope)

        exerciseRepository.getTodayTotalDuration().onEach { duration ->
            _uiState.update {
                it.copy(exercise = it.exercise.copy(duration = duration))
            }
        }.launchIn(viewModelScope)

        exerciseRepository.getTodayTotalSteps().onEach { steps ->
            _uiState.update {
                it.copy(exercise = it.exercise.copy(steps = steps))
            }
        }.launchIn(viewModelScope)

        moodRepository.getTodayRecords().onEach { records ->
            val latest = records.firstOrNull()
            _uiState.update {
                it.copy(mood = MoodState(latest?.moodLevel, latest?.moodIcon ?: ""))
            }
        }.launchIn(viewModelScope)

        healthTipRepository.getRandomTip().onEach { tip ->
            _uiState.update { it.copy(healthTip = tip) }
        }.launchIn(viewModelScope)

        userRepository.getCurrentUser().onEach { user ->
            _uiState.update { it.copy(userName = user?.name ?: "") }
        }.launchIn(viewModelScope)

        // Streak
        computeStreak()
    }

    private fun computeStreak() {
        combine(
            waterRepository.getAllDistinctDates(),
            exerciseRepository.getAllDistinctDates(),
            moodRepository.getAllDistinctDates()
        ) { waterDates, exerciseDates, moodDates ->
            val allDates = (waterDates + exerciseDates + moodDates).toSet()
            val dateSet = allDates.map { LocalDate.parse(it, dateFormatter) }.toSet()
            val streak = countConsecutiveDays(allDates)
            Triple(dateSet, streak, Triple(waterDates.size, exerciseDates.size, moodDates.size))
        }.onEach { (dateSet, streak, counts) ->
            val achievements = generateAchievements(
                streakDays = streak,
                totalWaterDays = counts.first,
                totalExerciseDays = counts.second,
                totalMoodDays = counts.third
            )
            _uiState.update {
                it.copy(
                    streakDays = streak,
                    streakDates = dateSet,
                    achievements = achievements
                )
            }
        }.launchIn(viewModelScope)
    }

    private fun countConsecutiveDays(dates: Set<String>): Int {
        if (dates.isEmpty()) return 0
        var streak = 0
        var current = java.time.LocalDate.now()
        val formatter = java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd")
        while (dates.contains(current.format(formatter))) {
            streak++
            current = current.minusDays(1)
        }
        return streak
    }

    /**
     * 加载最近7天的趋势数据
     */
    private fun loadWeeklyData() {
        viewModelScope.launch {
            val waterAmounts = waterRepository.getLast7DaysAmounts()
            _uiState.update { it.copy(weeklyWaterAmounts = waterAmounts) }
            calculateWeeklyOverview()
        }
        viewModelScope.launch {
            val exerciseMinutes = exerciseRepository.getLast7DaysMinutes()
            _uiState.update { it.copy(weeklyExerciseMinutes = exerciseMinutes) }
            calculateWeeklyOverview()
        }
        viewModelScope.launch {
            val moodLevels = moodRepository.getLast7DaysMoodLevels()
            _uiState.update { it.copy(weeklyMoodLevels = moodLevels) }
            calculateWeeklyOverview()
        }
    }

    /**
     * 计算周概览数据（达标天数和趋势）
     */
    private fun calculateWeeklyOverview() {
        val waterAmounts = _uiState.value.weeklyWaterAmounts
        val exerciseMinutes = _uiState.value.weeklyExerciseMinutes
        val moodLevels = _uiState.value.weeklyMoodLevels

        if (waterAmounts.isEmpty() || exerciseMinutes.isEmpty() || moodLevels.isEmpty()) return

        // 计算达标天数
        val waterGoal = _uiState.value.water.goal
        val exerciseGoal = _uiState.value.exercise.goal
        
        val waterDays = waterAmounts.count { it >= waterGoal }
        val exerciseDays = exerciseMinutes.count { it >= exerciseGoal }
        val moodDays = moodLevels.count { it != null && it > 0 }
        val dietDays = 7 // 饮食默认7天（暂无具体数据）

        // 计算趋势（与上周对比）
        val waterTrend = calculateTrend(waterAmounts)
        val exerciseTrend = calculateTrend(exerciseMinutes)
        val moodTrend = calculateMoodTrend(moodLevels)
        val dietTrend = "→" // 饮食暂无数据

        _uiState.update {
            it.copy(
                weeklyWaterDays = waterDays,
                weeklyExerciseDays = exerciseDays,
                weeklyMoodDays = moodDays,
                weeklyDietDays = dietDays,
                weeklyWaterTrend = waterTrend,
                weeklyExerciseTrend = exerciseTrend,
                weeklyMoodTrend = moodTrend,
                weeklyDietTrend = dietTrend
            )
        }
    }

    /**
     * 计算数值趋势
     */
    private fun calculateTrend(values: List<Int>): String {
        if (values.size < 2) return "→"
        val firstHalf = values.take(3).average()
        val secondHalf = values.takeLast(3).average()
        return when {
            secondHalf > firstHalf * 1.1 -> "↑"
            secondHalf < firstHalf * 0.9 -> "↓"
            else -> "→"
        }
    }

    /**
     * 计算心情趋势
     */
    private fun calculateMoodTrend(values: List<Int?>): String {
        val validValues = values.filterNotNull()
        if (validValues.size < 2) return "→"
        val firstHalf = validValues.take(3).average()
        val secondHalf = validValues.takeLast(3).average()
        return when {
            secondHalf > firstHalf + 0.3 -> "↑"
            secondHalf < firstHalf - 0.3 -> "↓"
            else -> "→"
        }
    }

    private fun loadQuote() {
        viewModelScope.launch {
            when (val result = hitokotoRepository.fetchQuote()) {
                is NetworkResult.Success -> {
                    _uiState.update {
                        it.copy(extras = it.extras.copy(
                            quote = result.data.text,
                            quoteFrom = result.data.from
                        ))
                    }
                }
                else -> {}
            }
        }
    }

    private fun loadWeather() {
        viewModelScope.launch {
            when (val result = weatherRepository.fetchWeather()) {
                is NetworkResult.Success -> {
                    _uiState.update {
                        it.copy(weather = WeatherState(
                            temperature = result.data.temperature,
                            weatherEmoji = WeatherRepository.getWeatherEmoji(result.data.weatherCode),
                            city = result.data.city,
                            solarTerm = it.weather.solarTerm,
                            solarTermTip = it.weather.solarTermTip
                        ))
                    }
                }
                else -> {}
            }
        }
    }

    private fun loadSolarTerm() {
        val term = SolarTermUtil.getCurrentSolarTerm()
        if (term != null) {
            _uiState.update {
                it.copy(weather = it.weather.copy(
                    solarTerm = term.name,
                    solarTermTip = term.healthTip
                ))
            }
        }
    }

    private fun loadDailyChallenge() {
        val challenge = HealthChallengeProvider.getTodayChallenge()
        _uiState.update {
            it.copy(extras = it.extras.copy(
                challengeTitle = challenge.title,
                challengeDesc = challenge.description,
                challengeIcon = challenge.icon
            ))
        }
    }

    private fun loadDailyFood() {
        val food = FoodKnowledgeProvider.getTodayFood()
        _uiState.update {
            it.copy(extras = it.extras.copy(
                foodName = food.name,
                foodProperty = food.property,
                foodPropertyDesc = food.propertyDesc,
                foodBenefits = food.benefits,
                foodHowToEat = food.howToEat
            ))
        }
    }

    private fun loadServerTip() {
        viewModelScope.launch {
            when (val result = serverRepository.getDailyTip()) {
                is NetworkResult.Success -> {
                    result.data?.let { tip ->
                        _uiState.update {
                            it.copy(
                                serverTip = tip.content,
                                serverTipTitle = tip.title
                            )
                        }
                    }
                }
                else -> {} // 静默失败，使用本地贴士
            }
        }
    }

    private fun loadServerChallenge() {
        viewModelScope.launch {
            val date = java.time.LocalDate.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd"))
            when (val result = serverRepository.getDailyChallenge(date)) {
                is NetworkResult.Success -> {
                    result.data?.let { challenge ->
                        _uiState.update {
                            it.copy(
                                serverChallenge = challenge.description,
                                serverChallengeTitle = challenge.title,
                                serverChallengeIcon = challenge.icon
                            )
                        }
                    }
                }
                else -> {} // 静默失败，使用本地挑战
            }
        }
    }
}
