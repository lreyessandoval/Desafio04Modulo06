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
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.withContext
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

@RunWith(AndroidJUnit4::class)
class DatabaseTestDelete {

    private lateinit var db: WeatherDatabase
    private lateinit var weatherDao: WeatherDao

    // dummy data
    private val weather1 = WeatherDto(
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

    private val weather2 = WeatherDto(
        id = 101,
        currentTemp = 21.0,
        maxTemp = 10.0,
        minTemp = -10.0,
        pressure = 100.0,
        humidity = 10.0,
        windSpeed = 23.7,
        sunrise = 1688039234,
        sunset = 1688039234,
        cityName = "Arica",
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
        Dispatchers.resetMain()
        db.close()
    }

    @Test
    fun addAndDeleteAll() = runTest {
        withContext(Dispatchers.IO) {
            weatherDao.insertData(weather1.toEntity())
            // Esta parte no es necesaria, es solamente para validar que el datos que se inserto, esta ahi
            val countBefore = weatherDao.getWeatherData().first()?.count()
            assertEquals(countBefore, 1)
            //
            weatherDao.clearAll()
            val countAfter = weatherDao.getWeatherData().first()?.count()
            assertEquals(countAfter, 0)
        }
    }

    @Test
    fun addAndDeleteById() = runTest {
        weatherDao.insertData(weather1.toEntity())
        weatherDao.insertData(weather2.toEntity())

        val countBefore = weatherDao.getWeatherData().first()?.count()
        assertEquals(countBefore, 2)

        weatherDao.deleteById(100)
        val countAfter = weatherDao.getWeatherData().first()?.count()
        assertEquals(countAfter, 1)
    }
}
