package com.gymapp.ui.screens.exercises

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
import com.gymapp.data.db.entities.Exercise
import com.gymapp.ui.components.ExerciseImage
import com.gymapp.ui.theme.AccentGold
import com.gymapp.ui.theme.AccentRed
import com.gymapp.ui.theme.BackgroundPrimary
import com.gymapp.ui.theme.BackgroundSurface
import com.gymapp.ui.theme.TextPrimary
import com.gymapp.ui.theme.TextSecondary
import com.gymapp.viewmodel.ExerciseViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExerciseListScreen(
    onExerciseClick: (Int) -> Unit = {},
    viewModel: ExerciseViewModel = hiltViewModel()
) {
    val exercises by viewModel.exercises.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val selectedCategory by viewModel.selectedCategory.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundPrimary)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(BackgroundSurface)
                .padding(horizontal = 16.dp, vertical = 16.dp)
        ) {
            Text(
                text = "التمارين",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
        }

        OutlinedTextField(
            value = searchQuery,
            onValueChange = { viewModel.onSearchQueryChange(it) },
            placeholder = { Text("ابحث عن تمرين...", color = TextSecondary) },
            leadingIcon = {
                Icon(Icons.Default.Search, contentDescription = null, tint = TextSecondary)
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = AccentRed,
                unfocusedBorderColor = BackgroundSurface,
                focusedContainerColor = BackgroundSurface,
                unfocusedContainerColor = BackgroundSurface,
                cursorColor = AccentRed,
                focusedTextColor = TextPrimary,
                unfocusedTextColor = TextPrimary
            ),
            singleLine = true
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            viewModel.categories.forEach { category ->
                FilterChip(
                    selected = selectedCategory == category,
                    onClick = { viewModel.onCategorySelected(category) },
                    label = { Text(category, fontSize = 13.sp) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = AccentRed,
                        selectedLabelColor = TextPrimary,
                        containerColor = BackgroundSurface,
                        labelColor = TextSecondary
                    ),
                    border = FilterChipDefaults.filterChipBorder(
                        enabled = true,
                        selected = selectedCategory == category,
                        selectedBorderColor = AccentRed,
                        borderColor = BackgroundSurface
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = "${exercises.size} تمرين",
            color = TextSecondary,
            fontSize = 13.sp,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
        )

        if (exercises.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = AccentRed)
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
            ) {
                items(exercises, key = { it.id }) { exercise ->
                    ExerciseCardWithImage(
                        exercise = exercise,
                        onClick = { onExerciseClick(exercise.id) }
                    )
                }
            }
        }
    }
}

@Composable
fun ExerciseCardWithImage(
    exercise: Exercise,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = BackgroundSurface),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
        ) {
            ExerciseImage(
                url = exercise.imageUrl,
                modifier = Modifier.fillMaxSize()
            )

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color.Transparent,
                                Color.Black.copy(alpha = 0.85f)
                            ),
                            startY = 60f
                        )
                    )
            )

            DifficultyBadge(
                difficulty = exercise.difficulty,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(10.dp)
            )

            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(14.dp)
            ) {
                Text(
                    text = exercise.nameAr,
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    CategoryBadge(
                        text = exercise.category,
                        color = getCategoryColor(exercise.category)
                    )
                }
            }
        }
    }
}

@Composable
fun DifficultyBadge(
    difficulty: String,
    modifier: Modifier = Modifier
) {
    val color = when (difficulty) {
        "مبتدئ" -> Color(0xFF4CAF50)
        "متوسط" -> AccentGold
        "محترف" -> AccentRed
        else -> TextSecondary
    }
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(color.copy(alpha = 0.9f))
            .padding(horizontal = 10.dp, vertical = 4.dp)
    ) {
        Text(
            text = difficulty,
            color = Color.White,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun CategoryBadge(text: String, color: Color) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .background(color.copy(alpha = 0.2f))
            .padding(horizontal = 8.dp, vertical = 3.dp)
    ) {
        Text(
            text = text,
            color = color,
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

fun getCategoryColor(category: String): Color {
    return when (category) {
        "صدر" -> Color(0xFFE94560)
        "ظهر" -> Color(0xFF2196F3)
        "كتف" -> Color(0xFF9C27B0)
        "ذراع" -> Color(0xFFFF9800)
        "ساق" -> Color(0xFF4CAF50)
        "بطن" -> Color(0xFF00BCD4)
        "كارديو" -> Color(0xFFF5A623)
        else -> Color(0xFF8B8FA8)
    }
}

fun getCategoryEmoji(category: String): String {
    return when (category) {
        "صدر" -> "💪"
        "ظهر" -> "🔙"
        "كتف" -> "🏋️"
        "ذراع" -> "💪"
        "ساق" -> "🦵"
        "بطن" -> "🎯"
        "كارديو" -> "🏃"
        else -> "💪"
    }
}