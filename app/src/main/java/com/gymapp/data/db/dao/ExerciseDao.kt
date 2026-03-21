package com.gymapp.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.gymapp.data.db.entities.Exercise
import kotlinx.coroutines.flow.Flow

@Dao
interface ExerciseDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(exercise: Exercise)

    @Query("SELECT COUNT(*) FROM exercises")
    suspend fun getCount(): Int

    @Query("SELECT COUNT(*) FROM exercises")
    fun getTotalCount(): Flow<Int>

    @Query("SELECT * FROM exercises ORDER BY nameAr ASC")
    fun getAllExercises(): Flow<List<Exercise>>

    @Query("SELECT * FROM exercises WHERE category = :category ORDER BY nameAr ASC")
    fun getExercisesByCategory(category: String): Flow<List<Exercise>>

    @Query("""
        SELECT * FROM exercises 
        WHERE (nameAr LIKE '%' || :query || '%' OR nameEn LIKE '%' || :query || '%') 
        AND (:category = 'الكل' OR category = :category) 
        ORDER BY nameAr ASC
    """)
    fun searchAndFilter(query: String, category: String): Flow<List<Exercise>>

    @Query("SELECT * FROM exercises WHERE id = :id")
    fun getExerciseById(id: Int): Flow<Exercise?>

    @Query("SELECT * FROM exercises WHERE equipmentId = :equipmentId ORDER BY nameAr ASC")
    fun getExercisesByEquipmentId(equipmentId: Int): Flow<List<Exercise>>

    @Query("UPDATE exercises SET gifUrl = :gifUrl, imageUrl = :gifUrl WHERE id = :id")
    suspend fun updateGifUrl(id: Int, gifUrl: String)
}