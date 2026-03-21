package com.gymapp.data.repository

import com.gymapp.data.db.dao.EquipmentDao
import com.gymapp.data.db.dao.ExerciseDao
import com.gymapp.data.db.entities.Equipment
import com.gymapp.data.db.entities.Exercise
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class EquipmentRepository @Inject constructor(
    private val equipmentDao: EquipmentDao,
    private val exerciseDao: ExerciseDao
) {
    fun getAllEquipment(): Flow<List<Equipment>> =
        equipmentDao.getAllEquipment()

    fun searchAndFilter(query: String, type: String): Flow<List<Equipment>> =
        equipmentDao.searchAndFilter(query, type)

    fun getEquipmentById(id: Int): Flow<Equipment?> =
        equipmentDao.getEquipmentById(id)

    fun getExercisesByEquipmentId(equipmentId: Int): Flow<List<Exercise>> =
        exerciseDao.getExercisesByEquipmentId(equipmentId)
}