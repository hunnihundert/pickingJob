package com.ocfulfillment.fulfillmentapp.data.remote


import com.ocfulfillment.fulfillmentapp.data.model.PickingPostAction
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.PATCH
import retrofit2.http.Path

interface PickingJobsApi {
    @PATCH("api/pickjobs/{pickJobId}")
    suspend fun updatePickJob(
        @Header("Authorization") bearerToken: String,
        @Path("pickJobId") pickJobId: String,
        @Body pickingPostAction: PickingPostAction
    )
}