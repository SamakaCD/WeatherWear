package com.ivansadovyi.weather.wear.api.deserializers

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.ivansadovyi.weather.wear.ForecastedWeather
import com.ivansadovyi.weather.wear.api.ForecastResponse
import com.ivansadovyi.weather.wear.api.deserialize
import java.lang.reflect.Type
import java.util.*

class ForecastResponseDeserializer : JsonDeserializer<ForecastResponse> {

	override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): ForecastResponse {
		return ForecastResponse(
				items = json.asJsonObject
						.getAsJsonArray("list")
						.map { deserializeItem(it.asJsonObject, context) }
		)
	}

	private fun deserializeItem(json: JsonObject, context: JsonDeserializationContext): ForecastedWeather {
		return ForecastedWeather(
				icon = json.getAsJsonArray("weather")[0].asJsonObject["icon"].deserialize(context),
				date = Date(json["dt"].asLong * 1000),
				avgTemperature = json.getAsJsonObject("main")["temp"].asInt,
				minTemperature = json.getAsJsonObject("main")["temp_min"].asInt,
				maxTemperature = json.getAsJsonObject("main")["temp_max"].asInt
		)
	}
}