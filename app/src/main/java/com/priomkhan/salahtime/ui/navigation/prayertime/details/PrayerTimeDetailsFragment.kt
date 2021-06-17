package com.priomkhan.salahtime.ui.navigation.prayertime.details

import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.priomkhan.salahtime.R
import com.priomkhan.salahtime.databinding.PrayerTimeDetailsFragmentBinding
import com.priomkhan.salahtime.util.GlobalVariables.Companion.TAG
import timber.log.Timber
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.chrono.HijrahDate
import java.time.format.DateTimeFormatter
import java.util.*

class PrayerTimeDetailsFragment : Fragment() {
    private lateinit var binding: PrayerTimeDetailsFragmentBinding

    private lateinit var prayerTimeDetailsViewModel: PrayerTimeDetailsViewModel

    private lateinit var navController: NavController

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.prayer_time_details_fragment,
            container,
            false
        )

        val arguments = PrayerTimeDetailsFragmentArgs.fromBundle(requireArguments())

        val viewModelFactory = PrayerTimeDetailsViewModelFactory.createFactory(
            this.requireActivity(),
            arguments.itemPrayerTime
        )
        prayerTimeDetailsViewModel = ViewModelProvider(this, viewModelFactory).get(
            PrayerTimeDetailsViewModel::class.java
        )

        binding.prayerTimeDetailsViewModel = prayerTimeDetailsViewModel
        binding.contentDetail.prayerTimeDetailsViewModel = prayerTimeDetailsViewModel

        prayerTimeDetailsViewModel.newPrayerTime.observe(viewLifecycleOwner, Observer {
            binding.contentDetail.hijriDate = prayerTimeDetailsViewModel.getHijriDate(it.dateTime.date)

            binding.contentDetail.item = it.apply {
                val formatter = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
                val thisItemDate = formatter.parse(it.dateTime.date)
                val cal = Calendar.getInstance()
                cal.time = thisItemDate!!
                val toFormat = SimpleDateFormat("EEEE MMM dd, yyyy", Locale.getDefault())
                it.dateTime.date = toFormat.format(cal.time)
            }

            binding.appBarImage.setImageResource(prayerTimeDetailsViewModel.getRandomImage())


        })

        prayerTimeDetailsViewModel.dataIsUpdated.observe(requireActivity(), Observer {
            Timber.tag(TAG).d("Data Updated.")
//            it?.let {
//                binding.contentDetail.item = it
//
//                //binding.contentDetail.notifyChange()
//            }
            if (it) {
                binding.contentDetail.invalidateAll()
            }
        })

        (requireActivity() as AppCompatActivity).run {
            supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        }

        setHasOptionsMenu(true)

        navController = Navigation.findNavController(
            requireActivity(), R.id.nav_host_fragment
        )

        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {

//        inflater.inflate(R.menu.menu_save, menu)
//        val menuItem = menu.findItem(R.id.action_save)
//        menuItem.icon = ContextCompat.getDrawable(requireContext(), R.drawable.ic_save)
        super.onCreateOptionsMenu(menu, inflater)
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId

        if(id == android.R.id.home){
           navController.navigateUp()
        }
//        else if(id == R.id.action_save){
//            Timber.tag(TAG).d("Menu Item Save Button Clicked")
//            prayerTimeDetailsViewModel.saveThisPrayerTime()
//            return true
//        }

        return super.onOptionsItemSelected(item)
    }
}