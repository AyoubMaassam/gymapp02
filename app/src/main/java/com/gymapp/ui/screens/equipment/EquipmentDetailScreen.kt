package com.gymapp.ui.screens.equipment

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.gymapp.data.db.entities.Exercise
import com.gymapp.ui.screens.exercises.getCategoryColor
import com.gymapp.ui.screens.exercises.getCategoryEmoji
import com.gymapp.ui.screens.exercises.parseJsonArray
import com.gymapp.ui.theme.AccentGold
import com.gymapp.ui.theme.AccentRed
import com.gymapp.ui.theme.BackgroundPrimary
import com.gymapp.ui.theme.BackgroundSurface
import com.gymapp.ui.theme.TextPrimary
import com.gymapp.ui.theme.TextSecondary
import com.gymapp.viewmodel.EquipmentViewModel

@Composable
fun EquipmentDetailScreen(
    equipmentId: Int,
    onBackClick: () -> Unit = {},
    onExerciseClick: (Int) -> Unit = {},
    viewModel: EquipmentViewModel = hiltViewModel()
) {
    val equipmentState = remember(equipmentId) {
        viewModel.getEquipmentById(equipmentId)
    }
    val equipment by equipmentState.collectAsStateWithLifecycle()

    val exercisesState = remember(equipmentId) {
        viewModel.getExercisesByEquipmentId(equipmentId)
    }
    val exercises by exercisesState.collectAsStateWithLifecycle()

    if (equipment == null) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(BackgroundPrimary),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(color = AccentRed)
        }
        return
    }

    val eq = equipment!!
    val setupInstructions = parseJsonArray(eq.setupInstructions)
    val safetyRules = parseJsonArray(eq.safetyRules)
    val muscleGroups = parseJsonArray(eq.muscleGroups)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundPrimary)
    ) {
        // Top Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(BackgroundSurface)
                .padding(horizontal = 8.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { onBackClick() }) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "رجوع",
                    tint = TextPrimary
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Text(
                    text = eq.nameAr,
                    color = TextPrimary,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = eq.nameEn,
                    color = TextSecondary,
                    fontSize = 13.sp
                )
            }
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(16.dp)
        ) {
            // Type Badge
            item {
                TypeBadge(
                    text = eq.type,
                    color = getTypeColor(eq.type)
                )
            }

            // Muscle Groups
            if (muscleGroups.isNotEmpty()) {
                item {
                    EquipmentSection(title = "العضلات المستهدفة", emoji = "💪") {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            muscleGroups.forEach { muscle ->
                                MuscleTag(text = muscle, color = AccentRed)
                            }
                        }
                    }
                }
            }

            // Setup Instructions
            if (setupInstructions.isNotEmpty()) {
                item {
                    EquipmentSection(title = "خطوات الإعداد", emoji = "🔧") {
                        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            setupInstructions.forEachIndexed { index, step ->
                                EquipmentInstructionRow(number = index + 1, text = step)
                            }
                        }
                    }
                }
            }

            // Safety Rules
            if (safetyRules.isNotEmpty()) {
                item {
                    EquipmentSection(title = "قواعد السلامة", emoji = "⚠️") {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            safetyRules.forEach { rule ->
                                EquipmentBulletRow(text = rule, color = AccentGold)
                            }
                        }
                    }
                }
            }

            // Exercises on this equipment
            if (exercises.isNotEmpty()) {
                item {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(top = 4.dp)
                    ) {
                        Text(text = "🏋️", fontSize = 18.sp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "التمارين على هذا الجهاز",
                            color = TextPrimary,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .background(AccentRed.copy(alpha = 0.15f))
                                .padding(horizontal = 8.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "${exercises.size}",
                                color = AccentRed,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                items(
                    items = exercises,
                    key = { it.id }
                ) { exercise ->
                    ExerciseRowCard(
                        exercise = exercise,
                        onClick = { onExerciseClick(exercise.id) }
                    )
                }
            }

            item { Spacer(modifier = Modifier.height(80.dp)) }
        }
    }
}

// ═══════════════ Helper Composables ═══════════════

@Composable
fun EquipmentSection(
    title: String,
    emoji: String,
    content: @Composable () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = BackgroundSurface)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = emoji, fontSize = 18.sp)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = title,
                    color = TextPrimary,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            Divider(
                modifier = Modifier.padding(vertical = 10.dp),
                color = BackgroundPrimary
            )
            content()
        }
    }
}

@Composable
fun EquipmentInstructionRow(number: Int, text: String) {
    Row(verticalAlignment = Alignment.Top) {
        Box(
            modifier = Modifier
                .size(26.dp)
                .clip(CircleShape)
                .background(AccentRed),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = number.toString(),
                color = Color.White,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
            )
        }
        Spacer(modifier = Modifier.width(10.dp))
        Text(
            text = text,
            color = TextPrimary,
            fontSize = 14.sp,
            lineHeight = 20.sp,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
fun EquipmentBulletRow(text: String, color: Color) {
    Row(verticalAlignment = Alignment.Top) {
        Box(
            modifier = Modifier
                .padding(top = 6.dp)
                .size(8.dp)
                .clip(CircleShape)
                .background(color)
        )
        Spacer(modifier = Modifier.width(10.dp))
        Text(
            text = text,
            color = TextPrimary,
            fontSize = 14.sp,
            lineHeight = 20.sp,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
fun MuscleTag(text: String, color: Color) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(color.copy(alpha = 0.15f))
            .padding(horizontal = 12.dp, vertical = 6.dp)
    ) {
        Text(
            text = text,
            color = color,
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
fun ExerciseRowCard(
    exercise: Exercise,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(
            containerColor = BackgroundPrimary
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(getCategoryColor(exercise.category).copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = getCategoryEmoji(exercise.category),
                    fontSize = 18.sp
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = exercise.nameAr,
                    color = TextPrimary,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = exercise.difficulty,
                    color = getDifficultyColor(exercise.difficulty),
                    fontSize = 12.sp
                )
            }
            Text(
                text = "›",
                color = TextSecondary,
                fontSize = 20.sp
            )
        }
    }
}

fun getDifficultyColor(difficulty: String): Color {
    return when (difficulty) {
        "مبتدئ" -> Color(0xFF4CAF50)
        "متوسط" -> AccentGold
        "محترف" -> AccentRed
        else -> TextSecondary
    }
}