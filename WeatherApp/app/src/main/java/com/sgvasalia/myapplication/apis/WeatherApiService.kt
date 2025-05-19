package com.sgvasalia.myapplication.apis.service

import com.sgvasalia.myapplication.apis.models.FiveDayWeatherResponse
import com.sgvasalia.myapplication.apis.models.WeatherResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherApiService {
    @GET("weather")
    fun getWeatherByCity(
        @Query("q") city: String,
        @Query("appid") apiKey: String,
        @Query("units") units: String = "metric"
    ): Call<WeatherResponse>

    @GET("forecast")
    fun getFiveDayWeatherByCity(
        @Query("q") city: String,
        @Query("appid") apiKey: String,
        @Query("units") units: String = "metric"
    ): Call<FiveDayWeatherResponse>
}
