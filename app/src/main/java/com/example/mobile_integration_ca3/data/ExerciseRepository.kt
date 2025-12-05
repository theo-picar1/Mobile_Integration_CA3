package com.example.mobile_integration_ca3.data

import android.util.Log
private const val TAG = "ExerciseRepository"

/**
 * Repository class to handle data operations.
 * It provides a clean API for the ViewModel to fetch data.
 */
class ExerciseRepository(
    private val api: ExerciseApi
) {
    suspend fun fetchExercises(): List<Exercise> {
        return try {
            // Call the Retrofit service to get the data
            api.getExercises()
        } catch (e: Exception) {
            Log.e(TAG, "Failed to fetch exercises from API: ${e.message}")
            // Throw the exception or return an empty list/special error object
            throw e
        }
    }
}