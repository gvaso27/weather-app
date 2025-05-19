package com.sgvasalia.myapplication.screens

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import coil.load
import com.sgvasalia.myapplication.R
import com.sgvasalia.myapplication.apis.ApiConstants
import com.sgvasalia.myapplication.apis.models.FiveDayWeatherResponse
import com.sgvasalia.myapplication.apis.repo.WeatherRepository
import com.sgvasalia.myapplication.databinding.FragmentHourlyBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HourlyFragment : Fragment() {

    private var _binding: FragmentHourlyBinding? = null
    private val binding get() = _binding!!

    private var currentCity: String = "Tbilisi"

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHourlyBinding.inflate(inflater, container, false)
        fiveDayWeather(currentCity)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        parentFragmentManager.setFragmentResultListener(
            "info_for_hourly_page",
            viewLifecycleOwner
        ) { _, bundle ->
            val cityName = bundle.getString("city_name") ?: "Tbilisi"
            if (cityName != currentCity) {
                currentCity = cityName
                fiveDayWeather(cityName)
            }
        }

        binding.imageButtonGeorgia.setOnClickListener {
            currentCity = "Tbilisi"
            fiveDayWeather(currentCity)
        }
        binding.imageButtonUk.setOnClickListener {
            currentCity = "London"
            fiveDayWeather(currentCity)
        }
        binding.imageButtonJamaica.setOnClickListener {
            currentCity = "Kingston"
            fiveDayWeather(currentCity)
        }
    }

    private fun fiveDayWeather(city: String) {
        WeatherRepository.getFiveDay.getFiveDayWeatherByCity(city, ApiConstants.API_KEY)
            .enqueue(object : Callback<FiveDayWeatherResponse> {
                override fun onResponse(
                    call: Call<FiveDayWeatherResponse>,
                    response: Response<FiveDayWeatherResponse>
                ) {
                    binding.capital.text = city
                    setFragmentResult("info_for_main_page", bundleOf("city_name" to city))
                    val data = response.body()
                    if (response.isSuccessful && data != null) {
                        binding.itemsContainer.removeAllViews()
                        for (weather in data.list) {
                            addHourlyItem(
                                weather.getFormattedDate(),
                                weather.getIcon(),
                                weather.getTemperature().toString(),
                                weather.getDescription()
                            )
                        }
                    } else {
                        val errorBody = response.errorBody()?.string()
                        binding.capital.text = "Failed: ${response.code()} - $errorBody"
                    }
                }

                override fun onFailure(call: Call<FiveDayWeatherResponse>, t: Throwable) {
                    binding.capital.text = "Error: ${t.localizedMessage}"
                }
            })
    }

    private fun addHourlyItem(time: String, iconUrl: String, temp: String, description: String) {
        val itemView = layoutInflater.inflate(R.layout.item, null, false)

        itemView.findViewById<TextView>(R.id.text_time).text = time
        itemView.findViewById<TextView>(R.id.text_temp).text = "${temp}°C"
        itemView.findViewById<TextView>(R.id.text_description).text = description
        itemView.findViewById<ImageView>(R.id.weather_icon).load("https://openweathermap.org/img/wn/${iconUrl}@2x.png")

        binding.itemsContainer.addView(itemView)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
