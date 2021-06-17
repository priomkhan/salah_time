package com.priomkhan.salahtime.ui.start

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.location.*
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.preference.PreferenceManager
import com.batoulapps.adhan.CalculationMethod
import com.google.android.material.snackbar.Snackbar
import com.priomkhan.salahtime.R
import com.priomkhan.salahtime.databinding.ActivitySplashBinding
import com.priomkhan.salahtime.network.CheckNetwork
import com.priomkhan.salahtime.ui.main.MainActivity
import com.priomkhan.salahtime.util.GlobalVariables
import com.priomkhan.salahtime.util.GlobalVariables.Companion.PERMISSION_REQUEST_LOCATION
import com.priomkhan.salahtime.util.GlobalVariables.Companion.TAG
import com.priomkhan.salahtime.util.GlobalVariables.Companion.cityName
import com.priomkhan.salahtime.util.GlobalVariables.Companion.countryName
import com.priomkhan.salahtime.util.GlobalVariables.Companion.isNetworkConnected
import com.priomkhan.salahtime.util.GlobalVariables.Companion.latitude
import com.priomkhan.salahtime.util.GlobalVariables.Companion.longitude
import com.priomkhan.salahtime.util.GlobalVariables.Companion.stateName
import com.priomkhan.salahtime.util.LocationUpdateDelay
import timber.log.Timber
import java.lang.Exception
import java.util.*


class SplashActivity : AppCompatActivity() , LocationListener, SharedPreferences.OnSharedPreferenceChangeListener{
    private lateinit var binding: ActivitySplashBinding
    private lateinit var viewModel: SplashViewModel
    private lateinit var layout: View
    private val DEFAULT_LOCATION_UPDATE_DELAY = LocationUpdateDelay.DELAY_1MIN
    private var locationManager: LocationManager? = null
    private var locationListener: LocationListener? = null

