package com.ocfulfillment.fulfillmentapp.ui.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ProgressBar
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.ocfulfillment.fulfillmentapp.R
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
    private lateinit var progressBar: ProgressBar

    private val pickingJobsList = mutableListOf<PickingJob>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        setHasOptionsMenu(true)
        binding = FragmentPickJobsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUi()
        initObserver()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId) {
            R.id.logout -> {
                mainViewModel.signOut()
                true
            }
            else -> true
        }
    }

    private fun initUi() {
        initRecyclerView()
        progressBar = binding.progressBarListItemStatusLoading
    }

    private fun initRecyclerView() {
        val pickingJobStatusChanger: (PickingJob, View, View) -> Unit = { pickingJob, button, progressBar ->
            mainViewModel.switchPickingJobStatus(pickingJob)
            button.isClickable = false
            (button as Button).text = ""
            progressBar.visibility = View.VISIBLE
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
        mainViewModel.errorMessage.observe(viewLifecycleOwner) { errorMessageEvent ->
            errorMessageEvent.getContentIfNotHandled()?.let { errorMessage ->
                Snackbar.make(requireActivity().findViewById(R.id.nav_host_fragment),errorMessage,Snackbar.LENGTH_SHORT).show()
            }
        }

        mainViewModel.progress.observe(viewLifecycleOwner) { progress ->
            when(progress) {
                MainViewModel.Progress.Idle -> {
                    progressBar.visibility = View.GONE
                }
                MainViewModel.Progress.Loading -> {
                    progressBar.visibility = View.VISIBLE
                }
        mainViewModel.user.observe(viewLifecycleOwner) { firebaseUser ->
            if(firebaseUser == null) {
                findNavController().navigate(PickJobsFragmentDirections.actionPickJobsFragmentToLoginFragment())
            }
        }
    }
}