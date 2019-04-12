package com.ivansadovyi.weather.wear.api

import com.google.gson.GsonBuilder
import com.ivansadovyi.weather.wear.Weather
import com.ivansadovyi.weather.wear.api.deserializers.CurrentWeatherResponseDeserializer
import com.ivansadovyi.weather.wear.api.deserializers.ForecastResponseDeserializer
import com.ivansadovyi.weather.wear.api.deserializers.IconDeserializer
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiServiceContainer {

	val apiService: ApiService

	init {
		val gson = GsonBuilder()
				.registerTypeAdapter(Weather.Icon::class.java, IconDeserializer())
				.registerTypeAdapter(CurrentWeatherResponse::class.java, CurrentWeatherResponseDeserializer())
				.registerTypeAdapter(ForecastResponse::class.java, ForecastResponseDeserializer())
				.create()

		val httpClient = OkHttpClient.Builder()
				.addInterceptor(object : Interceptor {
					override fun intercept(chain: Interceptor.Chain): Response {
						val original = chain.request()
						val originalHttpUrl = original.url()
						val url = originalHttpUrl.newBuilder()
								.addQueryParameter("APPID", ApiService.API_KEY)
								.addQueryParameter("units", "metric")
								.build()

						val requestBuilder = original.newBuilder().url(url)
						val request = requestBuilder.build()
						return chain.proceed(request)
					}
				})
				.addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
				.build()

		apiService = Retrofit.Builder()
				.baseUrl(ApiService.API_URL)
				.client(httpClient)
				.addConverterFactory(GsonConverterFactory.create(gson))
				.addCallAdapterFactory(CoroutineCallAdapterFactory())
				.build()
				.create(ApiService::class.java)
	}
}
