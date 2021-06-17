package com.priomkhan.salahtime.localdata

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface SalahDao {
    @Query("SELECT * FROM prayer_time")
    fun getAll(): List<PrayerTime>

    @Query("SELECT * FROM prayer_time WHERE date_string == :date")
    fun getDataOfDate(date: String): PrayerTime

    @Insert
    suspend fun insertPrayerTime(item: PrayerTime)

    @Insert
    suspend fun insertPrayerTimes(itemList: List<PrayerTime>)

    @Update
    suspend fun updatePrayerTime(data:PrayerTime)

    @Query("DELETE FROM prayer_time")
    suspend fun deleteAll()

    /*Update Notification*/
    @Query("UPDATE prayer_time set fajr_doNotify= NOT fajr_doNotify WHERE date_string == :date")
    suspend fun updateFajrNotification(date: String): Int

    @Query("UPDATE prayer_time set sunrise_doNotify= NOT sunrise_doNotify WHERE date_string == :date")
    suspend fun updateSunriseNotification(date: String): Int

    @Query("UPDATE prayer_time set dhuhr_doNotify= NOT dhuhr_doNotify WHERE date_string == :date")
    suspend fun updateDhuhrNotification(date: String): Int

    @Query("UPDATE prayer_time set asr_doNotify= NOT asr_doNotify WHERE date_string == :date")
    suspend fun updateAsrNotification(date: String): Int

    @Query("UPDATE prayer_time set maghrib_doNotify= NOT maghrib_doNotify WHERE date_string == :date")
    suspend fun updateMaghribNotification(date: String): Int

    @Query("UPDATE prayer_time set isha_doNotify= NOT isha_doNotify WHERE date_string == :date")
    suspend fun updateIshaNotification(date: String): Int


    /*Update if Payer is Done*/
    @Query("UPDATE prayer_time set fajr_doNotify= NOT fajr_isDone WHERE date_string == :date")
    suspend fun updateFajrIsDone(date: String): Int

    @Query("UPDATE prayer_time set sunrise_doNotify= NOT sunrise_isDone WHERE date_string == :date")
    suspend fun updateSunriseIsDone(date: String): Int

    @Query("UPDATE prayer_time set dhuhr_doNotify= NOT dhuhr_isDone WHERE date_string == :date")
    suspend fun updateDhuhrIsDone(date: String): Int

    @Query("UPDATE prayer_time set asr_doNotify= NOT asr_isDone WHERE date_string == :date")
    suspend fun updateAsrIsDone(date: String): Int

    @Query("UPDATE prayer_time set maghrib_doNotify= NOT maghrib_isDone WHERE date_string == :date")
    suspend fun updateMaghribIsDone(date: String): Int

    @Query("UPDATE prayer_time set isha_doNotify= NOT isha_isDone WHERE date_string == :date")
    suspend fun updateIshaIsDone(date: String): Int




}