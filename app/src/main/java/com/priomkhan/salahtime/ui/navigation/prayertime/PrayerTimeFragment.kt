package com.priomkhan.salahtime.ui.navigation.prayertime

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.preference.PreferenceManager
import com.batoulapps.adhan.CalculationMethod
import com.priomkhan.salahtime.R
import com.priomkhan.salahtime.SettingsActivity
import com.priomkhan.salahtime.databinding.FragmentPrayerTimeBinding
import com.priomkhan.salahtime.receiver.AdhanBroadcastReceiver
import com.priomkhan.salahtime.services.EndLessService
import com.priomkhan.salahtime.services.ServiceState
import com.priomkhan.salahtime.services.getServiceState
import com.priomkhan.salahtime.util.Actions
import com.priomkhan.salahtime.util.GlobalVariables
import com.priomkhan.salahtime.util.GlobalVariables.Companion.TAG
import com.priomkhan.salahtime.util.GlobalVariables.Companion.asrAdhanOn
import com.priomkhan.salahtime.util.GlobalVariables.Companion.dhuhrAdhanOn
import com.priomkhan.salahtime.util.GlobalVariables.Companion.fajrAdhanOn
import com.priomkhan.salahtime.util.GlobalVariables.Companion.ishaAdhanOn
import com.priomkhan.salahtime.util.GlobalVariables.Companion.maghribAdhanOn
import com.priomkhan.salahtime.util.GlobalVariables.Companion.notificationOn
import com.priomkhan.salahtime.util.GlobalVariables.Companion.sunriseNotificationOn
import timber.log.Timber


class PrayerTimeFragment : Fragment(), SharedPreferences.OnSharedPreferenceChangeListener {

    private lateinit var binding : FragmentPrayerTimeBinding
    private lateinit var prayerTimeViewModel: PrayerTimeViewModel
    private lateinit var adapter: PrayerTimeAdapter
    //private lateinit var adapter: PrayerTimeViewPager
    private var isLoading : Boolean = false


    //private lateinit var requestPermissionLauncher :
    private var alarmManager : AlarmManager? = null
    private lateinit var pendingIntent: PendingIntent

    private var fajrTimeAdjustmentValue : Int = 0
    private var dhuhrTimeAdjustmentValue : Int = 0
    private var asrTimeAdjustmentValue : Int = 0
    private var maghribTimeAdjustmentValue : Int = 0
    private var ishaTimeAdjustmentValue : Int = 0

    //** Member variables to hold the name of the shared preferences file
    private lateinit var mPreferences: SharedPreferences
    //** Name your shared preferences file
    private val sharedPrefFile : String = "com.priomkhan.salahtime"



    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        //val root = inflater.inflate(R.layout.fragment_prayer_time, container, false)
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_prayer_time, container, false)

        mPreferences = requireContext().getSharedPreferences(sharedPrefFile, Context.MODE_PRIVATE)
        setupSharedPreference()

        val viewModelFactory = PrayerTimeViewModelFactory.createFactory(this.requireActivity())
        prayerTimeViewModel = ViewModelProvider(this, viewModelFactory).get(PrayerTimeViewModel::class.java)

        val itemClickListener = ItemClickListener{ item ->
            //Timber.tag(TAG).d("itemClicked: $item")
           this.findNavController().navigate(
               PrayerTimeFragmentDirections.actionNavigationPrayerTimeToPrayerTimeDetailsFragment(
                   item
               )
           )
        }

        adapter = PrayerTimeAdapter(itemClickListener)
        //adapter = PrayerTimeViewPager()
        prayerTimeViewModel.prayerTimes.observe(viewLifecycleOwner, Observer {

            //Timber.tag(TAG).d("PrayerTimes ViewModel Data Observer: Data Updated")
            adapter.data = it
            //adapter.data.add(it)
            //adapter.submitList(it)
            binding.recyclerView.adapter = adapter
            binding.swipeLayout.isRefreshing = false


        })

        prayerTimeViewModel.monthlyWebData.observe(viewLifecycleOwner, Observer {
            val newWebPrayerList = it
            for (data in newWebPrayerList){
                data.apply {
                    this.Fajr.doNotify = fajrAdhanOn
                    this.Sunrise.doNotify = sunriseNotificationOn
                    this.Dhuhr.doNotify = dhuhrAdhanOn
                    this.Asr.doNotify = asrAdhanOn
                    this.Maghrib.doNotify = maghribAdhanOn
                    this.Isha.doNotify = ishaAdhanOn
                    prayerTimeViewModel.checkIfPrayerDone(this)
                }
            }
            prayerTimeViewModel.insertPrayerListToDatabase(newWebPrayerList)
        })




//        adapter.registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
//            override fun onItemRangeChanged(positionStart: Int, itemCount: Int) {
//                //super.onItemRangeChanged(positionStart, itemCount)
//                //Timber.tag(TAG).d("RecyclerView Item Range Changed..........")
//            }
//
//            override fun onChanged() {
//                //Timber.tag(TAG).d("RecyclerView Data Changed..........")
//                //scrollToPosition()
//            }
//
//
//        })



//        binding.recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
//
//            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
//                super.onScrollStateChanged(recyclerView, newState)
//            }
//
//            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
//                super.onScrolled(recyclerView, dx, dy)
//                val visibleItemCount: Int = binding.recyclerView.layoutManager?.childCount ?: 0
//                val totalItemCount: Int = binding.recyclerView.layoutManager?.itemCount ?: 0
//                val linearLayoutManager = binding.recyclerView.layoutManager as LinearLayoutManager
//                val firstVisibleItemPosition: Int =
//                    linearLayoutManager.findFirstVisibleItemPosition()
//
//                // Load more if we have reach the end to the recyclerView
//                if (visibleItemCount + firstVisibleItemPosition >= totalItemCount && firstVisibleItemPosition >= 0) {
//                    if (!isLoading) {
//                        isLoading = true
//                        //loadMoreItems()
////                        val lastData = adapter.data.last
////                        lastData?.let {
////                            val lastDate: String = lastData.dateTime?.readable.toString()
////                            prayerTimeViewModel.loadNextDay(lastDate)
////
////                        }
//                    }
//
//
//                }
//            }
//
//        })

