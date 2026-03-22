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

    suspend fun getProgramWithDays(programId: Int) = programDao.getProgramWithDays(programId)
}
