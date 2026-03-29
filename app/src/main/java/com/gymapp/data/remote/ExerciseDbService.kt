package com.gymapp.data.remote

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

data class ExerciseApiData(
    val id: String,
    val name: String,
    val gifUrl: String,
    val target: String,
    val equipment: String,
    val bodyPart: String
)

object ExerciseDbService {

    suspend fun fetchExerciseByName(name: String): ExerciseApiData? {
        return withContext(Dispatchers.IO) {
            try {
                // تنظيف الاسم للحصول على نتائج أفضل
                val cleanName = name.lowercase()
                    .replace("barbell ", "")
                    .replace("dumbbell ", "")
                    .replace("flat ", "")
                    .replace("seated ", "")
                    .trim()

                val encodedName = cleanName.replace(" ", "%20")
                val url = URL(
                    "${ApiConfig.BASE_URL}/exercises/name/$encodedName?limit=10&offset=0"
                )
                val connection = url.openConnection() as HttpURLConnection
                connection.requestMethod = "GET"
                connection.setRequestProperty("x-rapidapi-key", ApiConfig.RAPID_API_KEY)
                connection.setRequestProperty("x-rapidapi-host", ApiConfig.RAPID_API_HOST)
                connection.setRequestProperty("Content-Type", "application/json")
                connection.connectTimeout = 15000
                connection.readTimeout = 15000

                if (connection.responseCode == HttpURLConnection.HTTP_OK) {
                    val response = connection.inputStream.bufferedReader().readText()
                    val results = parseExercises(response)

                    // محاولة مطابقة أفضل نتيجة
                    val bestMatch = results.find { it.name.contains(name.lowercase()) }
                        ?: results.firstOrNull()

                    bestMatch
                } else {
                    val error = connection.errorStream?.bufferedReader()?.readText()
                    Log.e("GymApp", "خطأ API لبحث $name ($cleanName): ${connection.responseCode} — $error")
                    null
                }
            } catch (e: Exception) {
                Log.e("GymApp", "استثناء API لبحث $name: ${e.message}")
                null
            }
        }
    }

    suspend fun fetchAllExercises(): List<ExerciseApiData> {
        return withContext(Dispatchers.IO) {
            val allExercises = mutableListOf<ExerciseApiData>()
            val bodyParts = listOf(
                "chest", "back", "shoulders",
                "upper arms", "upper legs", "lower legs",
                "waist", "cardio"
            )
            bodyParts.forEach { part ->
                val exercises = fetchByBodyPart(part)
                allExercises.addAll(exercises)
                Log.d("GymApp", "جلبت ${exercises.size} من $part — أول gif: ${exercises.firstOrNull()?.gifUrl}")
            }
            Log.d("GymApp", "إجمالي: ${allExercises.size}")
            allExercises
        }
    }

    private suspend fun fetchByBodyPart(bodyPart: String): List<ExerciseApiData> {
        return withContext(Dispatchers.IO) {
            try {
                val encodedPart = bodyPart.replace(" ", "%20")
                val url = URL(
                    "${ApiConfig.BASE_URL}/exercises/bodyPart/$encodedPart?limit=50&offset=0"
                )
                val connection = url.openConnection() as HttpURLConnection
                connection.requestMethod = "GET"
                connection.setRequestProperty("x-rapidapi-key", ApiConfig.RAPID_API_KEY)
                connection.setRequestProperty("x-rapidapi-host", ApiConfig.RAPID_API_HOST)
                connection.setRequestProperty("Content-Type", "application/json")
                connection.connectTimeout = 15000
                connection.readTimeout = 15000

                if (connection.responseCode == HttpURLConnection.HTTP_OK) {
                    val response = connection.inputStream.bufferedReader().readText()
                    parseExercises(response)
                } else {
                    val error = connection.errorStream?.bufferedReader()?.readText()
                    Log.e("GymApp", "خطأ $bodyPart: ${connection.responseCode} — $error")
                    emptyList()
                }
            } catch (e: Exception) {
                Log.e("GymApp", "استثناء $bodyPart: ${e.message}")
                emptyList()
            }
        }
    }

    private fun parseExercises(json: String): List<ExerciseApiData> {
        val result = mutableListOf<ExerciseApiData>()
        try {
            val array = JSONArray(json)
            for (i in 0 until array.length()) {
                val obj: JSONObject = array.getJSONObject(i)
                val id = obj.optString("id", "")

                // بناء رابط الصورة باستخدام endpoint /image الجديد
                val gifUrl = "${ApiConfig.BASE_URL}/image?exerciseId=$id&resolution=360&rapidapi-key=${ApiConfig.RAPID_API_KEY}"

                result.add(
                    ExerciseApiData(
                        id = id,
                        name = obj.optString("name", ""),
                        gifUrl = gifUrl,
                        target = obj.optString("target", ""),
                        equipment = obj.optString("equipment", ""),
                        bodyPart = obj.optString("bodyPart", "")
                    )
                )
            }
        } catch (e: Exception) {
            Log.e("GymApp", "خطأ parsing: ${e.message}")
        }
        return result
    }
}