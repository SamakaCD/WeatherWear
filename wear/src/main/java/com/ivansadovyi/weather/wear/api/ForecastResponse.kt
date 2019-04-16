package com.ivansadovyi.weather.wear.api

import com.ivansadovyi.weather.wear.ForecastedWeather

data class ForecastResponse(val items: List<ForecastedWeather>)
