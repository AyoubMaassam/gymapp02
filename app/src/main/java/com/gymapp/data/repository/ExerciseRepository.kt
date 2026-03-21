package com.gymapp.data.repository

import com.gymapp.data.db.dao.ExerciseDao
import com.gymapp.data.db.entities.Exercise
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ExerciseRepository @Inject constructor(
    private val exerciseDao: ExerciseDao
) {
    fun getAllExercises(): Flow<List<Exercise>> =
        exerciseDao.getAllExercises()

    fun getExercisesByCategory(category: String): Flow<List<Exercise>> =
        exerciseDao.getExercisesByCategory(category)

    fun searchAndFilter(query: String, category: String): Flow<List<Exercise>> =
        exerciseDao.searchAndFilter(query, category)

    fun getExerciseById(id: Int): Flow<Exercise?> =
        exerciseDao.getExerciseById(id)
}