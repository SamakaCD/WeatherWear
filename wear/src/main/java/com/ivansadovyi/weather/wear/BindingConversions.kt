package com.ivansadovyi.weather.wear

import androidx.databinding.BindingConversion

object BindingConversions {

	@JvmStatic
	@BindingConversion
	fun convertWeatherIconToResId(icon: Weather.Icon?): Int {
		return when (icon) {
			Weather.Icon.SUNNY -> R.drawable.ic_sunny
			Weather.Icon.PARTLY_CLOUDY -> R.drawable.ic_partly_cloudy
			Weather.Icon.CLOUDY -> R.drawable.ic_cloudy
			Weather.Icon.RAIN -> R.drawable.ic_rain
			null -> 0
		}
	}
}
