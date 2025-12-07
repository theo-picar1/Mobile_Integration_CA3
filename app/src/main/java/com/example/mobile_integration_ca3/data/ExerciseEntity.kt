package com.example.mobile_integration_ca3.data

import androidx.room.Entity
import androidx.room.PrimaryKey

// Holds tables and their fields and types

@Entity(tableName = "exercises")
data class ExerciseEntity(
    @PrimaryKey val name: String,
    val description: String,
    val image: String,
    val bodyPart: String,
    val difficulty: String,
    val needsEquipment: Boolean
)