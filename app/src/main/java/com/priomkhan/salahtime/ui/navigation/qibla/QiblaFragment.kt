package com.priomkhan.salahtime.ui.navigation.qibla

import android.content.Context
import android.content.SharedPreferences
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.RotateAnimation
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.preference.PreferenceManager
import com.priomkhan.salahtime.R
import com.priomkhan.salahtime.databinding.FragmentQiblaBinding
import com.priomkhan.salahtime.util.GlobalVariables.Companion.TAG
import timber.log.Timber
import kotlin.math.roundToInt


class QiblaFragment : Fragment(), SharedPreferences.OnSharedPreferenceChangeListener, SensorEventListener {
    private lateinit var binding : FragmentQiblaBinding
    private lateinit var qiblaViewModel: QiblaViewModel

    private var latitude : Double = 0.0
    private var longitude: Double = 0.0

    //** Member variables to hold the name of the shared preferences file
    private lateinit var mPreferences: SharedPreferences
    //** Name your shared preferences file
    private val sharedPrefFile : String = "com.priomkhan.salahtime"

    private var isLocationFetched = MutableLiveData<Boolean>()

    private lateinit var mSensorManager: SensorManager

    private val accelerometerReading = FloatArray(3)
    private val magnetometerReading = FloatArray(3)

    private val rotationMatrix = FloatArray(9)
    private val orientationAngles = FloatArray(3)

    private var currentDegreesMute = MutableLiveData<Double>()

    private var angle : Int =0

    private lateinit var vibrator: Vibrator

    private var qiblaDirection : Double = 0.0

    private lateinit var needleAnimation: RotateAnimation
    private var currentNeedleDegrees : Float = 0f

    private var mLastAccelerometerSet = false
    private var mLastMagnetometerSet = false

