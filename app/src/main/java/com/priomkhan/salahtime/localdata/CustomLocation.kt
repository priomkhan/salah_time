package com.priomkhan.salahtime.localdata

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "custom_location")
data class CustomLocation (
                           val latitude: String,
                           val longitude: String,
                           val timezone: String):Parcelable {
}
