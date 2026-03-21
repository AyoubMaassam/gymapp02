package com.gymapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gymapp.data.db.dao.PersonalRecord
import com.gymapp.data.db.dao.WeeklyVolume
import com.gymapp.data.db.entities.WorkoutSession
import com.gymapp.data.repository.WorkoutRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class ProgressViewModel @Inject constructor(
    private val workoutRepository: WorkoutRepository
) : ViewModel() {

    val totalSessions: StateFlow<Int> =
        workoutRepository.getTotalSessionsCount()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = 0
            )

    val totalDurationMinutes: StateFlow<Int> =
        workoutRepository.getTotalDurationMinutes()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = 0
            )

    val totalVolume: StateFlow<Float> =
        workoutRepository.getTotalVolume()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = 0f
            )

    val personalRecords: StateFlow<List<PersonalRecord>> =
        workoutRepository.getPersonalRecords()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = emptyList()
            )

    val recentSessions: StateFlow<List<WorkoutSession>> =
        workoutRepository.getRecentSessions()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = emptyList()
            )

    val weeklyVolume: StateFlow<List<WeeklyVolume>> =
        workoutRepository.getWeeklyVolume(getStartOf4WeeksAgo())
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = emptyList()
            )

    private fun getStartOf4WeeksAgo(): String {
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.WEEK_OF_YEAR, -4)
        calendar.set(Calendar.DAY_OF_WEEK, calendar.firstDayOfWeek)
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return sdf.format(calendar.time)
    }

    fun formatHours(minutes: Int): String {
        val hours = minutes / 60
        val mins = minutes % 60
        return if (hours > 0) "${hours}س ${mins}د" else "${mins}د"
    }

    fun formatVolume(volume: Float): String {
        return if (volume >= 1000) {
            "${"%.1f".format(volume / 1000)} طن"
        } else {
            "${volume.toInt()} كغ"
        }
    }

    fun formatDate(dateStr: String): String {
        return try {
            val inputSdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val outputSdf = SimpleDateFormat("d MMM", Locale("ar"))
            val date = inputSdf.parse(dateStr)
            outputSdf.format(date!!)
        } catch (e: Exception) {
            dateStr
        }
    }
}