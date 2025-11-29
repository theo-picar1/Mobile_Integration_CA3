package com.example.mobile_integration_ca3.data

data class Exercise(
    val exercise_name: String,
    val exercise_description: String,
    val image: String,
    val body_part: String,
    val difficulty: String,
    val needs_equipment: Boolean
)