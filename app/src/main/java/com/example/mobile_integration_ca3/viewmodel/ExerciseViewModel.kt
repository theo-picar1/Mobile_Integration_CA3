package com.example.mobile_integration_ca3.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mobile_integration_ca3.data.Exercise
import com.example.mobile_integration_ca3.data.ExerciseRepository
import kotlinx.coroutines.launch

/**
 * Sealed class to represent the different states of the UI.
 */
sealed class ExerciseUiState {
    data object Loading : ExerciseUiState()
    data class Success(val exercises: List<Exercise>) : ExerciseUiState()
    data class Error(val message: String) : ExerciseUiState()
}

/**
 * ViewModel responsible for preparing and managing the data for the UI.
 * It uses the Repository to fetch data from the network.
 */
class ExerciseViewModel(private val repository: ExerciseRepository) : ViewModel() {

    // Mutable state that holds the current state of the UI (Loading, Success, or Error)
    var uiState: ExerciseUiState by mutableStateOf(ExerciseUiState.Loading)
        private set // Only allow changes from within the ViewModel

    init {
        // Automatically fetch data when the ViewModel is created
        loadExercises()
    }

    private fun loadExercises() {
        // Launch a coroutine in the scope of the ViewModel
        viewModelScope.launch {
            uiState = ExerciseUiState.Loading // Set state to loading

            try {
                val exercises = repository.fetchExercises()
                if (exercises.isNotEmpty()) {
                    uiState = ExerciseUiState.Success(exercises) // Set state to success
                } else {
                    uiState = ExerciseUiState.Error("API returned no exercises.")
                }
            } catch (e: Exception) {
                uiState = ExerciseUiState.Error("Network Error: Failed to load data.") // Set state to error
            }
        }
    }

    // Function to find a specific exercise by name for the detail screen
    fun getExerciseByName(name: String): Exercise? {
        return when (val state = uiState) {
            is ExerciseUiState.Success -> state.exercises.firstOrNull { it.exercise_name == name }
            else -> null
        }
    }
}