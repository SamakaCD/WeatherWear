package com.ivansadovyi.weather.wear.api

import kotlinx.coroutines.Deferred
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {

	@GET("data/2.5/weather")
	fun getCurrentWeatherAsync(
			@Query("lat") latitude: Double,
			@Query("lon") longitude: Double
	): Deferred<CurrentWeatherResponse>

	companion object {
		const val API_URL = "https://api.openweathermap.org"
		const val API_KEY = "847f321ade59558c2aa7234a4d0182a1"
	}
}
