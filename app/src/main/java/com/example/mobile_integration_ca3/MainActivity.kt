package com.example.mobile_integration_ca3

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.mobile_integration_ca3.data.RetrofitClient
import com.example.mobile_integration_ca3.data.Exercise
import com.example.mobile_integration_ca3.data.ExerciseRepository
import com.example.mobile_integration_ca3.ui.theme.Mobile_Integration_CA3Theme
import com.example.mobile_integration_ca3.viewmodel.ExerciseUiState
import com.example.mobile_integration_ca3.viewmodel.ExerciseViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.lang.Boolean.toString
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.ui.draw.clip
import androidx.compose.material.icons.filled.Image
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import coil.compose.AsyncImage

// Tag for logging in MainActivity and related functions
private const val TAG = "MainActivity"

// WINDOW SIZE CLASS DEFINITION

enum class WindowWidthClass { Compact, Medium, Expanded }

/**
 * Calculates the current window width class based on screen width.
 * Standard breakpoints are 600dp (Medium) and 840dp (Expanded).
 */
@Composable
fun getWindowWidthClass(): WindowWidthClass {
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp

    return when {
        screenWidth < 600.dp -> WindowWidthClass.Compact
        screenWidth < 840.dp -> WindowWidthClass.Medium
        else -> WindowWidthClass.Expanded
    }
}

/**
 * Factory class to instantiate the ViewModel with the required dependencies (the Repository).
 */
class ExerciseViewModelFactory(
    private val repository: ExerciseRepository
) : androidx.lifecycle.ViewModelProvider.Factory {
    override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ExerciseViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ExerciseViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}


class MainActivity : ComponentActivity() {

    // Create the Repository instance once, using the Retrofit service
    private val repository = ExerciseRepository(RetrofitClient.apiService)

    // Create the Factory for the ViewModel
    private val viewModelFactory = ExerciseViewModelFactory(repository)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate: Activity started.") // Log activity start

