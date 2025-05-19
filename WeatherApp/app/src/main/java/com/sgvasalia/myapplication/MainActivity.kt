package com.sgvasalia.myapplication

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.sgvasalia.myapplication.databinding.ActivityMainBinding
import com.sgvasalia.myapplication.screens.HourlyFragment
import com.sgvasalia.myapplication.screens.WeatherFragment

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private var isDay: Boolean = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        updateBackgroundColor(isDay)

        binding.viewPager.adapter = object : FragmentStateAdapter(this) {
            override fun getItemCount(): Int = 2
            override fun createFragment(position: Int): Fragment =
                if (position == 0) WeatherFragment() else HourlyFragment()
        }

        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> binding.viewPager.currentItem = 0
                R.id.nav_hourly -> binding.viewPager.currentItem = 1
            }
            true
        }

        binding.viewPager.registerOnPageChangeCallback(object : androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                binding.bottomNavigation.menu.getItem(position).isChecked = true
            }
        })

        supportFragmentManager.setFragmentResultListener(
            WeatherFragment.DAY_NIGHT_REQUEST_KEY,
            this
        ) { _, result ->
            val newIsDay = result.getBoolean(WeatherFragment.IS_DAY_KEY)
            if (newIsDay != isDay) {
                isDay = newIsDay
                updateBackgroundColor(isDay)
            }
        }
    }

    private fun updateBackgroundColor(isDay: Boolean) {
        val colorResId = if (isDay) {
            R.color.day_background
        } else {
            R.color.night_background
        }
        binding.viewPager.setBackgroundColor(ContextCompat.getColor(this, colorResId))
    }
}