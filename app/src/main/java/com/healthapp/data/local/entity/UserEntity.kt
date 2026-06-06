package com.healthapp.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val name: String = "",
    val gender: String = "",        // male/female
    val birthDate: Long = 0L,       // timestamp
    val height: Float = 0f,         // cm
    val weight: Float = 0f,         // kg
    val occupation: String = "",
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)
