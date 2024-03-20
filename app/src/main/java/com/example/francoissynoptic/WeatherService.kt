package com.example.francoissynoptic.network

import com.google.gson.annotations.SerializedName
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path

data class WeatherData(
    @SerializedName("name") val name: String,
    @SerializedName("temp") val temp: Int,
    @SerializedName("condition") val condition: String
)

interface WeatherService {
    @GET("api/v1/weather/{city}")
    suspend fun getWeather(@Path("city") city: String): WeatherData
}

object RetrofitClient {
    private const val BASE_URL = "https://api.jamesdecelis.com/"

    val webservice by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(WeatherService::class.java)
    }
}
