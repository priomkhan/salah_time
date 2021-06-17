package com.priomkhan.salahtime.localdata

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "calculation_method")
data class CalculationMethod(
                             @ColumnInfo(name = "name")
                             val name: String): Parcelable{

}
