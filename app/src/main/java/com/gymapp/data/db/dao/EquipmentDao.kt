package com.gymapp.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.gymapp.data.db.entities.Equipment
import kotlinx.coroutines.flow.Flow

@Dao
interface EquipmentDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(equipment: Equipment)

    @Query("SELECT COUNT(*) FROM equipment")
    suspend fun getCount(): Int

    @Query("SELECT COUNT(*) FROM equipment")
    fun getTotalCount(): Flow<Int>

    @Query("SELECT * FROM equipment ORDER BY nameAr ASC")
    fun getAllEquipment(): Flow<List<Equipment>>

    @Query("""
        SELECT * FROM equipment 
        WHERE (nameAr LIKE '%' || :query || '%' OR nameEn LIKE '%' || :query || '%') 
        AND (:type = 'الكل' OR type = :type) 
        ORDER BY nameAr ASC
    """)
    fun searchAndFilter(query: String, type: String): Flow<List<Equipment>>

    @Query("SELECT * FROM equipment WHERE id = :id")
    fun getEquipmentById(id: Int): Flow<Equipment?>
}