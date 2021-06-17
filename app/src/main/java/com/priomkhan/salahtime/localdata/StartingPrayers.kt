package com.priomkhan.salahtime.localdata

import android.content.Context
import androidx.annotation.WorkerThread
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.priomkhan.salahtime.R
import com.priomkhan.salahtime.util.executeThread
import org.json.JSONArray
import org.json.JSONException
import timber.log.Timber
import java.io.BufferedReader

class StartingPrayers(private val context: Context) : RoomDatabase.Callback() {
    override fun onCreate(db: SupportSQLiteDatabase) = executeThread {
        Timber.tag("PRAYERLOG").d( "StartingTeas-> onCreate()")
        fillWithStartingData(context)
    }

    @WorkerThread
    private fun fillWithStartingData(context: Context) {
        val dao = SalahDatabase.getInstance(context).salahDao()
        Timber.tag("PRAYERLOG").d( "Database Callback called.")
        try {
            val requestContent = loadJsonArray(context)


            if(requestContent!=null){
                val jsonContent = requestContent.getJSONObject(0)
                val code =  jsonContent.getInt("code")
                Timber.tag("PRAYERLOG").d( "Status Code: ${code.toString()}")
            }
        }catch (e : JSONException){
            e.printStackTrace()
        }

    }


    private fun loadJsonArray(context: Context): JSONArray? {
        val inputStream = context.resources.openRawResource(R.raw.sample_prayer_time)
        BufferedReader(inputStream.reader()).use {
            return JSONArray(it.readText())
        }
    }
}
