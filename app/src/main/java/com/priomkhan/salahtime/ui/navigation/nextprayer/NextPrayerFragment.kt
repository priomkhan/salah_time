package com.priomkhan.salahtime.ui.navigation.nextprayer

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.view.*
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.preference.PreferenceManager
import com.batoulapps.adhan.CalculationMethod
import com.batoulapps.adhan.CalculationParameters
import com.batoulapps.adhan.Prayer
import com.priomkhan.salahtime.R
import com.priomkhan.salahtime.SettingsActivity
import com.priomkhan.salahtime.databinding.FragmentNextPrayerBinding
import com.priomkhan.salahtime.localdata.NextPrayer
import com.priomkhan.salahtime.receiver.AdhanBroadcastReceiver
import com.priomkhan.salahtime.services.EndLessService
import com.priomkhan.salahtime.services.ServiceState
import com.priomkhan.salahtime.services.getServiceState
import com.priomkhan.salahtime.util.Actions
import com.priomkhan.salahtime.util.GlobalVariables
import com.priomkhan.salahtime.util.GlobalVariables.Companion.TAG
import com.priomkhan.salahtime.util.GlobalVariables.Companion.latitude
import com.priomkhan.salahtime.util.GlobalVariables.Companion.longitude
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.*

class NextPrayerFragment : Fragment(), SharedPreferences.OnSharedPreferenceChangeListener {

    private lateinit var nextPrayerViewModel: NextPrayerViewModel
    private lateinit var binding : FragmentNextPrayerBinding

    private var isLocationFetched = MutableLiveData<Boolean>()

    private var long : Double = 0.0
    private var lat : Double = 0.0

    private var fajrTimeAdjustmentValue : Int = 0
    private var dhuhrTimeAdjustmentValue : Int = 0
    private var asrTimeAdjustmentValue : Int = 0
    private var maghribTimeAdjustmentValue : Int = 0
    private var ishaTimeAdjustmentValue : Int = 0

    private var calculationMethodName = ""
    private var calculationMethod : CalculationParameters = CalculationMethod.NORTH_AMERICA.parameters
    var cityLocation : String = ""

    //** Member variables to hold the name of the shared preferences file
    private lateinit var mPreferences: SharedPreferences
    //** Name your shared preferences file
    private val sharedPrefFile : String = "com.priomkhan.salahtime"



    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        //val root = inflater.inflate(R.layout.fragment_next_prayer, container, false)

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_next_prayer, container, false)

        val viewModelFactory = NextPrayerViewModelFactory.createFactory(this.requireActivity())
        nextPrayerViewModel = ViewModelProvider(this, viewModelFactory).get(NextPrayerViewModel::class.java)

