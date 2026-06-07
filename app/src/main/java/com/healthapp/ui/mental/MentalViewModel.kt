package com.healthapp.ui.mental

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.healthapp.data.local.entity.MoodRecord
import com.healthapp.data.remote.HitokotoRepository
import com.healthapp.data.remote.NetworkResult
import com.healthapp.data.repository.MoodRepository
import com.healthapp.util.Constants
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import javax.inject.Inject

data class EmotionTipGroup(
    val title: String,
    val tips: List<String>
)

data class MentalUiState(
    val recentMoods: List<MoodRecord> = emptyList(),
    val showAddMoodDialog: Boolean = false,
    val selectedMoodLevel: Int = 3,
    val selectedMoodIcon: String = "\ud83d\ude10",
    val moodNote: String = "",
    val isBreathing: Boolean = false,
    val breathingPhase: String = "", // "inhale", "hold", "exhale"
    val breathingCount: Int = 0,
    val showBreathingExercise: Boolean = false,
    val emotionTips: List<EmotionTipGroup> = emptyList(),
    val quote: String = "",
    val quoteFrom: String = "",
    val weeklyMoodLevels: List<Int?> = emptyList(),
    // === 新增：心情统计数据 ===
    val weeklyAverage: Float = 0f,
    val bestDay: String = "",
    val worstDay: String = "",
    val trend: String = "stable" // "improving" / "stable" / "declining"
)

@HiltViewModel
class MentalViewModel @Inject constructor(
    private val moodRepository: MoodRepository,
    private val hitokotoRepository: HitokotoRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(MentalUiState())
    val uiState: StateFlow<MentalUiState> = _uiState.asStateFlow()

    private var breathingJob: Job? = null

    init {
        loadData()
        loadEmotionTips()
        loadQuote()
    }

    private fun loadData() {
        // Load weekly mood data
        viewModelScope.launch {
            val levels = moodRepository.getLast7DaysMoodLevels()
            _uiState.update { it.copy(weeklyMoodLevels = levels) }
            calculateMoodStatistics(levels)
        }

        moodRepository.getRecentRecords(7).onEach { records ->
            _uiState.update { it.copy(recentMoods = records) }
        }.launchIn(viewModelScope)
    }

    /**
     * 计算心情统计数据
     */
    private fun calculateMoodStatistics(levels: List<Int?>) {
        val validLevels = levels.filterNotNull()
        if (validLevels.isEmpty()) return

        val average = validLevels.average().toFloat()
        
        // 找到最好和最差的一天
        val days = listOf("周一", "周二", "周三", "周四", "周五", "周六", "周日")
        var bestDay = ""
        var worstDay = ""
        var bestLevel = 0
        var worstLevel = Int.MAX_VALUE
        
        levels.forEachIndexed { index, level ->
            if (level != null) {
                if (level > bestLevel) {
                    bestLevel = level
                    bestDay = days[index]
                }
                if (level < worstLevel) {
                    worstLevel = level
                    worstDay = days[index]
                }
            }
        }

        // 计算趋势
        val firstHalf = validLevels.take(3).average()
        val secondHalf = validLevels.takeLast(3).average()
        val trend = when {
            secondHalf > firstHalf + 0.3 -> "improving"
            secondHalf < firstHalf - 0.3 -> "declining"
            else -> "stable"
        }

        _uiState.update {
            it.copy(
                weeklyAverage = average,
                bestDay = bestDay,
                worstDay = worstDay,
                trend = trend
            )
        }
    }

    private fun loadEmotionTips() {
        _uiState.update { it.copy(emotionTips = listOf(
            EmotionTipGroup("感到焦虑时", listOf(
                "深呼吸，专注于当下的呼吸",
                "把担心的事情写下来，分析最坏结果",
                "做一些简单的运动，如散步或拉伸",
                "找信任的人聊聊，不要一个人扛着"
            )),
            EmotionTipGroup("情绪低落时", listOf(
                "允许自己有不好的情绪，不要压抑",
                "做一些让自己开心的小事，如听音乐、看电影",
                "整理房间或做家务，行动能改善心情",
                "早点睡觉，睡眠不足会加重负面情绪"
            )),
            EmotionTipGroup("压力大时", listOf(
                "把大任务分解成小步骤，一次只做一件事",
                "设定界限，学会说\"不\"",
                "安排休息时间，不要一直紧绷",
                "回忆过去成功克服困难的经历"
            ))
        )) }
    }

    private fun loadQuote() {
        viewModelScope.launch {
            when (val result = hitokotoRepository.fetchQuote()) {
                is NetworkResult.Success -> {
                    _uiState.update {
                        it.copy(quote = result.data.text, quoteFrom = result.data.from)
                    }
                }
                is NetworkResult.Error -> { /* non-critical */ }
                is NetworkResult.Loading -> {}
            }
        }
    }

    fun showAddMoodDialog() {
        _uiState.update { it.copy(showAddMoodDialog = true) }
    }

    fun hideAddMoodDialog() {
        _uiState.update {
            it.copy(
                showAddMoodDialog = false,
                selectedMoodLevel = 3,
                selectedMoodIcon = "😐",
                moodNote = ""
            )
        }
    }

    fun selectMood(level: Int, icon: String) {
        _uiState.update {
            it.copy(
                selectedMoodLevel = level,
                selectedMoodIcon = icon
            )
        }
    }

    fun updateMoodNote(note: String) {
        _uiState.update { it.copy(moodNote = note) }
    }

    fun saveMoodRecord() {
        val state = _uiState.value
        viewModelScope.launch {
            moodRepository.addMood(
                moodLevel = state.selectedMoodLevel,
                moodIcon = state.selectedMoodIcon,
                note = state.moodNote.ifBlank { null }
            )
            hideAddMoodDialog()
        }
    }

    fun deleteMoodRecord(record: MoodRecord) {
        viewModelScope.launch {
            moodRepository.deleteRecord(record)
        }
    }

    fun showBreathingExercise() {
        _uiState.update { it.copy(showBreathingExercise = true) }
    }

    fun hideBreathingExercise() {
        _uiState.update {
            it.copy(
                showBreathingExercise = false,
                isBreathing = false,
                breathingPhase = "",
                breathingCount = 0
            )
        }
    }

    fun startBreathing() {
        breathingJob?.cancel()
        breathingJob = viewModelScope.launch {
            _uiState.update { it.copy(isBreathing = true) }
            while (isActive) {
                // Inhale phase
                phaseLoop("inhale", Constants.BREATHING_INHALE_SECONDS)
                if (!isActive) break

                // Hold phase
                phaseLoop("hold", Constants.BREATHING_HOLD_SECONDS)
                if (!isActive) break

                // Exhale phase
                phaseLoop("exhale", Constants.BREATHING_EXHALE_SECONDS)
            }
        }
    }

    private suspend fun phaseLoop(phase: String, totalSeconds: Int) {
        _uiState.update { it.copy(breathingPhase = phase, breathingCount = totalSeconds) }
        for (i in (totalSeconds - 1) downTo 1) {
            delay(1000)
            _uiState.update { it.copy(breathingCount = i) }
        }
        delay(1000)
    }

    fun stopBreathing() {
        breathingJob?.cancel()
        breathingJob = null
        _uiState.update { it.copy(isBreathing = false, breathingPhase = "", breathingCount = 0) }
    }
}
