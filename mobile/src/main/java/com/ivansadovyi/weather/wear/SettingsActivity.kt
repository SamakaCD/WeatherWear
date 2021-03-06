package com.ivansadovyi.weather.wear

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.crashlytics.android.Crashlytics
import com.google.android.gms.wearable.PutDataMapRequest
import com.google.android.gms.wearable.Wearable
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.model.TypeFilter
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import io.fabric.sdk.android.Fabric
import kotlinx.android.synthetic.main.activity_settings.*
import java.util.*

class SettingsActivity : AppCompatActivity() {

	private val preferences by lazy { Preferences(this) }

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		Fabric.with(this, Crashlytics())
		setContentView(R.layout.activity_settings)
		setTitle(R.string.settings_title)
		restorePreferences()
		Places.initialize(this, PLACES_API_KEY)
	}

	fun onSelectLocationClick(view: View) {
		val fields = Arrays.asList(Place.Field.NAME, Place.Field.LAT_LNG)
		val intent = Autocomplete.IntentBuilder(AutocompleteActivityMode.FULLSCREEN, fields)
				.setTypeFilter(TypeFilter.CITIES)
				.build(this)
		startActivityForResult(intent, REQUEST_CODE_PLACE_AUTOCOMPLETE)
	}

	fun onSendFeedbackClick(view: View) {
		val intent = Intent(Intent.ACTION_SENDTO)
		intent.data = Uri.parse("mailto:")
		intent.putExtra(Intent.EXTRA_EMAIL, getString(R.string.feedback_email))
		intent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.feedback_subject))
		if (intent.resolveActivity(packageManager) != null) {
			startActivity(intent)
		}
	}

	private fun onSelectPlace(place: Place) {
		preferences.savePlaceName(place.name.orEmpty())
		restorePreferences()

		val dataClient = Wearable.getDataClient(this)
		val request = PutDataMapRequest.create("/location")
		request.dataMap.putDouble("latitude", place.latLng?.latitude ?: 0.0)
		request.dataMap.putDouble("longitude", place.latLng?.longitude ?: 0.0)
		request.setUrgent()
		dataClient.putDataItem(request.asPutDataRequest())
	}

	private fun restorePreferences() {
		preferences.getPlaceName()?.let { placeName ->
			placeNameTextView.text = placeName
			placeNameTextView.visibility = View.VISIBLE
		}
	}

	override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
		super.onActivityResult(requestCode, resultCode, data)
		when (requestCode) {
			REQUEST_CODE_PLACE_AUTOCOMPLETE -> {
				if (resultCode == Activity.RESULT_OK && data != null) {
					val place = Autocomplete.getPlaceFromIntent(data)
					onSelectPlace(place)
				}
			}
		}
	}

	companion object {
		private const val REQUEST_CODE_PLACE_AUTOCOMPLETE = 1
		private const val PLACES_API_KEY = "AIzaSyDJMPXdHdSU8rlUJuDqSyEobHS0JeoAFYc"
	}
}
