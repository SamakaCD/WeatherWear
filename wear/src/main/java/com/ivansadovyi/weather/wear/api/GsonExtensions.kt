package com.ivansadovyi.weather.wear.api

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonElement
import com.google.gson.reflect.TypeToken

inline fun <reified T> JsonElement.deserialize(context: JsonDeserializationContext): T {
	val type = object : TypeToken<T>() {}.type
	return context.deserialize(this, type)
}
