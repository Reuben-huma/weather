package com.example.weather.domain

data class Forecast(
    val datetime: String,
    val condition_id: Int,
    val condition_name: String,
    val condition_description: String,
    val temperature_current: Double,
    val temperature_minimum: Double,
    val temperature_maximum: Double
)

data class Weather(
    val city_name: String,
    val condition_name: String,
    val temperature_current: Double,
    val temperature_minimum: Double,
    val temperature_maximum: Double,
    val condition_id: Int,
    val condition_description: String
)

enum class WeatherApiStatus {
    LOADING,
    ERROR,
    DONE
}

