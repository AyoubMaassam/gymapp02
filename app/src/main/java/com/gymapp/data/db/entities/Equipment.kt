package com.gymapp.data.db.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "equipment")
data class Equipment(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val nameAr: String = "",
    val nameEn: String = "",
    val type: String = "",
    val imagePath: String = "",
    val setupInstructions: String = "",
    val safetyRules: String = "",
    val muscleGroups: String = ""
)