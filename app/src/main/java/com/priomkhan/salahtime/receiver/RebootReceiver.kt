package com.priomkhan.salahtime.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import com.priomkhan.salahtime.services.EndLessService
import com.priomkhan.salahtime.services.ServiceState
import com.priomkhan.salahtime.services.getServiceState
import com.priomkhan.salahtime.util.Actions
import com.priomkhan.salahtime.util.GlobalVariables
import timber.log.Timber

class RebootReceiver : BroadcastReceiver() {

    private val adhanBroadcastReceiver = AdhanBroadcastReceiver()

    override fun onReceive(context: Context, intent: Intent) {
        // This method is called when the BroadcastReceiver is receiving an Intent broadcast from reboot.
        if (intent.action == Intent.ACTION_BOOT_COMPLETED && getServiceState(context) == ServiceState.STARTED) {
            Intent(context, EndLessService::class.java).also {
                it.action = Actions.START.name
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    Timber.tag(GlobalVariables.TAG).d("Starting the service in >=28 Mode from a BroadcastReceiver")
                    context.startForegroundService(it)
                    Timber.tag(GlobalVariables.TAG).d("RebootReceiver: Resetting Alarm.")
                    adhanBroadcastReceiver.cancelAlarm(context)
                    adhanBroadcastReceiver.setNextAlarm(context)
                    return
                }
                Timber.tag(GlobalVariables.TAG).d("Starting the service in < 28 Mode from a BroadcastReceiver")
                context.startService(it)
            }
        }
    }
}