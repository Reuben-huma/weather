package com.example.weather.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.weather.domain.Forecast
import com.example.weather.domain.Weather

/**
 * DatabaseWeather represents a weather entity in the database.
 */
@Entity(tableName = "weather_table")
data class DatabaseWeather(
    @PrimaryKey
    val id: Int,
    val city_name: String,
    val condition_id: Int,
    val condition_name: String,
    val condition_description: String,
    val temperature_current: Double,
    val temperature_minimum: Double,
    val temperature_maximum: Double,
)

/**
 * Convert Database entity to domain objects
 */
fun DatabaseWeather.asDomainModel(): Weather {
    return Weather(
        city_name = city_name,
        condition_id = condition_id,
        condition_description = condition_description,
        condition_name = condition_name,
        temperature_current = temperature_current,
        temperature_minimum = temperature_minimum,
        temperature_maximum = temperature_maximum,
    )
}

/**
 * DatabaseForecast represents a forecast entity in the database.
 */
@Entity(tableName = "forecast_table")
data class DatabaseForecast(
    @PrimaryKey
    val datetime: String,
    val condition_id: Int,
    val condition_name: String,
    val condition_description: String,
    val temperature_current: Double,
    val temperature_minimum: Double,
    val temperature_maximum: Double
)

/**
 * Convert Database entity to domain objects
 */
fun List<DatabaseForecast>.asDomainModel(): List<Forecast> {
    return map {
        Forecast(
            datetime = it.datetime,
            condition_id = it.condition_id,
            condition_name = it.condition_name,
            condition_description = it.condition_description,
            temperature_current = it.temperature_current,
            temperature_minimum = it.temperature_minimum,
            temperature_maximum = it.temperature_maximum
        )
    }
}






