package com.priomkhan.salahtime.ui.navigation.prayertime.details

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.priomkhan.salahtime.R
import com.priomkhan.salahtime.localdata.*
import com.priomkhan.salahtime.repository.DataRepository
import com.priomkhan.salahtime.util.GlobalVariables
import com.priomkhan.salahtime.util.GlobalVariables.Companion.TAG
import timber.log.Timber
import java.lang.Exception
import java.time.LocalDate
import java.time.chrono.HijrahDate
import java.time.format.DateTimeFormatter
import java.util.*

class PrayerTimeDetailsViewModel (private val dataRepo: DataRepository, private val item: PrayerTime) : ViewModel() {
    val newPrayerTime = MutableLiveData<PrayerTime>()
    val dataIsUpdated = MutableLiveData<Boolean>()

    init {
        createDuplicate()
    }

    //private val _updatedPrayerTime = dataRepo.updatePrayerTime
    //val updatedPrayerTime: LiveData<PrayerTime> = _updatedPrayerTime

    fun getItem(): PrayerTime{
        return item
    }

    private fun createDuplicate(){
        val item = PrayerTime(item.id,
            CustomDate(item.dateTime.date),
            CustomLocation(item.location.latitude, item.location.longitude, item.location.timezone),
            Fajr(item.Fajr.time, item.Fajr.doNotify, item.Fajr.isDone),
            Sunrise(item.Sunrise.time, item.Sunrise.doNotify, item.Sunrise.isDone),
            Dhuhr(item.Dhuhr.time, item.Dhuhr.doNotify, item.Dhuhr.isDone),
            Asr(item.Asr.time, item.Asr.doNotify, item.Asr.isDone),
            Maghrib(item.Maghrib.time, item.Maghrib.doNotify, item.Maghrib.isDone),
            Isha(item.Isha.time, item.Isha.doNotify, item.Isha.isDone),
        )

        newPrayerTime.postValue(item)
    }

    fun getHijriDate(date: String): String{
        var formattedIslamicDate = ""
        try {
            val dateFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("MMM dd, yyyy", Locale.getDefault())
            val gregorianDate: LocalDate = LocalDate.parse(date, dateFormatter)
            val islamicDate: HijrahDate = HijrahDate.from(gregorianDate)
            val toDateFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("MMMM dd, yyyy")
            formattedIslamicDate = islamicDate.format(toDateFormatter)

        }catch (e: Exception){
            Timber.tag(GlobalVariables.TAG).e("NextPrayerViewModel Error:${e.message}")
        }
        return formattedIslamicDate
    }

    fun getRandomImage(): Int{
        val randomNumber = generateRandom()
        Timber.tag(TAG).d("Random Number $randomNumber")
        when(randomNumber){
            1->{
                return R.drawable.mosque_1
            }
            2->{
                return R.drawable.mosque_2
            }
            3->{
                return R.drawable.mosque_3
            }
            4->{
                return R.drawable.mosque_4
            }
            5->{
                return R.drawable.mosque_5
            }
            else->{
                return R.drawable.mosque_6
            }
        }
    }

    private fun generateRandom(): Int {
        return (1..6).random()
    }


//    fun updateNotification(view: View, waqtValue: String){
//        val image = view as ImageView
//        val waqtType = WaqtType.findByName(waqtValue)
//        Timber.tag(TAG).d("Notification Item Clicked on : $waqtValue, WaqtType: $waqtType")
//        val date = item.dateTime.date
//        when(waqtType){
//            WaqtType.Fajr->{
//                _dupItem.Fajr.doNotify = !_dupItem.Fajr.doNotify
//            }
//            WaqtType.Sunrise->{
//                _dupItem.Sunrise.doNotify = !_dupItem.Sunrise.doNotify
//            }
//            WaqtType.Dhuhr->{
//                _dupItem.Dhuhr.doNotify = !_dupItem.Dhuhr.doNotify
//            }
//            WaqtType.Asr->{
//                _dupItem.Asr.doNotify = !_dupItem.Asr.doNotify
//            }
//            WaqtType.Maghrib->{
//                _dupItem.Maghrib.doNotify = !_dupItem.Maghrib.doNotify
//            }
//            else -> {
//                _dupItem.Isha.doNotify = !_dupItem.Isha.doNotify
//            }
//
//
//        }
//        Timber.tag(TAG).d("$_dupItem")
//
////        when(waqtType){
////            WaqtType.Fajr->{
////                dataRepo.updateNotification(waqtType, date)
////            }
////            WaqtType.Sunrise->{
////                dataRepo.updateNotification(waqtType,date)
////            }
////            WaqtType.Dhuhr->{
////                dataRepo.updateNotification(waqtType, date)
////            }
////            WaqtType.Asr->{
////                dataRepo.updateNotification(waqtType, date)
////            }
////            WaqtType.Maghrib->{
////                dataRepo.updateNotification(waqtType, date)
////            }
////            else -> {
////                dataRepo.updateNotification(waqtType, date)
////            }
////        }
//
//        dataIsUpdated.postValue(true)
//
//    }

//    fun saveThisPrayerTime(){
//        dataRepo.updatePrayerTime(_dupItem)
//    }


}