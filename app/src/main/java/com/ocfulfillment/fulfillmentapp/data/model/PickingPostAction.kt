package com.ocfulfillment.fulfillmentapp.data.model

data class PickingPostAction(
    val version: Long,
    val actions: List<ModificationAction>
) {
    data class ModificationAction(
        val action: String,
        val status: String
    )
}