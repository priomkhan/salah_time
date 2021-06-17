package com.priomkhan.salahtime.localdata

import android.os.Parcelable
import androidx.room.*
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "prayer_time")
data class PrayerTime(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Long = 0,
    @Embedded
    var dateTime : CustomDate,
    @Embedded
    val location: CustomLocation,
    @Embedded(prefix = "fajr_")
    val Fajr: Fajr,
    @Embedded(prefix = "sunrise_")
    val Sunrise: Sunrise,
    @Embedded(prefix = "dhuhr_")
    val Dhuhr: Dhuhr,
    @Embedded(prefix = "asr_")
    val Asr: Asr,
    @Embedded(prefix = "maghrib_")
    val Maghrib: Maghrib,
    @Embedded(prefix = "isha_")
    val Isha: Isha
    ): Parcelable
