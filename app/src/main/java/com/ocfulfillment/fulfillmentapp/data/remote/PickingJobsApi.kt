package com.ocfulfillment.fulfillmentapp.data.remote


import retrofit2.http.*

interface PickingJobsApi {

    @PATCH("api/pickjobs/{pickJobId}")
    suspend fun updatePickJob(@Path("pickJobId") pickJobId: String)

}