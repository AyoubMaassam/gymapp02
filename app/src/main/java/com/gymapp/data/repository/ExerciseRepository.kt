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
        // التحقق مما إذا كان الرابط هو رابط ExerciseDB الصحيح
        // نتحقق من وجود "rapidapi-key" في الرابط لأنه جزء من الـ URL الجديد الذي بنيناه
        if (exercise.gifUrl.contains("exercisedb.p.rapidapi.com") && exercise.gifUrl.contains("rapidapi-key")) {
            return
        }

        try {
            val apiData = ExerciseDbService.fetchExerciseByName(exercise.nameEn)
            if (apiData != null && apiData.gifUrl.isNotEmpty()) {
                exerciseDao.updateGifUrl(exercise.id, apiData.gifUrl)
                Log.d("GymApp", "تم تحديث GIF للتمرين ${exercise.nameEn}: ${apiData.gifUrl}")
            } else {
                Log.w("GymApp", "لم يتم العثور على نتيجة لـ ${exercise.nameEn}")
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