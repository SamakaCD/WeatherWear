package com.ivansadovyi.weather.wear

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.os.Bundle
import android.support.wearable.activity.WearableActivity
import androidx.databinding.DataBindingUtil
import com.google.android.gms.wearable.DataMapItem
import com.google.android.gms.wearable.Wearable
import com.ivansadovyi.weather.wear.api.ApiServiceContainer
import com.ivansadovyi.weather.wear.databinding.ActivityMainBinding
import kotlinx.coroutines.*
import kotlinx.coroutines.tasks.await
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine


class MainActivity : WearableActivity() {

	private val coroutineScope = CoroutineScope(Dispatchers.Main + SupervisorJob())
	private lateinit var binding: ActivityMainBinding

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_main)
		binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
		coroutineScope.launch(exceptionHandler) {
			requestNetwork()
			val location = fetchLocation()
			binding.currentWeather = ApiServiceContainer.apiService
					.getCurrentWeatherAsync(location.latitude, location.longitude).await().weather
			binding.loading = false
		}
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

	private val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
		throwable.printStackTrace()
	}
}
