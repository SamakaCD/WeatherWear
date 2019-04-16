package com.ivansadovyi.weather.wear

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class Cache(context: Context) {

	private val preferences = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)
	private val gson = Gson()

	fun hasData(): Boolean {
		return preferences.contains(CURRENT_WEATHER) && preferences.contains(FORECAST) && preferences.contains(DAILY_FORECAST)
	}

	fun getCurrentWeather(): Weather {
		val jsonWeather = preferences.getString(CURRENT_WEATHER, null)
		return gson.fromJson(jsonWeather, Weather::class.java)
	}

	fun getForecast(): List<ForecastedWeather>? {
		val jsonForecast = preferences.getString(FORECAST, null)
		val type = object : TypeToken<List<ForecastedWeather>>() { }.type
		return gson.fromJson(jsonForecast, type)
	}

	fun getDailyForecast(): List<ForecastedWeather>? {
		val jsonDailyForecast = preferences.getString(DAILY_FORECAST, null)
		val type = object : TypeToken<List<ForecastedWeather>>() { }.type
		return gson.fromJson(jsonDailyForecast, type)
	}

	fun putCurrentWeather(currentWeather: Weather) {
		val jsonWeather = gson.toJson(currentWeather)
		preferences.edit().putString(CURRENT_WEATHER, jsonWeather).apply()
	}

	fun putForecast(forecast: List<ForecastedWeather>) {
		val type = object : TypeToken<List<ForecastedWeather>>() { }.type
		val jsonForecast = gson.toJson(forecast, type)
		preferences.edit().putString(FORECAST, jsonForecast).apply()
	}

	fun putDailyForecast(dailyForecast: List<ForecastedWeather>) {
		val type = object : TypeToken<List<ForecastedWeather>>() { }.type
		val jsonForecast = gson.toJson(dailyForecast, type)
		preferences.edit().putString(DAILY_FORECAST, jsonForecast).apply()
	}

	companion object {
		private const val PREFERENCES_NAME = "cache"
		private const val CURRENT_WEATHER = "current_weather"
		private const val FORECAST = "forecast"
		private const val DAILY_FORECAST = "daily_forecast"
	}
}