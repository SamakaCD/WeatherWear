package com.ivansadovyi.weather.wear

import java.util.*

data class ForecastedWeather(
		val icon: Weather.Icon,
		val date: Date,
		val avgTemperature: Int,
		val minTemperature: Int,
		val maxTemperature: Int
)
