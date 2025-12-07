package com.example.mobile_integration_ca3.viewmodel

import com.example.mobile_integration_ca3.data.Exercise

sealed class ExerciseUiState {
    data object Loading : ExerciseUiState()
    data class Success(val exercises: List<Exercise>) : ExerciseUiState()
    data class Error(val message: String) : ExerciseUiState()
}