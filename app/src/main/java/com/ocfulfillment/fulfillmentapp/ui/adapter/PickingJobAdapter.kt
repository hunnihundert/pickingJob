package com.ocfulfillment.fulfillmentapp.ui.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ocfulfillment.fulfillmentapp.data.model.PickingJob

class PickingJobAdapter(private val pickingJobs: List<PickingJob>, private val pickingJobStatusChanger: (PickingJob) -> (Unit)): RecyclerView.Adapter<PickingJobViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PickingJobViewHolder {
        return PickingJobViewHolder.create(parent)
    }

    override fun onBindViewHolder(holder: PickingJobViewHolder, position: Int) {
        val item = pickingJobs[position]
        holder.bind(item, pickingJobStatusChanger)
    }

    override fun getItemCount(): Int {
        return pickingJobs.size
    }
}