package com.ocfulfillment.fulfillmentapp.ui.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.ocfulfillment.fulfillmentapp.repository.PickingJobRepository

class MainViewModelFactory(private val repository: PickingJobRepository, private val application: Application): ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return MainViewModel(repository, application) as T
    }
}