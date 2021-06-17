package com.priomkhan.salahtime.webdata

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query
import java.util.*

interface AdhanTimeService {
    @GET("/v1/calendarByCity")
    suspend fun getPrayerTimeByCity(@Query(value="city") city: String,
                                    @Query(value="country") country: String,
                                    @Query(value="method") method: Int,
                                    @Query(value="month") month: Int,
                                    @Query(value="year") year: Int) : Response<AdhanTimes>
}

interface AdhanTimeMethods{
    @GET("/v1/methods")
    suspend fun getPrayerTimeCalculationMethods(): Response<Methods>
}