    //** Member variables to hold the name of the shared preferences file
    private lateinit var mPreferences: SharedPreferences
    //** Name your shared preferences file
    private val sharedPrefFile : String = "com.priomkhan.salahtime"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.activity_splash)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_splash)

        //**  Initialize the shared preferences
        mPreferences = getSharedPreferences(sharedPrefFile, MODE_PRIVATE)

        setupSharedPreference()

        //Check Network Connection
        CheckNetwork(applicationContext)

        val viewModelFactory = SplashViewModelFactory.createFactory(this)
        viewModel = ViewModelProvider(this, viewModelFactory).get(SplashViewModel::class.java)

        layout = findViewById(R.id.starting_activity)

        getLocation()

        viewModel.mLocationPermissionGranted.observe(this, {
            if (it) {
                //Timber.tag(TAG).d("mLocationPermissionGranted = True")
                updateCurrentLocation()

            }
        })

        viewModel.currentLocation.observe(this, {
            //Timber.tag(TAG).d("currentLocation updated")
            saveCurrentLocation(it)
            checkNetworkConnectivity()

        })


    }

    //Setup sharePreference and get the signature.
    private fun setupSharedPreference() {
        mPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        mPreferences.registerOnSharedPreferenceChangeListener(this)

    }

    private fun checkNetworkConnectivity() {
        if (isNetworkConnected) {


            binding.simpleProgressBar.visibility = ProgressBar.GONE
            goMainActivity()

        } else {

            Handler(Looper.myLooper()!!).postDelayed({

                binding.simpleProgressBar.visibility = ProgressBar.GONE

                Toast.makeText(this,
                    "You are offline.",
                    Toast.LENGTH_LONG
                ).show()

            }, 3000)

            goMainActivity()

        }
    }

    private fun goMainActivity() {
        Handler(Looper.myLooper()!!).postDelayed({
            val mainIntent = Intent(this, MainActivity::class.java)
            startActivity(mainIntent)
            finish()
        }, 3000)
    }

    private fun getLocation() {
        if (checkSelfPermission(android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            Snackbar.make(layout, R.string.location_permission_available, Snackbar.LENGTH_SHORT)
                .show()
            Timber.tag(TAG).d(resources.getString(R.string.location_permission_available))
            viewModel.mLocationPermissionGranted.postValue(true)
        } else {
            requestLocationPermission()
        }
    }

    private fun requestLocationPermission() {
        if (shouldShowRequestPermissionRationale(android.Manifest.permission.ACCESS_FINE_LOCATION)) {
            Snackbar.make(layout, R.string.location_access_required, Snackbar.LENGTH_INDEFINITE)
                .setAction(
                    R.string.ok,
                    View.OnClickListener {
                        requestPermissions(
                            arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                            GlobalVariables.PERMISSION_REQUEST_LOCATION
                        )
                    }).show()
        } else {
            Snackbar.make(layout, R.string.location_permission_not_available, Snackbar.LENGTH_SHORT)
                .show()

            requestPermissions(
                arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                GlobalVariables.PERMISSION_REQUEST_LOCATION
            )
        }
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        if (requestCode == PERMISSION_REQUEST_LOCATION) {
            // Request for camera permission.
            if (grantResults.size == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission has been granted. Start camera preview Activity.
                Snackbar.make(layout, R.string.location_permission_granted, Snackbar.LENGTH_SHORT)
                    .show()
                Timber.tag(TAG).d(resources.getString(R.string.location_permission_granted))
                viewModel.mLocationPermissionGranted.postValue(true)
            } else {
                // Permission request was denied.
                Snackbar.make(layout, R.string.location_permission_denied, Snackbar.LENGTH_SHORT)
                    .show()
                Timber.tag(TAG).d(resources.getString(R.string.location_permission_denied))
                Toast.makeText(
                    this,
                    "This application needs location permission to work properly.",
                    Toast.LENGTH_LONG
                ).show()
                this.finishActivity(100)

            }
        }
    }


    private fun updateCurrentLocation() {
        try {
            //Timber.tag(TAG).d("Updating current location....")

            locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager


            val hasGPS = locationManager!!.isProviderEnabled(LocationManager.GPS_PROVIDER)
            val hasNetwork = locationManager!!.isProviderEnabled(LocationManager.NETWORK_PROVIDER)

            if (hasGPS || hasNetwork){
                Timber.tag(TAG).d("Location Provider Enabled: hasGPS: $hasGPS, hasNetwork: $hasNetwork")

                if (hasGPS){
                    locationManager!!.requestLocationUpdates(LocationManager.GPS_PROVIDER, 6000, 1F, this)
                }

                if(hasNetwork){
                    locationManager!!.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 7000, 1F, this)
                }

            }else{
                Timber.tag(TAG).d("Location Provider Not Enabled: hasGPS: $hasGPS, hasNetwork: $hasNetwork")
                Toast.makeText(this, "Please Turn On Device Location and Restart thia Application.", Toast.LENGTH_LONG).show()

            }

        } catch (e: SecurityException) {

            Timber.tag(TAG).d(e.message ?: "Error: Unable to get current location")

        }
    }

    override fun onLocationChanged(location: Location) {
        Timber.tag(TAG).d("Got Location By Provider: ${location.provider}")
        viewModel.currentLocation.postValue(location)
    }

    private fun saveCurrentLocation(location: Location) {

        latitude = location.latitude
        longitude = location.longitude

        try {
            val geoCoder = Geocoder(this, Locale.getDefault())
            val addressList: List<Address> = geoCoder.getFromLocation(location.latitude, location.longitude, 1)
            val address = addressList[0]
            address?.let {
                cityName = it.locality
                stateName = it.adminArea
                countryName = it.countryName
            }


        }catch (e: Exception){
            Timber.tag(TAG).e(e)
        }


        Timber.tag(TAG).d("Current Location latitude: $latitude, longitude: $longitude, City: $cityName, State: $stateName, Country: $countryName")

    }

    override fun onPause() {
        super.onPause()
        saveToSharedPref()

    }

    override fun onResume() {
        super.onResume()
        fetchFromSharedPref()
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {

    }

    private fun saveToSharedPref(){
        //Timber.tag(TAG).d("Saving SharedPref.......................")
        val preferencesEditor : SharedPreferences.Editor = mPreferences.edit()
        preferencesEditor.putString(getString(R.string.last_location_latitude), latitude.toString())
        preferencesEditor.putString(getString(R.string.last_location_longitude), longitude.toString())
        preferencesEditor.putString(getString(R.string.last_location_city_name), cityName)
        preferencesEditor.putString(getString(R.string.last_location_state_name), stateName)
        preferencesEditor.putString(getString(R.string.last_location_country_name), countryName)
        preferencesEditor.apply()

    }

    private fun fetchFromSharedPref(){
        try {
            GlobalVariables.notificationOn = mPreferences.getBoolean(getString(R.string.is_adhan_alarm_on), false)
            GlobalVariables.fajrAdhanOn = mPreferences.getBoolean(getString(R.string.is_fajr_adhan_alarm_on), false)
            GlobalVariables.sunriseNotificationOn = mPreferences.getBoolean(getString(R.string.is_sunrise_alarm_on), false)
            GlobalVariables.dhuhrAdhanOn = mPreferences.getBoolean(getString(R.string.is_dhuhr_adhan_alarm_on), false)
            GlobalVariables.asrAdhanOn = mPreferences.getBoolean(getString(R.string.is_asr_adhan_alarm_on), false)
            GlobalVariables.maghribAdhanOn = mPreferences.getBoolean(getString(R.string.is_maghrib_adhan_alarm_on), false)
            GlobalVariables.ishaAdhanOn = mPreferences.getBoolean(getString(R.string.is_isha_adhan_alarm_on), false)
            val calculationMethodString : String? = mPreferences.getString(getString(R.string.adhan_time_calculation_method), CalculationMethod.NORTH_AMERICA.name)
            calculationMethodString?.let {
                GlobalVariables.calculationMethodName =it
            }


            GlobalVariables.fajrTimeAdjustmentValue = mPreferences.getInt(getString(R.string.fajr_time_adjustment), 0)
            GlobalVariables.dhuhrTimeAdjustmentValue = mPreferences.getInt(getString(R.string.dhuhr_time_adjustment), 0)
            GlobalVariables.asrTimeAdjustmentValue = mPreferences.getInt(getString(R.string.asr_time_adjustment), 0)
            GlobalVariables.maghribTimeAdjustmentValue = mPreferences.getInt(getString(R.string.maghrib_time_adjustment), 0)
            GlobalVariables.ishaTimeAdjustmentValue = mPreferences.getInt(getString(R.string.isha_time_adjustment), 0)

            latitude = mPreferences.getString(
                applicationContext.getString(R.string.last_location_latitude),
                "0.0"
            )!!.toDouble()
            longitude = mPreferences.getString(
                applicationContext.getString(R.string.last_location_longitude),
                "0.0"
            )!!.toDouble()
            cityName = mPreferences.getString(
                applicationContext.getString(R.string.last_location_city_name),
                ""
            )!!
        }catch (e: Exception){
            Timber.tag(TAG).d( "SettingsActivity: Error fetching shared pref: $e")
        }
    }

}