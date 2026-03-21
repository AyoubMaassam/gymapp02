package com.gymapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gymapp.data.db.dao.EquipmentDao
import com.gymapp.data.db.dao.ExerciseDao
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
class HomeViewModel @Inject constructor(
    private val workoutRepository: WorkoutRepository,
    private val exerciseDao: ExerciseDao,
    private val equipmentDao: EquipmentDao
) : ViewModel() {

    val totalExercises: StateFlow<Int> =
        exerciseDao.getTotalCount()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = 0
            )

    val totalEquipment: StateFlow<Int> =
        equipmentDao.getTotalCount()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = 0
            )

    val weeklySessionsCount: StateFlow<Int> =
        workoutRepository.getSessionsCountThisWeek(getStartOfWeek())
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = 0
            )

    private fun getStartOfWeek(): String {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.DAY_OF_WEEK, calendar.firstDayOfWeek)
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return sdf.format(calendar.time)
    }

    fun getCurrentDayAndDate(): Pair<String, String> {
        val calendar = Calendar.getInstance()
        val dayFormat = SimpleDateFormat("EEEE", Locale("ar"))
        val dateFormat = SimpleDateFormat("d MMMM yyyy", Locale("ar"))
        return Pair(
            dayFormat.format(calendar.time),
            dateFormat.format(calendar.time)
        )
    }

    fun getGreeting(): String {
        val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        return when (hour) {
            in 5..11 -> "صباح الخير 🌅"
            in 12..17 -> "مساء الخير ☀️"
            in 18..21 -> "مساء النور 🌆"
            else -> "أهلاً بك 🌙"
        }
    }
}