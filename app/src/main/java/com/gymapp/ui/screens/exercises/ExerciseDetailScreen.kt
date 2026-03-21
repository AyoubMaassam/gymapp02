package com.gymapp.ui.screens.exercises

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.gymapp.ui.components.ExerciseImage
import com.gymapp.ui.theme.AccentGold
import com.gymapp.ui.theme.AccentRed
import com.gymapp.ui.theme.BackgroundPrimary
import com.gymapp.ui.theme.BackgroundSurface
import com.gymapp.ui.theme.TextPrimary
import com.gymapp.ui.theme.TextSecondary
import com.gymapp.viewmodel.ExerciseViewModel
import org.json.JSONArray

@Composable
fun ExerciseDetailScreen(
    exerciseId: Int,
    onBackClick: () -> Unit = {},
    viewModel: ExerciseViewModel = hiltViewModel()
) {
    val exerciseState = remember(exerciseId) { viewModel.getExerciseById(exerciseId) }
    val exercise by exerciseState.collectAsStateWithLifecycle()

    if (exercise == null) {
        Box(
            modifier = Modifier.fillMaxSize().background(BackgroundPrimary),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(color = AccentRed)
        }
        return
    }

    val ex = exercise!!
    val instructions = parseJsonArray(ex.instructions)
    val primaryMuscles = parseJsonArray(ex.primaryMuscles)
    val secondaryMuscles = parseJsonArray(ex.secondaryMuscles)
    val tips = parseJsonArray(ex.tips)
    val commonMistakes = parseJsonArray(ex.commonMistakes)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundPrimary)
            .verticalScroll(rememberScrollState())
    ) {
        // Hero Image
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(260.dp)
        ) {
            ExerciseImage(
                url = ex.imageUrl.ifEmpty { ex.gifUrl },
                modifier = Modifier.fillMaxSize(),
                showDownloadButton = true
            )

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color.Black.copy(alpha = 0.4f),
                                Color.Transparent,
                                Color.Black.copy(alpha = 0.7f)
                            )
                        )
                    )
            )

            IconButton(
                onClick = { onBackClick() },
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(8.dp)
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(Color.Black.copy(alpha = 0.5f))
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "رجوع",
                    tint = Color.White
                )
            }

            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(16.dp)
            ) {
                Text(
                    text = ex.nameAr,
                    color = Color.White,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = ex.nameEn,
                    color = Color.White.copy(alpha = 0.8f),
                    fontSize = 14.sp
                )
            }
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                CategoryBadge(
                    text = ex.category,
                    color = getCategoryColor(ex.category)
                )
                DifficultyBadge(difficulty = ex.difficulty)
            }

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = AccentRed.copy(alpha = 0.12f))
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "🎯", fontSize = 24.sp)
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(text = "اقتراح السيتات", color = TextSecondary, fontSize = 12.sp)
                        Text(text = ex.setsRepsSuggestion, color = AccentRed, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            if (primaryMuscles.isNotEmpty()) {
                DetailSection(title = "العضلات الرئيسية", emoji = "💪") {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        primaryMuscles.forEach { muscle ->
                            MuscleChip(text = muscle, color = AccentRed)
                        }
                    }
                }
            }

            if (secondaryMuscles.isNotEmpty()) {
                DetailSection(title = "العضلات المساعدة", emoji = "🔧") {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        secondaryMuscles.forEach { muscle ->
                            MuscleChip(text = muscle, color = AccentGold)
                        }
                    }
                }
            }

            if (instructions.isNotEmpty()) {
                DetailSection(title = "خطوات الأداء", emoji = "📋") {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        instructions.forEachIndexed { index, step ->
                            InstructionRow(number = index + 1, text = step)
                        }
                    }
                }
            }

            if (tips.isNotEmpty()) {
                DetailSection(title = "نصائح مهمة", emoji = "💡") {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        tips.forEach { tip -> BulletRow(text = tip, color = AccentGold) }
                    }
                }
            }

            if (commonMistakes.isNotEmpty()) {
                DetailSection(title = "الأخطاء الشائعة", emoji = "⚠️") {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        commonMistakes.forEach { mistake -> BulletRow(text = mistake, color = AccentRed) }
                    }
                }
            }

            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}

@Composable
fun DetailSection(title: String, emoji: String, content: @Composable () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = BackgroundSurface)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = emoji, fontSize = 18.sp)
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = title, color = TextPrimary, fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
            Divider(modifier = Modifier.padding(vertical = 10.dp), color = BackgroundPrimary)
            content()
        }
    }
}

@Composable
fun InstructionRow(number: Int, text: String) {
    Row(verticalAlignment = Alignment.Top) {
        Box(
            modifier = Modifier.size(26.dp).clip(CircleShape).background(AccentRed),
            contentAlignment = Alignment.Center
        ) {
            Text(text = number.toString(), color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
        }
        Spacer(modifier = Modifier.width(10.dp))
        Text(text = text, color = TextPrimary, fontSize = 14.sp, lineHeight = 20.sp, modifier = Modifier.weight(1f))
    }
}

@Composable
fun BulletRow(text: String, color: Color) {
    Row(verticalAlignment = Alignment.Top) {
        Box(modifier = Modifier.padding(top = 6.dp).size(8.dp).clip(CircleShape).background(color))
        Spacer(modifier = Modifier.width(10.dp))
        Text(text = text, color = TextPrimary, fontSize = 14.sp, lineHeight = 20.sp, modifier = Modifier.weight(1f))
    }
}

@Composable
fun MuscleChip(text: String, color: Color) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(color.copy(alpha = 0.15f))
            .padding(horizontal = 12.dp, vertical = 6.dp)
    ) {
        Text(text = text, color = color, fontSize = 13.sp, fontWeight = FontWeight.Medium)
    }
}

fun parseJsonArray(json: String): List<String> {
    return try {
        val array = JSONArray(json)
        List(array.length()) { array.getString(it) }
    } catch (e: Exception) {
        emptyList()
    }
}