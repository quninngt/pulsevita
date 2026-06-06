package com.healthapp.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "exercise_records")
data class ExerciseRecord(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val date: String,
    val type: String,          // walking/office_exercise/yoga
    val duration: Int,         // minutes
    val steps: Int? = null,
    val timestamp: Long = System.currentTimeMillis(),
    val note: String? = null
)
