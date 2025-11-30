package com.example.mobile_integration_ca3

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.mobile_integration_ca3.data.ExerciseRepository
import com.example.mobile_integration_ca3.data.Exercise
import com.example.mobile_integration_ca3.ui.theme.Mobile_Integration_CA3Theme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Get exercises with fetch function in ExerciseRepo file
        val repository = ExerciseRepository(this)
        val exercises = repository.loadExercises()

        enableEdgeToEdge()
        setContent {
            Mobile_Integration_CA3Theme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    LazyColumn {
                        // Layout each Exercise on screen
                        items(exercises) { exercise ->
                            ExerciseCard(exercise = exercise, modifier = Modifier.padding(innerPadding))
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ExerciseCard(exercise: Exercise, modifier: Modifier = Modifier) {
    Column(modifier = modifier) {
        Text(text = "Name: ${exercise.exercise_name}")
        Text(text = "Description: ${exercise.exercise_description}")
        Text(text = "image: ${exercise.image}")
        Text(text = "body_part: ${exercise.body_part}")
        Text(text = "difficulty: ${exercise.difficulty}")
        Text(text = "needs_equipment: ${exercise.needs_equipment}")
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    Mobile_Integration_CA3Theme {
        Greeting("Android")
    }
}