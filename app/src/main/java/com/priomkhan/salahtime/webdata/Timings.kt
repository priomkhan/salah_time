package com.priomkhan.salahtime.webdata


import com.google.gson.annotations.SerializedName

data class Timings(
    @SerializedName("Asr")
    val Asr: String,
    @SerializedName("Dhuhr")
    val Dhuhr: String,
    @SerializedName("Fajr")
    val Fajr: String,
    @SerializedName("Imsak")
    val Imsak: String,
    @SerializedName("Isha")
    val Isha: String,
    @SerializedName("Maghrib")
    val Maghrib: String,
    @SerializedName("Midnight")
    val Midnight: String,
    @SerializedName("Sunrise")
    val Sunrise: String,
    @SerializedName("Sunset")
    val Sunset: String
)