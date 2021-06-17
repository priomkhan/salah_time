package com.priomkhan.salahtime.webdata


import com.google.gson.annotations.SerializedName

data class Methods(
    @SerializedName("code")
    val code: Int,
    @SerializedName("status")
    val status: String
)