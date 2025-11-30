package com.example.mobile_integration_ca3

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
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
                    // Layout Exercise list on screen
                    ExerciseList(exercises = exercises, modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}

@Composable
fun ExerciseList(exercises: List<Exercise>, modifier: Modifier = Modifier) {
    LazyColumn(modifier = modifier.fillMaxSize()) {
        // Layout each Exercise on screen
        items(exercises) { exercise ->
            ExerciseCard(
                exercise = exercise,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            )
        }
    }
}

@Composable
fun ExerciseCard(exercise: Exercise, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = exercise.exercise_name, style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = exercise.exercise_description, style = MaterialTheme.typography.bodyMedium)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = exercise.image, style = MaterialTheme.typography.bodySmall)
            Text(text = "Body Part: ${exercise.body_part}", style = MaterialTheme.typography.bodySmall)
            Text(text = "Difficulty: ${exercise.difficulty}", style = MaterialTheme.typography.bodySmall)
            Text(text = "Needs Equipment: ${exercise.needs_equipment}", style = MaterialTheme.typography.bodySmall)
        }
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