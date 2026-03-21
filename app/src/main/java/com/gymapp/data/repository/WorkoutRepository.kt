package com.gymapp.data.repository

import com.gymapp.data.db.dao.PersonalRecord
import com.gymapp.data.db.dao.WeeklyVolume
import com.gymapp.data.db.dao.WorkoutDao
import com.gymapp.data.db.entities.ExerciseLog
import com.gymapp.data.db.entities.WorkoutSession
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class WorkoutRepository @Inject constructor(
    private val workoutDao: WorkoutDao
) {
    fun getTotalSessionsCount(): Flow<Int> =
        workoutDao.getTotalSessionsCount()

    fun getSessionsCountThisWeek(startOfWeek: String): Flow<Int> =
        workoutDao.getSessionsCountThisWeek(startOfWeek)

    fun getRecentSessions(): Flow<List<WorkoutSession>> =
        workoutDao.getRecentSessions()

    suspend fun insertSession(session: WorkoutSession): Long =
        workoutDao.insertSession(session)

    suspend fun insertLog(log: ExerciseLog) =
        workoutDao.insertLog(log)

    fun getLogsBySession(sessionId: Int): Flow<List<ExerciseLog>> =
        workoutDao.getLogsBySession(sessionId)

    fun getTotalDurationMinutes(): Flow<Int> =
        workoutDao.getTotalDurationMinutes()

    fun getTotalVolume(): Flow<Float> =
        workoutDao.getTotalVolume()

    fun getPersonalRecords(): Flow<List<PersonalRecord>> =
        workoutDao.getPersonalRecords()

    fun getWeeklyVolume(startDate: String): Flow<List<WeeklyVolume>> =
        workoutDao.getWeeklyVolume(startDate)
}