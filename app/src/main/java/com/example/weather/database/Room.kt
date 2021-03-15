package com.example.weather.database

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface WeatherDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(weather: DatabaseWeather)

    @Query("select * from weather_table")
    fun getWeather() : LiveData<DatabaseWeather?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll( forecasts: List<DatabaseForecast>)

    @Query("select * from forecast_table")
    fun getForecasts(): LiveData<List<DatabaseForecast>?>
}

@Database(entities = [DatabaseWeather::class, DatabaseForecast::class], version = 1, exportSchema = false)
abstract class WeatherDatabase: RoomDatabase() {
    abstract val weatherDao: WeatherDao
}

private lateinit var INSTANCE: WeatherDatabase

fun getDatabase(context: Context): WeatherDatabase {
    synchronized(WeatherDatabase::class.java) {
        if(!::INSTANCE.isInitialized) {
            INSTANCE = Room.databaseBuilder(context.applicationContext,
                WeatherDatabase::class.java,
                "weatherdb")
                .allowMainThreadQueries()
                .build()
        }
    }
    return INSTANCE
}