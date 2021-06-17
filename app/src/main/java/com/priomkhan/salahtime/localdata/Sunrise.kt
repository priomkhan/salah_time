package com.priomkhan.salahtime.localdata

import android.content.Context
import android.graphics.drawable.Drawable
import android.os.Parcelable
import androidx.core.content.res.ResourcesCompat
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.priomkhan.salahtime.R
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "sunrise_table")
data class Sunrise(
    @ColumnInfo(name = "time")
    val time: String,
    @ColumnInfo(name = "doNotify")
    var doNotify: Boolean=false,
    @ColumnInfo(name = "isDone")
    var isDone:Boolean=false
): Parcelable{
    fun getNotificationIcon(context: Context): Drawable?{

        return if(isDone){
            ResourcesCompat.getDrawable(context.resources, R.drawable.ic_is_done, null)

        }else{
            if (doNotify){
                ResourcesCompat.getDrawable(context.resources, R.drawable.ic_notification_on, null)

            }else{
                ResourcesCompat.getDrawable(context.resources, R.drawable.ic_notification_off, null)

            }
        }
    }
}
