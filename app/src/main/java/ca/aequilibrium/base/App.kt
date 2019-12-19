/**
 * Created by Vincent Cho on 12/19/19.
 * Copyright (c) 2019 aequilibrium LLC. All rights reserved.
 */
package ca.aequilibrium.base

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import ca.aequilibrium.base.di.appComponent
import ca.aequilibrium.base.di.applicationModules
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class App : Application() {

    override fun onCreate() {
        super.onCreate()
        instance = this
        startKoin {
            androidLogger()
            androidContext(this@App)
            modules(appComponent)
        }
        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = getString(R.string.msg_channel_name)
            val descriptionText = getString(R.string.msg_channel_description)
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            // Register the channel with the system
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    companion object {
        lateinit var instance: App
            private set

        const val RC_READ = 1234 /* Resolve conflict for reading credential */
        const val RC_SAVE = 2345 /* Resolve conflict for saving credential */
        const val RC_GOOGLE_SIGN_IN = 3344 /* Resolve conflict for Google sign in */
        const val CHANNEL_ID = "aeq-base-app"
    }
}
