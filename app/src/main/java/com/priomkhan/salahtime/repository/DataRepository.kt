package com.priomkhan.salahtime.repository

import android.content.Context
import androidx.annotation.WorkerThread
import androidx.lifecycle.MutableLiveData
import com.batoulapps.adhan.*
import com.batoulapps.adhan.CalculationMethod
import com.batoulapps.adhan.data.DateComponents
import com.priomkhan.salahtime.localdata.*
import com.priomkhan.salahtime.util.GlobalVariables
import com.priomkhan.salahtime.util.GlobalVariables.Companion.TAG
import com.priomkhan.salahtime.util.GlobalVariables.Companion.asrTimeAdjustmentValue
import com.priomkhan.salahtime.util.GlobalVariables.Companion.calculationMethodName
import com.priomkhan.salahtime.util.GlobalVariables.Companion.cityName
import com.priomkhan.salahtime.util.GlobalVariables.Companion.countryName
import com.priomkhan.salahtime.util.GlobalVariables.Companion.dhuhrTimeAdjustmentValue
import com.priomkhan.salahtime.util.GlobalVariables.Companion.fajrTimeAdjustmentValue
import com.priomkhan.salahtime.util.GlobalVariables.Companion.ishaTimeAdjustmentValue
import com.priomkhan.salahtime.util.GlobalVariables.Companion.latitude
import com.priomkhan.salahtime.util.GlobalVariables.Companion.longitude
import com.priomkhan.salahtime.util.GlobalVariables.Companion.maghribTimeAdjustmentValue
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.*


class DataRepository(private val dao: SalahDao) {


    var netWorkNotAvailable= MutableLiveData<Boolean>()
    var thisMonthWebData = MutableLiveData<List<PrayerTime>>()
    var thisMonthData = MutableLiveData<List<PrayerTime>>()
//    var nextMonthData = MutableLiveData<List<PrayerTime>>()
    var qiblaDirection = MutableLiveData<Double>()
    lateinit var tempDay : Date


    init {

    }

    fun loadData(){

        CoroutineScope(Dispatchers.IO).launch {

            val data = dao.getAll()
            if (data.isEmpty()){
                //Timber.tag(TAG).d("Loading Data From Internet...")


                getNextThirtyDaysFromWeb()

            }else{
                //Timber.tag(TAG).d("Show Data From Database...")
                thisMonthData.postValue(data)
            }
        }

    }

    fun refreshData() {
        CoroutineScope(Dispatchers.IO).launch {
            //Timber.tag(TAG).d("Deleting All Data from Database.")
            if (networkAvailable()){
                dao.deleteAll()
                getNextThirtyDaysFromWeb()
            }else{
                Timber.tag(TAG).d("Network Not Available.")
            }

        }
    }

    @WorkerThread
    private suspend fun getNextThirtyDaysFromWeb(){

        val today = Date()
        val listOfData = LinkedList<PrayerTime>()
        if (networkAvailable()){
            for (i in 1..30){
                if (i==1){
                    tempDay = today
                    listOfData.add(loadTodayFromWeb())
                }else{
                    tempDay = getNextDay(tempDay)
                    listOfData.add(loadNextDayFromWeb(tempDay))
                }
            }
            thisMonthWebData.postValue(listOfData)

        }else{
            Timber.tag(TAG).d("Network Not Available.")
        }

    }

    private fun loadTodayFromWeb() : PrayerTime{
        return getDataFromWeb(Date())
    }

    private fun loadNextDayFromWeb(date: Date): PrayerTime{
        return getDataFromWeb(date)
    }

    private fun getNextDay(date: Date): Date{
//        val formatter = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
//        //val formattedDate = formatter.parse(date.toString()) ?: Date()
//        //val formattedDate =
        val cal = Calendar.getInstance()
        cal.time = date
        cal.add(Calendar.DATE, 1)
        val nextDay = cal.time
        Timber.tag(TAG).d("Input date : $date. Next Day : $nextDay")

        return nextDay
    }


