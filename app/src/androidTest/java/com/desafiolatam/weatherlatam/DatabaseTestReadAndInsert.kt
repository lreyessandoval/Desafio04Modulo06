package com.desafiolatam.weatherlatam


import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.desafiolatam.weatherlatam.data.local.WeatherDao
import com.desafiolatam.weatherlatam.data.local.WeatherDatabase
import com.desafiolatam.weatherlatam.data.toEntity
import com.desafiolatam.weatherlatam.model.WeatherDto
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.withContext
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

@RunWith(AndroidJUnit4::class)
class DatabaseTestReadAndInsert {

    private lateinit var db: WeatherDatabase
    private lateinit var weatherDao: WeatherDao

    // dummy data
    private val weather = WeatherDto(
        id = 100,
        currentTemp = 21.0,
        maxTemp = 10.0,
        minTemp = -10.0,
        pressure = 100.0,
        humidity = 10.0,
        windSpeed = 23.7,
        sunrise = 1688039234,
        sunset = 1688039234,
        cityName = "Santiago",
    )

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(
            context, WeatherDatabase::class.java
        ).build()

        weatherDao = db.weatherDao()
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    @Test
    fun readCityName() = runTest {
        withContext(Dispatchers.IO) {
            weatherDao.insertData(weather.toEntity())
            val cityName = weatherDao.getWeatherDataById(100).first()?.cityName
            assertEquals(cityName, "Santiago")
        }
    }

    @Test
    fun readCurrentTemp() = runTest {
        withContext(Dispatchers.IO) {
            weatherDao.insertData(weather.toEntity())
            val currentTemp = weatherDao.getWeatherDataById(100).first()?.currentTemp
            assertEquals(currentTemp, 21.0)
        }
    }

    @Test
    fun readSunrise() = runTest {
        withContext(Dispatchers.IO) {
            weatherDao.insertData(weather.toEntity())
            val sunrise = weatherDao.getWeatherDataById(100).first()?.sunrise
            assertEquals(sunrise, 1688039234L)
        }
    }
}