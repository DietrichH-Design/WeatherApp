package com.example.weatherapp.network

import com.example.weatherapp.models.WeatherAlert
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET

data class WeatherAlertResponse(
    val features: List<Feature> = ArrayList()
)

data class Feature(
    val id: String,
    val properties: Properties
)

data class Properties(
    val event: String,
    val effective: String,
    val ends: String,
    val senderName: String
)

interface WeatherAlertService {
    @GET("alerts/active?status=actual&message_type=alert")
    suspend fun getWeatherAlerts(): WeatherAlertResponse

    companion object {
        private const val BASE_URL = "https://api.weather.gov/"

        fun create(): WeatherAlertService {
            val logger = HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BASIC }

            val client = OkHttpClient.Builder()
                .addInterceptor(logger)
                .build()

            return Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(WeatherAlertService::class.java)
        }
    }
}
