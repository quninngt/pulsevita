package com.healthapp.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "diet_records")
data class DietRecord(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val date: String,
    val mealType: String,      // breakfast/lunch/dinner/snack
    val description: String,
    val calories: Int? = null,
    val timestamp: Long = System.currentTimeMillis()
)
