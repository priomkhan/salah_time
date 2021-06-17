package com.priomkhan.salahtime.ui.navigation.nextprayer

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.batoulapps.adhan.CalculationParameters
import com.priomkhan.salahtime.localdata.NextPrayer
import com.priomkhan.salahtime.repository.DataRepository
import java.util.*

class NextPrayerViewModel(private val dataRepo: DataRepository) : ViewModel() {

    private val _nextPrayer = MutableLiveData<NextPrayer>().apply {
        //value = "This is Ramadan Fragment"
    }
    val text: LiveData<NextPrayer> = _nextPrayer

    fun getNextPrayerTime(lat: Double, long: Double, params: CalculationParameters): HashMap<String, Date> {
        return dataRepo.getNextPrayerTime(lat, long, params)
    }

    fun getCurrentPrayerTime(lat: Double, long: Double, params: CalculationParameters): HashMap<String, Date> {
        return dataRepo.getCurrentPrayerTime(lat, long, params)
    }

}