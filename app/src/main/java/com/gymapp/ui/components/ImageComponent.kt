package com.gymapp.ui.components

import android.Manifest
import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.widget.Toast
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BrokenImage
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.ImageLoader
import coil.compose.AsyncImage
import coil.compose.AsyncImagePainter
import coil.compose.rememberAsyncImagePainter
import coil.disk.DiskCache
import coil.memory.MemoryCache
import coil.request.ImageRequest
import coil.request.SuccessResult
import com.gymapp.ui.theme.AccentRed
import com.gymapp.ui.theme.BackgroundSurface
import com.gymapp.ui.theme.TextSecondary
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.OutputStream

@Composable
fun ExerciseImage(
    url: String,
    modifier: Modifier = Modifier,
    showDownloadButton: Boolean = false
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var isLoading by remember { mutableStateOf(true) }
    var isError by remember { mutableStateOf(false) }

    val alpha by animateFloatAsState(
        targetValue = if (isLoading) 0f else 1f,
        animationSpec = tween(durationMillis = 600),
        label = "fade"
    )

    Box(modifier = modifier) {
        if (url.isEmpty() || isError) {
            PlaceholderBox(modifier = Modifier.fillMaxSize())
        } else {
            AsyncImage(
                model = ImageRequest.Builder(context)
                    .data(url)
                    .crossfade(true)
                    .diskCacheKey(url)
                    .memoryCacheKey(url)
                    .build(),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxSize()
                    .alpha(alpha),
                onState = { state ->
                    when (state) {
                        is AsyncImagePainter.State.Loading -> {
                            isLoading = true
                            isError = false
                        }
                        is AsyncImagePainter.State.Success -> {
                            isLoading = false
                            isError = false
                        }
                        is AsyncImagePainter.State.Error -> {
                            isLoading = false
                            isError = true
                        }
                        else -> {}
                    }
                }
            )

            if (isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(BackgroundSurface),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        color = AccentRed,
                        modifier = Modifier.size(36.dp),
                        strokeWidth = 3.dp
                    )
                }
            }

            if (showDownloadButton && !isLoading && !isError) {
                IconButton(
                    onClick = {
                        scope.launch {
                            downloadImage(context, url)
                        }
                    },
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(8.dp)
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(Color.Black.copy(alpha = 0.5f))
                ) {
                    Icon(
                        imageVector = Icons.Default.Download,
                        contentDescription = "تحميل",
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun PlaceholderBox(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.background(BackgroundSurface),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.Default.FitnessCenter,
            contentDescription = null,
            tint = TextSecondary.copy(alpha = 0.4f),
            modifier = Modifier.size(48.dp)
        )
    }
}

private suspend fun downloadImage(context: Context, url: String) {
    withContext(Dispatchers.IO) {
        try {
            val loader = ImageLoader(context)
            val request = ImageRequest.Builder(context).data(url).build()
            val result = loader.execute(request)
            if (result is SuccessResult) {
                val bitmap = (result.drawable as BitmapDrawable).bitmap
                saveImageToGallery(context, bitmap)
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "تم حفظ الصورة في المعرض ✓", Toast.LENGTH_SHORT).show()
                }
            }
        } catch (e: Exception) {
            withContext(Dispatchers.Main) {
                Toast.makeText(context, "فشل تحميل الصورة", Toast.LENGTH_SHORT).show()
            }
        }
    }
}

private fun saveImageToGallery(context: Context, bitmap: Bitmap) {
    val filename = "GymApp_${System.currentTimeMillis()}.jpg"
    val outputStream: OutputStream?

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, filename)
            put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
            put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + "/GymApp")
        }
        val uri = context.contentResolver.insert(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            contentValues
        )
        outputStream = uri?.let { context.contentResolver.openOutputStream(it) }
    } else {
        val imagesDir = Environment.getExternalStoragePublicDirectory(
            Environment.DIRECTORY_PICTURES
        ).toString() + "/GymApp"
        val dir = java.io.File(imagesDir)
        if (!dir.exists()) dir.mkdirs()
        val file = java.io.File(dir, filename)
        outputStream = java.io.FileOutputStream(file)
    }

    outputStream?.use {
        bitmap.compress(Bitmap.CompressFormat.JPEG, 90, it)
    }
}