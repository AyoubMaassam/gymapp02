package com.gymapp.ui.screens.programs

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.gymapp.data.db.entities.ProgramExercise
import com.gymapp.data.db.entities.WorkoutDay
import com.gymapp.data.db.entities.WorkoutProgram
import com.gymapp.ui.components.ExerciseImage
import com.gymapp.ui.theme.AccentRed
import com.gymapp.ui.theme.BackgroundPrimary
import com.gymapp.viewmodel.ProgramViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProgramsScreen(
    viewModel: ProgramViewModel = hiltViewModel()
) {
    val programs by viewModel.programs.collectAsStateWithLifecycle()
    val selectedGoal by viewModel.selectedGoal.collectAsStateWithLifecycle()
    var showAddDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            Column(
                modifier = Modifier
                    .background(Color(0xFF1A1A1A).copy(alpha = 0.9f))
                    .padding(top = 48.dp, start = 16.dp, end = 16.dp, bottom = 8.dp)
            ) {
                // Search Bar
                OutlinedTextField(
                    value = "",
                    onValueChange = {},
                    placeholder = { Text("ابحث عن برنامج...", color = Color(0xFFA3A3A3)) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(50.dp))
                        .background(Color(0xFF2D2D2D)),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFFF97316),
                        unfocusedBorderColor = Color(0xFF404040)
                    ),
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = Color(0xFFA3A3A3)) }
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "البرامج",
                    fontSize = 30.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Tabs
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    viewModel.goals.forEach { goal ->
                        val isSelected = selectedGoal == goal
                        Button(
                            onClick = { viewModel.onGoalSelected(goal) },
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isSelected) Color(0xFF1A1A1A) else Color(0xFF2D2D2D)
                            ),
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                if (isSelected) Color(0xFFF97316) else Color(0xFF1A1A1A)
                            )
                        ) {
                            Text(
                                text = goal,
                                color = if (isSelected) Color(0xFFF97316) else Color(0xFFA3A3A3),
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true },
                containerColor = Color(0xFFF97316),
                contentColor = Color.White
            ) {
                Icon(Icons.Default.Add, contentDescription = "اضافة برنامج")
            }
        },
        containerColor = Color.Transparent
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(Color(0xFF1A1A1A), Color(0xFF2D2D2D))
                    )
                )
                .padding(padding)
        ) {
            if (programs.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("لا توجد برامج مضافة بعد", color = Color.Gray)
                }
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    contentPadding = PaddingValues(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(programs) { program ->
                        ProgramCard(program)
                    }
                }
            }
        }
    }

    if (showAddDialog) {
        AddProgramDialog(
            onDismiss = { showAddDialog = false },
            viewModel = viewModel
        )
    }
}

@Composable
fun ProgramCard(program: WorkoutProgram) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .border(1.dp, Color(0xFF404040), RoundedCornerShape(12.dp)),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF2D2D2D))
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(36.dp)
                    .background(Color.Black),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = program.name,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp)
                    .background(Color.Black)
            ) {
                ExerciseImage(
                    url = program.imageUrl,
                    modifier = Modifier.fillMaxSize()
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(Color(0xFF2D2D2D), Color(0xFF1A1A1A))
                        )
                    )
                    .padding(12.dp)
            ) {
                Text(
                    text = "المدة: ${program.durationWeeks} أسابيع",
                    color = Color(0xFFA3A3A3),
                    fontSize = 12.sp
                )
                Text(
                    text = "الشدة: ${program.intensity}",
                    color = Color(0xFFA3A3A3),
                    fontSize = 12.sp
                )
                Spacer(modifier = Modifier.height(12.dp))
                Button(
                    onClick = { /* Detail */ },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2D2D2D)),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFF97316).copy(alpha = 0.5f))
                ) {
                    Text("عرض التفاصيل", color = Color(0xFFF97316), fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }
            }
        }
    }
}

@Composable
fun AddProgramDialog(onDismiss: () -> Unit, viewModel: ProgramViewModel) {
    var name by remember { mutableStateOf("") }
    var goal by remember { mutableStateOf("تضخيم") }
    var duration by remember { mutableStateOf("4") }
    var intensity by remember { mutableStateOf("متوسط") }

    val exercises by viewModel.allExercises.collectAsStateWithLifecycle()

    // الأيام المضافة: List of (DayName, List of ProgramExercise)
    val days = remember { mutableStateListOf<Pair<String, MutableList<ProgramExercise>>>() }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("إضافة برنامج جديد", color = Color.White) },
        containerColor = Color(0xFF1A1A1A),
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("اسم البرنامج") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(unfocusedTextColor = Color.White, focusedTextColor = Color.White)
                )

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    // Goal Dropdown
                    Box(modifier = Modifier.weight(1f)) {
                        var expanded by remember { mutableStateOf(false) }
                        OutlinedButton(onClick = { expanded = true }, modifier = Modifier.fillMaxWidth()) {
                            Text(goal)
                        }
                        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                            listOf("تضخيم", "تنشيف", "قوة", "لياقتي").forEach {
                                DropdownMenuItem(text = { Text(it) }, onClick = { goal = it; expanded = false })
                            }
                        }
                    }

                    OutlinedTextField(
                        value = duration,
                        onValueChange = { duration = it },
                        label = { Text("المدة (أسابيع)") },
                        modifier = Modifier.weight(1f),
                        colors = OutlinedTextFieldDefaults.colors(unfocusedTextColor = Color.White, focusedTextColor = Color.White)
                    )
                }

                Divider(color = Color.Gray)

                // Days Section
                days.forEach { dayPair ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF2D2D2D))
                    ) {
                        Column(modifier = Modifier.padding(8.dp)) {
                            Text(text = dayPair.first, color = Color(0xFFF97316), fontWeight = FontWeight.Bold)

                            dayPair.second.forEach { progEx ->
                                val exerciseName = exercises.find { it.id == progEx.exerciseId }?.nameAr ?: "اختر تمرين"
                                Row(
                                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(text = exerciseName, modifier = Modifier.weight(1f), color = Color.White, fontSize = 12.sp)
                                    Text(text = "${progEx.sets}x${progEx.reps}", color = Color.Gray, fontSize = 12.sp)
                                }
                            }

                            Button(
                                onClick = {
                                    if (exercises.isNotEmpty()) {
                                        dayPair.second.add(
                                            ProgramExercise(
                                                dayId = 0,
                                                exerciseId = exercises.first().id,
                                                sets = 3,
                                                reps = "12",
                                                restSeconds = 60,
                                                order = dayPair.second.size
                                            )
                                        )
                                    }
                                },
                                modifier = Modifier.fillMaxWidth().height(32.dp),
                                contentPadding = PaddingValues(0.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Color.DarkGray)
                            ) {
                                Text("إضافة تمرين", fontSize = 10.sp)
                            }
                        }
                    }
                }

                Button(
                    onClick = {
                        days.add("اليوم ${days.size + 1}" to mutableStateListOf())
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF404040))
                ) {
                    Text("إضافة يوم")
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (name.isNotEmpty()) {
                        val finalDays = days.map { (dayName, exList) ->
                            WorkoutDay(
                                programId = 0,
                                dayName = dayName,
                                dayOrder = 0
                            ) to exList.toList()
                        }
                        viewModel.saveProgram(
                            name = name,
                            goal = goal,
                            duration = duration.toIntOrNull() ?: 4,
                            intensity = intensity,
                            days = finalDays
                        )
                        onDismiss()
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF97316))
            ) {
                Text("حفظ")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("إلغاء", color = Color.Gray)
            }
        }
    )
}