        enableEdgeToEdge()
        setContent {
            Log.d(TAG, "setContent: Composable content setting up.") // Log content setup

            Mobile_Integration_CA3Theme {
                // Get the ViewModel instance using the factory
                val viewModel: ExerciseViewModel = viewModel(factory = viewModelFactory)
                // Observe the UI state from the ViewModel
                val uiState = viewModel.uiState

                // Get the current screen width class
                val widthClass = getWindowWidthClass()

                val navController = rememberNavController()

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    containerColor = MaterialTheme.colorScheme.background
                ) { innerPadding ->

                    // Handling the State
                    when (uiState) {
                        is ExerciseUiState.Loading -> LoadingScreen(modifier = Modifier.padding(innerPadding))

                        is ExerciseUiState.Error -> ErrorScreen(
                            message = uiState.message,
                            modifier = Modifier.padding(innerPadding)
                        )

                        is ExerciseUiState.Success -> {
                            // If successful, proceed to set up navigation with the loaded exercises
                            NavHost(
                                navController = navController,
                                startDestination = "exercises",
                                modifier = Modifier.padding(innerPadding)
                            ) {

                                // Exercises screen (default screen)
                                composable("exercises") {
                                    Log.d(TAG, "NavHost: Navigated to 'exercises' screen.") // Log screen navigation
                                    ExerciseListScreen(
                                        exercises = uiState.exercises, // Use data from the state
                                        onExerciseClick = { name ->
                                            Log.i(TAG, "onExerciseClick: Navigating to details for '$name'.") // Log click event
                                            navController.navigate("exercise/$name")
                                        },
                                        widthClass = widthClass
                                    )
                                }

                                // Specific exercise screen
                                composable("exercise/{name}") { backStackEntry ->
                                    val name = backStackEntry.arguments?.getString("name")!!
                                    // Use the ViewModel to safely find the exercise
                                    val exercise = viewModel.getExerciseByName(name)

                                    if (exercise != null) {
                                        Log.d(TAG, "NavHost: Navigated to 'exercise/$name' detail screen.")
                                        ExerciseDetailScreen(
                                            exercise = exercise,
                                            navController = navController,
                                            widthClass = widthClass
                                        )
                                    } else {
                                        // Handle case where exercise is not found
                                        Log.e(TAG, "NavHost: Exercise '$name' not found in state.")
                                        navController.popBackStack() // Go back
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun LoadingScreen(modifier: Modifier = Modifier) {
    Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            CircularProgressIndicator()
            Spacer(modifier = Modifier.height(16.dp))
            Text("Loading Exercises...", style = MaterialTheme.typography.bodyLarge)
        }
    }
}

@Composable
fun ErrorScreen(message: String, modifier: Modifier = Modifier) {
    Box(modifier = modifier.fillMaxSize().padding(32.dp), contentAlignment = Alignment.Center) {
        Text(
            "Error: $message",
            color = MaterialTheme.colorScheme.error,
            style = MaterialTheme.typography.titleMedium
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExerciseListScreen(
    exercises: List<Exercise>,
    onExerciseClick: (String) -> Unit,
    widthClass: WindowWidthClass
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Workout Explorer",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        ExerciseList(
            exercises = exercises,
            onExerciseClick = onExerciseClick,
            widthClass = widthClass,
            modifier = Modifier.padding(innerPadding)
        )
    }
}

@Composable
fun ExerciseList(
    exercises: List<Exercise>,
    onExerciseClick: (String) -> Unit,
    widthClass: WindowWidthClass,
    modifier: Modifier = Modifier
) {
    Log.v(TAG, "ExerciseList: Composing list with ${exercises.size} items.") // Log composable entry

    val columnCount = when (widthClass) {
        WindowWidthClass.Compact -> 1 // Phones, portrait mode
        WindowWidthClass.Medium -> 2 // Small tablets, large phones, landscape mode
        WindowWidthClass.Expanded -> 3 // Tablets, foldables
    }

    if (columnCount == 1) {
        // Use LazyColumn for a single column layout (Compact)
        LazyColumn(
            modifier = modifier.fillMaxSize(),
            contentPadding = PaddingValues(top = 8.dp, bottom = 8.dp)
        ) {
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
    } else {
        // Use LazyVerticalGrid for multiple columns (Medium/Expanded)
        LazyVerticalGrid(
            columns = GridCells.Fixed(columnCount),
            modifier = modifier.fillMaxSize().padding(horizontal = 8.dp),
            contentPadding = PaddingValues(top = 8.dp, bottom = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            itemsIndexed(exercises) { index, exercise ->
                ExerciseCard(
                    exercise = exercise,
                    itemIndex = index,
                    onExerciseClick = onExerciseClick,
                    modifier = Modifier.fillMaxWidth()
                )
            }
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
    // Log composition/recomposition of a specific card
    Log.v(TAG, "ExerciseCard: Composing card for '${exercise.exercise_name}'.")

    // Track if card already visible (loaded)
    val isVisible = rememberSaveable { mutableStateOf(false) }

    val opacity = remember { Animatable(if (isVisible.value) 1f else 0f) }
    val offsetY = remember { Animatable(if (isVisible.value) 0f else 50f) }

    LaunchedEffect(key1 = exercise.exercise_name) {
        if (!isVisible.value) {
            Log.d(TAG, "ExerciseCard: Starting animation for '${exercise.exercise_name}' at index $itemIndex.") // Log animation start
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
            Log.v(TAG, "ExerciseCard: Animation finished for '${exercise.exercise_name}'.") // Log animation finish
        }
    }

    Card(
        modifier = modifier
            .graphicsLayer {
                alpha = opacity.value
                translationY = offsetY.value
            },
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onSurface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(12.dp),
        onClick = {
            Log.d(TAG, "Button Click: 'View Details' for '${exercise.exercise_name}'.") // Log button click
            onExerciseClick(exercise.exercise_name)
        }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // LEFT PANE: Text Details (Takes 60% of width)
            Column(
                modifier = Modifier.weight(0.6f) // Allocate 60% of the space to text
            ) {
                // Exercise Name
                Text(
                    text = exercise.exercise_name,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.height(4.dp))

                // (Body Part & Difficulty)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Body Part
                    Icon(
                        Icons.Filled.FitnessCenter,
                        contentDescription = "Body Part",
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(Modifier.width(4.dp))
                    Text(exercise.body_part, style = MaterialTheme.typography.bodySmall)

                    Spacer(Modifier.width(12.dp))

                    // Difficulty
                    Icon(
                        Icons.Filled.TrendingUp,
                        contentDescription = "Difficulty",
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(Modifier.width(4.dp))
                    Text(exercise.difficulty, style = MaterialTheme.typography.bodySmall)
                }

                Spacer(modifier = Modifier.height(12.dp))

                Button(
                    onClick = {
                        Log.d(TAG, "Button Click: 'View Details' for '${exercise.exercise_name}'.") // Log button click
                        onExerciseClick(exercise.exercise_name)
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.secondary,
                        contentColor = MaterialTheme.colorScheme.onSecondary
                    ),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text("View Details", style = MaterialTheme.typography.labelMedium)
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            // RIGHT PANE: Image (Takes 40% of width)
            Box(
                modifier = Modifier
                    .weight(0.4f) // Allocate 40% of the space
                    .aspectRatio(1f) // Ensure the box is square
                    .clip(RoundedCornerShape(8.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        Icons.Filled.Image,
                        contentDescription = "Image Placeholder",
                        modifier = Modifier.size(32.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(Modifier.height(4.dp))
                    AsyncImage(
                        model = exercise.image,
                        contentDescription = exercise.exercise_name,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExerciseDetailScreen(
    exercise: Exercise,
    navController: NavController,
    widthClass: WindowWidthClass
) {
    Log.d(TAG, "ExerciseDetailScreen: Composing details for '${exercise.exercise_name}'.") // Log screen entry

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(exercise.exercise_name, color = MaterialTheme.colorScheme.onSurface) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onSurface,
                    navigationIconContentColor = MaterialTheme.colorScheme.onSurface
                ),
                navigationIcon = {
                    IconButton(onClick = {
                        Log.i(TAG, "IconButton Click: Back button pressed on detail screen.") // Log back press
                        navController.popBackStack()
                    }) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            )
        },
    ) { innerPadding ->

        // Apply a two-pane layout for Medium and Expanded width classes
        if (widthClass == WindowWidthClass.Compact) {
            // Default single-column layout for small screens (Compact)
            SingleColumnDetailLayout(exercise, innerPadding)
        } else {
            // Two-pane layout for tablets and landscape orientation (Medium/Expanded)
            TwoPaneDetailLayout(exercise, innerPadding)
        }
    }
}

// Helper Composable for the default single-column layout
@Composable
private fun SingleColumnDetailLayout(exercise: Exercise, innerPadding: PaddingValues) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)
            .padding(16.dp)
    ) {
        ImageCard(exercise, Modifier.fillMaxWidth().height(220.dp))
        Spacer(modifier = Modifier.height(20.dp))
        DetailsCard(exercise, Modifier.fillMaxWidth())
    }
}

// Helper Composable for the two-pane (side-by-side) layout
@Composable
private fun TwoPaneDetailLayout(exercise: Exercise, innerPadding: PaddingValues) {
    Row(
        modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)
            .padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        // Left Pane: Image (takes 40% of the space)
        ImageCard(
            exercise,
            Modifier.weight(0.4f).fillMaxHeight()
        )

        // Right Pane: Details (takes 60% of the space)
        DetailsCard(
            exercise,
            Modifier.weight(0.6f).fillMaxHeight()
        )
    }
}

// IMAGE HELPER FUNCTION
@Composable
private fun ImageCard(exercise: Exercise, modifier: Modifier) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(6.dp)
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            AsyncImage(
                model = exercise.image,
                contentDescription = exercise.exercise_name,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}

// DETAIL HELPER FUNCTION
@Composable
private fun DetailsCard(exercise: Exercise, modifier: Modifier) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(Modifier.padding(20.dp)
                .fillMaxHeight()
                .verticalScroll(rememberScrollState())) {
            Text(
                "Description",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(Modifier.height(6.dp))
            Text(
                exercise.exercise_description,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(Modifier.height(16.dp))
            Text(
                "Details",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(Modifier.height(10.dp))

            DetailRow("Body Part", exercise.body_part)
            DetailRow("Difficulty", exercise.difficulty)
            DetailRow("Equipment Needed", toString(exercise.needs_equipment))
        }
    }
}

@Composable
fun DetailRow(label: String, value: String) {
    Log.v(TAG, "DetailRow: Composing row - $label: $value") // Log detail row composition

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.weight(1f).padding(end = 8.dp)
        )
        Text(
            value,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.End,
            modifier = Modifier.weight(1f)
        )
    }
}