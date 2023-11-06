package com.example.weatherapp.models

import android.os.Parcelable
import androidx.versionedparcelable.ParcelField
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

data class WeatherAlert(
    val id: String,
    val event: String,
    val effective: String,
    val ends: String,
    val senderName: String
) {
    private val random = Random()

    // Function to generate a random seed for the image URL
    private fun generateRandomSeed(): String {
        return UUID.randomUUID().toString()
    }

    // Function to format the duration as a string
    fun getFormattedDuration(): String {
        if (effective != null && ends != null) {
            val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssX", Locale.getDefault())
            try {
                val startDateTime = dateFormat.parse(effective)
                val endDateTime = dateFormat.parse(ends)

                if (startDateTime != null && endDateTime != null) {
                    val durationInMillis = endDateTime.time - startDateTime.time

                    val hours = TimeUnit.MILLISECONDS.toHours(durationInMillis)
                    val minutes = TimeUnit.MILLISECONDS.toMinutes(durationInMillis - TimeUnit.HOURS.toMillis(hours))

                    return String.format(Locale.getDefault(), "%02d:%02d", hours, minutes)
                }
            } catch (e: ParseException) {
                // Log the exception or handle it as needed
            }
        }

        return "N/A"
    }

    // Function to get the URL for a unique random image
    fun getImageUrl(): String {
        val seed = generateRandomSeed()
        return "https://picsum.photos/seed/$seed/1000"
    }
}