    private fun getDataFromWeb(dateValue: Date): PrayerTime{

        val coordinates = Coordinates(latitude, longitude)

        //val date = DateComponents(2021, 4, 28)
        val date = DateComponents.from(dateValue)
        val params: CalculationParameters = CalculationMethod.valueOf(calculationMethodName).parameters

        params.adjustments.fajr = fajrTimeAdjustmentValue
        params.adjustments.dhuhr = dhuhrTimeAdjustmentValue
        params.adjustments.asr = asrTimeAdjustmentValue
        params.adjustments.maghrib = maghribTimeAdjustmentValue
        params.adjustments.isha = ishaTimeAdjustmentValue


        val prayerTimes = PrayerTimes(coordinates, date, params)

        //Timber.tag(TAG).d("New PrayerTime Magrib Unformatted: ${prayerTimes.maghrib}")
        val format = SimpleDateFormat("hh:mm a", Locale.getDefault())
        val formattedTime = format.format(prayerTimes.maghrib)
        //Timber.tag(TAG).d("New PrayerTime Magrib formatted: $formatedTime")

        val newPrayerTime = PrayerTime(
            0,
            CustomDate(formatDate(dateValue)),
            CustomLocation(latitude.toString(), longitude.toString(), "$countryName/$cityName"),
            Fajr(formatTime(prayerTimes.fajr)),
            Sunrise(formatTime(prayerTimes.sunrise)),
            Dhuhr(formatTime(prayerTimes.dhuhr)),
            Asr(formatTime(prayerTimes.asr)),
            Maghrib(formatTime(prayerTimes.maghrib)),
            Isha(formatTime(prayerTimes.isha))
        )



        //Timber.tag(TAG).d("PrayerTime: $newPrayerTime")
        return newPrayerTime
    }

    private fun formatTime(date: Date): String{

        val format = SimpleDateFormat("hh:mm a", Locale.getDefault())
        val formattedTime = format.format(date)

        return formattedTime.toString()

    }

    private fun formatDate(date: Date): String{

        val format = SimpleDateFormat("MMM dd, YYYY", Locale.getDefault())
        val formattedDate = format.format(date)

        return formattedDate.toString()

    }

    private fun networkAvailable():Boolean{

        return if (GlobalVariables.isNetworkConnected){
            netWorkNotAvailable.postValue(false)
            true
        }else{
            netWorkNotAvailable.postValue(true)
            false
        }

    }

    private fun getDataFromDB(date: String): PrayerTime? {
        return dao.getDataOfDate(date)
    }

    fun updatePrayerTime(item: PrayerTime) {
        CoroutineScope(Dispatchers.IO).launch {
            dao.updatePrayerTime(item)
        }
    }

    fun getCurrentPrayerTime(lat: Double, long: Double, params: CalculationParameters) : HashMap<String, Date> {
        var map = getCurrentPrayerTimeToday(lat, long, params)
        if (map.isEmpty()){
            map = getCurrentPrayerTimeYesterday(lat, long, params)
        }

        return map
    }

    private fun getCurrentPrayerTimeToday(lat: Double, long: Double, params: CalculationParameters): HashMap<String, Date>{
        val map = hashMapOf<String, Date>()
        val coordinates = Coordinates(lat, long)
        Timber.tag(TAG).d("Current Location: ${coordinates.latitude}:${coordinates.longitude}")
        val today = Calendar.getInstance()


        try {
            val date = DateComponents.from(today.time)

            val prayerTimes = PrayerTimes(coordinates, date, params)
            val currentPrayerName : Prayer = prayerTimes.currentPrayer()
            val currentPrayerTime =prayerTimes.timeForPrayer(currentPrayerName)
            map[currentPrayerName.name] = currentPrayerTime

        }catch (e: Exception){
            Timber.tag(TAG).e("No More Prayer Today")
        }

        return map
    }

    private fun getCurrentPrayerTimeYesterday(lat: Double, long: Double, params: CalculationParameters): HashMap<String, Date>{
        val map = hashMapOf<String, Date>()
        val coordinates = Coordinates(lat, long)
        Timber.tag(TAG).d("Current Location: ${coordinates.latitude}:${coordinates.longitude}")
        val tomorrow = Calendar.getInstance()
        tomorrow.add(Calendar.DATE, -1)

        try {
            val date = DateComponents.from(tomorrow.time)

            val prayerTimes = PrayerTimes(coordinates, date, params)
            val currentPrayerName : Prayer = prayerTimes.currentPrayer()
            val currentPrayerTime =prayerTimes.timeForPrayer(currentPrayerName)

            Timber.tag(TAG).d("Tomorrow ${tomorrow.time}, next prayer $currentPrayerName at $currentPrayerTime")

            map[currentPrayerName.name] = currentPrayerTime

        }catch (e: Exception){
            Timber.tag(TAG).e(e)
        }

        return map
    }

