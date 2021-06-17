package com.priomkhan.salahtime

import android.content.SharedPreferences
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.*
import com.batoulapps.adhan.CalculationMethod
import com.priomkhan.salahtime.util.GlobalVariables.Companion.TAG
import com.priomkhan.salahtime.util.GlobalVariables.Companion.asrAdhanOn
import com.priomkhan.salahtime.util.GlobalVariables.Companion.asrTimeAdjustmentValue
import com.priomkhan.salahtime.util.GlobalVariables.Companion.calculationMethodName
import com.priomkhan.salahtime.util.GlobalVariables.Companion.dhuhrAdhanOn
import com.priomkhan.salahtime.util.GlobalVariables.Companion.dhuhrTimeAdjustmentValue
import com.priomkhan.salahtime.util.GlobalVariables.Companion.fajrAdhanOn
import com.priomkhan.salahtime.util.GlobalVariables.Companion.fajrTimeAdjustmentValue
import com.priomkhan.salahtime.util.GlobalVariables.Companion.ishaAdhanOn
import com.priomkhan.salahtime.util.GlobalVariables.Companion.ishaTimeAdjustmentValue
import com.priomkhan.salahtime.util.GlobalVariables.Companion.maghribAdhanOn
import com.priomkhan.salahtime.util.GlobalVariables.Companion.maghribTimeAdjustmentValue
import com.priomkhan.salahtime.util.GlobalVariables.Companion.notificationOn
import com.priomkhan.salahtime.util.GlobalVariables.Companion.sunriseNotificationOn
import timber.log.Timber

class SettingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.settings_activity)
        if (savedInstanceState == null) {
            supportFragmentManager
                .beginTransaction()
                .replace(R.id.settings, SettingsFragment())
                .commit()
        }
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    class SettingsFragment : PreferenceFragmentCompat() {
        //** Member variables to hold the name of the shared preferences file
        private lateinit var mPreferences: SharedPreferences
        //** Name your shared preferences file
        private val sharedPrefFile : String = "com.priomkhan.salahtime"


        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            setPreferencesFromResource(R.xml.preferences, rootKey)
            setupSharedPreference()

            val listPreferencesCalculationMethod = findPreference<Preference>(getString(R.string.pref_adhan_calculation_method_key)) as ListPreference?

            listPreferencesCalculationMethod?.let { pref->

                pref.setOnPreferenceChangeListener { preference, newValue ->
                    if (preference is ListPreference){
                        val index = preference.findIndexOfValue(newValue.toString())
                        val entry = preference.entries[index]
                        val entryValue = preference.entryValues[index] as String

                        calculationMethodName = entryValue

                        Timber.tag(TAG).d("ListPref: selected val\", \" position: $index value: $entry, entryvalue: $calculationMethodName")
                    }

                    true
                }

            }


            val listPreferencesAdjustFajrTime = findPreference<Preference>(getString(R.string.pref_adjust_fajr_time_key)) as ListPreference?

            listPreferencesAdjustFajrTime?.let { pref->

                pref.setOnPreferenceChangeListener { preference, newValue ->
                    if (preference is ListPreference){
                        val index = preference.findIndexOfValue(newValue.toString())
                        val entry = preference.entries[index]
                        val entryValue = preference.entryValues[index] as String

                        fajrTimeAdjustmentValue = entryValue.toInt()

                        Timber.tag(TAG).d("ListPref Fajr: selected val\", \" position: $index value: $entry, entryvalue: $fajrTimeAdjustmentValue")
                    }

                    true
                }

            }

            val listPreferencesAdjustDhuhrTime = findPreference<Preference>(getString(R.string.pref_adjust_dhuhr_time_key)) as ListPreference?

            listPreferencesAdjustDhuhrTime?.let { pref->

                pref.setOnPreferenceChangeListener { preference, newValue ->
                    if (preference is ListPreference){
                        val index = preference.findIndexOfValue(newValue.toString())
                        val entry = preference.entries[index]
                        val entryValue = preference.entryValues[index] as String

                        dhuhrTimeAdjustmentValue = entryValue.toInt()

                        Timber.tag(TAG).d("ListPref Dhuhr: selected val\", \" position: $index value: $entry, entryvalue: $dhuhrTimeAdjustmentValue")
                    }

                    true
                }

            }

            val listPreferencesAdjustAsrTime = findPreference<Preference>(getString(R.string.pref_adjust_asr_time_key)) as ListPreference?

            listPreferencesAdjustAsrTime?.let { pref->

                pref.setOnPreferenceChangeListener { preference, newValue ->
                    if (preference is ListPreference){
                        val index = preference.findIndexOfValue(newValue.toString())
                        val entry = preference.entries[index]
                        val entryValue = preference.entryValues[index] as String

                        asrTimeAdjustmentValue = entryValue.toInt()

                        Timber.tag(TAG).d("ListPref Asr: selected val\", \" position: $index value: $entry, entryvalue: $asrTimeAdjustmentValue")
                    }

                    true
                }

            }

            val listPreferencesAdjustMaghribTime = findPreference<Preference>(getString(R.string.pref_adjust_maghrib_time_key)) as ListPreference?

            listPreferencesAdjustMaghribTime?.let { pref->

                pref.setOnPreferenceChangeListener { preference, newValue ->
                    if (preference is ListPreference){
                        val index = preference.findIndexOfValue(newValue.toString())
                        val entry = preference.entries[index]
                        val entryValue = preference.entryValues[index] as String

                        maghribTimeAdjustmentValue = entryValue.toInt()

                        Timber.tag(TAG).d("ListPref Maghrib: selected val\", \" position: $index value: $entry, entryvalue: $maghribTimeAdjustmentValue")
                    }

                    true
                }

            }

            val listPreferencesAdjustIshaTime = findPreference<Preference>(getString(R.string.pref_adjust_isha_time_key)) as ListPreference?

            listPreferencesAdjustIshaTime?.let { pref->

                pref.setOnPreferenceChangeListener { preference, newValue ->
                    if (preference is ListPreference){
                        val index = preference.findIndexOfValue(newValue.toString())
                        val entry = preference.entries[index]
                        val entryValue = preference.entryValues[index] as String

                        ishaTimeAdjustmentValue = entryValue.toInt()

                        Timber.tag(TAG).d("ListPref Isha: selected val\", \" position: $index value: $entry, entryvalue: $ishaTimeAdjustmentValue")
                    }

                    true
                }

            }

        }

        //Setup sharePreference and get the signature.
        private fun setupSharedPreference() {
            mPreferences = PreferenceManager.getDefaultSharedPreferences(context)

        }

        override fun onPreferenceTreeClick(preference: Preference?): Boolean {

            when(preference?.key){
                getString(R.string.pref_notification_key)->{
                    val on = (preference as SwitchPreference).isChecked
                    notificationOn = if (on){
                        Timber.tag(TAG).d("SettingsFragment-> Notification Turned ON.")
                        true


                    }else{
                        Timber.tag(TAG).d("SettingsFragment-> Notification Turned OFF.")

                        false
                    }
                }
                getString(R.string.pref_fajr_notification_key)->{
                    val on = (preference as SwitchPreference).isChecked
                    fajrAdhanOn = if (on){
                        Timber.tag(TAG).d("SettingsFragment-> Fajr Adhan Turned ON.")
                        true


                    }else{
                        Timber.tag(TAG).d("SettingsFragment-> Fajr Adhan Turned OFF.")

                        false
                    }
                }

                getString(R.string.pref_sunrise_notification_key)->{
                    val on = (preference as SwitchPreference).isChecked
                    sunriseNotificationOn = if (on){
                        Timber.tag(TAG).d("SettingsFragment-> Sunrise Notification Turned ON.")
                        true


                    }else{
                        Timber.tag(TAG).d("SettingsFragment-> Sunrise Notification Turned OFF.")

                        false
                    }
                }

                getString(R.string.pref_dhuhr_notification_key)->{
                    val on = (preference as SwitchPreference).isChecked
                    dhuhrAdhanOn = if (on){
                        Timber.tag(TAG).d("SettingsFragment-> Dhuhr Adhan Turned ON.")
                        true


                    }else{
                        Timber.tag(TAG).d("SettingsFragment-> Dhuhr Adhan Turned OFF.")

                        false
                    }
                }


                getString(R.string.pref_asr_notification_key)->{
                    val on = (preference as SwitchPreference).isChecked
                    asrAdhanOn = if (on){
                        Timber.tag(TAG).d("SettingsFragment-> Asr Adhan Turned ON.")
                        true


                    }else{
                        Timber.tag(TAG).d("SettingsFragment-> Asr Adhan Turned OFF.")

                        false
                    }
                }

                getString(R.string.pref_maghrib_notification_key)->{
                    val on = (preference as SwitchPreference).isChecked
                    maghribAdhanOn = if (on){
                        Timber.tag(TAG).d("SettingsFragment-> Maghrib Adhan Turned ON.")
                        true


                    }else{
                        Timber.tag(TAG).d("SettingsFragment-> Maghrib Adhan Turned OFF.")

                        false
                    }
                }

                getString(R.string.pref_isha_notification_key)->{
                    val on = (preference as SwitchPreference).isChecked
                    ishaAdhanOn = if (on){
                        Timber.tag(TAG).d("SettingsFragment-> Isha Adhan Turned ON.")
                        true


                    }else{
                        Timber.tag(TAG).d("SettingsFragment-> Isha Adhan Turned OFF.")

                        false
                    }
                }

            }

            return super.onPreferenceTreeClick(preference)
        }





        override fun onPause() {
            super.onPause()
            saveToSharedPref()
        }

        override fun onResume() {
            super.onResume()
            fetchFromSharedPref()
        }

        private fun saveToSharedPref(){
            val preferencesEditor : SharedPreferences.Editor = mPreferences.edit()
            Timber.tag(TAG).d("Saving to SharedPreference NotificationOn: $notificationOn, Fajr: $fajrAdhanOn, Sunrise: $sunriseNotificationOn, Dhuhr: $dhuhrAdhanOn, Asr: $asrAdhanOn, Maghrib: $maghribAdhanOn, Isha: $ishaAdhanOn ")
            preferencesEditor.putBoolean(getString(R.string.is_adhan_alarm_on), notificationOn)
            preferencesEditor.putBoolean(getString(R.string.is_fajr_adhan_alarm_on), fajrAdhanOn)
            preferencesEditor.putBoolean(getString(R.string.is_sunrise_alarm_on), sunriseNotificationOn)
            preferencesEditor.putBoolean(getString(R.string.is_dhuhr_adhan_alarm_on), dhuhrAdhanOn)
            preferencesEditor.putBoolean(getString(R.string.is_asr_adhan_alarm_on), asrAdhanOn)
            preferencesEditor.putBoolean(getString(R.string.is_maghrib_adhan_alarm_on), maghribAdhanOn)
            preferencesEditor.putBoolean(getString(R.string.is_isha_adhan_alarm_on), ishaAdhanOn)
            preferencesEditor.putString(getString(R.string.adhan_time_calculation_method), calculationMethodName)
            preferencesEditor.putInt(getString(R.string.fajr_time_adjustment), fajrTimeAdjustmentValue)
            preferencesEditor.putInt(getString(R.string.dhuhr_time_adjustment), dhuhrTimeAdjustmentValue)
            preferencesEditor.putInt(getString(R.string.asr_time_adjustment), asrTimeAdjustmentValue)
            preferencesEditor.putInt(getString(R.string.maghrib_time_adjustment), maghribTimeAdjustmentValue)
            preferencesEditor.putInt(getString(R.string.isha_time_adjustment), ishaTimeAdjustmentValue)
            preferencesEditor.apply()
        }

        private fun fetchFromSharedPref(){
            try {
                notificationOn = mPreferences.getBoolean(getString(R.string.is_adhan_alarm_on), false)
                fajrAdhanOn = mPreferences.getBoolean(getString(R.string.is_fajr_adhan_alarm_on), false)
                sunriseNotificationOn = mPreferences.getBoolean(getString(R.string.is_sunrise_alarm_on), false)
                dhuhrAdhanOn = mPreferences.getBoolean(getString(R.string.is_dhuhr_adhan_alarm_on), false)
                asrAdhanOn = mPreferences.getBoolean(getString(R.string.is_asr_adhan_alarm_on), false)
                maghribAdhanOn = mPreferences.getBoolean(getString(R.string.is_maghrib_adhan_alarm_on), false)
                ishaAdhanOn = mPreferences.getBoolean(getString(R.string.is_isha_adhan_alarm_on), false)
                val calculationMethodString : String? = mPreferences.getString(requireContext().getString(R.string.adhan_time_calculation_method), CalculationMethod.NORTH_AMERICA.name)
               calculationMethodString?.let {
                   calculationMethodName =it
               }

                fajrTimeAdjustmentValue = mPreferences.getInt(getString(R.string.fajr_time_adjustment), 0)
                dhuhrTimeAdjustmentValue = mPreferences.getInt(getString(R.string.dhuhr_time_adjustment), 0)
                asrTimeAdjustmentValue = mPreferences.getInt(getString(R.string.asr_time_adjustment), 0)
                maghribTimeAdjustmentValue = mPreferences.getInt(getString(R.string.maghrib_time_adjustment), 0)
                ishaTimeAdjustmentValue = mPreferences.getInt(getString(R.string.isha_time_adjustment), 0)
            }catch (e: Exception){
                Timber.tag(TAG).d( "SettingsActivity: Error fetching shared pref: $e")
            }
        }


    }
}