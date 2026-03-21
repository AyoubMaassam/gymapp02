package com.gymapp.data.remote

import android.util.Log
import com.gymapp.data.db.AppDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object ExerciseImageSync {

    // روابط صور حقيقية للتمارين من GitHub مباشرة
    private val exerciseImages = mapOf(
        1  to "https://raw.githubusercontent.com/yuhonas/free-exercise-db/main/exercises/Barbell_Bench_Press_-_Medium_Grip/0.jpg",
        2  to "https://raw.githubusercontent.com/yuhonas/free-exercise-db/main/exercises/Barbell_Incline_Bench_Press_-_Medium_Grip/0.jpg",
        3  to "https://raw.githubusercontent.com/yuhonas/free-exercise-db/main/exercises/Dumbbell_Bench_Press/0.jpg",
        4  to "https://raw.githubusercontent.com/yuhonas/free-exercise-db/main/exercises/Dumbbell_Flyes/0.jpg",
        5  to "https://raw.githubusercontent.com/yuhonas/free-exercise-db/main/exercises/Cable_Cross-Over/0.jpg",
        6  to "https://raw.githubusercontent.com/yuhonas/free-exercise-db/main/exercises/Push-Up/0.jpg",
        7  to "https://raw.githubusercontent.com/yuhonas/free-exercise-db/main/exercises/Dumbbell_Incline_Bench_Press/0.jpg",
        8  to "https://raw.githubusercontent.com/yuhonas/free-exercise-db/main/exercises/Chest_Dip/0.jpg",
        9  to "https://raw.githubusercontent.com/yuhonas/free-exercise-db/main/exercises/Wide-Grip_Lat_Pulldown/0.jpg",
        10 to "https://raw.githubusercontent.com/yuhonas/free-exercise-db/main/exercises/Seated_Cable_Row/0.jpg",
        11 to "https://raw.githubusercontent.com/yuhonas/free-exercise-db/main/exercises/Barbell_Deadlift/0.jpg",
        12 to "https://raw.githubusercontent.com/yuhonas/free-exercise-db/main/exercises/Barbell_Bent_Over_Row/0.jpg",
        13 to "https://raw.githubusercontent.com/yuhonas/free-exercise-db/main/exercises/Pull-Up/0.jpg",
        14 to "https://raw.githubusercontent.com/yuhonas/free-exercise-db/main/exercises/One-Arm_Dumbbell_Row/0.jpg",
        15 to "https://raw.githubusercontent.com/yuhonas/free-exercise-db/main/exercises/Face_Pull/0.jpg",
        16 to "https://raw.githubusercontent.com/yuhonas/free-exercise-db/main/exercises/Hyperextensions__Back_Extensions_/0.jpg",
        17 to "https://raw.githubusercontent.com/yuhonas/free-exercise-db/main/exercises/Dumbbell_Shoulder_Press/0.jpg",
        18 to "https://raw.githubusercontent.com/yuhonas/free-exercise-db/main/exercises/Side_Lateral_Raise/0.jpg",
        19 to "https://raw.githubusercontent.com/yuhonas/free-exercise-db/main/exercises/Front_Dumbbell_Raise/0.jpg",
        20 to "https://raw.githubusercontent.com/yuhonas/free-exercise-db/main/exercises/Dumbbell_Shrug/0.jpg",
        21 to "https://raw.githubusercontent.com/yuhonas/free-exercise-db/main/exercises/Rear_Delt_Fly/0.jpg",
        22 to "https://raw.githubusercontent.com/yuhonas/free-exercise-db/main/exercises/Barbell_Shoulder_Press/0.jpg",
        23 to "https://raw.githubusercontent.com/yuhonas/free-exercise-db/main/exercises/Machine_Shoulder__Military__Press/0.jpg",
        24 to "https://raw.githubusercontent.com/yuhonas/free-exercise-db/main/exercises/Barbell_Curl/0.jpg",
        25 to "https://raw.githubusercontent.com/yuhonas/free-exercise-db/main/exercises/Alternate_Incline_Dumbbell_Curl/0.jpg",
        26 to "https://raw.githubusercontent.com/yuhonas/free-exercise-db/main/exercises/Hammer_Curls/0.jpg",
        27 to "https://raw.githubusercontent.com/yuhonas/free-exercise-db/main/exercises/EZ-Bar_Preacher_Curl/0.jpg",
        28 to "https://raw.githubusercontent.com/yuhonas/free-exercise-db/main/exercises/Triceps_Pushdown/0.jpg",
        29 to "https://raw.githubusercontent.com/yuhonas/free-exercise-db/main/exercises/EZ-Bar_Skullcrusher/0.jpg",
        30 to "https://raw.githubusercontent.com/yuhonas/free-exercise-db/main/exercises/Triceps_Kickback/0.jpg",
        31 to "https://raw.githubusercontent.com/yuhonas/free-exercise-db/main/exercises/Triceps_Dip/0.jpg",
        32 to "https://raw.githubusercontent.com/yuhonas/free-exercise-db/main/exercises/Barbell_Squat/0.jpg",
        33 to "https://raw.githubusercontent.com/yuhonas/free-exercise-db/main/exercises/Leg_Press/0.jpg",
        34 to "https://raw.githubusercontent.com/yuhonas/free-exercise-db/main/exercises/Lying_Leg_Curls/0.jpg",
        35 to "https://raw.githubusercontent.com/yuhonas/free-exercise-db/main/exercises/Leg_Extensions/0.jpg",
        36 to "https://raw.githubusercontent.com/yuhonas/free-exercise-db/main/exercises/Romanian_Deadlift/0.jpg",
        37 to "https://raw.githubusercontent.com/yuhonas/free-exercise-db/main/exercises/Dumbbell_Lunges/0.jpg",
        38 to "https://raw.githubusercontent.com/yuhonas/free-exercise-db/main/exercises/Hack_Squat/0.jpg",
        39 to "https://raw.githubusercontent.com/yuhonas/free-exercise-db/main/exercises/Standing_Calf_Raises/0.jpg",
        40 to "https://raw.githubusercontent.com/yuhonas/free-exercise-db/main/exercises/Seated_Calf_Raise/0.jpg",
        41 to "https://raw.githubusercontent.com/yuhonas/free-exercise-db/main/exercises/Bodyweight_Squat/0.jpg",
        42 to "https://raw.githubusercontent.com/yuhonas/free-exercise-db/main/exercises/Crunch/0.jpg",
        43 to "https://raw.githubusercontent.com/yuhonas/free-exercise-db/main/exercises/Plank/0.jpg",
        44 to "https://raw.githubusercontent.com/yuhonas/free-exercise-db/main/exercises/Leg_Raises/0.jpg",
        45 to "https://raw.githubusercontent.com/yuhonas/free-exercise-db/main/exercises/Russian_Twist/0.jpg",
        46 to "https://raw.githubusercontent.com/yuhonas/free-exercise-db/main/exercises/Cable_Crunch/0.jpg",
        47 to "https://raw.githubusercontent.com/yuhonas/free-exercise-db/main/exercises/Jogging_Treadmill/0.jpg",
        48 to "https://raw.githubusercontent.com/yuhonas/free-exercise-db/main/exercises/Stationary_Bike_Run/0.jpg",
        49 to "https://raw.githubusercontent.com/yuhonas/free-exercise-db/main/exercises/Rowing_Stationary/0.jpg",
        50 to "https://raw.githubusercontent.com/yuhonas/free-exercise-db/main/exercises/Jumping_Jacks/0.jpg"
    )

    suspend fun syncGifUrls(db: AppDatabase) {
        withContext(Dispatchers.IO) {
            try {
                var updatedCount = 0
                exerciseImages.forEach { (exerciseId, imageUrl) ->
                    db.exerciseDao().updateGifUrl(exerciseId, imageUrl)
                    updatedCount++
                    Log.d("GymApp", "✓ id=$exerciseId url=$imageUrl")
                }
                Log.d("GymApp", "تم تحديث $updatedCount تمرين بصور حقيقية")
            } catch (e: Exception) {
                Log.e("GymApp", "خطأ: ${e.message}")
            }
        }
    }
}