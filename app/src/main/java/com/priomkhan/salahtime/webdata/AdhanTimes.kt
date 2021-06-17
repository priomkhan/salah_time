package com.priomkhan.salahtime.webdata


import com.google.gson.annotations.SerializedName

data class AdhanTimes(
    @SerializedName("code")
    val code: Int,
    @SerializedName("status")
    val status: String,
    @SerializedName("data")
    val `data`: List<Data>
)