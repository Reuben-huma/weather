package com.example.weather.network

import com.example.weather.BuildConfig
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import okhttp3.Request
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

private const val BASE_URL = "https://community-open-weather-map.p.rapidapi.com/"
private const val WEATHER_KEY = BuildConfig.WEATHER_KEY

private val moshi = Moshi.Builder()
    .add(KotlinJsonAdapterFactory())
    .build()

var defaultHttpClient: OkHttpClient = OkHttpClient.Builder()
    .addInterceptor { chain ->
        val request: Request = chain.request().newBuilder()
            .addHeader("x-rapidapi-key", WEATHER_KEY)
            .build()
        chain.proceed(request)
    }.build()

private val retrofit = Retrofit.Builder()
    .baseUrl(BASE_URL)
    .client(defaultHttpClient)
    .addConverterFactory(MoshiConverterFactory.create(moshi))
    .build()

interface WeatherService {

    /**
     * @GET Returns the current weather using Geographical coordinates (latitude, longitude).
     * https://community-open-weather-map.p.rapidapi.com/weather?lat={lat}&lon={lon}&units=metric
     */
    @GET("weather")
    suspend fun getWeatherFromCoordinates(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("units") units: String
    ): NetworkWeather

    /**
     * @GET Returns the 5 day forecast using Geographical coordinates (latitude, longitude).
     * https://community-open-weather-map.p.rapidapi.com/forecast?lat={lat}&lon={lon}&units=metric
     */
    @GET("forecast")
    suspend fun getForecastsFromCoordinates(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("units") units: String
    ): NetworkForecastContainer

    /**
     * @GET Returns the current weather using City Name.
     * https://community-open-weather-map.p.rapidapi.com/weather?q=London&units=metric
     */
    @GET("weather")
    suspend fun getWeatherFromName(
        @Query("q") city: String,
        @Query("units") units: String
    ): NetworkWeather

    /**
     * @GET Returns the 5 day forecast using City Name.
     * https://community-open-weather-map.p.rapidapi.com/forecast?q={cityname}&units=metric
     */
    @GET("forecast")
    suspend fun getForecastsFromName(
        @Query("q") city: String,
        @Query("units") units: String
    ): NetworkForecastContainer
}

/**
 * Main entry point for network access.
 * Example: `WeatherAPI.retrofitService.getWeatherFromCoordinates`
 */
object WeatherAPI {
    val retrofitService: WeatherService by lazy {
        retrofit.create(WeatherService::class.java)
    }
}