    var cityLocation : String = ""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_qibla, container, false)

        mPreferences = requireContext().getSharedPreferences(sharedPrefFile, Context.MODE_PRIVATE)

        setupSharedPreference()

        val viewModelFactory = QiblaViewModelFactory.createFactory(this.requireActivity())
        qiblaViewModel = ViewModelProvider(this, viewModelFactory).get(QiblaViewModel::class.java)


        mSensorManager = requireContext().getSystemService(Context.SENSOR_SERVICE) as SensorManager

        qiblaViewModel.text.observe(viewLifecycleOwner, Observer {

            qiblaDirection = it
            binding.textDirectionAngle.text =resources.getString(R.string.qibla_direction_by_location, cityLocation,it.roundToInt().toString())
        })

        isLocationFetched.observe(viewLifecycleOwner, Observer {
            if (it) {
                qiblaViewModel.getQibla(latitude, longitude)
            }
        })

        currentDegreesMute.observe(viewLifecycleOwner, Observer {
            angle = (it * 100).roundToInt() / 100

            val variance = qiblaDirection - angle
            if (variance > -2 && variance < 2) {
                playVibration()
            }
            binding.textPhoneCurrentDegree.text = resources.getString(
                R.string.device_current_orientation,
                angle.toString()
            )
            animateCompass(it.toFloat())
        })

        vibrator =  requireContext().getSystemService(Context.VIBRATOR_SERVICE) as Vibrator

        needleAnimation = RotateAnimation(
            currentNeedleDegrees,
            0f,
            Animation.RELATIVE_TO_SELF,
            .5f,
            Animation.RELATIVE_TO_SELF,
            .5f
        )

        return binding.root
    }




    override fun onResume() {
        super.onResume()
//        prayerTimeViewModel.reloadData()
        fetchFromSharedPref()


        // Get updates from the accelerometer and magnetometer at a constant rate.
        // To make batch operations more efficient and reduce power consumption,
        // provide support for delaying updates to the application.
        //
        // In this example, the sensor reporting delay is small enough such that
        // the application receives an update before the system checks the sensor
        // readings again.
        mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)?.also { accelerometer ->
            mSensorManager.registerListener(
                this,
                accelerometer,
                SensorManager.SENSOR_DELAY_NORMAL,
                SensorManager.SENSOR_DELAY_UI
            )
        }
        mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)?.also { magneticField ->
            mSensorManager.registerListener(
                this,
                magneticField,
                SensorManager.SENSOR_DELAY_NORMAL,
                SensorManager.SENSOR_DELAY_UI
            )
        }



    }

    override fun onPause() {
        super.onPause()

        // Don't receive any more updates from either sensor.
        mSensorManager.unregisterListener(this)
    }


    private fun fetchFromSharedPref() {
       latitude = mPreferences.getString(getString(R.string.last_location_latitude), "0.0")!!.toDouble()
        longitude = mPreferences.getString(getString(R.string.last_location_longitude), "0.0")!!.toDouble()
        cityLocation = mPreferences.getString(getString(R.string.last_location_city_name), "")!!
        Timber.tag(TAG).d("Fetched Data from SharedPref Latitude: $latitude, Longitude: $longitude")
        isLocationFetched.postValue(true)

    }

    //Setup sharePreference and get the signature.
    private fun setupSharedPreference() {
        mPreferences = PreferenceManager.getDefaultSharedPreferences(requireContext())
        mPreferences.registerOnSharedPreferenceChangeListener(this)

    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {

    }

    // Get readings from accelerometer and magnetometer. To simplify calculations,
    // consider storing these readings as unit vectors.
    override fun onSensorChanged(event: SensorEvent?) {
        event?.let { mEvent->

            if (mEvent.sensor.type == Sensor.TYPE_ACCELEROMETER) {

                System.arraycopy(
                    mEvent.values,
                    0,
                    accelerometerReading,
                    0,
                    accelerometerReading.size
                )
                mLastAccelerometerSet = true
            } else if (mEvent.sensor.type == Sensor.TYPE_MAGNETIC_FIELD) {
                System.arraycopy(mEvent.values, 0, magnetometerReading, 0, magnetometerReading.size)
                mLastMagnetometerSet = true
            }
        }

        updateOrientationAngles()
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        Timber.tag(TAG).d("Sensor Accuracy Changed.")
    }

    // Compute the three orientation angles based on the most recent readings from
    // the device's accelerometer and magnetometer.
    private fun updateOrientationAngles() {
        // Update rotation matrix, which is needed to update orientation angles.
        SensorManager.getRotationMatrix(
            rotationMatrix,
            null,
            accelerometerReading,
            magnetometerReading
        )

        // "rotationMatrix" now has up-to-date information.

        val orientation = SensorManager.getOrientation(rotationMatrix, orientationAngles)

        currentDegreesMute.postValue((Math.toDegrees(orientation[0].toDouble()) + 360.0) % 360.0)



        // "orientationAngles" now has up-to-date information.

        //Timber.tag(TAG).d("Degree: $degrees")

    }

    private fun playVibration(){
        val pattern = longArrayOf(0, 100, 1000)
        vibrator.vibrate(VibrationEffect.createWaveform(pattern, -1))
    }

    private fun animateCompass(deviceDegree: Float) {

        //Showing North
        //binding.imgCompass.rotation = ((-orientationAngles[0])*180/3.14159).toFloat()

        //Show Qibla

        if (mLastAccelerometerSet && mLastMagnetometerSet) {



            val qiblaDirectionInFloat = qiblaDirection.toFloat()

            val needleBearTo = (-deviceDegree+qiblaDirectionInFloat)

            //Timber.tag(TAG).d("DeviceDegree: $deviceDegree, QiblaDegree: $qiblaDirection, NeedleBearTo: $needleBearTo")

            val ra = RotateAnimation(
                currentNeedleDegrees,
                needleBearTo,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF,
                0.5f
            )
            ra.duration = 250
            ra.fillAfter = true
            binding.imgCompass.startAnimation(ra)
            currentNeedleDegrees = needleBearTo
        }

    }

}