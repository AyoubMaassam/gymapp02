package com.gymapp.ui.screens.programs

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
import com.gymapp.data.db.entities.ProgramExercise
import com.gymapp.viewmodel.ProgramViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProgramDetailScreen(
    programId: Int,
    onBackClick: () -> Unit,
    viewModel: ProgramViewModel = hiltViewModel()
) {
    val programWithDaysState = remember(programId) { viewModel.getProgramWithDays(programId) }
    val programWithDays by programWithDaysState.collectAsStateWithLifecycle()
    val allExercises by viewModel.allExercises.collectAsStateWithLifecycle()

    var showAddExerciseDialog by remember { mutableStateOf<Int?>(null) }
    var exerciseToEdit by remember { mutableStateOf<ProgramExercise?>(null) }

    if (programWithDays == null) {
        Box(modifier = Modifier.fillMaxSize().background(Color(0xFF1A1A1A)), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = Color(0xFFF97316))
        }
        return
    }

    val program = programWithDays!!.program
    val days = programWithDays!!.days

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(program.name, color = Color.White, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "رجوع", tint = Color.White)
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.deleteProgram(program); onBackClick() }) {
                        Icon(Icons.Default.Delete, contentDescription = "حذف", tint = Color.Red)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF1A1A1A))
            )
        },
        containerColor = Color(0xFF1A1A1A)
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(Brush.verticalGradient(listOf(Color(0xFF1A1A1A), Color(0xFF2D2D2D)))),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(days) { dayWithEx ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF2D2D2D)),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF404040))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = dayWithEx.day.dayName, color = Color(0xFFF97316), fontWeight = FontWeight.Bold, fontSize = 18.sp)
                            IconButton(onClick = { showAddExerciseDialog = dayWithEx.day.id }) {
                                Icon(Icons.Default.AddCircle, contentDescription = "إضافة تمرين", tint = Color(0xFFF97316))
                            }
                        }

                        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = Color(0xFF404040))

                        dayWithEx.exercises.forEach { progEx ->
                            val exercise = allExercises.find { it.id == progEx.exerciseId }
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 8.dp)
                                    .clickable { exerciseToEdit = progEx },
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(text = exercise?.nameAr ?: "تمرين غير معروف", color = Color.White, fontWeight = FontWeight.Medium)
                                    Text(text = "${progEx.sets} سيتات × ${progEx.reps} تكرار • ${progEx.weight} كجم", color = Color.Gray, fontSize = 12.sp)
                                }
                                IconButton(onClick = { viewModel.deleteExercise(progEx) }) {
                                    Icon(Icons.Default.Close, contentDescription = "إزالة", tint = Color.Gray, modifier = Modifier.size(16.dp))
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (showAddExerciseDialog != null) {
        AddExerciseToProgramDialog(
            exercises = allExercises,
            onDismiss = { showAddExerciseDialog = null },
            onConfirm = { exerciseId ->
                viewModel.addExerciseToDay(showAddExerciseDialog!!, exerciseId, 0)
                showAddExerciseDialog = null
            }
        )
    }

    if (exerciseToEdit != null) {
        EditProgramExerciseDialog(
            exercise = exerciseToEdit!!,
            exerciseName = allExercises.find { it.id == exerciseToEdit!!.exerciseId }?.nameAr ?: "",
            onDismiss = { exerciseToEdit = null },
            onConfirm = { updated ->
                viewModel.updateExercise(updated)
                exerciseToEdit = null
            }
        )
    }
}

@Composable
fun AddExerciseToProgramDialog(
    exercises: List<com.gymapp.data.db.entities.Exercise>,
    onDismiss: () -> Unit,
    onConfirm: (Int) -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    val filtered = exercises.filter { it.nameAr.contains(searchQuery) || it.nameEn.contains(searchQuery, ignoreCase = true) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("اختر تمرين", color = Color.White) },
        containerColor = Color(0xFF1A1A1A),
        text = {
            Column(modifier = Modifier.fillMaxWidth().heightIn(max = 400.dp)) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    label = { Text("بحث...") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                LazyColumn(modifier = Modifier.weight(1f)) {
                    items(filtered) { ex ->
                        Text(
                            text = ex.nameAr,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onConfirm(ex.id) }
                                .padding(vertical = 12.dp),
                            color = Color.White
                        )
                    }
                }
            }
        },
        confirmButton = {},
        dismissButton = { TextButton(onClick = onDismiss) { Text("إلغاء") } }
    )
}

@Composable
fun EditProgramExerciseDialog(
    exercise: ProgramExercise,
    exerciseName: String,
    onDismiss: () -> Unit,
    onConfirm: (ProgramExercise) -> Unit
) {
    var sets by remember { mutableStateOf(exercise.sets.toString()) }
    var reps by remember { mutableStateOf(exercise.reps) }
    var weight by remember { mutableStateOf(exercise.weight.toString()) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(exerciseName, color = Color.White) },
        containerColor = Color(0xFF1A1A1A),
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(value = sets, onValueChange = { sets = it }, label = { Text("السيتات") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = reps, onValueChange = { reps = it }, label = { Text("التكرارات") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = weight, onValueChange = { weight = it }, label = { Text("الوزن (كجم)") }, modifier = Modifier.fillMaxWidth())
            }
        },
        confirmButton = {
            Button(onClick = {
                onConfirm(exercise.copy(
                    sets = sets.toIntOrNull() ?: exercise.sets,
                    reps = reps,
                    weight = weight.toDoubleOrNull() ?: exercise.weight
                ))
            }, colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF97316))) {
                Text("تحديث")
            }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("إلغاء") } }
    )
}
