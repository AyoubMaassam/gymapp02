package com.gymapp.data.repository

import android.util.Log
import com.gymapp.data.db.dao.ExerciseDao
import com.gymapp.data.db.entities.Exercise
import com.gymapp.data.remote.ExerciseDbService
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ExerciseRepository @Inject constructor(
    private val exerciseDao: ExerciseDao
) {
    suspend fun ensureExerciseGif(exercise: Exercise) {
        // إذا كان الرابط يحتوي على "exercisedb" أو ليس فارغاً ويبدو حقيقياً، قد لا نحتاج لتحديثه
        // لكن المطلب هو استخدام ExerciseDB API بدلاً من الصور المحلية
        // الروابط الحالية في Seeder هي picsum.photos أو روابط GitHub

        if (exercise.gifUrl.contains("exercisedb.p.rapidapi.com") || exercise.gifUrl.contains("giphy.com")) {
            return
        }

        try {
            val apiData = ExerciseDbService.fetchExerciseByName(exercise.nameEn)
            if (apiData != null && apiData.gifUrl.isNotEmpty()) {
                exerciseDao.updateGifUrl(exercise.id, apiData.gifUrl)
                Log.d("GymApp", "تم تحديث GIF للتمرين ${exercise.nameEn}: ${apiData.gifUrl}")
            }
        } catch (e: Exception) {
            Log.e("GymApp", "فشل تحديث GIF لـ ${exercise.nameEn}: ${e.message}")
        }
    }

    fun getAllExercises(): Flow<List<Exercise>> =
        exerciseDao.getAllExercises()

    fun getExercisesByCategory(category: String): Flow<List<Exercise>> =
        exerciseDao.getExercisesByCategory(category)

    fun searchAndFilter(query: String, category: String): Flow<List<Exercise>> =
        exerciseDao.searchAndFilter(query, category)

    fun getExerciseById(id: Int): Flow<Exercise?> =
        exerciseDao.getExerciseById(id)
}