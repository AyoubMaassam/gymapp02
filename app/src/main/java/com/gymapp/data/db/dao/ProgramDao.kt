package com.gymapp.data.db.dao

import androidx.room.*
import com.gymapp.data.db.entities.ProgramExercise
import com.gymapp.data.db.entities.WorkoutDay
import com.gymapp.data.db.entities.WorkoutProgram
import kotlinx.coroutines.flow.Flow

@Dao
interface ProgramDao {

    @Query("SELECT * FROM workout_programs ORDER BY id DESC")
    fun getAllPrograms(): Flow<List<WorkoutProgram>>

    @Query("SELECT * FROM workout_programs WHERE goal = :goal ORDER BY id DESC")
    fun getProgramsByGoal(goal: String): Flow<List<WorkoutProgram>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProgram(program: WorkoutProgram): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDay(day: WorkoutDay): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProgramExercise(exercise: ProgramExercise)

    @Transaction
    @Query("SELECT * FROM workout_programs WHERE id = :programId")
    suspend fun getProgramWithDays(programId: Int): ProgramWithDays?
}

data class ProgramWithDays(
    @Embedded val program: WorkoutProgram,
    @Relation(
        parentColumn = "id",
        entityColumn = "programId",
        entity = WorkoutDay::class
    )
    val days: List<DayWithExercises>
)

data class DayWithExercises(
    @Embedded val day: WorkoutDay,
    @Relation(
        parentColumn = "id",
        entityColumn = "dayId"
    )
    val exercises: List<ProgramExercise>
)
