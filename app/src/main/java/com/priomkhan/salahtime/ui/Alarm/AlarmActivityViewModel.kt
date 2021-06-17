package com.priomkhan.salahtime.ui.Alarm

import androidx.lifecycle.ViewModel
import com.batoulapps.adhan.Prayer
import com.priomkhan.salahtime.repository.DataRepository

class AlarmActivityViewModel(private val dataRepo: DataRepository) : ViewModel(){


    fun isDone(prayer: Prayer){
        dataRepo.prayerIsDone(prayer)
    }


}