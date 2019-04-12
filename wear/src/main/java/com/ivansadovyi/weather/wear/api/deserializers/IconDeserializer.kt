package com.ivansadovyi.weather.wear.api.deserializers

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.ivansadovyi.weather.wear.Weather
import java.lang.reflect.Type

class IconDeserializer : JsonDeserializer<Weather.Icon> {

	override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): Weather.Icon {
		val value = json.asString.orEmpty()
		return when {
			value.startsWith("01") -> Weather.Icon.SUNNY
			value.startsWith("02") -> Weather.Icon.PARTLY_CLOUDY
			value.startsWith("03") || value.startsWith("04") -> Weather.Icon.CLOUDY
			value.startsWith("09") || value.startsWith("10") -> Weather.Icon.RAIN
			else -> Weather.Icon.SUNNY
		}
	}
}