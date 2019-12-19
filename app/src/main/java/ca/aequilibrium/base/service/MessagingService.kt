/**
 * Created by Vincent Cho on 12/19/19.
 * Copyright (c) 2019 aequilibrium LLC . All rights reserved.
 */
package ca.aequilibrium.base.service

import android.content.Intent
import android.graphics.BitmapFactory
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import ca.aequilibrium.base.App
import ca.aequilibrium.base.App.Companion.CHANNEL_ID
import ca.aequilibrium.base.R
import ca.aequilibrium.base.ui.MainActivity
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.orhanobut.logger.Logger
import java.net.URL

class MessagingService : FirebaseMessagingService() {

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        // TODO: Handle FCM messages here.
        // If the application is in the foreground handle both data and notification messages here.
        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated.
        Logger.d("From: " + remoteMessage.from!!)
        Logger.d("Notification Message Body: " + remoteMessage.notification?.body)

        sendNotification(remoteMessage)
    }

    override fun onNewToken(token: String) {
        Logger.d("Refreshed token: $token")
        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.
        // todo
    }

    private fun sendNotification(remoteMessage: RemoteMessage) {
        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)

        //val pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
        //    PendingIntent.FLAG_ONE_SHOT)

        val url_value = URL(remoteMessage.notification?.imageUrl?.toString())
        val mIcon1 = BitmapFactory.decodeStream(url_value.openConnection().getInputStream())

        var builder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_logo)
            .setLargeIcon(mIcon1)
            .setContentTitle(remoteMessage.notification?.title)
            .setContentText(remoteMessage.notification?.body)
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
        /* uncomment to handle this notification by tapping it */
        //.setContentIntent(pendingIntent)

        with(NotificationManagerCompat.from(App.instance)) {
            //todo notificationId need to be a unique int for each notification that you must define
            notify(0, builder.build())
        }
    }
}