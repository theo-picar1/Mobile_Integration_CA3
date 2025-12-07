package com.example.mobile_integration_ca3.data

import android.content.Context
import androidx.room.Room

// Repository class to handle data operations by combining ExerciseDao and ExerciseDatabase.
class ExerciseRepository(
    private val api: ExerciseApi,
    private val context: Context
) {
    private val db = Room.databaseBuilder(
        context,
        ExerciseDatabase::class.java,
        "exercise_db"
    ).build()

    private val dao = db.exerciseDao()

    suspend fun fetchExercises(): List<Exercise> {
        val remote = api.getExercises()

        val entities = remote.map {
            ExerciseEntity(
                name = it.exercise_name,
                description = it.exercise_description,
                image = it.image,
                bodyPart = it.body_part,
                difficulty = it.difficulty,
                needsEquipment = it.needs_equipment
            )
        }

        dao.insertAll(entities)

        return remote
    }

    suspend fun getExercisesFromDb(): List<Exercise> {
        return dao.getAllExercises().map {
            Exercise(
                exercise_name = it.name,
                exercise_description = it.description,
                image = it.image,
                body_part = it.bodyPart,
                difficulty = it.difficulty,
                needs_equipment = it.needsEquipment
            )
        }
    }
}