package com.hellowo.teamfinder.fcm

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.media.RingtoneManager
import android.net.Uri
import android.support.v4.app.NotificationCompat
import android.util.Log
import com.bumptech.glide.Glide
import com.bumptech.glide.request.animation.GlideAnimation
import com.bumptech.glide.request.target.SimpleTarget

import com.google.firebase.messaging.RemoteMessage
import com.hellowo.teamfinder.AppConst
import com.hellowo.teamfinder.R
import com.hellowo.teamfinder.ui.activity.MainActivity
import com.hellowo.teamfinder.utils.PUSH_TYPE_CHAT_MESSAGE
import com.hellowo.teamfinder.utils.makePublicPhotoUrl
import jp.wasabeef.glide.transformations.CropCircleTransformation

import org.json.JSONException
import org.json.JSONObject
import android.content.pm.PackageManager



class MessagingService : com.google.firebase.messaging.FirebaseMessagingService() {

    override fun onMessageReceived(remoteMessage: RemoteMessage?) {
        if (remoteMessage?.data!!.isNotEmpty()) {
            try {
                val data = JSONObject(remoteMessage.data["data"])
                val pushType = data.getInt("pushType")
                when(pushType) {
                    PUSH_TYPE_CHAT_MESSAGE -> makeChatMessageNoti(data)
                }
            } catch (e: JSONException) {
                e.printStackTrace()
            }
        }
    }

    private fun makeChatMessageNoti(data: JSONObject) {
        val userId = data.getString("userId")
        val userName = data.getString("userName")
        val message = data.getString("message")
        val chatId = data.getString("chatId")
        var resource: Bitmap? = null
        try{
            resource = Glide.with(this).load(makePublicPhotoUrl(userId))
                    .asBitmap().transform(CropCircleTransformation(this)).into(100, 100).get()
        }catch (e: Exception){}

        val manager = packageManager
        val intent = manager.getLaunchIntentForPackage(packageName)
        intent.addCategory(Intent.CATEGORY_LAUNCHER)
        intent.putExtra(AppConst.EXTRA_CHAT_ID, chatId)
        val pendingIntent = PendingIntent.getActivity(this,0, intent, PendingIntent.FLAG_ONE_SHOT)

        sendNotification(userName, message, resource, pendingIntent)
    }

    private fun sendNotification(subject: String, message: String, icon: Bitmap?, pIntent: PendingIntent) {
        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val notificationBuilder = NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.small_logo)
                .setColor(getColor(R.color.colorPrimary))
                .setContentTitle(subject)
                .setContentText(message)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pIntent)

        icon?.let { notificationBuilder.setLargeIcon(it) }

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        notificationManager.notify(0, notificationBuilder.build())
    }
}