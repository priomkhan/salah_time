package com.priomkhan.salahtime.ui.navigation.prayertime.details

import android.app.Activity
import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.priomkhan.salahtime.localdata.PrayerTime
import com.priomkhan.salahtime.repository.DataRepository
import java.lang.reflect.InvocationTargetException

class PrayerTimeDetailsViewModelFactory(private val repository: DataRepository?, private val item: PrayerTime): ViewModelProvider.Factory{
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        try {
            return modelClass.getConstructor(DataRepository::class.java, PrayerTime::class.java)
                .newInstance(repository, item)
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
        fun createFactory(activity: Activity, item: PrayerTime):PrayerTimeDetailsViewModelFactory{
            val context = activity.applicationContext ?: throw IllegalStateException("Not yet attached to Application")
            return PrayerTimeDetailsViewModelFactory(DataRepository.getInstance(context), item)
        }
    }
}