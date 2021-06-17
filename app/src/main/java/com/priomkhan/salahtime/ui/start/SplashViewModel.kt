package com.priomkhan.salahtime.ui.start

import android.location.Location
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.GeoPoint
import com.priomkhan.salahtime.repository.DataRepository

class SplashViewModel (private val dataRepo: DataRepository): ViewModel(){

    val payerData = dataRepo.thisMonthData

    val mLocationPermissionGranted = MutableLiveData<Boolean>()
    val currentLocation = MutableLiveData<Location>()
}
