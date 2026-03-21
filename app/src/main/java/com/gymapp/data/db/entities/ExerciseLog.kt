package com.gymapp.data.db.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "exercise_logs",
    foreignKeys = [
        ForeignKey(
            entity = WorkoutSession::class,
            parentColumns = ["id"],
            childColumns = ["sessionId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Exercise::class,
            parentColumns = ["id"],
            childColumns = ["exerciseId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["sessionId"]),
        Index(value = ["exerciseId"])
    ]
)
data class ExerciseLog(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val sessionId: Int = 0,
    val exerciseId: Int = 0,
    val setNumber: Int = 0,
    val weightKg: Float = 0f,
    val reps: Int = 0,
    val restSeconds: Int = 0,
    val feltDifficulty: Int = 0
)