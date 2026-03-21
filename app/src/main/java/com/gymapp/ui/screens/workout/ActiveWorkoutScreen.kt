package com.gymapp.ui.screens.workout

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.gymapp.data.db.entities.Exercise
import com.gymapp.ui.screens.exercises.getCategoryColor
import com.gymapp.ui.screens.exercises.getCategoryEmoji
import com.gymapp.ui.theme.AccentGold
import com.gymapp.ui.theme.AccentRed
import com.gymapp.ui.theme.BackgroundPrimary
import com.gymapp.ui.theme.BackgroundSurface
import com.gymapp.ui.theme.TextPrimary
import com.gymapp.ui.theme.TextSecondary
import com.gymapp.viewmodel.LogEntry
import com.gymapp.viewmodel.WorkoutViewModel

@Composable
fun ActiveWorkoutScreen(
    onWorkoutFinished: () -> Unit = {},
    viewModel: WorkoutViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val selectedExercise by viewModel.selectedExercise.collectAsStateWithLifecycle()
    val sessionLogs by viewModel.sessionLogs.collectAsStateWithLifecycle()
    val totalVolume by viewModel.totalVolume.collectAsStateWithLifecycle()
    val elapsedSeconds by viewModel.elapsedSeconds.collectAsStateWithLifecycle()
    val timerSeconds by viewModel.timerSeconds.collectAsStateWithLifecycle()
    val timerRunning by viewModel.timerRunning.collectAsStateWithLifecycle()
    val timerFinished by viewModel.timerFinished.collectAsStateWithLifecycle()
    val weightInput by viewModel.weightInput.collectAsStateWithLifecycle()
    val repsInput by viewModel.repsInput.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()

    var showFinishDialog by remember { mutableStateOf(false) }
    var showExercisePicker by remember { mutableStateOf(false) }

    // Vibrate when timer finishes
    LaunchedEffect(timerFinished) {
        if (timerFinished) {
            vibrate(context)
            viewModel.onTimerVibrateHandled()
        }
    }

    // Finish Dialog
    if (showFinishDialog) {
        AlertDialog(
            onDismissRequest = { showFinishDialog = false },
            containerColor = BackgroundSurface,
            title = {
                Text(
                    text = "إنهاء التمرين؟",
                    color = TextPrimary,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text(
                    text = "سيتم حفظ ${sessionLogs.size} جولة بإجمالي حجم ${totalVolume.toInt()} كيلو",
                    color = TextSecondary
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        showFinishDialog = false
                        viewModel.finishWorkout { onWorkoutFinished() }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = AccentRed)
                ) {
                    Text("حفظ وإنهاء", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { showFinishDialog = false }) {
                    Text("تراجع", color = TextSecondary)
                }
            }
        )
    }

    // Exercise Picker Dialog
    if (showExercisePicker) {
        ExercisePickerDialog(
            searchQuery = searchQuery,
            exercises = viewModel.getFilteredExercises(),
            onSearchChange = { viewModel.onSearchQueryChange(it) },
            onExerciseSelected = {
                viewModel.onExerciseSelected(it)
                showExercisePicker = false
            },
            onDismiss = { showExercisePicker = false }
        )
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundPrimary),
        contentPadding = PaddingValues(bottom = 100.dp)
    ) {
        // ═══ Header ═══
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(BackgroundSurface)
                    .padding(horizontal = 16.dp, vertical = 16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "التمرين النشط 🏋️",
                            color = TextPrimary,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = viewModel.formatElapsedTime(),
                            color = AccentGold,
                            fontSize = 14.sp
                        )
                    }
                    Button(
                        onClick = { showFinishDialog = true },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = AccentRed
                        ),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("إنهاء", color = Color.White, fontSize = 14.sp)
                    }
                }
            }
        }

        // ═══ Session Stats Bar ═══
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                MiniStatCard(
                    modifier = Modifier.weight(1f),
                    label = "الوقت",
                    value = viewModel.formatElapsedTime(),
                    color = AccentGold
                )
                MiniStatCard(
                    modifier = Modifier.weight(1f),
                    label = "الجولات",
                    value = sessionLogs.size.toString(),
                    color = AccentRed
                )
                MiniStatCard(
                    modifier = Modifier.weight(1f),
                    label = "الحجم (كغ)",
                    value = totalVolume.toInt().toString(),
                    color = Color(0xFF4CAF50)
                )
            }
        }

        // ═══ Exercise Picker ═══
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = BackgroundSurface)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "التمرين المختار",
                        color = TextSecondary,
                        fontSize = 13.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    if (selectedExercise == null) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .background(AccentRed.copy(alpha = 0.1f))
                                .border(
                                    width = 1.dp,
                                    color = AccentRed.copy(alpha = 0.4f),
                                    shape = RoundedCornerShape(10.dp)
                                )
                                .clickable { showExercisePicker = true }
                                .padding(14.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "+ اختر تمريناً",
                                color = AccentRed,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    } else {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .background(
                                    getCategoryColor(selectedExercise!!.category)
                                        .copy(alpha = 0.1f)
                                )
                                .clickable { showExercisePicker = true }
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = getCategoryEmoji(selectedExercise!!.category),
                                fontSize = 24.sp
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = selectedExercise!!.nameAr,
                                    color = TextPrimary,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = selectedExercise!!.category,
                                    color = TextSecondary,
                                    fontSize = 12.sp
                                )
                            }
                            Text(
                                text = "تغيير",
                                color = AccentRed,
                                fontSize = 12.sp
                            )
                        }
                    }
                }
            }
        }

        // ═══ Set Input ═══
        if (selectedExercise != null) {
            item {
                Spacer(modifier = Modifier.height(12.dp))
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = BackgroundSurface)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "تسجيل جولة جديدة",
                            color = TextPrimary,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            OutlinedTextField(
                                value = weightInput,
                                onValueChange = { viewModel.onWeightChange(it) },
                                label = { Text("الوزن (كغ)", color = TextSecondary) },
                                modifier = Modifier.weight(1f),
                                keyboardOptions = KeyboardOptions(
                                    keyboardType = KeyboardType.Decimal
                                ),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = AccentRed,
                                    unfocusedBorderColor = BackgroundPrimary,
                                    focusedTextColor = TextPrimary,
                                    unfocusedTextColor = TextPrimary,
                                    cursorColor = AccentRed
                                ),
                                singleLine = true
                            )
                            OutlinedTextField(
                                value = repsInput,
                                onValueChange = { viewModel.onRepsChange(it) },
                                label = { Text("التكرارات", color = TextSecondary) },
                                modifier = Modifier.weight(1f),
                                keyboardOptions = KeyboardOptions(
                                    keyboardType = KeyboardType.Number
                                ),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = AccentRed,
                                    unfocusedBorderColor = BackgroundPrimary,
                                    focusedTextColor = TextPrimary,
                                    unfocusedTextColor = TextPrimary,
                                    cursorColor = AccentRed
                                ),
                                singleLine = true
                            )
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        Button(
                            onClick = { viewModel.addSet() },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = AccentRed
                            ),
                            shape = RoundedCornerShape(10.dp),
                            enabled = weightInput.isNotEmpty() && repsInput.isNotEmpty()
                        ) {
                            Text(
                                text = "+ أضف جولة",
                                color = Color.White,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }

        // ═══ Rest Timer ═══
        item {
            Spacer(modifier = Modifier.height(12.dp))
            RestTimerCard(
                seconds = timerSeconds,
                isRunning = timerRunning,
                onStart = { viewModel.startTimer() },
                onPause = { viewModel.pauseTimer() },
                onReset = { viewModel.resetTimer(90) }
            )
        }

        // ═══ Session Logs ═══
        if (sessionLogs.isNotEmpty()) {
            item {
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "الجولات المسجلة",
                    color = TextPrimary,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
            items(sessionLogs.reversed()) { log ->
                LogRow(log = log)
            }
        }
    }
}

