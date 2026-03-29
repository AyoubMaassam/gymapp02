package com.gymapp.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.gymapp.ui.screens.equipment.EquipmentDetailScreen
import com.gymapp.ui.screens.equipment.EquipmentListScreen
import com.gymapp.ui.screens.exercises.ExerciseDetailScreen
import com.gymapp.ui.screens.exercises.ExerciseListScreen
import com.gymapp.ui.screens.home.HomeScreen
import com.gymapp.ui.screens.musclemap.MuscleMapScreen
import com.gymapp.ui.screens.profile.ProfileScreen
import com.gymapp.ui.screens.progress.ProgressScreen
import com.gymapp.ui.screens.programs.ProgramDetailScreen
import com.gymapp.ui.screens.programs.ProgramsScreen
import com.gymapp.ui.screens.workout.ActiveWorkoutScreen

@Composable
fun NavGraph(navController: NavHostController = rememberNavController()) {
    NavHost(
        navController = navController,
        startDestination = "home"
    ) {
        composable("home") {
            HomeScreen(
                onNavigateToExercises = {
                    navController.navigate("exercise_list")
                },
                onNavigateToEquipment = {
                    navController.navigate("equipment_list")
                },
                onNavigateToWorkout = {
                    navController.navigate("active_workout")
                }
            )
        }
        composable("exercise_list") {
            ExerciseListScreen(
                onExerciseClick = { exerciseId ->
                    navController.navigate("exercise_detail/$exerciseId")
                }
            )
        }
        composable(
            route = "exercise_detail/{exerciseId}",
            arguments = listOf(navArgument("exerciseId") { type = NavType.IntType })
        ) { backStackEntry ->
            val exerciseId = backStackEntry.arguments?.getInt("exerciseId") ?: 0
            ExerciseDetailScreen(
                exerciseId = exerciseId,
                onBackClick = { navController.popBackStack() }
            )
        }
        composable("equipment_list") {
            EquipmentListScreen(
                onEquipmentClick = { equipmentId ->
                    navController.navigate("equipment_detail/$equipmentId")
                }
            )
        }
        composable(
            route = "equipment_detail/{equipmentId}",
            arguments = listOf(navArgument("equipmentId") { type = NavType.IntType })
        ) { backStackEntry ->
            val equipmentId = backStackEntry.arguments?.getInt("equipmentId") ?: 0
            EquipmentDetailScreen(
                equipmentId = equipmentId,
                onBackClick = { navController.popBackStack() },
                onExerciseClick = { exerciseId ->
                    navController.navigate("exercise_detail/$exerciseId")
                }
            )
        }
        composable("muscle_map") {
            MuscleMapScreen()
        }
        composable("active_workout") {
            ActiveWorkoutScreen(
                onWorkoutFinished = {
                    navController.navigate("home") {
                        popUpTo("home") { inclusive = true }
                    }
                }
            )
        }
        composable("progress") {
            ProgressScreen()
        }
        composable("programs") {
            ProgramsScreen(
                onProgramClick = { programId ->
                    navController.navigate("program_detail/$programId")
                }
            )
        }
        composable(
            route = "program_detail/{programId}",
            arguments = listOf(navArgument("programId") { type = NavType.IntType })
        ) { backStackEntry ->
            val programId = backStackEntry.arguments?.getInt("programId") ?: 0
            ProgramDetailScreen(
                programId = programId,
                onBackClick = { navController.popBackStack() }
            )
        }
        composable("profile") {
            ProfileScreen()
        }
    }
}