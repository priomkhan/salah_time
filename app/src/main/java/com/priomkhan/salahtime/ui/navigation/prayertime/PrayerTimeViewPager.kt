package com.priomkhan.salahtime.ui.navigation.prayertime

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.priomkhan.salahtime.R
import com.priomkhan.salahtime.databinding.PrayerTimeListItemBinding
import com.priomkhan.salahtime.localdata.PrayerTime
import com.priomkhan.salahtime.localdata.WaqtType
import com.priomkhan.salahtime.util.GlobalVariables
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.*

class PrayerTimeViewPager (): PagedListAdapter<PrayerTime, PrayerTimeViewPager.PrayerTimeViewHolder>(DIFF_CALLBACK){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PrayerTimeViewHolder {
        return PrayerTimeViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: PrayerTimeViewHolder, position: Int) {
        val item = getItem(position)
        item?.let {
            holder.bind(item)
        }

    }


    companion object {

        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<PrayerTime>() {
            override fun areItemsTheSame(oldItem: PrayerTime, newItem: PrayerTime): Boolean {
                return oldItem.dateTime == newItem.dateTime
            }

            override fun areContentsTheSame(oldItem: PrayerTime, newItem: PrayerTime): Boolean {
                return oldItem == newItem
            }
        }
    }


    class PrayerTimeViewHolder internal constructor(private val binding: PrayerTimeListItemBinding) : RecyclerView.ViewHolder(binding.root){
        private val context = itemView.context

        fun bind(item: PrayerTime){

            binding.item = item

            val itemDate = item.dateTime?.date
            val formatter = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
            //val formattedDate : String = formatter.format(Date()).replace(".", "", true)
            val formattedDate : String = formatter.format(Date())
            if(itemDate.equals(formattedDate)){
                Timber.tag(GlobalVariables.TAG).d("$itemDate == $formattedDate")
                binding.txtDate.text = context.getString(R.string.today_text)
            }else{
                Timber.tag(GlobalVariables.TAG).d("$itemDate != $formattedDate")
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