package com.example.weatherapp

import com.example.weatherapp.models.WeatherAlert
import com.example.weatherapp.network.WeatherAlertService
import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.signature.ObjectKey
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private val alerts = mutableListOf<WeatherAlert>()
    private lateinit var alertsAdapter: AlertsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        recyclerView = findViewById(R.id.recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(this)
        alertsAdapter = AlertsAdapter(alerts)
        recyclerView.adapter = alertsAdapter

        loadWeatherAlerts()
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun loadWeatherAlerts() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val service = WeatherAlertService.create()
                val response = service.getWeatherAlerts()

                if (response.features.isNotEmpty()) {
                    val weatherAlerts = response.features.map {
                        WeatherAlert(
                            id = it.id,
                            event = it.properties?.event ?: "",
                            effective = it.properties?.effective ?: "",
                            ends = it.properties?.ends ?: "",
                            senderName = it.properties?.senderName ?: ""
                        )
                    }

                    withContext(Dispatchers.Main) {
                        alerts.addAll(weatherAlerts)
                        alertsAdapter.notifyDataSetChanged()
                    }
                } else {
                    Log.e("NetworkError", "Features list is empty")
                }
            } catch (e: Exception) {
                Log.e("NetworkError", "Exception: ${e.message}", e)
            }
        }
    }



}


class AlertsAdapter(private val alerts: List<WeatherAlert>) :
    RecyclerView.Adapter<AlertsAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val eventTextView: TextView = itemView.findViewById(R.id.event_text_view)
        val senderNameTextView: TextView = itemView.findViewById(R.id.sender_name_text_view)
        val durationTextView: TextView = itemView.findViewById(R.id.duration_text_view)
        val startDateTextView: TextView = itemView.findViewById(R.id.start_date_text_view)
        val endDateTextView: TextView = itemView.findViewById(R.id.end_date_text_view)
        val imageView: ImageView = itemView.findViewById(R.id.image_view)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.alert_item, parent, false)

        return ViewHolder(view)
    }

    override fun getItemId(position: Int): Long {
        return alerts[position].hashCode().toLong()
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val alert = alerts[position]

        holder.eventTextView.text = "Event: ${alert.event ?: "Not Available"}"
        holder.senderNameTextView.text = "Source: ${alert.senderName ?: "Not Available"}"
        holder.durationTextView.text = "Duration: ${alert.getFormattedDuration()}"
        holder.startDateTextView.text = "Start Date: ${alert.effective ?: "Not Available"}"
        holder.endDateTextView.text = "End Date: ${alert.ends ?: "Not Available"}"

        val imageUrl = alert.getImageUrl()

        Glide.with(holder.itemView.context)
            .load(imageUrl)
            .apply(RequestOptions.centerCropTransform())
            .transition(DrawableTransitionOptions.withCrossFade())
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .into(holder.imageView)
    }

    override fun getItemCount() = alerts.size
}





