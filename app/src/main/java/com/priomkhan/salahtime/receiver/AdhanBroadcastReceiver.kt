package com.priomkhan.salahtime.receiver

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.PreferenceManager
import com.batoulapps.adhan.CalculationMethod
import com.batoulapps.adhan.CalculationParameters
import com.batoulapps.adhan.Prayer
import com.priomkhan.salahtime.R
import com.priomkhan.salahtime.repository.DataRepository
import com.priomkhan.salahtime.services.AlarmService
import com.priomkhan.salahtime.util.GlobalVariables
import com.priomkhan.salahtime.util.GlobalVariables.Companion.ALARM_ID
import com.priomkhan.salahtime.util.GlobalVariables.Companion.TAG
import com.priomkhan.salahtime.util.GlobalVariables.Companion.notificationOn
import timber.log.Timber
import java.lang.Exception
import java.util.*


class AdhanBroadcastReceiver : BroadcastReceiver(), SharedPreferences.OnSharedPreferenceChangeListener{
    private lateinit var alarmManager: AlarmManager

    private var long : Double = 0.0
    private var lat : Double = 0.0
    private var fajrTimeAdjustmentValue : Int = 0
    private var dhuhrTimeAdjustmentValue : Int = 0
    private var asrTimeAdjustmentValue : Int = 0
    private var maghribTimeAdjustmentValue : Int = 0
    private var ishaTimeAdjustmentValue : Int = 0
    private var calculationMethod : CalculationParameters = CalculationMethod.NORTH_AMERICA.parameters
    private var prayerName = ""

    //** Member variables to hold the name of the shared preferences file
    private lateinit var mPreferences: SharedPreferences
    //** Name your shared preferences file
    private val sharedPrefFile : String = "com.priomkhan.salahtime"

    override fun onReceive(context: Context?, intent: Intent?) {

        context?.let {
            mPreferences = context.getSharedPreferences(sharedPrefFile, AppCompatActivity.MODE_PRIVATE)
            setupSharedPreference(context)
            try {
                val isAdhanAlarmOn = mPreferences.getBoolean(context.getString(R.string.is_adhan_alarm_on), false)
                Timber.tag(TAG).d("AdhanBroadcastReceiver: isAdhanAlarmOn: $isAdhanAlarmOn")
                if (isAdhanAlarmOn){
                    if (intent?.action == "com.priomkhan.salahtime") {
                        Timber.tag(TAG).d("Alarm Just Fired at ${Date()} for ${intent.getStringExtra(context.getString(R.string.next_prayer_name))}")
                        val nextPrayerName = intent.getStringExtra(context.getString(R.string.next_prayer_name))
                        var nextPrayerAlarmOn =false
                        when (nextPrayerName){
                            Prayer.FAJR.name->{
                                nextPrayerAlarmOn = mPreferences.getBoolean(context.getString(R.string.is_fajr_adhan_alarm_on), false)
                            }
                            Prayer.SUNRISE.name->{
                                nextPrayerAlarmOn = mPreferences.getBoolean(context.getString(R.string.is_sunrise_alarm_on), false)
                            }
                            Prayer.DHUHR.name->{
                                nextPrayerAlarmOn = mPreferences.getBoolean(context.getString(R.string.is_dhuhr_adhan_alarm_on), false)
                            }
                            Prayer.ASR.name->{
                                nextPrayerAlarmOn = mPreferences.getBoolean(context.getString(R.string.is_asr_adhan_alarm_on), false)
                            }
                            Prayer.MAGHRIB.name->{
                                nextPrayerAlarmOn = mPreferences.getBoolean(context.getString(R.string.is_maghrib_adhan_alarm_on), false)
                            }
                            Prayer.ISHA.name->{
                                nextPrayerAlarmOn = mPreferences.getBoolean(context.getString(R.string.is_isha_adhan_alarm_on), false)
                            }
                        }

                        if (nextPrayerAlarmOn){
                            startAlarmService(context, intent)
                        }else{
                            Timber.tag(TAG).d("Alarm for this event is Muted.")
                        }


                        setNextAlarm(it)

                    }else if(intent?.action == "com.priomkhan.salahtime.resetalram"){
                        Timber.tag(TAG).d("Reset Alarm Brodcast Received.")

                        mPreferences = context.getSharedPreferences(sharedPrefFile, AppCompatActivity.MODE_PRIVATE)
                        Timber.tag(TAG).d("Resetting Alarm.")
                        cancelAlarm(it)

                        val nextPrayerName = intent.getStringExtra(context.getString(R.string.next_prayer_name))
                        var nextPrayerAlarmOn =false
                        when (nextPrayerName){
                            Prayer.FAJR.name->{
                                nextPrayerAlarmOn = mPreferences.getBoolean(context.getString(R.string.is_fajr_adhan_alarm_on), false)
                            }
                            Prayer.SUNRISE.name->{
                                nextPrayerAlarmOn = mPreferences.getBoolean(context.getString(R.string.is_sunrise_alarm_on), false)
                            }
                            Prayer.DHUHR.name->{
                                nextPrayerAlarmOn = mPreferences.getBoolean(context.getString(R.string.is_dhuhr_adhan_alarm_on), false)
                            }
                            Prayer.ASR.name->{
                                nextPrayerAlarmOn = mPreferences.getBoolean(context.getString(R.string.is_asr_adhan_alarm_on), false)
                            }
                            Prayer.MAGHRIB.name->{
                                nextPrayerAlarmOn = mPreferences.getBoolean(context.getString(R.string.is_maghrib_adhan_alarm_on), false)
                            }
                            Prayer.ISHA.name->{
                                nextPrayerAlarmOn = mPreferences.getBoolean(context.getString(R.string.is_isha_adhan_alarm_on), false)
                            }
                        }

                        if (nextPrayerAlarmOn){
                            startAlarmService(context, intent)
                        }else{
                            Timber.tag(TAG).d("Alarm for this event is Muted.")
                        }
                        setNextAlarm(it)

                    }
                }
            }catch (e: Exception){
                Timber.tag(TAG).e(e)
            }


        }

    }

