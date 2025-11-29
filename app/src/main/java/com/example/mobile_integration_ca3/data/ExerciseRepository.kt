package com.example.mobile_integration_ca3.data

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class ExerciseRepository(private val context: Context) {
    fun loadExercises(): List<Exercise> {
        val json = context.assets.open("exercises.json")
            .bufferedReader()
            .use { it.readText() }

        val listType = object : TypeToken<List<Exercise>>() {}.type
        return Gson().fromJson(json, listType)
    }
}
