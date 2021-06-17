package com.priomkhan.salahtime.services

import android.app.*
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.*
import android.widget.Toast
import com.priomkhan.salahtime.R
import com.priomkhan.salahtime.ui.main.MainActivity
import com.priomkhan.salahtime.ui.start.SplashActivity
import com.priomkhan.salahtime.util.Actions
import com.priomkhan.salahtime.util.GlobalVariables.Companion.TAG
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.*

class EndLessService : Service() {

    private var wakeLock: PowerManager.WakeLock? = null
    private var isServiceStarted = false

    override fun onCreate() {
        super.onCreate()
        Timber.tag(TAG).d("The service has been created".toUpperCase(Locale.ROOT))
        val notification = createNotification()
        startForeground(resources.getString(R.string.endless_service_foreground_id).toInt(), notification)
    }

    override fun onDestroy() {
        super.onDestroy()
        Timber.tag(TAG).d("The service has been destroyed.".toUpperCase(Locale.ROOT))
        Toast.makeText(this, "All Notification Stopped.", Toast.LENGTH_SHORT).show()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Timber.tag(TAG).d("onStartCommand executed with startId: $startId")
        if (intent != null) {
            val action = intent.action
            Timber.tag(TAG).d("using an intent with action $action")
            val msg = Message()
            msg.arg1 = startId
            when (action) {

                Actions.START.name -> startService()
                Actions.STOP.name -> stopService()
                else -> Timber.tag(TAG).d("This should never happen. No action in the received intent")
            }
        } else {
            Timber.tag(TAG).d(
                "with a null intent. It has been probably restarted by the system."
            )
        }
        // by returning this we make sure the service is restarted if the system kills the service
        return START_STICKY
    }


    private fun startService() {
        if (isServiceStarted) return
        Timber.tag(TAG).d("Starting the foreground service task")
        Toast.makeText(this, "Service starting its task", Toast.LENGTH_SHORT).show()
        isServiceStarted = true
        setServiceState(this, ServiceState.STARTED)

        // we need this lock so our service gets not affected by Doze Mode
        wakeLock =
            (getSystemService(Context.POWER_SERVICE) as PowerManager).run {
                newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "EndlessService::lock").apply {
                    acquire()
                }
            }

        // we're starting a loop in a coroutine
        GlobalScope.launch(Dispatchers.IO) {
            while (isServiceStarted) {
                launch(Dispatchers.IO) {
                    keepMeAlive()
                }
                delay(1 * 60 * 1000)
            }
            Timber.tag(TAG).d("End of the loop for the service")
        }
    }

    private fun stopService() {
        Timber.tag(TAG).d("Stopping the foreground service")
        Toast.makeText(this, "Service stopping", Toast.LENGTH_SHORT).show()
        try {
            wakeLock?.let {
                if (it.isHeld) {
                    it.release()
                }
            }
            stopForeground(true)
            stopSelf()
        } catch (e: Exception) {
            Timber.tag(TAG).d("Service stopped without being started: ${e.message}")
        }
        isServiceStarted = false
        setServiceState(this, ServiceState.STOPPED)
        stopSelf()
    }


    private fun keepMeAlive() {

        Timber.tag(TAG).d("I am Alive at ${Date()}")


    }

    private fun createNotification(): Notification {
        val notificationChannelId = applicationContext.getString(R.string.endless_service_notifications_channel_id)
        // depending on the Android API that we're dealing with we will have
        // to use a specific method to create the notification
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            val channel = NotificationChannel(
                notificationChannelId,
                getString(R.string.endless_service_notifications_channel_name),
                NotificationManager.IMPORTANCE_HIGH
            ).let {
                it.description = applicationContext.getString(R.string.endless_service_notifications_channel_description)
                it.enableLights(true)
                it.lightColor = Color.RED
                it.enableVibration(true)
                it.vibrationPattern = longArrayOf(100, 200, 300, 400, 500, 400, 300, 200, 400)
                it
            }
            notificationManager.createNotificationChannel(channel)
        }

        val pendingIntent: PendingIntent = Intent(applicationContext, MainActivity::class.java).let { notificationIntent ->
            PendingIntent.getActivity(applicationContext, System.currentTimeMillis().toInt(), notificationIntent, 0)
        }

        val builder: Notification.Builder = Notification.Builder(
            applicationContext,
            notificationChannelId
        )

        return builder
            .setContentTitle(applicationContext.getString(R.string.endless_service_notifications_content_title))
            .setContentText(applicationContext.getString(R.string.endless_service_notifications_content_text))
            .setContentIntent(pendingIntent)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setTicker("Ticker text")
            .build()

    }

    override fun onBind(intent: Intent): IBinder? {
        Timber.tag(TAG).d("Some component want to bind with the service")
        // We don't provide binding, so return null
        return null
    }

    companion object {
        //private const val ENDLESS_SERVICE_CHANNEL_ID = "13131313"
    }
}