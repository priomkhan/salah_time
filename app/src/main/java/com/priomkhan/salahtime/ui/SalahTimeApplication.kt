package com.priomkhan.salahtime.ui

import android.app.Activity
import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.pm.ActivityInfo
import android.os.Bundle
import com.priomkhan.salahtime.BuildConfig
import com.priomkhan.salahtime.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber


class SalahTimeApplication : Application(){
    private val applicationScope = CoroutineScope(Dispatchers.IO)
    override fun onCreate() {
        super.onCreate()

        //Prevent Landscape mood.
        registerActivityLifecycleCallbacks(object: Application.ActivityLifecycleCallbacks{
            override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
                activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
            }

            override fun onActivityStarted(activity: Activity) {

            }

            override fun onActivityResumed(activity: Activity) {

            }

            override fun onActivityPaused(activity: Activity) {

            }

            override fun onActivityStopped(activity: Activity) {

            }

            override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {

            }

            override fun onActivityDestroyed(activity: Activity) {

            }

        })

        createNotificationChannel()

        delayedInit()



    }

    private fun createNotificationChannel() {
        val serviceChannel = NotificationChannel(
            resources.getString(R.string.alarm_service_notifications_channel_id),
            resources.getString(R.string.alarm_service_notifications_channel_name),
            NotificationManager.IMPORTANCE_DEFAULT
        )
        val manager = getSystemService(
            NotificationManager::class.java
        )
        manager.createNotificationChannel(serviceChannel)
    }

    private fun delayedInit() {
        applicationScope.launch {
            //Initialize Timber Log
            if (BuildConfig.DEBUG){
                Timber.plant(Timber.DebugTree())
            }

        }

        Timber.tag("PRAYERLOG").d("TeaCupApplication-> Database Initialization Stated.")

    }

}