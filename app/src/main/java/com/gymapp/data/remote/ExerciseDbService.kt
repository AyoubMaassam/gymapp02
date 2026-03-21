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
                    "https://exercisedb.p.rapidapi.com/exercises/bodyPart/$encodedPart?limit=50&offset=0"
                )
                val connection = url.openConnection() as HttpURLConnection
                connection.requestMethod = "GET"
                connection.setRequestProperty("x-rapidapi-key", "e0e653cf28mshf22b9df3a93071bp19d3eejsn99c658ec804b")
                connection.setRequestProperty("x-rapidapi-host", "exercisedb.p.rapidapi.com")
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

                // نطبع كل مفاتيح أول عنصر لمعرفة بنية الـ JSON
                if (i == 0) {
                    Log.d("GymApp", "مفاتيح JSON: ${obj.keys().asSequence().toList()}")
                    Log.d("GymApp", "قيم أول عنصر: $obj")
                }

                // نجرب كل الأسماء الممكنة للصورة
                val gifUrl = listOf(
                    obj.optString("gifUrl", ""),
                    obj.optString("gif_url", ""),
                    obj.optString("imageUrl", ""),
                    obj.optString("image_url", ""),
                    obj.optString("image", ""),
                    obj.optString("images", ""),
                    obj.optString("thumbnail", "")
                ).firstOrNull { it.isNotEmpty() && it.startsWith("http") } ?: ""

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