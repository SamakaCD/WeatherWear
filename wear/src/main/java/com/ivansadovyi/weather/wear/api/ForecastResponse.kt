package com.ivansadovyi.weather.wear.api

import com.ivansadovyi.weather.wear.DailyWeather

data class ForecastResponse(val items: List<DailyWeather>)
