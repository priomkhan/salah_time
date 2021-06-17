package com.priomkhan.salahtime.ui.navigation.qibla

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.priomkhan.salahtime.repository.DataRepository
import com.priomkhan.salahtime.util.GlobalVariables.Companion.TAG
import timber.log.Timber

class QiblaViewModel(private val dataRepo: DataRepository) : ViewModel() {

    private val _qiblaDirectionAngle = dataRepo.qiblaDirection
    val text: LiveData<Double> = _qiblaDirectionAngle

    fun getQibla(latitude: Double, longitude: Double){
        Timber.tag(TAG).d("Getting Qibla direction with Latitude: $latitude, Longitude: $longitude")
        dataRepo.getQiblaAngle(latitude, longitude)
    }
}