// ═══════════════ Helper Composables ═══════════════

@Composable
fun MiniStatCard(
    modifier: Modifier = Modifier,
    label: String,
    value: String,
    color: Color
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = BackgroundSurface)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = value,
                color = color,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = label,
                color = TextSecondary,
                fontSize = 11.sp,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun RestTimerCard(
    seconds: Int,
    isRunning: Boolean,
    onStart: () -> Unit,
    onPause: () -> Unit,
    onReset: () -> Unit
) {
    val progress by animateFloatAsState(
        targetValue = seconds / 90f,
        animationSpec = tween(500),
        label = "timer"
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = BackgroundSurface)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "مؤقت الراحة",
                color = TextSecondary,
                fontSize = 13.sp
            )
            Spacer(modifier = Modifier.height(16.dp))

            Box(contentAlignment = Alignment.Center) {
                CircularProgressIndicator(
                    progress = { progress },
                    modifier = Modifier.size(120.dp),
                    color = if (seconds <= 10) AccentRed else AccentGold,
                    trackColor = BackgroundPrimary,
                    strokeWidth = 8.dp,
                    strokeCap = StrokeCap.Round
                )
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "%02d:%02d".format(seconds / 60, seconds % 60),
                        color = TextPrimary,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = if (seconds == 0) "انتهى ✓" else "ثانية",
                        color = TextSecondary,
                        fontSize = 11.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Button(
                    onClick = { onReset() },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = BackgroundPrimary
                    ),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("إعادة", color = TextSecondary)
                }
                Button(
                    onClick = { if (isRunning) onPause() else onStart() },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isRunning) AccentGold else AccentRed
                    ),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.width(110.dp)
                ) {
                    Text(
                        text = if (isRunning) "⏸ إيقاف" else "▶ ابدأ",
                        color = Color.White
                    )
                }
            }
        }
    }
}

