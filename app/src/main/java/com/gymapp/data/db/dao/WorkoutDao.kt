package com.gymapp.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.gymapp.data.db.entities.ExerciseLog
import com.gymapp.data.db.entities.WorkoutSession
import kotlinx.coroutines.flow.Flow

@Dao
interface WorkoutDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertSession(session: WorkoutSession): Long

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertLog(log: ExerciseLog)

    @Query("SELECT COUNT(*) FROM workout_sessions")
    fun getTotalSessionsCount(): Flow<Int>

    @Query("SELECT COUNT(*) FROM workout_sessions WHERE date >= :startOfWeek")
    fun getSessionsCountThisWeek(startOfWeek: String): Flow<Int>

    @Query("SELECT * FROM workout_sessions ORDER BY date DESC LIMIT 10")
    fun getRecentSessions(): Flow<List<WorkoutSession>>

    @Query("SELECT * FROM exercise_logs WHERE sessionId = :sessionId ORDER BY id ASC")
    fun getLogsBySession(sessionId: Int): Flow<List<ExerciseLog>>

    @Query("SELECT COALESCE(SUM(durationMinutes), 0) FROM workout_sessions")
    fun getTotalDurationMinutes(): Flow<Int>

    @Query("SELECT COALESCE(SUM(totalVolume), 0) FROM workout_sessions")
    fun getTotalVolume(): Flow<Float>

    @Query("""
        SELECT e.nameAr, MAX(l.weightKg) as maxWeight
        FROM exercise_logs l
        INNER JOIN exercises e ON l.exerciseId = e.id
        GROUP BY l.exerciseId
        ORDER BY maxWeight DESC
        LIMIT 20
    """)
    fun getPersonalRecords(): Flow<List<PersonalRecord>>

    @Query("""
        SELECT date, SUM(totalVolume) as weekVolume
        FROM workout_sessions
        WHERE date >= :startDate
        GROUP BY strftime('%W-%Y', date)
        ORDER BY date ASC
    """)
    fun getWeeklyVolume(startDate: String): Flow<List<WeeklyVolume>>
}

data class PersonalRecord(
    val nameAr: String,
    val maxWeight: Float
)

data class WeeklyVolume(
    val date: String,
    val weekVolume: Float
)