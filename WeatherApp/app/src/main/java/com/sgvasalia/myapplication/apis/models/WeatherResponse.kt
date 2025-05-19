package com.sgvasalia.myapplication.apis.models

data class WeatherResponse(
    val name: String,
    val main: Main,
    val weather: List<Weather>,
    val sys: Sys,
    val dt: Long
)

data class Main(
    val temp: Float,
    val feels_like: Float,
    val humidity: Int,
    val pressure: Int
)

data class Weather(
    val description: String,
    val icon: String
)

data class Sys(
    val sunrise: Long,
    val sunset: Long
)
