package com.ivansadovyi.weather.wear

class Weather(
		val temperature: Int,
		val icon: Icon,
		val description: String
) {

	enum class Icon {
		SUNNY,
		PARTLY_CLOUDY,
		CLOUDY,
		RAIN
	}
}