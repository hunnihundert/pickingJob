package com.ocfulfillment.fulfillmentapp.repository

import android.util.Log
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.ocfulfillment.fulfillmentapp.data.model.PickingPostAction
import com.ocfulfillment.fulfillmentapp.data.remote.PickingJobsApi
import com.ocfulfillment.fulfillmentapp.util.ACTION_MODIFY_PICKJOB

class PickingJobRepository(private val pickingJobsApi: PickingJobsApi) {

    private val db = Firebase.firestore

    internal suspend fun updatePickingJob(accessToken: String, version: Long, pickingJobId: String, pickingJobStatus: String) {
        val bearerToken = "Bearer $accessToken"
        val action = PickingPostAction.ModificationAction(ACTION_MODIFY_PICKJOB, pickingJobStatus)
        val actions = listOf(action)
        val pickingPostAction = PickingPostAction(version, actions)
        pickingJobsApi.updatePickJob(bearerToken, pickingJobId, pickingPostAction)
    }

    internal fun getPickingJobsDatabaseReference(): CollectionReference {
        return db.collection("mobile_android-picking-v1-pickjobs")
    }


    companion object {
        private const val TAG = "PickingJobRepository"
    }

}