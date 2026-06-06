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
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val waterRepository: WaterRepository,
    private val exerciseRepository: ExerciseRepository,
    private val moodRepository: MoodRepository,
    private val healthTipRepository: HealthTipRepository,
    private val userRepository: UserRepository,
    private val weatherRepository: WeatherRepository,
    private val hitokotoRepository: HitokotoRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

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
            countConsecutiveDays(allDates)
        }.onEach { streak ->
            _uiState.update { it.copy(streakDays = streak) }
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
}
