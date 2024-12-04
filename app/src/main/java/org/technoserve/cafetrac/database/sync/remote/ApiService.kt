

package com.example.cafetrac.database.sync.remote

import com.example.cafetrac.database.models.DeviceFarmDto
import org.technoserve.cafetrac.database.models.FarmRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST


interface ApiService {

    @POST("/api/farm/sync/")
    suspend fun syncFarms(@Body farms: List<DeviceFarmDto>): Response<Any>

    @POST("/api/farm/restore/")
    suspend fun getFarmsByDeviceId(@Body request: FarmRequest): List<Any>
}
