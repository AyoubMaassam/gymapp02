package com.gymapp.viewmodel

import android.os.CountDownTimer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gymapp.data.db.entities.Exercise
import com.gymapp.data.db.entities.ExerciseLog
import com.gymapp.data.db.entities.WorkoutSession
import com.gymapp.data.repository.ExerciseRepository
import com.gymapp.data.repository.WorkoutRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

data class LogEntry(
    val exerciseName: String,
    val setNumber: Int,
    val weightKg: Float,
    val reps: Int
)

@HiltViewModel
class WorkoutViewModel @Inject constructor(
    private val workoutRepository: WorkoutRepository,
    private val exerciseRepository: ExerciseRepository
) : ViewModel() {

    // ═══ Exercise Search ═══
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    val exercises: StateFlow<List<Exercise>> =
        exerciseRepository.getAllExercises()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = emptyList()
            )

    private val _selectedExercise = MutableStateFlow<Exercise?>(null)
    val selectedExercise: StateFlow<Exercise?> = _selectedExercise.asStateFlow()

    // ═══ Set Input ═══
    private val _weightInput = MutableStateFlow("")
    val weightInput: StateFlow<String> = _weightInput.asStateFlow()

    private val _repsInput = MutableStateFlow("")
    val repsInput: StateFlow<String> = _repsInput.asStateFlow()

    // ═══ Session Logs ═══
    private val _sessionLogs = MutableStateFlow<List<LogEntry>>(emptyList())
    val sessionLogs: StateFlow<List<LogEntry>> = _sessionLogs.asStateFlow()

    private val _totalVolume = MutableStateFlow(0f)
    val totalVolume: StateFlow<Float> = _totalVolume.asStateFlow()

    // ═══ Timer ═══
    private val _timerSeconds = MutableStateFlow(90)
    val timerSeconds: StateFlow<Int> = _timerSeconds.asStateFlow()

    private val _timerRunning = MutableStateFlow(false)
    val timerRunning: StateFlow<Boolean> = _timerRunning.asStateFlow()

    private val _timerFinished = MutableStateFlow(false)
    val timerFinished: StateFlow<Boolean> = _timerFinished.asStateFlow()

    private var countDownTimer: CountDownTimer? = null
    private var remainingSeconds = 90

    // ═══ Session Time ═══
    private val _elapsedSeconds = MutableStateFlow(0)
    val elapsedSeconds: StateFlow<Int> = _elapsedSeconds.asStateFlow()

    private var sessionTimer: CountDownTimer? = null
    private val _sessionStarted = MutableStateFlow(false)

    // ═══ Saved IDs for DB ═══
    private val dbLogs = mutableListOf<ExerciseLog>()
    private var setCounter = mutableMapOf<Int, Int>()

    init {
        startSessionTimer()
    }

    private fun startSessionTimer() {
        sessionTimer = object : CountDownTimer(Long.MAX_VALUE, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                _elapsedSeconds.value += 1
            }
            override fun onFinish() {}
        }
        sessionTimer?.start()
        _sessionStarted.value = true
    }

    // ═══ Exercise Selection ═══
    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
    }

    fun onExerciseSelected(exercise: Exercise) {
        _selectedExercise.value = exercise
        _weightInput.value = ""
        _repsInput.value = ""
    }

    fun onWeightChange(value: String) {
        if (value.matches(Regex("^\\d*\\.?\\d*$"))) {
            _weightInput.value = value
        }
    }

    fun onRepsChange(value: String) {
        if (value.matches(Regex("^\\d*$"))) {
            _repsInput.value = value
        }
    }

    // ═══ Add Set ═══
    fun addSet() {
        val exercise = _selectedExercise.value ?: return
        val weight = _weightInput.value.toFloatOrNull() ?: return
        val reps = _repsInput.value.toIntOrNull() ?: return
        if (reps <= 0) return

        val currentSet = (setCounter[exercise.id] ?: 0) + 1
        setCounter[exercise.id] = currentSet

        val log = LogEntry(
            exerciseName = exercise.nameAr,
            setNumber = currentSet,
            weightKg = weight,
            reps = reps
        )
        _sessionLogs.value = _sessionLogs.value + log
        _totalVolume.value += weight * reps

        dbLogs.add(
            ExerciseLog(
                sessionId = 0,
                exerciseId = exercise.id,
                setNumber = currentSet,
                weightKg = weight,
                reps = reps,
                restSeconds = 90 - remainingSeconds,
                feltDifficulty = 5
            )
        )

        startRestTimer()
        _weightInput.value = ""
        _repsInput.value = ""
    }

    // ═══ Timer Controls ═══
    fun startTimer() {
        if (_timerRunning.value) return
        _timerRunning.value = true
        _timerFinished.value = false

        countDownTimer = object : CountDownTimer(
            remainingSeconds * 1000L, 1000
        ) {
            override fun onTick(millisUntilFinished: Long) {
                remainingSeconds = (millisUntilFinished / 1000).toInt()
                _timerSeconds.value = remainingSeconds
            }

            override fun onFinish() {
                _timerSeconds.value = 0
                _timerRunning.value = false
                _timerFinished.value = true
                remainingSeconds = 0
            }
        }
        countDownTimer?.start()
    }

    fun pauseTimer() {
        countDownTimer?.cancel()
        _timerRunning.value = false
    }

    fun resetTimer(seconds: Int = 90) {
        countDownTimer?.cancel()
        remainingSeconds = seconds
        _timerSeconds.value = seconds
        _timerRunning.value = false
        _timerFinished.value = false
    }

    private fun startRestTimer() {
        resetTimer(90)
        startTimer()
    }

    fun onTimerVibrateHandled() {
        _timerFinished.value = false
    }

    // ═══ Finish Workout ═══
    fun finishWorkout(onDone: () -> Unit) {
        viewModelScope.launch {
            val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val today = sdf.format(Date())
            val durationMinutes = _elapsedSeconds.value / 60

            val session = WorkoutSession(
                date = today,
                durationMinutes = durationMinutes,
                totalVolume = _totalVolume.value,
                notes = ""
            )

            val sessionId = workoutRepository.insertSession(session).toInt()

            dbLogs.forEach { log ->
                workoutRepository.insertLog(log.copy(sessionId = sessionId))
            }

            sessionTimer?.cancel()
            countDownTimer?.cancel()
            onDone()
        }
    }

    fun getFilteredExercises(): List<Exercise> {
        val query = _searchQuery.value.trim()
        return if (query.isEmpty()) {
            exercises.value
        } else {
            exercises.value.filter {
                it.nameAr.contains(query, ignoreCase = true) ||
                        it.nameEn.contains(query, ignoreCase = true)
            }
        }
    }

    fun formatElapsedTime(): String {
        val seconds = _elapsedSeconds.value
        val minutes = seconds / 60
        val secs = seconds % 60
        return "%02d:%02d".format(minutes, secs)
    }

    override fun onCleared() {
        super.onCleared()
        sessionTimer?.cancel()
        countDownTimer?.cancel()
    }
}