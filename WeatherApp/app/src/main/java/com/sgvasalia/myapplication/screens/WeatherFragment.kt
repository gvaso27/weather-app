package com.sgvasalia.myapplication.screens

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import coil.load
import com.sgvasalia.myapplication.apis.ApiConstants
import com.sgvasalia.myapplication.apis.models.WeatherResponse
import com.sgvasalia.myapplication.apis.repo.WeatherRepository
import com.sgvasalia.myapplication.databinding.FragmentWeatherBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class WeatherFragment : Fragment() {

    private var _binding: FragmentWeatherBinding? = null
    private val binding get() = _binding!!

    private var currentCity: String = "Tbilisi"

    companion object {
        const val DAY_NIGHT_REQUEST_KEY = "day_night_request_key"
        const val IS_DAY_KEY = "is_day_key"
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentWeatherBinding.inflate(inflater, container, false)
        fetchWeather(currentCity)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        parentFragmentManager.setFragmentResultListener(
            "info_for_main_page",
            viewLifecycleOwner
        ) { _, bundle ->
            val cityName = bundle.getString("city_name") ?: "Tbilisi"
            if (cityName != currentCity) {
                currentCity = cityName
                fetchWeather(cityName)
            }
        }

        binding.imageButtonGeorgia.setOnClickListener {
            currentCity = "Tbilisi"
            fetchWeather(currentCity)
        }
        binding.imageButtonUk.setOnClickListener {
            currentCity = "London"
            fetchWeather(currentCity)
        }
        binding.imageButtonJamaica.setOnClickListener {
            currentCity = "Kingston"
            fetchWeather(currentCity)
        }
    }

    private fun fetchWeather(city: String) {
        WeatherRepository.getSingleDay.getWeatherByCity(city, ApiConstants.API_KEY)
            .enqueue(object : Callback<WeatherResponse> {
                override fun onResponse(
                    call: Call<WeatherResponse>,
                    response: Response<WeatherResponse>
                ) {
                    val data = response.body()
                    if (response.isSuccessful && data != null) {
                        val isDay = data.dt in data.sys.sunrise..data.sys.sunset

                        setFragmentResult(DAY_NIGHT_REQUEST_KEY, bundleOf(IS_DAY_KEY to isDay))
                        setFragmentResult("info_for_hourly_page", bundleOf("city_name" to city))

                        binding.apply {
                            textTemperature.text = "${data.main.temp}°C"
                            valueTemperature.text = "${data.main.temp}°C"
                            valueFeelsLike.text = "${data.main.feels_like}°C"
                            valuePressure.text = "${data.main.pressure} hPa"
                            valueHumidity.text = "${data.main.humidity}%"
                            textWeatherCondition.text =
                                data.weather[0].description.replaceFirstChar { it.uppercase() }
                            weatherIcon.load("https://openweathermap.org/img/wn/${data.weather[0].icon}@2x.png")
                            capital.text = city
                        }
                    } else {
                        binding.valueTemperature.text = "Failed to load weather data"
                    }
                }

                override fun onFailure(call: Call<WeatherResponse>, t: Throwable) {
                    binding.valueTemperature.text = "Error: ${t.localizedMessage}"
                }
            })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
