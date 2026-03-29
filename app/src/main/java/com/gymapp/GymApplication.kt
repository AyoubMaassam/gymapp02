package com.gymapp

import android.app.Application
import android.util.Log
import android.os.Build
import coil.Coil
import coil.ImageLoader
import coil.decode.GifDecoder
import coil.decode.ImageDecoderDecoder
import coil.disk.DiskCache
import coil.memory.MemoryCache
import com.gymapp.data.db.AppDatabase
import com.gymapp.data.remote.ExerciseImageSync
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@HiltAndroidApp
class GymApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        setupCoil()
        // syncExerciseImages() // Removed bulk sync of hardcoded URLs
    }

    private fun setupCoil() {
        val imageLoader = ImageLoader.Builder(this)
            .components {
                if (Build.VERSION.SDK_INT >= 28) {
                    add(ImageDecoderDecoder.Factory())
                } else {
                    add(GifDecoder.Factory())
                }
            }
            .memoryCache {
                MemoryCache.Builder(this)
                    .maxSizePercent(0.25)
                    .build()
            }
            .diskCache {
                DiskCache.Builder()
                    .directory(cacheDir.resolve("image_cache"))
                    .maxSizeBytes(100L * 1024 * 1024)
                    .build()
            }
            .crossfade(true)
            .build()
        Coil.setImageLoader(imageLoader)
    }

    private fun syncExerciseImages() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val db = AppDatabase.getDatabase(applicationContext)
                ExerciseImageSync.syncGifUrls(db)
            } catch (e: Exception) {
                Log.e("GymApp", "خطأ: ${e.message}")
            }
        }
    }
}