package com.priomkhan.salahtime.localdata

enum class WaqtType (val value: String){
    Fajr("Fajr"),
    Sunrise("Sunrise"),
    Dhuhr("Dhuhr"),
    Asr("Asr"),
    Maghrib("Maghrib"),
    Isha("Isha");

    companion object {
        fun findByName(name: String?) = when (name) {
            Fajr.value -> Fajr
            Sunrise.value -> Sunrise
            Dhuhr.value -> Dhuhr
            Asr.value -> Asr
            Maghrib.value -> Maghrib
            else -> Isha
        }
    }

}