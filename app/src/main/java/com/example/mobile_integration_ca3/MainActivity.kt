package com.example.mobile_integration_ca3

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.mobile_integration_ca3.data.ExerciseRepository
import com.example.mobile_integration_ca3.data.Exercise
import com.example.mobile_integration_ca3.ui.theme.Mobile_Integration_CA3Theme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.lang.Boolean.toString

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        setContent {
            Mobile_Integration_CA3Theme {
                // Get exercises with fetch function in ExerciseRepo file
                val repository = ExerciseRepository(this)
                val exercises = repository.loadExercises()

                val navController = rememberNavController()

                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    NavHost(
                        navController = navController,
                        startDestination = "exercises",
                        modifier = Modifier.padding(innerPadding)
                    ) {
                        // Exercises screen (default screen)
                        composable("exercises") {
                            ExerciseList(
                                exercises = exercises,
                                onExerciseClick = { name ->
                                    navController.navigate("exercise/$name")
                                },
                                modifier = Modifier.padding(innerPadding)
                            )
                        }

                        // Specific exercise screen
                        composable("exercise/{name}") { backStackEntry ->
                            val name = backStackEntry.arguments?.getString("name")!!
                            val exercise = exercises.first { it.exercise_name == name }

                            ExerciseDetailScreen(exercise, navController)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ExerciseList(
    exercises: List<Exercise>,
    onExerciseClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(modifier = modifier.fillMaxSize()) {
        // Layout each Exercise on screen
        itemsIndexed(exercises) { index, exercise ->
            ExerciseCard(
                exercise = exercise,
                itemIndex = index,
                onExerciseClick = onExerciseClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            )
        }
    }
}

@Composable
fun ExerciseCard(
    exercise: Exercise,
    itemIndex: Int,
    onExerciseClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    // Track if card already visible (loaded)
    val isVisible = rememberSaveable { mutableStateOf(false) }

    val opacity = remember { Animatable(if (isVisible.value) 1f else 0f) }
    val offsetY = remember { Animatable(if (isVisible.value) 0f else 50f) }

    LaunchedEffect(key1 = exercise.exercise_name) {
        if (!isVisible.value) {
            delay(itemIndex % 10 * 100L)

            launch {
                opacity.animateTo(
                    targetValue = 1f,
                    animationSpec = tween(durationMillis = 500)
                )
            }
            launch {
                offsetY.animateTo(
                    targetValue = 0f,
                    animationSpec = tween(
                        durationMillis = 500,
                        easing = LinearEasing
                    )
                )
            }
            // Mark as visible so it doesn't animate again
            isVisible.value = true
        }
    }

    Card(
        modifier = modifier
            .graphicsLayer {
                alpha = opacity.value
                translationY = offsetY.value
            },
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = exercise.exercise_name,
                style = MaterialTheme.typography.titleMedium
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(text = "Image: ${exercise.image}", style = MaterialTheme.typography.bodySmall)
            Text(
                text = "Body Part: ${exercise.body_part}",
                style = MaterialTheme.typography.bodySmall
            )
            Text(
                text = "Difficulty: ${exercise.difficulty}",
                style = MaterialTheme.typography.bodySmall
            )
            Text(
                text = "Needs Equipment: ${exercise.needs_equipment}",
                style = MaterialTheme.typography.bodySmall
            )

            Spacer(modifier = Modifier.height(12.dp))

            Button(onClick = { onExerciseClick(exercise.exercise_name) }) {
                Text("View Details")
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExerciseDetailScreen(
    exercise: Exercise,
    navController: NavController
) {

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(exercise.exercise_name) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
        ) {

            // IMAGE CARD
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                ),
                elevation = CardDefaults.cardElevation(6.dp)
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Image: ${exercise.image}")
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // DETAILS CARD
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                ),
                elevation = CardDefaults.cardElevation(4.dp)
            ) {
                Column(Modifier.padding(20.dp)) {
                    Text("Description", style = MaterialTheme.typography.titleMedium)
                    Spacer(Modifier.height(6.dp))
                    Text(exercise.exercise_description)

                    Spacer(Modifier.height(16.dp))
                    Text("Details", style = MaterialTheme.typography.titleMedium)
                    Spacer(Modifier.height(10.dp))

                    DetailRow("Body Part", exercise.body_part)
                    DetailRow("Difficulty", exercise.difficulty)
                    DetailRow("Equipment Needed", toString(exercise.needs_equipment))
                }
            }
        }
    }
}

@Composable
fun DetailRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, style = MaterialTheme.typography.bodyMedium)
        Text(value, style = MaterialTheme.typography.bodyMedium)
    }
}