

package com.example.egnss4coffeev2.database.remote

import com.example.egnss4coffeev2.database.FarmDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST


interface ApiService {

    @POST("/api/farm/sync/")
    suspend fun syncFarms(@Body farms: List<FarmDto>): Response<Any>
}
