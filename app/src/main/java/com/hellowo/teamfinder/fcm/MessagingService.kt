package com.hellowo.teamfinder.fcm

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.net.Uri
import android.support.v4.app.NotificationCompat
import android.util.Log

import com.google.firebase.messaging.RemoteMessage
import com.hellowo.teamfinder.R
import com.hellowo.teamfinder.ui.activity.MainActivity

import org.json.JSONException
import org.json.JSONObject

class MessagingService : com.google.firebase.messaging.FirebaseMessagingService() {

    override fun onMessageReceived(remoteMessage: RemoteMessage?) {
        if (remoteMessage?.data!!.isNotEmpty()) {
            try {
                val data = JSONObject(remoteMessage.data["data"])
                val subject = data.getString("subject")
                val message = data.getString("message")
                Log.d("aaa", subject + "/" + message)
                sendNotification(subject, message)
            } catch (e: JSONException) {
                e.printStackTrace()
            }

        }
    }

    private fun sendNotification(subject: String, message: String) {
        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val pendingIntent = PendingIntent.getActivity(this, 0, intent,
                PendingIntent.FLAG_ONE_SHOT)

        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val notificationBuilder = NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher_round)
                .setColor(getColor(R.color.colorPrimary))
                .setContentTitle(subject)
                .setContentText(message)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent)

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        notificationManager.notify(0, notificationBuilder.build())
    }
}