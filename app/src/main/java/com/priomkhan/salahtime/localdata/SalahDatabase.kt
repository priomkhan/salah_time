package com.priomkhan.salahtime.localdata

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [PrayerTime::class], version = 1, exportSchema = false)
abstract class SalahDatabase : RoomDatabase() {
    abstract fun salahDao(): SalahDao

    companion object{
        @Volatile
        private var INSTANCE : SalahDatabase ?= null

        /**
         * Returns an instance of Room Database
         *
         * @param context application context
         * @return The singleton TeaDatabase
         */
        fun getInstance(context: Context): SalahDatabase {
            synchronized(this){
                var instance = INSTANCE
                if (instance == null){
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        SalahDatabase::class.java,
                        "salah_database"
                    ).fallbackToDestructiveMigration().build()
                }
                return instance
            }
        }
    }
}
