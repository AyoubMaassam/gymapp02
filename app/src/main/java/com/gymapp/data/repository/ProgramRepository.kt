package com.gymapp.data.repository

import com.gymapp.data.db.dao.ProgramDao
import com.gymapp.data.db.entities.ProgramExercise
import com.gymapp.data.db.entities.WorkoutDay
import com.gymapp.data.db.entities.WorkoutProgram
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ProgramRepository @Inject constructor(
    private val programDao: ProgramDao
) {
    fun getAllPrograms(): Flow<List<WorkoutProgram>> = programDao.getAllPrograms()

    fun getProgramsByGoal(goal: String): Flow<List<WorkoutProgram>> =
        if (goal == "الكل" || goal.isEmpty()) programDao.getAllPrograms()
        else programDao.getProgramsByGoal(goal)

    suspend fun createProgram(
        program: WorkoutProgram,
        days: List<Pair<WorkoutDay, List<ProgramExercise>>>
    ) {
        val programId = programDao.insertProgram(program).toInt()
        days.forEach { (day, exercises) ->
            val dayId = programDao.insertDay(day.copy(programId = programId)).toInt()
            exercises.forEach { exercise ->
                programDao.insertProgramExercise(exercise.copy(dayId = dayId))
            }
        }
    }

    fun getProgramWithDays(programId: Int) = programDao.getProgramWithDays(programId)

    suspend fun deleteProgram(program: WorkoutProgram) = programDao.deleteProgram(program)

    suspend fun deleteExercise(exercise: ProgramExercise) = programDao.deleteExercise(exercise)

    suspend fun updateExercise(exercise: ProgramExercise) = programDao.updateExercise(exercise)

    suspend fun updateProgram(program: WorkoutProgram) = programDao.updateProgram(program)

    suspend fun addExerciseToDay(dayId: Int, exerciseId: Int, order: Int) {
        programDao.insertProgramExercise(
            ProgramExercise(
                dayId = dayId,
                exerciseId = exerciseId,
                sets = 3,
                reps = "12",
                weight = 0.0,
                restSeconds = 60,
                order = order
            )
        )
    }
}
