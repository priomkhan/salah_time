package com.priomkhan.salahtime.services

import android.app.Notification
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.media.RingtoneManager
import android.net.Uri
import android.os.*
import android.widget.Toast
import androidx.core.app.NotificationCompat
import com.batoulapps.adhan.Prayer
import com.priomkhan.salahtime.R
import com.priomkhan.salahtime.ui.Alarm.AlarmActivity
import com.priomkhan.salahtime.util.GlobalVariables.Companion.TAG
import timber.log.Timber
import java.util.*


class AlarmService : Service() {

    private lateinit var mpAdhan: MediaPlayer
    private lateinit var mpFajr: MediaPlayer
    private lateinit var mpSunrise: MediaPlayer
    private lateinit var vibrator: Vibrator
    private lateinit var defaultDeviceAlarm: Uri

    private var serviceLooper: Looper? = null
    private var serviceHandler: ServiceHandler? = null

    // Handler that receives messages from the thread
    private inner class ServiceHandler(looper: Looper) : Handler(looper) {

        override fun handleMessage(msg: Message) {
            // Normally we would do some work here, like download a file.
            // For our sample, we just sleep for 5 seconds.
            try {
                Thread.sleep(5000)
            } catch (e: InterruptedException) {
                // Restore interrupt status.
                Thread.currentThread().interrupt()
            }

            // Stop the service using the startId, so that we don't stop
            // the service in the middle of handling another job
            stopSelf(msg.arg1)
        }
    }

    override fun onCreate() {
        super.onCreate()
        Timber.tag(TAG).d("Alarm Service Started.....")
        try {

            mpAdhan = MediaPlayer.create(this, R.raw.adhan)
            mpFajr = MediaPlayer.create(this, R.raw.adhan_fajr)

            defaultDeviceAlarm = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
            mpSunrise = MediaPlayer.create(this, defaultDeviceAlarm)

            mpAdhan.isLooping = false
            mpFajr.isLooping =false
            mpSunrise.isLooping = false

            vibrator =  getSystemService(Context.VIBRATOR_SERVICE) as Vibrator

        }catch (e: Exception){
            Timber.tag(TAG).d("AlarmService: $e")
        }

    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        val prayerName = intent.getStringExtra(getString(R.string.next_prayer_name))
        Timber.tag(TAG).d("Alarm Service: Next Prayer Name: $prayerName")
        val notificationIntent = Intent(this, AlarmActivity::class.java)

        val pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0)

        val notification: Notification = NotificationCompat.Builder(
            this,
            resources.getString(R.string.alarm_service_notifications_channel_id)
        )
            .setContentTitle(resources.getString(R.string.alarm_service_notifications_content_title))
            .setContentText(
                resources.getString(
                    R.string.alarm_service_notifications_content_text,
                    prayerName
                )
            )
            .setSmallIcon(R.drawable.ic_alarm)
            .setContentIntent(pendingIntent)
            .build()


        when (prayerName) {
            Prayer.FAJR.name -> {

                mpFajr.start()

            }
            Prayer.SUNRISE.name -> {
                mpSunrise.start()
            }
            else -> {
                mpAdhan.start()
            }
        }


        val pattern = longArrayOf(0, 100, 1000, 200, 2000, 300)
        vibrator.vibrate(VibrationEffect.createWaveform(pattern, -1))
        startForeground(
            resources.getString(R.string.alarm_service_foreground_service_id).toInt(),
            notification
        )
        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        Timber.tag(TAG).d("Alarm service has been destroyed.".toUpperCase(Locale.ROOT))
        Toast.makeText(this, "Adhan Stopped.", Toast.LENGTH_SHORT).show()
        mpAdhan.stop()
        mpSunrise.stop()
        mpFajr.stop()
        vibrator.cancel()
        stopForeground(true)
        stopSelf(resources.getString(R.string.alarm_service_foreground_service_id).toInt())

    }

//    private fun isMuted(context: Context): Boolean {
//        val audio: AudioManager = context.getSystemService(AUDIO_SERVICE) as AudioManager
//        return audio.ringerMode != AudioManager.RINGER_MODE_NORMAL
//    }
//
//    fun isCarUiMode(c: Context): Boolean {
//        val uiModeManager = c.getSystemService(Context.UI_MODE_SERVICE) as UiModeManager
//        return if (uiModeManager.currentModeType == Configuration.UI_MODE_TYPE_CAR) {
//            LogHelper.d(TAG, "Running in Car mode")
//            true
//        } else {
//            LogHelper.d(TAG, "Running in a non-Car mode")
//            false
//        }
//    }


    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    companion object{

    }



}
