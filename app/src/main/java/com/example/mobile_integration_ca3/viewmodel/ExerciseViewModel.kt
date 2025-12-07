package com.example.mobile_integration_ca3.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mobile_integration_ca3.data.ExerciseRepository
import com.example.mobile_integration_ca3.data.Exercise
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class ExerciseViewModel(
    private val repository: ExerciseRepository
): ViewModel() {

    private val _uiState = MutableStateFlow<ExerciseUiState>(ExerciseUiState.Loading)
    val uiState = _uiState

    fun loadExercises() {
        viewModelScope.launch {
            _uiState.value = ExerciseUiState.Loading

            try {
                val data = repository.fetchExercises()
                _uiState.value = ExerciseUiState.Success(data)
            } catch (e: Exception) {
                val cached = repository.getExercisesFromDb()

                _uiState.value = if (cached.isNotEmpty()) {
                    ExerciseUiState.Success(cached)
                } else {
                    ExerciseUiState.Error("No data available")
                }
            }
        }
    }

    fun getExerciseByName(name: String): Exercise? {
        val currentState = uiState.value

        return if (currentState is ExerciseUiState.Success) {
            currentState.exercises.find { it.exercise_name == name }
        } else {
            null
        }
    }
}