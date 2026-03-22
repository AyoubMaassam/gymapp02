package com.gymapp.di

import android.content.Context
import com.gymapp.data.db.AppDatabase
import com.gymapp.data.db.dao.EquipmentDao
import com.gymapp.data.db.dao.ExerciseDao
import com.gymapp.data.db.dao.ProgramDao
import com.gymapp.data.db.dao.WorkoutDao
import com.gymapp.data.repository.EquipmentRepository
import com.gymapp.data.repository.ExerciseRepository
import com.gymapp.data.repository.ProgramRepository
import com.gymapp.data.repository.WorkoutRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase {
        return AppDatabase.getDatabase(context)
    }

    @Provides
    fun provideExerciseDao(db: AppDatabase): ExerciseDao = db.exerciseDao()

    @Provides
    fun provideEquipmentDao(db: AppDatabase): EquipmentDao = db.equipmentDao()

    @Provides
    fun provideWorkoutDao(db: AppDatabase): WorkoutDao = db.workoutDao()

    @Provides
    fun provideProgramDao(db: AppDatabase): ProgramDao = db.programDao()

    @Provides
    @Singleton
    fun provideExerciseRepository(exerciseDao: ExerciseDao): ExerciseRepository =
        ExerciseRepository(exerciseDao)

    @Provides
    @Singleton
    fun provideEquipmentRepository(
        equipmentDao: EquipmentDao,
        exerciseDao: ExerciseDao
    ): EquipmentRepository =
        EquipmentRepository(equipmentDao, exerciseDao)

    @Provides
    @Singleton
    fun provideWorkoutRepository(workoutDao: WorkoutDao): WorkoutRepository =
        WorkoutRepository(workoutDao)

    @Provides
    @Singleton
    fun provideProgramRepository(programDao: ProgramDao): ProgramRepository =
        ProgramRepository(programDao)
}