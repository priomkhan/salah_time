package com.priomkhan.salahtime.webdata


import com.google.gson.annotations.SerializedName

data class Offset(
    @SerializedName("Asr")
    val Asr: Int,
    @SerializedName("Dhuhr")
    val Dhuhr: Int,
    @SerializedName("Fajr")
    val Fajr: Int,
    @SerializedName("Imsak")
    val Imsak: Int,
    @SerializedName("Isha")
    val Isha: Int,
    @SerializedName("Maghrib")
    val Maghrib: Int,
    @SerializedName("Midnight")
    val Midnight: Int,
    @SerializedName("Sunrise")
    val Sunrise: Int,
    @SerializedName("Sunset")
    val Sunset: Int
)