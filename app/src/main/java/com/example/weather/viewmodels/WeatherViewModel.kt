package com.example.weather.viewmodels

import android.app.Application
import androidx.lifecycle.*
import com.example.weather.database.getDatabase
import com.example.weather.domain.WeatherApiStatus
import com.example.weather.repository.WeatherRepository
import kotlinx.coroutines.launch
import timber.log.Timber
import java.io.IOException

/**
 * WeatherViewModel designed to store and manage UI-related data in a lifecycle conscious way. This
 * allows data to survive configuration changes such as screen rotations. In addition, background
 * work such as fetching network results can continue through configuration changes and deliver
 * results after the new Fragment or Activity is available.
 *
 * @param application The application that this viewmodel is attached to, it's safe to hold a
 * reference to applications across rotation since Application is never recreated during actiivty
 * or fragment lifecycle events.
 */
class WeatherViewModel(application: Application) : AndroidViewModel(application) {

    /**
     * The data source this ViewModel will fetch results from.
     */
    private val weatherRepository = WeatherRepository(getDatabase(application))

    /**
     * The Live data property that will be used to update the layout.
     */
    val currentWeather = weatherRepository.weather

    /**
     * The Live data property that will be used to update the recyler view layout.
     */
    val forecasts = weatherRepository.forecasts

    /**
     * @Variable _status
     * The internal MutableLiveData that stores the status of the most recent request
     * @Variable status
     * The external immutable LiveData for the request status
     */
    private var _status = MutableLiveData<WeatherApiStatus>()
    val status: LiveData<WeatherApiStatus> = _status

    /**
     * init{} is called immediately when this ViewModel is created.
     */
    init {
        setStatus(WeatherApiStatus.LOADING)
    }

    /**
     * Refresh data from the repository. Use a coroutine launch to run in a
     * background thread.
     */
    fun refreshDataFromRepository(latitude: Double, longitude: Double) {
        viewModelScope.launch {
            try {
                weatherRepository.getWeatherFromCoordinates(lat = latitude, lon = longitude, units = "metric")
                weatherRepository.getForecastsFromCoordinates(lat = latitude, lon = longitude, units = "metric")
                setStatus(WeatherApiStatus.DONE)
            }
            catch (networkError: IOException) {
                if(currentWeather.value == null || forecasts.value.isNullOrEmpty())
                    setStatus(WeatherApiStatus.ERROR)
                    Timber.d("Network error occurred")
            }
        }
    }

    /**
     * Refresh data from the repository. Use a coroutine launch to run in a
     * background thread.
     */
    fun refreshDataFromRepository(city: String) {
        viewModelScope.launch {
            try {
                weatherRepository.getWeatherFromName(city = city, units = "metric")
                weatherRepository.getForecastsFromName(city = city, units = "metric")
                setStatus(WeatherApiStatus.DONE)
            }
            catch (networkError: IOException) {
                if(currentWeather.value == null || forecasts.value.isNullOrEmpty())
                    setStatus(WeatherApiStatus.ERROR)
                    Timber.d("Network error occurred")
            }
        }
    }

    /**
     * Set the Weather Api status.
     */
    fun setStatus(status: WeatherApiStatus) {
        _status.value = status
    }

    /**
     * Factory for constructing the WeatherViewModel with a parameter.
     */
    class Factory(private val app: Application) : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(WeatherViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return WeatherViewModel(app) as T
            }
            throw IllegalArgumentException("Unable to construct ViewModel")
        }
    }
}

