package com.example.weather.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.example.weather.database.WeatherDatabase
import com.example.weather.database.asDomainModel
import com.example.weather.domain.Forecast
import com.example.weather.domain.Weather
import com.example.weather.network.WeatherAPI
import com.example.weather.network.asDatabaseModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber

/**
 * Repository for fetching Weather information from the network and storing it on disk.
 */
class WeatherRepository(private val database: WeatherDatabase) {

    /**
     * Property used in the view model to access the current weather data from the database.
     */
    val weather: LiveData<Weather> = Transformations.map(database.weatherDao.getWeather()) {
        Timber.d("database.videoDao.getWeather()")
        it?.asDomainModel()
    }

    /**
     * Property used in the view model to access the forecasts data from the database.
     */
    val forecasts: LiveData<List<Forecast>> = Transformations.map(database.weatherDao.getForecasts()) {
        Timber.d("database.weatherDao.getForecasts()")
        it?.asDomainModel()
    }

    suspend fun getWeatherFromName(city: String, units: String) {
        withContext(Dispatchers.IO) {
            Timber.d("getWeatherFromName is called")
            val weather = WeatherAPI.retrofitService.getWeatherFromName(city = city, units = units)
            database.weatherDao.insert(weather.asDatabaseModel())
        }
    }

    suspend fun getForecastsFromName(city: String, units: String) {
        withContext(Dispatchers.IO) {
            Timber.d("getForecastsFromName is called")
            val forecasts = WeatherAPI.retrofitService.getForecastsFromName(city = city, units = units)
            database.weatherDao.insertAll(forecasts.asDatabaseModel())
        }
    }

    suspend fun getWeatherFromCoordinates(lat: Double, lon: Double, units: String) {
        withContext(Dispatchers.IO) {
            val weather = WeatherAPI.retrofitService.getWeatherFromCoordinates(lat = lat, lon = lon, units = units)
            database.weatherDao.insert(weather.asDatabaseModel())
        }
    }

    suspend fun getForecastsFromCoordinates(lat: Double, lon: Double, units: String) {
        withContext(Dispatchers.IO) {
            Timber.d("getForecastsFromName is called")
            val forecasts = WeatherAPI.retrofitService.getForecastsFromCoordinates(lat = lat, lon = lon, units = units)
            database.weatherDao.insertAll(forecasts.asDatabaseModel())
        }
    }
}