//        nextPrayerViewModel.text.observe(viewLifecycleOwner, Observer {
//            binding.textDashboard.text = it.time
//        })


        mPreferences = requireContext().getSharedPreferences(sharedPrefFile, Context.MODE_PRIVATE)
        setupSharedPreference()

        isLocationFetched.observe(viewLifecycleOwner, Observer {
            if (it) {

                calculationMethod = CalculationMethod.valueOf(calculationMethodName).parameters
                calculationMethod.adjustments.fajr = this.fajrTimeAdjustmentValue
                calculationMethod.adjustments.dhuhr = this.dhuhrTimeAdjustmentValue
                calculationMethod.adjustments.asr = this.asrTimeAdjustmentValue
                calculationMethod.adjustments.maghrib = this.maghribTimeAdjustmentValue
                calculationMethod.adjustments.isha = this.ishaTimeAdjustmentValue


                val mapCurrentPrayer = nextPrayerViewModel.getCurrentPrayerTime(
                    lat,
                    long,
                    calculationMethod
                )




                if (mapCurrentPrayer.isNotEmpty()) {
                    for (data in mapCurrentPrayer) {
                        val currentPrayerName = data.key
                        val currentPrayerTime = data.value

                        if(currentPrayerName == Prayer.SUNRISE.name){
                            binding.textHeaderCurrentPrayer.text = resources.getString(R.string.txt_header_current_prayer, "Notification")
                        }else{
                            binding.textHeaderCurrentPrayer.text = resources.getString(R.string.txt_header_current_prayer, "Adhan")
                        }

                        val format = SimpleDateFormat("hh:mm a", Locale.getDefault())
                        val formattedCurrentPrayerTime = format.format(currentPrayerTime)
                        val today = Calendar.getInstance().time
                        val dateFormatter = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
                        val formattedDate = dateFormatter.format(today)
                        val currentPrayer = NextPrayer(
                            formattedDate,
                            currentPrayerName,
                            formattedCurrentPrayerTime.toString(),
                            cityLocation
                        )
                        binding.currentPrayer = currentPrayer
                    }
                }

                val mapNextPrayer = nextPrayerViewModel.getNextPrayerTime(
                    lat,
                    long,
                    calculationMethod
                )

                if (mapNextPrayer.isNotEmpty()) {
                    for (data in mapNextPrayer) {
                        val nextPrayerName = data.key
                        val nextPrayerTime = data.value

                        if(nextPrayerName == Prayer.SUNRISE.name){
                            binding.textHeaderNextPrayer.text = resources.getString(R.string.txt_header_next_prayer, "Notification")
                        }else{
                            binding.textHeaderNextPrayer.text = resources.getString(R.string.txt_header_next_prayer, "Adhan")
                        }
                        val formatTime = SimpleDateFormat("hh:mm a", Locale.getDefault())
                        val formattedNextPrayerTime = formatTime.format(nextPrayerTime)
                        val today = Calendar.getInstance().time
                        val dateFormatter = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
                        val formattedDate = dateFormatter.format(today)
                        val nextPrayer = NextPrayer(
                            formattedDate,
                            nextPrayerName,
                            formattedNextPrayerTime.toString(),
                            cityLocation
                        )
                        binding.nextPrayer = nextPrayer

                        val nextPrayerRemainingTime =
                            nextPrayerTime.time - Calendar.getInstance().timeInMillis

                        val timer = object : CountDownTimer(nextPrayerRemainingTime, 1000) {
                            override fun onTick(millisUntilFinished: Long) {
                                val seconds = (millisUntilFinished / 1000) % 60
                                val minutes = (millisUntilFinished / (1000 * 60) % 60)
                                val hours = (millisUntilFinished / (1000 * 60 * 60) % 24)
                                val timeRemaining = String.format(
                                    "- %02d : %02d : %02d",
                                    hours,
                                    minutes,
                                    seconds
                                )

                                binding.textCountDownClock.text = timeRemaining
                            }

                            override fun onFinish() {
                                binding.textCountDownClock.text = ""
                                if (isAdded){
                                    fetchFromSharedPref()
                                }
                            }

                        }

                        timer.start()
                    }
                }


            }
        })


        setHasOptionsMenu(true)
        return binding.root
    }


    //Setup sharePreference and get the signature.
    private fun setupSharedPreference() {
        mPreferences = PreferenceManager.getDefaultSharedPreferences(requireContext())
        mPreferences.registerOnSharedPreferenceChangeListener(this)

    }

    override fun onResume() {
        super.onResume()
        fetchFromSharedPref()
    }

    private fun fetchFromSharedPref(){
        try {
            this.lat = mPreferences.getString(
                requireContext().getString(R.string.last_location_latitude),
                "0.0"
            )!!.toDouble()
            this.long = mPreferences.getString(
                requireContext().getString(R.string.last_location_longitude),
                "0.0"
            )!!.toDouble()
            this.cityLocation = mPreferences.getString(
                requireContext().getString(R.string.last_location_city_name),
                ""
            )!!
            val calculationMethodNameString = mPreferences.getString(
                requireContext().getString(R.string.adhan_time_calculation_method),
                CalculationMethod.NORTH_AMERICA.name
            )
            calculationMethodNameString?.let {
                this.calculationMethodName = it
            }

            this.fajrTimeAdjustmentValue = mPreferences.getInt(getString(R.string.fajr_time_adjustment), 0)
            this.dhuhrTimeAdjustmentValue = mPreferences.getInt(getString(R.string.dhuhr_time_adjustment), 0)
            this.asrTimeAdjustmentValue = mPreferences.getInt(getString(R.string.asr_time_adjustment), 0)
            this.maghribTimeAdjustmentValue = mPreferences.getInt(getString(R.string.maghrib_time_adjustment), 0)
            this.ishaTimeAdjustmentValue = mPreferences.getInt(getString(R.string.isha_time_adjustment), 0)

            Timber.tag(TAG).d("Fetched Data from SharedPref Latitude: $latitude, Longitude: $longitude")
            isLocationFetched.postValue(true)
        }catch (e: Exception){
            Timber.tag(TAG).d("NextPrayerFragment: $e")
        }

    }


    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
        if (isAdded){ //return true if the fragment is currently added to the Activity.
            try {
                when(key){
                    requireContext().getString(R.string.pref_notification_key) ->{
                        val newVal = sharedPreferences!!.getBoolean(
                            getString(R.string.pref_notification_key),
                            false
                        )
                        newVal.let {
                            if (it){
                                //Timber.tag(TAG).d("MainActivity-> Notification Turned ON.")
                                //updateAlarmStatus()
                                GlobalVariables.notificationOn = true
                                Timber.tag(TAG).d("START THE FOREGROUND SERVICE ON DEMAND")
                                actionOnService(Actions.START)

                                updateAlarmStatus()
                            }else{
                                //Timber.tag(TAG).d("MainActivity-> Notification Turned OFF.")
                                //cancelAllAlarm()
                                GlobalVariables.notificationOn = false
                                Timber.tag(TAG).d("STOP THE FOREGROUND SERVICE ON DEMAND")
                                actionOnService(Actions.STOP)
                                updateAlarmStatus()
                            }
                        }
                    }

                }
            }catch (e: Exception){
                Timber.tag(TAG).e("PrayerRimeFragment: $e")
            }
        }

    }

    private fun updateAlarmStatus(){
        val prayerAlarmReceiver = AdhanBroadcastReceiver()
        prayerAlarmReceiver.cancelAlarm(requireContext())
        prayerAlarmReceiver.setNextAlarm(requireContext())
    }

    private fun actionOnService(action: Actions) {
        if (getServiceState(requireContext()) == ServiceState.STOPPED && action == Actions.STOP) return
        Intent(requireContext(), EndLessService::class.java).also {
            it.action = action.name
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                //Timber.tag(TAG).d("Starting the service in >=28 Mode")
                requireActivity().startForegroundService(it)
                return
            }
            //Timber.tag(TAG).d("Starting the service in < 28 Mode")
            requireActivity().startService(it)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.main_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.action_settings->{
                val intent : Intent = Intent(requireContext(), SettingsActivity::class.java)
                startActivity(intent)

                return true
            }

            R.id.action_refresh->{
                refreshData()
            }
        }


        return super.onOptionsItemSelected(item)
    }

    private fun refreshData(){
        binding.invalidateAll()
        fetchFromSharedPref()
    }
}