package com.ivansadovyi.weather.wear

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.os.Bundle
import android.support.wearable.activity.WearableActivity
import android.view.View
import androidx.databinding.DataBindingUtil
import com.crashlytics.android.Crashlytics
import com.google.android.gms.wearable.DataMapItem
import com.google.android.gms.wearable.Wearable
import com.ivansadovyi.weather.wear.api.ApiServiceContainer
import com.ivansadovyi.weather.wear.databinding.ActivityMainBinding
import io.fabric.sdk.android.Fabric
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.*
import kotlinx.coroutines.tasks.await
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class MainActivity : WearableActivity() {

	private val cache by lazy { Cache(this) }
	private val coroutineScope = CoroutineScope(Dispatchers.Main + SupervisorJob())
	private lateinit var binding: ActivityMainBinding

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
		Fabric.with(this, Crashlytics())
		coroutineScope.launch(exceptionHandler) {
			restoreCachedData()
			requestNetwork()
			val location = fetchLocation()

			val currentWeatherResponse = async {
				val currentWeather = ApiServiceContainer.apiService
						.getCurrentWeatherAsync(location.latitude, location.longitude).await().weather
				cache.putCurrentWeather(currentWeather)
				binding.currentWeather = currentWeather
			}

			val forecastResponse = async {
				val forecast = ApiServiceContainer.apiService
						.getForecastAsync(location.latitude, location.longitude).await().items
				cache.putForecast(forecast)
				binding.forecast = forecast
			}

			val dailyForecastResponse = async {
				val dailyForecast = ApiServiceContainer.apiService
						.getForecastAsync(location.latitude, location.longitude).await().items
						.groupBy { it.date.date }
						.map { (_, items) ->
							ForecastedWeather(
									icon = items.first().icon,
									date = items.first().date,
									avgTemperature = items.map { it.avgTemperature }.average().toInt(),
									minTemperature = items.map { it.minTemperature }.min() ?: 0,
									maxTemperature = items.map { it.maxTemperature }.max() ?: 0
							)
						}
				cache.putDailyForecast(dailyForecast)
				binding.dailyForecast = dailyForecast
			}

			currentWeatherResponse.await()
			forecastResponse.await()
			dailyForecastResponse.await()
			binding.loading = false
		}
	}

	fun onDailyForecastClick(view: View) {
		scrollView.pageScroll(View.FOCUS_DOWN)
	}

	private suspend fun fetchLocation(): Location {
		val locationDataItem = Wearable.getDataClient(this@MainActivity).dataItems.await().first()
		val locationDataMap = DataMapItem.fromDataItem(locationDataItem).dataMap
		val latitude = locationDataMap.getDouble("latitude")
		val longitude = locationDataMap.getDouble("longitude")
		return Location(latitude, longitude)
	}

	private suspend fun requestNetwork() {
		val request = NetworkRequest.Builder()
				.addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
				.addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR)
				.addTransportType(NetworkCapabilities.TRANSPORT_BLUETOOTH)
				.addCapability(NetworkCapabilities.NET_CAPABILITY_NOT_METERED)
				.addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
				.build()

		return suspendCoroutine { continuation ->
			val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
			connectivityManager.requestNetwork(request, object : ConnectivityManager.NetworkCallback() {
				override fun onAvailable(network: Network) {
					if (connectivityManager.bindProcessToNetwork(network)) {
						continuation.resume(Unit)
					} else {
						continuation.resumeWithException(RuntimeException("Can not bind process to network"))
					}
				}
			})
		}
	}

	private fun restoreCachedData() {
		if (cache.hasData()) {
			binding.currentWeather = cache.getCurrentWeather()
			binding.forecast = cache.getForecast()
			binding.dailyForecast = cache.getDailyForecast()
			binding.loading = false
		}
	}

	private val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
		throwable.printStackTrace()
		Crashlytics.logException(throwable)
	}
}
