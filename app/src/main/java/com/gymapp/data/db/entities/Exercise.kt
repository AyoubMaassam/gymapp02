package com.gymapp.data.db.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "exercises")
data class Exercise(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val nameAr: String = "",
    val nameEn: String = "",
    val category: String = "",
    val difficulty: String = "",
    val equipmentId: Int = 0,
    val primaryMuscles: String = "",
    val secondaryMuscles: String = "",
    val instructions: String = "",
    val tips: String = "",
    val commonMistakes: String = "",
    val setsRepsSuggestion: String = "",
    val imagePath: String = "",
    val imageUrl: String = "",
    val gifUrl: String = ""
)