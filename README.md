#  Workout Explorer

A modern Android application built with Jetpack Compose that allows users to browse a list of exercises fetched from an external API. It features clean architecture principles, state management using `ViewModel` and `StateFlow`, and a fully adaptive UI that adjusts to different screen sizes (phones, tablets, and foldables).

## Features

* **Exercise List View:** Displays a dynamic list of exercises with their name, body part, and difficulty.
* **Offline Resilience:** Implemented using **Room** to cache all exercises. If the network call fails, the app falls back to displaying cached data, ensuring a good user experience even without internet.
* **Adaptive Layout:** Uses **Window Size Classes** to switch between a single-column list (Compact) and a multi-column grid (Medium/Expanded).
* **Detailed View:** Provides a dedicated screen for each exercise, showing its description and specific details.
* **Two-Pane Detail Layout:** On larger screens (Medium/Expanded), the detail screen switches to a two-pane layout, displaying the image and details side-by-side.
* **Loading & Error States:** Clear feedback for users during data loading or in case of an API error.
* **Entrance Animation:** Exercises in the list use a subtle fade-in and slide-up animation for a smooth user experience.
* **Coil Integration:** Uses the Coil library for asynchronous image loading and caching.
* **Logging:** Includes detailed Android logging (`Log.d`, `Log.i`, `Log.v`) for debugging lifecycle events, network operations, and UI interactions.

## Architecture and Technologies

The application follows best practices for Android development, utilizing the following technologies:

### Core Technologies
* **Language:** Kotlin
* **UI Toolkit:** Jetpack Compose
* **Navigation:** Jetpack Compose Navigation
* **State Management:** `ViewModel` and `StateFlow`
* **Dependency Injection (Manual):** `ExerciseViewModelFactory` is used to inject the `ExerciseRepository`.

### Data Layer
* **Networking:** **Retrofit** is used to fetch exercise data from a JSON file hosted on a GitHub repository.
* **Local Caching:** **Room Persistence Library** for caching data.
* **Image Loading:** Coil for efficient, asynchronous image handling.
* **Repository Pattern:** `ExerciseRepository` orchestrates data flow, implementing a Network-First, Cache-Fallback strategy.

### Adaptive UI
The app dynamically adjusts its layout based on the available screen width using custom **Window Width Classes**:

| Class | Screen Width | Use Case | List Layout | Detail Layout |
| :--- | :--- | :--- | :--- | :--- |
| **Compact** | `< 600 dp` | Portrait Phones | Single Column (`LazyColumn`) | Single Column |
| **Medium** | `600 dp - 840 dp` | Landscape Phones, Small Tablets | Two-Column Grid (`LazyVerticalGrid`) | Two-Pane |
| **Expanded** | `> 840 dp` | Large Tablets, Foldables | Three-Column Grid (`LazyVerticalGrid`) | Two-Pane |

## Project Structure

The architecture is split into presentation, data, and utilities layers:

* **`com.example.mobile_integration_ca3`** (Root Package)
    * `MainActivity.kt`: Main Activity, Entry Point, NavHost setup, and Adaptive UI Logic.
    * **`ui.theme/`**: Standard composables for Color, Theme, and Type.
    * **`data/`** (Data Layer)
        * `Exercise.kt`: Network data class/Model.
        * `ExerciseEntity.kt`: **Room** data class (Database Table definition).
        * `ExerciseDao.kt`: **Room** Data Access Object (DAO) for SQL operations.
        * `ExerciseDatabase.kt`: **Room** Database instantiation.
        * `RetrofitClient.kt`: Retrofit setup, pointing to `exercises.json` on GitHub.
        * `ExerciseRepository.kt`: Orchestrates data flow (Network call $\to$ Cache save $\to$ Cache load).
    * **`viewmodel/`** (Presentation Layer)
        * `ExerciseUiState.kt`: Sealed class for UI state (`Loading`, `Success`, `Error`).
        * `ExerciseViewModel.kt`: Fetches data via the Repository and manages the `uiState`.

### Key Code Implementations

* **`ExerciseViewModel.kt`**: Implements the core data fetching logic:
    1.  Tries to fetch data from the network.
    2.  On network failure, it attempts to load cached data.
    3.  If cached data exists, it shows that; otherwise, it sets `ExerciseUiState.Error("No data available")`.

* **`ExerciseDao.kt`**: Defines core database operations: `insertAll`, `getAllExercises`, and `getExerciseByName`.

### Key Composables

* `MainActivity`: Sets up the theme, determines the `WindowWidthClass`, initializes the `ViewModel`, and hosts the `NavHost`.
* `ExerciseListScreen`: Displays the main list using `Scaffold` and `TopAppBar`.
* `ExerciseList`: Selects between `LazyColumn` (Compact) and `LazyVerticalGrid` (Medium/Expanded) based on `widthClass`.
* `ExerciseCard`: Represents a single item in the list, featuring the entrance animation via `Animatable` and `graphicsLayer`.
* `ExerciseDetailScreen`: Presents the specific details of an exercise, implementing the single-column or two-pane layout based on `widthClass`.

## Getting Started

### Installation

1.  Clone the repository:
    ```bash
    git clone [your-repo-link]
    ```
2.  Open the project in Android Studio.
3.  Ensure network permissions are granted in `AndroidManifest.xml` (for API calls).
4.  Build and run the application on an Android device or emulator.

**Note:** To test the adaptive layout, use the built-in Resizable Emulator in Android Studio or switch orientations on a physical device. To test the **Offline Resilience**, disable Wi-Fi/data on the device after the first successful load, then kill and restart the app.

## Future Enhancements

* **Search/Filtering:** Add functionality to search and filter exercises by body part, difficulty, or equipment needed.
* **Dependency Injection Framework:** Replace manual injection with Hilt for cleaner dependency management.
* **Improved Error Handling:** Implement a retry mechanism for API errors on the UI level.
* **Dedicated Image Loading:** Refactor `AsyncImage` to include loading/error placeholders for better UX.