    private fun startAlarmService(context: Context, intent: Intent) {
        try {
            prayerName = intent.getStringExtra(context.getString(R.string.next_prayer_name)).orEmpty()

            saveToSharedPref(context)

            Timber.tag(TAG).d("Starting Alarm Service for $prayerName")

            val intentService = Intent(context, AlarmService::class.java)
            intentService.putExtra(context.getString(R.string.next_prayer_name), prayerName)
            context.startForegroundService(intentService)
        }catch (e: Exception){
            Timber.tag(TAG).e(e)
        }

    }

    private fun saveToSharedPref(context: Context){
        val preferencesEditor : SharedPreferences.Editor = mPreferences.edit()
        if (prayerName.isNotBlank()){
            Timber.tag(TAG).d( "AdhanBroadcastReceiver: Saving to SharedPreference: $prayerName")
            preferencesEditor.putString(context.getString(R.string.next_prayer_name), prayerName)
            preferencesEditor.apply()
        }else{
            Timber.tag(TAG).d( "Nothing to Save in SharedPreference.")
        }
    }

    fun setNextAlarm(context: Context){

        mPreferences = context.getSharedPreferences(sharedPrefFile, AppCompatActivity.MODE_PRIVATE)

        setupSharedPreference(context)

        val longitudeString = mPreferences.getString(context.getString(R.string.last_location_longitude), "0.0")
        longitudeString?.let {long->
            Timber.tag(TAG).d("####SharedPref: Longitude: $long")
            this.long = long.toDouble()
        }

        val latitudeString = mPreferences.getString(context.getString(R.string.last_location_latitude), "0.0")
        latitudeString?.let {lat->
            Timber.tag(TAG).d("####SharedPref: Latitude: $lat")
            this.lat = lat.toDouble()
        }

        this.fajrTimeAdjustmentValue = mPreferences.getInt(context.getString(R.string.fajr_time_adjustment), 0)
        this.dhuhrTimeAdjustmentValue = mPreferences.getInt(context.getString(R.string.dhuhr_time_adjustment), 0)
        this.asrTimeAdjustmentValue = mPreferences.getInt(context.getString(R.string.asr_time_adjustment), 0)
        this.maghribTimeAdjustmentValue = mPreferences.getInt(context.getString(R.string.maghrib_time_adjustment), 0)
        this.ishaTimeAdjustmentValue = mPreferences.getInt(context.getString(R.string.isha_time_adjustment), 0)

        val calculationMethodString = mPreferences.getString(context.getString(R.string.adhan_time_calculation_method), CalculationMethod.NORTH_AMERICA.name)
        calculationMethodString?.let {
            calculationMethod = CalculationMethod.valueOf(it).parameters
        }

        val params: CalculationParameters = calculationMethod
        params.adjustments.fajr = this.fajrTimeAdjustmentValue
        params.adjustments.dhuhr = this.dhuhrTimeAdjustmentValue
        params.adjustments.asr = this.asrTimeAdjustmentValue
        params.adjustments.maghrib = this.maghribTimeAdjustmentValue
        params.adjustments.isha = this.ishaTimeAdjustmentValue

        // Create an explicit intent for an Activity in your app
        alarmManager= context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        val intent = Intent(context, AdhanBroadcastReceiver::class.java)
        intent.action = "com.priomkhan.salahtime"



        val repo = DataRepository.getInstance(context)!!
        val nextPrayer = repo.getNextPrayerTime(lat, long, params)
        var nextPrayerName = ""
        var nextPrayerTime = Calendar.getInstance().time

        nextPrayer.let {
            if (nextPrayer.isNotEmpty()){

                for (value in nextPrayer){
                    nextPrayerName= value.key
                    nextPrayerTime = value.value
                }

                Timber.tag(TAG).d("New Alarm Created at ${Date()},  NextPrayerName: $nextPrayerName, NextPrayerTime: $nextPrayerTime")

                intent.putExtra(context.getString(R.string.next_prayer_name), nextPrayerName)
                val cal: Calendar = Calendar.getInstance(TimeZone.getDefault(), Locale.getDefault())


                //For Test alarm in next 1 min
//                    cal.apply {
//                        set(Calendar.HOUR_OF_DAY, get(Calendar.HOUR_OF_DAY))
//                        set(Calendar.MINUTE, get(Calendar.MINUTE) + 1)
//                        set(Calendar.SECOND, get(Calendar.SECOND) + 2)
//                    }

                //To show the real prayer time.
                cal.time = nextPrayerTime

                Timber.tag(TAG).d(
                    "Calender: Next Prayer at: ${cal.get(Calendar.HOUR_OF_DAY)}:${
                        cal.get(
                            Calendar.MINUTE
                        )
                    }:${cal.get(Calendar.SECOND)}"
                )

                val pendingIntent = PendingIntent.getBroadcast(
                    context,
                    ALARM_ID,
                    intent,
                    PendingIntent.FLAG_CANCEL_CURRENT
                )

                alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    cal.timeInMillis,
                    pendingIntent
                )

            }
        }

    }

    fun cancelAlarm(context: Context) {
        // If the alarm has been set, cancel it.
        if (!::alarmManager.isInitialized) {
            alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        }

        val intent = Intent(context, AdhanBroadcastReceiver::class.java)
        val alarmIntent = PendingIntent.getBroadcast(
            context,
            ALARM_ID,
            intent,
            PendingIntent.FLAG_CANCEL_CURRENT
        )

        if (!notificationOn){
            alarmManager.cancel(alarmIntent)
            Timber.tag(TAG).d("All Alarm Canceled.")
        }


    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {

    }

    private fun setupSharedPreference(context: Context) {
        mPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        mPreferences.registerOnSharedPreferenceChangeListener(this)
    }
}