@Composable
fun LogRow(log: LogEntry) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = BackgroundSurface)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .clip(CircleShape)
                        .background(AccentRed.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = log.setNumber.toString(),
                        color = AccentRed,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = log.exerciseName,
                    color = TextPrimary,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium
                )
            }
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "${log.weightKg.toInt()} كغ",
                        color = AccentGold,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(text = "وزن", color = TextSecondary, fontSize = 10.sp)
                }
                Divider(
                    modifier = Modifier
                        .height(28.dp)
                        .width(1.dp),
                    color = BackgroundPrimary
                )
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "${log.reps}",
                        color = Color(0xFF4CAF50),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(text = "تكرار", color = TextSecondary, fontSize = 10.sp)
                }
            }
        }
    }
}

@Composable
fun ExercisePickerDialog(
    searchQuery: String,
    exercises: List<Exercise>,
    onSearchChange: (String) -> Unit,
    onExerciseSelected: (Exercise) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = BackgroundSurface,
        title = {
            Text(
                text = "اختر تمريناً",
                color = TextPrimary,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = onSearchChange,
                    placeholder = { Text("ابحث...", color = TextSecondary) },
                    leadingIcon = {
                        Icon(
                            Icons.Default.Search,
                            contentDescription = null,
                            tint = TextSecondary
                        )
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = AccentRed,
                        unfocusedBorderColor = BackgroundPrimary,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary,
                        cursorColor = AccentRed,
                        focusedContainerColor = BackgroundPrimary,
                        unfocusedContainerColor = BackgroundPrimary
                    ),
                    singleLine = true,
                    shape = RoundedCornerShape(10.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))
                LazyColumn(
                    modifier = Modifier.height(300.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    items(exercises) { exercise ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .clickable { onExerciseSelected(exercise) }
                                .padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = getCategoryEmoji(exercise.category),
                                fontSize = 18.sp
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = exercise.nameAr,
                                    color = TextPrimary,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Medium
                                )
                                Text(
                                    text = exercise.category,
                                    color = TextSecondary,
                                    fontSize = 11.sp
                                )
                            }
                        }
                        Divider(color = BackgroundPrimary, thickness = 0.5.dp)
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("إلغاء", color = TextSecondary)
            }
        }
    )
}

fun vibrate(context: Context) {
    try {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val manager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE)
                    as VibratorManager
            manager.defaultVibrator.vibrate(
                VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE)
            )
        } else {
            @Suppress("DEPRECATION")
            val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator.vibrate(
                    VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE)
                )
            } else {
                @Suppress("DEPRECATION")
                vibrator.vibrate(500)
            }
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }
}