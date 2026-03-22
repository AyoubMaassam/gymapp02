package com.gymapp.data.db.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(tableName = "workout_programs")
data class WorkoutProgram(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,
    val goal: String, // تضخيم، تنشيف، قوة، لياقة
    val durationWeeks: Int,
    val intensity: String = "متوسط",
    val imageUrl: String = ""
)

@Entity(
    tableName = "workout_days",
    foreignKeys = [
        ForeignKey(
            entity = WorkoutProgram::class,
            parentColumns = ["id"],
            childColumns = ["programId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class WorkoutDay(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val programId: Int,
    val dayName: String, // اليوم الأول، الصدر، إلخ
    val dayOrder: Int
)

@Entity(
    tableName = "program_exercises",
    foreignKeys = [
        ForeignKey(
            entity = WorkoutDay::class,
            parentColumns = ["id"],
            childColumns = ["dayId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Exercise::class,
            parentColumns = ["id"],
            childColumns = ["exerciseId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class ProgramExercise(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val dayId: Int,
    val exerciseId: Int,
    val sets: Int,
    val reps: String,
    val restSeconds: Int,
    val order: Int
)
