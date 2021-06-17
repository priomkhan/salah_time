package com.priomkhan.salahtime.network

import android.content.Context
import android.net.ConnectivityManager
import android.net.ConnectivityManager.NetworkCallback
import android.net.Network
import android.net.NetworkRequest
import com.priomkhan.salahtime.util.GlobalVariables.Companion.TAG
import com.priomkhan.salahtime.util.GlobalVariables.Companion.isNetworkConnected
import timber.log.Timber


class CheckNetwork(private val context: Context) {

    init {
        registerNetworkCallback()
    }

    private fun registerNetworkCallback() {
        try {
            val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val builder = NetworkRequest.Builder()
            connectivityManager.registerDefaultNetworkCallback(object : NetworkCallback() {
                override fun onAvailable(network: Network) {
                    isNetworkConnected = true // Global Static Variable
                    Timber.tag(TAG).d("Connection Available")
                }

                override fun onLost(network: Network) {
                    isNetworkConnected = false // Global Static Variable
                    Timber.tag(TAG).d("Connection Lost")
                }
            }
            )
            isNetworkConnected = false
        } catch (e: Exception) {
            isNetworkConnected = false
        }
    }


}