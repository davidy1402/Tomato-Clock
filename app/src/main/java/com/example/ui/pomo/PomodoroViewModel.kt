package com.example.ui.pomo

import android.app.Application
import android.os.CountDownTimer
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.audio.NoiseSynthesizer
import com.example.audio.NoiseType
import com.example.data.AppDatabase
import com.example.data.FocusSession
import com.example.data.FocusRepository
import com.example.ui.theme.SereneThemeType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class TimerMode(val displayName: String, val defaultMinutes: Int) {
    WORK("专注时间", 25),
    SHORT_BREAK("短休一下", 5),
    LONG_BREAK("长休放松", 15)
}

class PomodoroViewModel(
    application: Application,
    private val repository: FocusRepository
) : AndroidViewModel(application) {

    // Theme Management
    private val _currentTheme = MutableStateFlow(SereneThemeType.FOREST_DAWN)
    val currentTheme: StateFlow<SereneThemeType> = _currentTheme.asStateFlow()

    // Timer States
    private val _currentMode = MutableStateFlow(TimerMode.WORK)
    val currentMode: StateFlow<TimerMode> = _currentMode.asStateFlow()

    private val _timerMinutes = MutableStateFlow(TimerMode.WORK.defaultMinutes)
    val timerMinutes: StateFlow<Int> = _timerMinutes.asStateFlow()

    private val _timeLeftSeconds = MutableStateFlow(TimerMode.WORK.defaultMinutes * 60)
    val timeLeftSeconds: StateFlow<Int> = _timeLeftSeconds.asStateFlow()

    private val _isTimerRunning = MutableStateFlow(false)
    val isTimerRunning: StateFlow<Boolean> = _isTimerRunning.asStateFlow()

    // White Noise Manager
    private val _currentNoise = MutableStateFlow(NoiseType.NONE)
    val currentNoise: StateFlow<NoiseType> = _currentNoise.asStateFlow()

    private val _volume = MutableStateFlow(0.5f)
    val volume: StateFlow<Float> = _volume.asStateFlow()

    private val noiseSynthesizer = NoiseSynthesizer()
    private var countDownTimer: CountDownTimer? = null

    // Session Data
    val historySessions: StateFlow<List<FocusSession>> = repository.allSessions
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val totalMinutesFocused: StateFlow<Int?> = repository.totalMinutesFocused
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val completedCount: StateFlow<Int> = repository.completedSessionsCount
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    override fun onCleared() {
        super.onCleared()
        countDownTimer?.cancel()
        noiseSynthesizer.stop()
    }

    // Timer Actions
    fun setTimerMode(mode: TimerMode) {
        _currentMode.value = mode
        _timeLeftSeconds.value = mode.defaultMinutes * 60
        _timerMinutes.value = mode.defaultMinutes
        pauseTimer()
    }

    fun setCustomMinutes(minutes: Int) {
        val boundedMinutes = minutes.coerceIn(1, 120)
        _timerMinutes.value = boundedMinutes
        _timeLeftSeconds.value = boundedMinutes * 60
        pauseTimer()
    }

    fun startTimer() {
        if (_isTimerRunning.value) return

        _isTimerRunning.value = true

        countDownTimer = object : CountDownTimer(_timeLeftSeconds.value * 1000L, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                _timeLeftSeconds.value = (millisUntilFinished / 1000).toInt()
            }

            override fun onFinish() {
                _timeLeftSeconds.value = 0
                _isTimerRunning.value = false
                handleSessionCompleted()
            }
        }.start()

        // Auto start the selected white noise if one is selected when starting work
        if (_currentNoise.value != NoiseType.NONE) {
            noiseSynthesizer.start(_currentNoise.value)
        }
    }

    fun pauseTimer() {
        countDownTimer?.cancel()
        countDownTimer = null
        _isTimerRunning.value = false
    }

    fun resetTimer() {
        pauseTimer()
        _timeLeftSeconds.value = _timerMinutes.value * 60
    }

    private fun handleSessionCompleted() {
        viewModelScope.launch {
            val completedSession = FocusSession(
                durationMinutes = _timerMinutes.value,
                sessionType = _currentMode.value.displayName,
                whiteNoiseUsed = _currentNoise.value.displayName,
                themeUsed = _currentTheme.value.displayName,
                completed = true
            )
            repository.insert(completedSession)

            // Dynamic flow to short/long break automatically
            if (_currentMode.value == TimerMode.WORK) {
                setTimerMode(TimerMode.SHORT_BREAK)
            } else {
                setTimerMode(TimerMode.WORK)
            }
        }
    }

    // Audio Actions
    fun toggleWhiteNoise(type: NoiseType) {
        if (_currentNoise.value == type) {
            _currentNoise.value = NoiseType.NONE
            noiseSynthesizer.stop()
        } else {
            _currentNoise.value = type
            // Synthesizer will only play if either the timer is actively running, or we let the user preview it
            // Let's always run it immediately so users can play audio while working or breaking
            noiseSynthesizer.start(type)
        }
    }

    fun setNoiseVolume(vol: Float) {
        _volume.value = vol
        noiseSynthesizer.setVolume(vol)
    }

    // Theme Actions
    fun selectTheme(theme: SereneThemeType) {
        _currentTheme.value = theme
    }

    // Data Actions
    fun deleteHistoryItem(id: Int) {
        viewModelScope.launch {
            repository.deleteById(id)
        }
    }

    fun clearHistory() {
        viewModelScope.launch {
            repository.clearAll()
        }
    }
}

// ViewModel Factory
class PomodoroViewModelFactory(
    private val application: Application,
    private val repository: FocusRepository
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PomodoroViewModel::class.java)) {
            return PomodoroViewModel(application, repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
