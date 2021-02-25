package com.ocfulfillment.fulfillmentapp.repository

import android.util.Log
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.ocfulfillment.fulfillmentapp.data.model.PickingJob
import com.ocfulfillment.fulfillmentapp.data.remote.PickingJobsApi
import kotlinx.coroutines.tasks.await

class PickingJobRepository(private val pickingJobsApi: PickingJobsApi) {

    internal suspend fun updatePickingJob(pickingJobId: String) {
        pickingJobsApi.updatePickJob(pickingJobId)
    }

    internal suspend fun getPickingJobs(): List<PickingJob> {
        val db = Firebase.firestore
        val pickingJobs = mutableListOf<PickingJob>()
        db.collection("mobile_android-picking-v1-pickjobs")
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    val pickingJob = document.toObject(PickingJob::class.java)
                    pickingJobs.add(pickingJob)
                }
            }.await()
        return pickingJobs
    }

    companion object {
        private const val TAG = "PickingJobRepository"
    }
}