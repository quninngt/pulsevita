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
    val selectedMoodIcon: String = "😐",
    val moodNote: String = "",
    val isBreathing: Boolean = false,
    val breathingPhase: String = "", // "inhale", "hold", "exhale"
    val breathingCount: Int = 0,
    val showBreathingExercise: Boolean = false,
    val emotionTips: List<EmotionTipGroup> = emptyList(),
    val quote: String = "",
    val quoteFrom: String = ""
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
        moodRepository.getRecentRecords(7).onEach { records ->
            _uiState.update { it.copy(recentMoods = records) }
        }.launchIn(viewModelScope)
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
