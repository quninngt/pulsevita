package com.healthapp.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "health_tips")
data class HealthTip(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val category: String,      // diet/exercise/mental/tcm
    val title: String,
    val content: String,
    val season: String? = null // spring/summer/autumn/winter
)
