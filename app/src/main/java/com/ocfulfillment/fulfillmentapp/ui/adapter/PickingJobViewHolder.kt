package com.ocfulfillment.fulfillmentapp.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ocfulfillment.fulfillmentapp.data.model.PickingJob
import com.ocfulfillment.fulfillmentapp.databinding.ListItemPickJobBinding

class PickingJobViewHolder(private val binding: ListItemPickJobBinding): RecyclerView.ViewHolder(binding.root) {

    fun bind(pickingJob: PickingJob, pickingJobStatusChanger: (PickingJob) -> Unit) {
        val pickingJobId = binding.textViewPickJobsId
        val pickingJobStatus = binding.buttonPickJobsStatus
        pickingJobId.text = pickingJob.id
        pickingJobStatus.text = pickingJob.status
        pickingJobStatus.setOnClickListener { pickingJobStatusChanger(pickingJob) }

    }

    companion object {
        fun create(parent: ViewGroup): PickingJobViewHolder {
            val inflater = LayoutInflater.from(parent.context)
            val binding = ListItemPickJobBinding.inflate(inflater, parent,false)
            return PickingJobViewHolder(binding)
        }
    }
}