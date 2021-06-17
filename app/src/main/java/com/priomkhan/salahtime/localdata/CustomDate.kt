package com.priomkhan.salahtime.localdata

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "custom_date")
data class CustomDate (
                       @ColumnInfo(name = "date_string")
                       var date: String):Parcelable{

}
