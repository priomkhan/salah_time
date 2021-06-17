package com.priomkhan.salahtime.ui.Alarm

import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.preference.PreferenceManager
import com.batoulapps.adhan.Prayer
import com.priomkhan.salahtime.R
import com.priomkhan.salahtime.databinding.ActivityAlarmBinding
import com.priomkhan.salahtime.services.AlarmService
import com.priomkhan.salahtime.ui.main.MainActivity
import com.priomkhan.salahtime.ui.navigation.prayertime.PrayerTimeViewModel
import com.priomkhan.salahtime.util.GlobalVariables.Companion.TAG
import timber.log.Timber

class AlarmActivity : AppCompatActivity(), SharedPreferences.OnSharedPreferenceChangeListener{
    private lateinit var binding: ActivityAlarmBinding

    private var prayerName :String = ""

    //** Member variables to hold the name of the shared preferences file
    private lateinit var mPreferences: SharedPreferences
    //** Name your shared preferences file
    private val sharedPrefFile : String = "com.priomkhan.salahtime"

    private lateinit var alarmActivityViewModel: AlarmActivityViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_alarm)
        //setContentView(R.layout.activity_alarm)

        val viewModelFactory = AlarmActivityViewModelFactory.createFactory(this)
        alarmActivityViewModel = ViewModelProvider(this, viewModelFactory).get(AlarmActivityViewModel::class.java)

        //**  Initialize the shared preferences
        mPreferences = getSharedPreferences(sharedPrefFile, MODE_PRIVATE)
        setupSharedPreference()
        try {
            prayerName = mPreferences.getString(resources.getString(R.string.next_prayer_name), "Prayer")!!
            Timber.tag(TAG).d("AlarmActivity:: prayerName: $prayerName")
        }catch(e: Exception){
            Timber.tag(TAG).e(e)
        }


        binding.textView.text = resources.getString(R.string.time_to_pray, prayerName)

        binding.activityRingDismiss.setOnClickListener {

            val intentService = Intent(
                this,
                AlarmService::class.java
            )
            stopService(intentService)
            alarmActivityViewModel.isDone(Prayer.valueOf(prayerName))
            val mainIntent = Intent(this, MainActivity::class.java)
            startActivity(mainIntent)
            finish()
        }

        animateClock()
    }


    //Setup sharePreference and get the signature.
    private fun setupSharedPreference() {
        mPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        mPreferences.registerOnSharedPreferenceChangeListener(this)
    }


    private fun animateClock() {
        val rotateAnimation = ObjectAnimator.ofFloat(
            binding.activityRingClock,
            "rotation",
            0f,
            20f,
            0f,
            -20f,
            0f
        )
        rotateAnimation.repeatCount = ValueAnimator.INFINITE
        rotateAnimation.duration = 800
        rotateAnimation.start()
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {

    }
}