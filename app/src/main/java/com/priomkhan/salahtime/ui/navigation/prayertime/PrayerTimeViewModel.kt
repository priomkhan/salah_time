package com.priomkhan.salahtime.ui.navigation.prayertime

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.priomkhan.salahtime.localdata.PrayerTime
import com.priomkhan.salahtime.repository.DataRepository
import com.priomkhan.salahtime.util.GlobalVariables.Companion.TAG
import kotlinx.coroutines.launch
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.*

class PrayerTimeViewModel(private val dataRepo: DataRepository) : ViewModel() {


//    private val _prayerTimes = MutableLiveData<List<PrayerTime>>().apply {
//
//        value = listOf<PrayerTime>(PrayerTime(
//            0,
//            CustomDate(0, "21 Apr 2021", "1617282061"),
//            CustomLocation(0, "43.7724382", "-79.538909", "America/Toronto"),
//            CalculationMethod(0, "Islamic Society of North America (ISNA)"),
//            "05:38 AM",
//            "06:59 AM",
//            "01:22 PM",
//             "04:57 PM",
//            "07:45 PM",
//            "07:45 PM",
//            "09:07 PM",
//            "05:28 AM",
//            "01:22 AM"
//        ))
//    }
//    val prayerTimes: LiveData<List<PrayerTime>> = _prayerTimes

    private val _monthlyWebData = dataRepo.thisMonthWebData
    val monthlyWebData: LiveData<List<PrayerTime>> = _monthlyWebData

    private val _prayerTimes = dataRepo.thisMonthData
    val prayerTimes: LiveData<List<PrayerTime>> = _prayerTimes

//    private val _nextMonthPrayerTimes = dataRepo.nextMonthData
//    val nextMonthPrayerTimes: LiveData<List<PrayerTime>> = _nextMonthPrayerTimes

    private val _netWorkNotAvailable = dataRepo.netWorkNotAvailable
    val netWorkNotAvailable : LiveData<Boolean> = _netWorkNotAvailable

    init {
        dataRepo.loadData()
    }

//    fun loadNextDay(nextDay: String) {
//        dataRepo.loadNextDay(nextDay)
//    }
    fun reloadData(){
        dataRepo.loadData()
    }

    fun refreshData(){
        dataRepo.refreshData()
    }

    fun insertPrayerListToDatabase(newWebPrayerList: List<PrayerTime>?) {
        viewModelScope.launch {
            dataRepo.savePrayerListToDatabase(newWebPrayerList)
        }

    }

    fun checkIfPrayerDone(prayerTime: PrayerTime){
        val dateOfPrayer = prayerTime.dateTime.date

        val today = Calendar.getInstance().time
        val dateFormatter = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
        val formattedDate = dateFormatter.format(today)

        if (dateOfPrayer==formattedDate){
            Timber.tag(TAG).d("Data Date: $dateOfPrayer, Today: $formattedDate")

            val currentTimeInMillis = Calendar.getInstance().timeInMillis


            if (currentTimeInMillis>getPrayerTimeInMillis(prayerTime.Fajr.time)){
                prayerTime.Fajr.isDone = true
            }
            if (currentTimeInMillis>getPrayerTimeInMillis(prayerTime.Sunrise.time)){
                prayerTime.Sunrise.isDone = true
            }
            if (currentTimeInMillis>getPrayerTimeInMillis(prayerTime.Dhuhr.time)){
                prayerTime.Dhuhr.isDone = true
            }
            if (currentTimeInMillis>getPrayerTimeInMillis(prayerTime.Asr.time)){
                prayerTime.Asr.isDone = true
            }
            if (currentTimeInMillis>getPrayerTimeInMillis(prayerTime.Maghrib.time)){
                prayerTime.Maghrib.isDone = true
            }
            if (currentTimeInMillis>getPrayerTimeInMillis(prayerTime.Isha.time)){
                prayerTime.Isha.isDone = true
            }

        }
    }

    fun getPrayerTimeInMillis(time: String): Long{
        val timeFormatter = SimpleDateFormat("hh:mm a", Locale.getDefault())
        val cal = Calendar.getInstance()
        val parsingTime = timeFormatter.parse(time)
        var prayerTimeInMillis : Long = 0
        parsingTime?.let {
            cal.time = parsingTime
            val hour = cal.get(Calendar.HOUR)
            val min = cal.get(Calendar.MINUTE)
            val ampm = cal.get(Calendar.AM_PM)
            val makeTodaysDate = Calendar.getInstance()
            makeTodaysDate.set(Calendar.HOUR, hour)
            makeTodaysDate.set(Calendar.MINUTE, min)
            makeTodaysDate.set(Calendar.AM_PM, ampm)
            prayerTimeInMillis = makeTodaysDate.timeInMillis
            Timber.tag(TAG).d("Prayer Time in Millis: $prayerTimeInMillis")
        }

        return prayerTimeInMillis
    }

//    fun getNextPrayerTime(): Map<String, Date> {
//        //return dataRepo.getNextPrayerTime()
//    }
}