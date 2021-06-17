package com.priomkhan.salahtime.ui.navigation.prayertime

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.priomkhan.salahtime.R
import com.priomkhan.salahtime.databinding.PrayerTimeListItemBinding
import com.priomkhan.salahtime.localdata.PrayerTime
import com.priomkhan.salahtime.localdata.WaqtType
import com.priomkhan.salahtime.util.GlobalVariables.Companion.TAG
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.*

class PrayerTimeAdapter(private val itemClickListener: ItemClickListener) : RecyclerView.Adapter<PrayerTimeAdapter.PrayerTimeViewHolder>() {

    var data = listOf<PrayerTime>()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

//    fun addData(item: PrayerTime){
//        data.add(item)
//        notifyDataSetChanged()
//    }

    fun getItemPosition(date: String): Int {
        for (i in data.indices) {
            if (data[i].dateTime?.date.equals(date)) {
                return i
            }
        }
        return -1
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PrayerTimeViewHolder {
        return PrayerTimeViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: PrayerTimeViewHolder, position: Int) {
        val item = data[position]

        holder.bind(item, itemClickListener)

    }

    override fun getItemCount(): Int {
        return data.size
    }

    class PrayerTimeViewHolder internal constructor(private val binding: PrayerTimeListItemBinding) : RecyclerView.ViewHolder(binding.root){
        private val context = itemView.context

        fun bind(item: PrayerTime, itemClickListener: ItemClickListener){

            binding.item = item
            binding.itemClickListener = itemClickListener

            val itemDate = item.dateTime?.date
            val formatter = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
            //val formattedDate : String = formatter.format(Date()).replace(".", "", true)
            val formattedDate : String = formatter.format(Date())
            if(itemDate.equals(formattedDate)){
               // Timber.tag(TAG).d("$itemDate == $formattedDate")
                binding.txtDate.text = context.getString(R.string.today_text, item.dateTime.date)
            }else{
                //Timber.tag(TAG).d("$itemDate != $formattedDate")
                binding.txtDate.text = itemDate
            }


            binding.txtLocation.text = item.location.timezone
            binding.cvFajr.nameText.text = WaqtType.Fajr.value
            binding.cvSunrise.nameText.text = WaqtType.Sunrise.value
            binding.cvDhuhr.nameText.text = WaqtType.Dhuhr.value
            binding.cvAsr.nameText.text = WaqtType.Asr.value
            binding.cvMaghrib.nameText.text = WaqtType.Maghrib.value
            binding.cvIsha.nameText.text = WaqtType.Isha.value

            binding.cvFajr.txtTime.text = item.Fajr.time
            binding.cvSunrise.txtTime.text = item.Sunrise.time
            binding.cvDhuhr.txtTime.text = item.Dhuhr.time
            binding.cvAsr.txtTime.text = item.Asr.time
            binding.cvMaghrib.txtTime.text = item.Maghrib.time
            binding.cvIsha.txtTime.text = item.Isha.time

            binding.cvFajr.btnNotification.setImageResource(getNotificationIcon(item.Fajr.doNotify, item.Fajr.isDone))
            binding.cvSunrise.btnNotification.setImageResource(getNotificationIcon(item.Sunrise.doNotify, item.Sunrise.isDone))
            binding.cvDhuhr.btnNotification.setImageResource(getNotificationIcon(item.Dhuhr.doNotify, item.Dhuhr.isDone))
            binding.cvAsr.btnNotification.setImageResource(getNotificationIcon(item.Asr.doNotify, item.Asr.isDone))
            binding.cvMaghrib.btnNotification.setImageResource(getNotificationIcon(item.Maghrib.doNotify, item.Maghrib.isDone))
            binding.cvIsha.btnNotification.setImageResource(getNotificationIcon(item.Isha.doNotify, item.Isha.isDone))

            binding.cvItem.setOnClickListener {
                itemClickListener.onClick(item)
            }
        }

        private fun getNotificationIcon(doNotify: Boolean, isDone: Boolean): Int {

            return if(isDone){
                R.drawable.ic_is_done
            }else{
                if (doNotify){
                    R.drawable.ic_notification_on
                }else{
                    R.drawable.ic_notification_off
                }
            }

        }

        companion object{
            fun from(parent: ViewGroup): PrayerTimeViewHolder{
                val layoutInflater = LayoutInflater.from(parent.context)
                val view = PrayerTimeListItemBinding.inflate(layoutInflater, parent, false)
                return PrayerTimeViewHolder(view)
            }
        }
    }
}

class ItemClickListener(val clickListener: (item: PrayerTime)-> Unit){
    fun onClick(item: PrayerTime){
        clickListener(item)
    }
}