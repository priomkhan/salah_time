package com.priomkhan.salahtime.ui.Alarm

import android.app.Activity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.priomkhan.salahtime.localdata.PrayerTime
import com.priomkhan.salahtime.repository.DataRepository
import com.priomkhan.salahtime.ui.navigation.prayertime.details.PrayerTimeDetailsViewModelFactory
import java.lang.reflect.InvocationTargetException

class AlarmActivityViewModelFactory(private val repository: DataRepository?): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        try {
            return modelClass.getConstructor(DataRepository::class.java).newInstance(repository)
        } catch (e: Throwable) {
            throw RuntimeException("Cannot create an instance of $modelClass", e)
        } catch (e: IllegalAccessException) {
            throw RuntimeException("Cannot create an instance of $modelClass", e)
        } catch (e: NoSuchMethodException) {
            throw RuntimeException("Cannot create an instance of $modelClass", e)
        } catch (e: InvocationTargetException) {
            throw RuntimeException("Cannot create an instance of $modelClass", e)
        }
    }

    companion object {
        fun createFactory(activity: Activity): AlarmActivityViewModelFactory {
            val context = activity.applicationContext ?: throw IllegalStateException("Not yet attached to Application")
            return AlarmActivityViewModelFactory(DataRepository.getInstance(context))
        }
    }
}