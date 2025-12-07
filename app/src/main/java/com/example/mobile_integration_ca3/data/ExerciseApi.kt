package com.example.mobile_integration_ca3.data

// For fetching the JSON from assets, via URL which is just our link to our github

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET

private const val BASE_URL = "https://raw.githubusercontent.com/theo-picar1/Mobile_Integration_CA3/refs/heads/main/app/src/main/assets/"

/**
 * Retrofit Interface for Exercise API Endpoints
 */
interface ExerciseApi {
    @GET("exercises.json")
    suspend fun getExercises(): List<Exercise>
}

/**
 * Singleton object for initializing Retrofit and providing the API service.
 */
object RetrofitClient {
    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val apiService: ExerciseApi by lazy {
        retrofit.create(ExerciseApi::class.java)
    }
}