package com.sgvasalia.myapplication.apis.models

import java.text.SimpleDateFormat
import java.util.*

data class FiveDayWeatherResponse(
    val cod: String,
    val message: Int,
    val cnt: Int,
    val list: List<ForecastItem>
)

data class ForecastItem(
    val dt: Long,
    val main: MainFive,
    val weather: List<WeatherFive>,
    val clouds: Clouds,
    val wind: Wind,
    val visibility: Int,
    val pop: Double,
    val rain: Rain?,
    val sys: SysFive,
    val dt_txt: String
) {
    fun getFormattedDate(): String {
        val sdf = SimpleDateFormat("hh a dd MMM", Locale.ENGLISH)
        sdf.timeZone = TimeZone.getDefault()
        return sdf.format(Date(dt * 1000)).uppercase()
    }

    fun getTemperature(): Double = main.temp

    fun getDescription(): String = if (weather.isNotEmpty()) weather[0].description else ""

    fun getIcon(): String = if (weather.isNotEmpty()) weather[0].icon else ""
}

data class MainFive(
    val temp: Double,
    val feels_like: Double,
    val temp_min: Double,
    val temp_max: Double,
    val pressure: Int,
    val sea_level: Int,
    val grnd_level: Int,
    val humidity: Int,
    val temp_kf: Double
)

data class WeatherFive(
    val id: Int,
    val main: String,
    val description: String,
    val icon: String
)

data class Clouds(
    val all: Int
)

data class Wind(
    val speed: Double,
    val deg: Int,
    val gust: Double
)

data class Rain(
    val `3h`: Double
)

data class SysFive(
    val pod: String
)