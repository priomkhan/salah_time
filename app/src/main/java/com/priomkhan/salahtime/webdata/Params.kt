package com.priomkhan.salahtime.webdata


import com.google.gson.annotations.SerializedName

data class Params(
    @SerializedName("Fajr")
    val Fajr: Int,
    @SerializedName("Isha")
    val Isha: Int
)