package com.ocfulfillment.fulfillmentapp.ui.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ocfulfillment.fulfillmentapp.data.model.PickingJob
import com.ocfulfillment.fulfillmentapp.data.remote.RetrofitClient
import com.ocfulfillment.fulfillmentapp.databinding.FragmentPickJobsBinding
import com.ocfulfillment.fulfillmentapp.repository.PickingJobRepository
import com.ocfulfillment.fulfillmentapp.ui.adapter.PickingJobAdapter
import com.ocfulfillment.fulfillmentapp.ui.viewmodel.MainViewModel
import com.ocfulfillment.fulfillmentapp.ui.viewmodel.MainViewModelFactory

class PickJobsFragment : Fragment() {

    private val mainViewModel: MainViewModel by activityViewModels {
        MainViewModelFactory(
            PickingJobRepository(RetrofitClient.getPickingJobsApi()),
            requireActivity().application
        )
    }

    private lateinit var binding: FragmentPickJobsBinding
    private lateinit var recyclerView: RecyclerView
    private lateinit var pickingJobsAdapter: PickingJobAdapter
    private lateinit var layoutManager: LinearLayoutManager

    private val pickingJobsList = mutableListOf<PickingJob>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPickJobsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUi()
        initObserver()
    }

    private fun initUi() {
        initRecyclerView()
    }

    private fun initRecyclerView() {
        val pickingJobStatusChanger: (PickingJob) -> Unit = { pickingJob ->
            mainViewModel.switchPickingJobStatus(pickingJob)
        }
        pickingJobsAdapter = PickingJobAdapter(pickingJobsList,pickingJobStatusChanger)
        layoutManager = LinearLayoutManager(requireContext())
        recyclerView = binding.recyclerViewPickingJobs
        recyclerView.adapter = pickingJobsAdapter
        recyclerView.layoutManager = layoutManager
    }

    private fun initObserver() {
        mainViewModel.getPickingJobsLiveData().observe(viewLifecycleOwner) { pickingJobs ->
            pickingJobsList.clear()
            pickingJobsList.addAll(pickingJobs)
            pickingJobsAdapter.notifyDataSetChanged()
        }
    }
}