package com.gymapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gymapp.data.db.entities.Exercise
import com.gymapp.data.db.entities.ProgramExercise
import com.gymapp.data.db.entities.WorkoutDay
import com.gymapp.data.db.entities.WorkoutProgram
import com.gymapp.data.repository.ExerciseRepository
import com.gymapp.data.repository.ProgramRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProgramViewModel @Inject constructor(
    private val repository: ProgramRepository,
    private val exerciseRepository: ExerciseRepository
) : ViewModel() {

    private val _selectedGoal = MutableStateFlow("الكل")
    val selectedGoal: StateFlow<String> = _selectedGoal.asStateFlow()

    val goals = listOf("الكل", "تضخيم", "تنشيف", "قوة", "لياقتي")

    @OptIn(ExperimentalCoroutinesApi::class)
    val programs: StateFlow<List<WorkoutProgram>> =
        _selectedGoal.flatMapLatest { goal ->
            repository.getProgramsByGoal(goal)
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val allExercises: StateFlow<List<Exercise>> =
        exerciseRepository.getAllExercises()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = emptyList()
            )

    fun onGoalSelected(goal: String) {
        _selectedGoal.value = goal
    }

    fun saveProgram(
        name: String,
        goal: String,
        duration: Int,
        intensity: String,
        days: List<Pair<WorkoutDay, List<ProgramExercise>>>
    ) {
        viewModelScope.launch {
            val program = WorkoutProgram(
                name = name,
                goal = goal,
                durationWeeks = duration,
                intensity = intensity
            )
            repository.createProgram(program, days)
        }
    }

    fun getProgramWithDays(programId: Int): StateFlow<com.gymapp.data.db.dao.ProgramWithDays?> =
        repository.getProgramWithDays(programId)
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = null
            )

    fun deleteProgram(program: WorkoutProgram) {
        viewModelScope.launch { repository.deleteProgram(program) }
    }

    fun deleteExercise(exercise: ProgramExercise) {
        viewModelScope.launch { repository.deleteExercise(exercise) }
    }

    fun updateExercise(exercise: ProgramExercise) {
        viewModelScope.launch { repository.updateExercise(exercise) }
    }

    fun addExerciseToDay(dayId: Int, exerciseId: Int, order: Int) {
        viewModelScope.launch { repository.addExerciseToDay(dayId, exerciseId, order) }
    }
}
