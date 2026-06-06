package com.healthapp.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "mood_records")
data class MoodRecord(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val date: String,
    val moodLevel: Int,        // 1-5 (很差-很好)
    val moodIcon: String,      // emoji or icon name
    val note: String? = null,
    val timestamp: Long = System.currentTimeMillis()
)
