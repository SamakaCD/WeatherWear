package com.ivansadovyi.weather.wear.api.deserializers

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.ivansadovyi.weather.wear.Weather
import com.ivansadovyi.weather.wear.api.CurrentWeatherResponse
import com.ivansadovyi.weather.wear.api.deserialize
import java.lang.reflect.Type

class CurrentWeatherResponseDeserializer : JsonDeserializer<CurrentWeatherResponse> {

	override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): CurrentWeatherResponse {
		val jsonObject = json.asJsonObject
		return CurrentWeatherResponse(
				weather = Weather(
						temperature = jsonObject.getAsJsonObject("main")["temp"].asInt,
						icon = jsonObject.getAsJsonArray("weather")[0].asJsonObject["icon"].deserialize(context),
						description = jsonObject.getAsJsonArray("weather")[0].asJsonObject["main"].asString
				)
		)
	}
}
