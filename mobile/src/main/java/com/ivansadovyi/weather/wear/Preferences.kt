package com.ivansadovyi.weather.wear

import android.content.Context

class Preferences(context: Context) {

	private val sharedPreferences = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)

	fun getPlaceName(): String? {
		return sharedPreferences.getString(PLACE_NAME, null)
	}

	fun savePlaceName(placeName: String) {
		return sharedPreferences.edit().putString(PLACE_NAME, placeName).apply()
	}

	companion object {
		private const val PREFERENCES_NAME = "preferences"
		private const val PLACE_NAME = "place_name"
	}
}
