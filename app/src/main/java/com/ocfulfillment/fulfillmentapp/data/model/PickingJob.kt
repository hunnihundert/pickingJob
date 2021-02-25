package com.ocfulfillment.fulfillmentapp.data.model

import com.google.firebase.Timestamp

data class PickingJob(
    var channel: String = "",
    var facilityRef: String  = "",
    var hasScannableCodes: Boolean = false,
    var id: String = "",
    var labelStatus: String = "",
    var lastModificationDate: Timestamp = Timestamp(0L,0),
    var orderDate: Timestamp = Timestamp(0L,0),
    var orderArticleCount: Long = 0L,
    var pickedArticleCount: Long = 0L,
    var priority: Long = 0L,
    var shortId: String = "",
    var status: String = "",
    var targetTime: Timestamp = Timestamp(0L,0),
    var targetVersion: Long = 0L,
    var tenantOrderId: String = "",
    var version: Long = 0L
)