    fun getNextPrayerTime(lat: Double, long: Double, params: CalculationParameters) : HashMap<String, Date> {
        var map = getNextPrayerTimeToday(lat, long, params)
        if (map.isEmpty()){
            map = getNextPrayerTimeTomorrow(lat, long, params)
        }

        return map
    }


    private fun getNextPrayerTimeToday(lat: Double, long: Double, params: CalculationParameters): HashMap<String, Date>{
        val map = hashMapOf<String, Date>()
        val coordinates = Coordinates(lat, long)
        Timber.tag(TAG).d("Current Location: ${coordinates.latitude}:${coordinates.longitude}")
        val today = Calendar.getInstance()


        try {
            val date = DateComponents.from(today.time)

            val prayerTimes = PrayerTimes(coordinates, date, params)
            val nextPrayerName : Prayer = prayerTimes.nextPrayer()
            val nextPrayerTime =prayerTimes.timeForPrayer(nextPrayerName)
            Timber.tag(TAG).d("Today $today")
            Timber.tag(TAG).d("Next prayer $nextPrayerName at $nextPrayerTime")
            map[nextPrayerName.name] = nextPrayerTime

        }catch (e: Exception){
            Timber.tag(TAG).e("No More Prayer Today")
        }

        return map
    }

    private fun getNextPrayerTimeTomorrow(lat: Double, long: Double, params: CalculationParameters): HashMap<String, Date>{
        val map = hashMapOf<String, Date>()
        val coordinates = Coordinates(lat, long)
        Timber.tag(TAG).d("Current Location: ${coordinates.latitude}:${coordinates.longitude}")
        val tomorrow = Calendar.getInstance()
        tomorrow.add(Calendar.DATE, 1)

        try {
            val date = DateComponents.from(tomorrow.time)

            val prayerTimes = PrayerTimes(coordinates, date, params)
            val nextPrayerName : Prayer = prayerTimes.nextPrayer()
            val nextPrayerTime =prayerTimes.timeForPrayer(nextPrayerName)

            Timber.tag(TAG).d("Tomorrow ${tomorrow.time}, next prayer $nextPrayerName at $nextPrayerTime")

            map[nextPrayerName.name] = nextPrayerTime

        }catch (e: Exception){
            Timber.tag(TAG).e(e)
        }

        return map
    }

    suspend fun savePrayerListToDatabase(newWebPrayerList: List<PrayerTime>?) {
        newWebPrayerList?.let {
            dao.deleteAll()
            dao.insertPrayerTimes(newWebPrayerList)
            loadData()
        }

    }

    fun getQiblaAngle(lat: Double, long: Double){

        val coordinates = Coordinates(lat, long)
        val qibla = Qibla(coordinates)
        val qiblaDirectionAngle : Double = qibla.direction
        Timber.tag(TAG).d("Direction: $qiblaDirectionAngle")

        qiblaDirection.postValue(qiblaDirectionAngle)

    }

    fun prayerIsDone(prayer: Prayer) {
        val today = Calendar.getInstance().time
        val dateFormatter = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
        val formattedDate = dateFormatter.format(today)

        CoroutineScope(Dispatchers.IO).launch {
            if (prayer == Prayer.FAJR) {
                dao.updateFajrIsDone(formattedDate)
            } else if (prayer == Prayer.SUNRISE) {
                dao.updateSunriseIsDone(formattedDate)
            } else if (prayer == Prayer.DHUHR) {
                dao.updateDhuhrIsDone(formattedDate)
            } else if (prayer == Prayer.ASR) {
                dao.updateAsrIsDone(formattedDate)
            } else if (prayer == Prayer.MAGHRIB) {
                dao.updateMaghribIsDone(formattedDate)
            } else if (prayer == Prayer.ISHA) {
                dao.updateIshaIsDone(formattedDate)
            }

        }
    }


    companion object {
        @Volatile
        private var instance: DataRepository? = null
        private const val PAGE_SIZE = 20

        fun getInstance(context: Context): DataRepository? {
            return instance ?: synchronized(DataRepository::class.java) {
                if (instance == null) {
                    val database = SalahDatabase.getInstance(context)
                    instance = DataRepository(database.salahDao())
                }
                return instance
            }
        }
    }
}