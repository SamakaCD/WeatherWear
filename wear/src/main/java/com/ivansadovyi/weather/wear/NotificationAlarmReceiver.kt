package com.ivansadovyi.weather.wear

import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat

class NotificationAlarmReceiver : BroadcastReceiver() {

	override fun onReceive(context: Context, intent: Intent?) {
		val contentIntent = Intent(context, MainActivity::class.java)
		val pendingIntent = PendingIntent.getActivity(context, 0, contentIntent, PendingIntent.FLAG_UPDATE_CURRENT)
		val notification = NotificationCompat.Builder(context)
				.setSmallIcon(R.drawable.ic_rain_notification)
				.setContentTitle(context.getString(R.string.notification_rain_title))
				.setContentText(context.getString(R.string.notification_rain_description))
				.setColor(ContextCompat.getColor(context, R.color.colorRainNotification))
				.setContentIntent(pendingIntent)
				.setPriority(NotificationCompat.PRIORITY_HIGH)
				.setDefaults(NotificationCompat.DEFAULT_ALL)
				.build()
		val notificationManager = NotificationManagerCompat.from(context)
		notificationManager.notify(0, notification)
	}
}