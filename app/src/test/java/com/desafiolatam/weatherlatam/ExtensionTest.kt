package com.desafiolatam.weatherlatam

import com.desafiolatam.weatherlatam.extension.toCelsius
import com.desafiolatam.weatherlatam.extension.toFahrenheit
import junit.framework.TestCase.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class ExtensionTest {

    //(°C × 9/5) + 32 = °F

    @Test
    fun toFahrenheit() {
        val fahrenheit = 10.0.toFahrenheit()
        assertEquals(fahrenheit, 50.0)
    }

    @Test
    fun toCelsius() {
        val celsius = 32.0.toCelsius()
        assertEquals(celsius, 0.0)
    }

    @Test
    fun negativeCelsiusToFahrenheit() {
        val celsius = (-10.0).toFahrenheit()
        assertEquals(celsius, 14.0)
    }

    @Test
    fun negativeCelsiusToNegativeFahrenheit() {
        val celsius = (-50.0).toFahrenheit()
        assertEquals(celsius, -58.0)
    }
}