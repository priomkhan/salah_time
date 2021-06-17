package com.priomkhan.salahtime.util

import com.batoulapps.adhan.CalculationMethod

class GlobalVariables {

    companion object{
        const val TAG = "PRAYERLOG"

        var isNetworkConnected: Boolean = false

        var notificationOn: Boolean = false

        var fajrAdhanOn : Boolean= false
        var sunriseNotificationOn : Boolean = false
        var dhuhrAdhanOn : Boolean = false
        var asrAdhanOn : Boolean = false
        var maghribAdhanOn : Boolean = false
        var ishaAdhanOn : Boolean = false

        var fajrTimeAdjustmentValue : Int = 0
        var dhuhrTimeAdjustmentValue : Int = 0
        var asrTimeAdjustmentValue : Int = 0
        var maghribTimeAdjustmentValue : Int = 0
        var ishaTimeAdjustmentValue : Int = 0


        //Prayer Time : https://aladhan.com/
        const val WEB_SERVICE_URL = "https://api.aladhan.com"
        const val BY_CITY_URL = "$WEB_SERVICE_URL/v1/calendarByCity"
        //const val BY_CITY_URL = "$WEB_SERVICE_URL/calendarByCity"
        const val PERMISSION_REQUEST_LOCATION = 0
        const val ALARM_ID = 1010

        var longitude : Double = 0.0
        var latitude : Double = 0.0
        var cityName : String = ""
        var stateName: String = ""
        var countryName: String =""

        //var calculationMethod : CalculationParameters = CalculationMethod.NORTH_AMERICA.parameters
        var calculationMethodName : String = CalculationMethod.NORTH_AMERICA.name


    }
}