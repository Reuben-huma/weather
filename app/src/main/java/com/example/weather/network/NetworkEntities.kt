package com.example.weather.network

import com.example.weather.database.DatabaseForecast
import com.example.weather.database.DatabaseWeather
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class NetworkForecastContainer(
    val cod: String,
    val message: Double,
    val cnt: Double,
    @Json(name = "list")
    val forecasts: List<NetworkForecast>,
    val city: NetworkCity
)

@JsonClass(generateAdapter = true)
data class NetworkForecast(
    val dt: Double,
    val main: NetworkMain,
    val weather: List<NetworkCondition>,
    val clouds: NetworkClouds,
    val wind: NetworkWind,
    val visibility: Double,
    val pop: Double,
    val sys: NetworkSystem,
    @Json(name = "dt_txt")
    val datetime: String
)

@JsonClass(generateAdapter = true)
data class NetworkCity(
    val id: Double,
    val name: String,
    val coord: NetworkCoordinates,
    val country: String,
    val population: Double,
    val timezone: Double,
    val sunrise: Double,
    val sunset: Double
)

@JsonClass(generateAdapter = true)
data class NetworkWeather(
    val coord: NetworkCoordinates,
    val weather: List<NetworkCondition>,
    val base: String,
    val main: NetworkMain,
    val visibility: Double,
    val wind: NetworkWind,
    val clouds: NetworkClouds,
    val dt: Double,
    val sys: NetworkSys,
    val timezone: Double,
    val id: Double,
    val name: String,
    val cod: Double
)

@JsonClass(generateAdapter = true)
data class NetworkCoordinates(
    val lon: Double,
    val lat: Double
)

@JsonClass(generateAdapter = true)
data class NetworkCondition(
    val id: Int,
    val main: String,
    val description: String,
    val icon: String
)

@JsonClass(generateAdapter = true)
data class NetworkMain(
    val temp: Double,
    val feels_like: Double,
    val temp_min: Double,
    val temp_max: Double,
    val pressure: Double,
    val sea_level: Double?,
    val grnd_level: Double?,
    val humidity: Double,
    val temp_kf: Double?
)

@JsonClass(generateAdapter = true)
data class NetworkWind(
    val speed: Double,
    val deg: Double
)

@JsonClass(generateAdapter = true)
data class NetworkRain(
    @Json(name = "1h") val oneHour: Double,
    @Json(name = "3h") val threeHour: Double
)

@JsonClass(generateAdapter = true)
data class NetworkClouds(
    val all: Double
)

@JsonClass(generateAdapter = true)
data class NetworkSys(
    val type: Double,
    val id: Double,
    val country: String,
    val sunrise: Double,
    val sunset: Double,
)

@JsonClass(generateAdapter = true)
data class NetworkSystem(
    val pod: String
)

/**
 * Convert Network current weather response to database objects
 */
fun NetworkWeather.asDatabaseModel(): DatabaseWeather {
    return DatabaseWeather(
        id = 1,
        city_name = name,
        condition_id = weather[0].id,
        condition_name = weather[0].main,
        condition_description = weather[0].description,
        temperature_current = main.temp,
        temperature_minimum = main.temp_min,
        temperature_maximum = main.temp_max
    )
}

/**
 * Convert Network forecast response to database objects
 */
fun NetworkForecastContainer.asDatabaseModel(): List<DatabaseForecast> {

    val forecastList = mutableListOf<NetworkForecast>()
    var currentDay = forecasts[0].datetime.substring(0, 10)
    lateinit var nextDay: String

    forecasts.forEach { forecast ->
        nextDay = forecast.datetime.substring(0, 10)

        if(currentDay != nextDay) {
            forecastList.add(forecast)
            currentDay = nextDay
        }
    }

    return forecastList.map {
        DatabaseForecast(
            datetime = it.datetime,
            condition_id = it.weather[0].id,
            condition_name = it.weather[0].main,
            condition_description = it.weather[0].description,
            temperature_current = it.main.temp,
            temperature_minimum = it.main.temp_min,
            temperature_maximum = it.main.temp_max
        )
    }
}

