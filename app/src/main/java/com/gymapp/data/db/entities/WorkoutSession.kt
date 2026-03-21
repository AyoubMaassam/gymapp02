package com.gymapp.data.db.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "workout_sessions")
data class WorkoutSession(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val date: String = "",
    val durationMinutes: Int = 0,
    val totalVolume: Float = 0f,
    val notes: String = ""
)