//        prayerTimeViewModel.nextMonthPrayerTimes.observe(viewLifecycleOwner, Observer {
//            //Timber.tag(TAG).d("Next Month Data")
//            for (item in it) {
//                //adapter.addData(item)
//            }
//            isLoading = false
//        })


        binding.swipeLayout.setOnRefreshListener {
            prayerTimeViewModel.refreshData()
        }

        prayerTimeViewModel.netWorkNotAvailable.observe(viewLifecycleOwner, Observer {
            if (it){
                Toast.makeText(requireContext(), "Network Not Available: Click Refresh When Network is Available.", Toast.LENGTH_LONG).show()
                binding.swipeLayout.isRefreshing = false
            }
        })

        setHasOptionsMenu(true)

        updateAlarmStatus()

        return binding.root
    }

    //Setup sharePreference and get the signature.
    private fun setupSharedPreference() {
        mPreferences = PreferenceManager.getDefaultSharedPreferences(requireContext())
        mPreferences.registerOnSharedPreferenceChangeListener(this)

    }

//    private fun scrollToPosition() {
//        val formatter = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
//        val formattedDate : String = formatter.format(Date()).replace(".", "", true)
//        val position = adapter.getItemPosition(formattedDate)
//        if (position >= 0) {
//            binding.recyclerView.scrollToPosition(position)
//        }
//    }

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
                prayerTimeViewModel.refreshData()
            }
        }


        return super.onOptionsItemSelected(item)
    }

    private fun updateAlarmStatus(){
        val prayerAlarmReceiver = AdhanBroadcastReceiver()
        prayerAlarmReceiver.cancelAlarm(requireContext())
        prayerAlarmReceiver.setNextAlarm(requireContext())
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
                                notificationOn = true
                                Timber.tag(TAG).d("START THE FOREGROUND SERVICE ON DEMAND")
                                actionOnService(Actions.START)

                                updateAlarmStatus()
                            }else{
                                //Timber.tag(TAG).d("MainActivity-> Notification Turned OFF.")
                                //cancelAllAlarm()
                                notificationOn = false
                                Timber.tag(TAG).d("STOP THE FOREGROUND SERVICE ON DEMAND")
                                actionOnService(Actions.STOP)
                                updateAlarmStatus()
                            }
                        }
                    }

                    requireContext().getString(R.string.pref_fajr_notification_key),
                    requireContext().getString(R.string.pref_sunrise_notification_key),
                    requireContext().getString(R.string.pref_dhuhr_notification_key),
                    requireContext().getString(R.string.pref_asr_notification_key),
                    requireContext().getString(R.string.pref_maghrib_notification_key),
                    requireContext().getString(R.string.pref_isha_notification_key)->{prayerTimeViewModel.refreshData()}

                }
            }catch (e: Exception){
                Timber.tag(TAG).e("PrayerRimeFragment: $e")
            }
        }

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

    override fun onResume() {
        super.onResume()
        prayerTimeViewModel.reloadData()
        fetchFromSharedPref()
    }

    override fun onPause() {
        super.onPause()
        //saveToSharedPref()
    }

    private fun saveToSharedPref(){
        //Timber.tag(TAG).d("Saving SharedPref.......................")
        val preferencesEditor : SharedPreferences.Editor = mPreferences.edit()
        preferencesEditor.putBoolean(getString(R.string.is_adhan_alarm_on), notificationOn)
        preferencesEditor.apply()

    }

    private fun fetchFromSharedPref(){
        try {
            fajrAdhanOn = mPreferences.getBoolean(getString(R.string.is_fajr_adhan_alarm_on), false)
            sunriseNotificationOn = mPreferences.getBoolean(getString(R.string.is_sunrise_alarm_on), false)
            dhuhrAdhanOn = mPreferences.getBoolean(getString(R.string.is_dhuhr_adhan_alarm_on), false)
            asrAdhanOn = mPreferences.getBoolean(getString(R.string.is_asr_adhan_alarm_on), false)
            maghribAdhanOn = mPreferences.getBoolean(getString(R.string.is_maghrib_adhan_alarm_on), false)
            ishaAdhanOn = mPreferences.getBoolean(getString(R.string.is_isha_adhan_alarm_on), false)

            val calculationMethodString : String? = mPreferences.getString(requireContext().getString(R.string.adhan_time_calculation_method), CalculationMethod.NORTH_AMERICA.name)
            calculationMethodString?.let {
                GlobalVariables.calculationMethodName =it
            }

            this.fajrTimeAdjustmentValue = mPreferences.getInt(getString(R.string.fajr_time_adjustment), 0)
            this.dhuhrTimeAdjustmentValue = mPreferences.getInt(getString(R.string.dhuhr_time_adjustment), 0)
            this.asrTimeAdjustmentValue = mPreferences.getInt(getString(R.string.asr_time_adjustment), 0)
            this.maghribTimeAdjustmentValue = mPreferences.getInt(getString(R.string.maghrib_time_adjustment), 0)
            this.ishaTimeAdjustmentValue = mPreferences.getInt(getString(R.string.isha_time_adjustment), 0)
        }catch (e: Exception){
            Timber.tag(TAG).d( "SettingsActivity: Error fetching shared pref: $e")
        }
    }
}