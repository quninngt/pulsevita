package com.healthapp.di

import android.content.Context
import com.healthapp.data.local.AppDatabase
import com.healthapp.data.local.dao.*
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
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return AppDatabase.getDatabase(context)
    }

    @Provides
    @Singleton
    fun provideUserDao(database: AppDatabase): UserDao {
        return database.userDao()
    }

    @Provides
    @Singleton
    fun provideWaterRecordDao(database: AppDatabase): WaterRecordDao {
        return database.waterRecordDao()
    }

    @Provides
    @Singleton
    fun provideExerciseRecordDao(database: AppDatabase): ExerciseRecordDao {
        return database.exerciseRecordDao()
    }

    @Provides
    @Singleton
    fun provideMoodRecordDao(database: AppDatabase): MoodRecordDao {
        return database.moodRecordDao()
    }

    @Provides
    @Singleton
    fun provideDietRecordDao(database: AppDatabase): DietRecordDao {
        return database.dietRecordDao()
    }

    @Provides
    @Singleton
    fun provideHealthTipDao(database: AppDatabase): HealthTipDao {
        return database.healthTipDao()
    }
}
