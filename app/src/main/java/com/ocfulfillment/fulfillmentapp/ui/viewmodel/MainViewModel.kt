package com.ocfulfillment.fulfillmentapp.ui.viewmodel

import android.app.Activity
import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.ocfulfillment.fulfillmentapp.R
import com.ocfulfillment.fulfillmentapp.data.model.PickingJob
import com.ocfulfillment.fulfillmentapp.repository.PickingJobRepository
import kotlinx.coroutines.launch

class MainViewModel(private val repository: PickingJobRepository, application: Application) :
    AndroidViewModel(application) {

    private var auth: FirebaseAuth = Firebase.auth
    private var token: String? = null

    val user: LiveData<FirebaseUser?>
        get() = _user
    private var _user = MutableLiveData<FirebaseUser?>(null)

    val email = MutableLiveData<String>()
    val password = MutableLiveData<String>()
    val emailInputError = MutableLiveData("")
    val passwordInputError = MutableLiveData("")

    internal fun login(activity: Activity) {
        resetErrorTexts()
        if (isInputValid()) {
            val email = email.value!!
            val password = password.value!!
            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(activity) { task ->
                    if (task.isSuccessful) {
                        _user.value = auth.currentUser
                        setToken()
                    } else {
                        setInvalidEmailOrPassWord()
                    }
                }
        }
    }

    private fun resetErrorTexts() {
        emailInputError.value = ""
        passwordInputError.value = ""
    }

    private fun isInputValid(): Boolean {
        var inputIsValid = true
        if (email.value.isNullOrBlank()) {
            emailInputError.value =
                getApplication<Application>().resources.getString(R.string.textInputLayout_login_emailEmpty)
            inputIsValid = false
        }

        if (password.value.isNullOrBlank()) {
            passwordInputError.value =
                getApplication<Application>().resources.getString(R.string.textInputLayout_login_passwordEmpty)
            inputIsValid = false
        }

        return inputIsValid
    }

    private fun setInvalidEmailOrPassWord() {
        emailInputError.value =
            getApplication<Application>().resources.getString(R.string.textInputLayout_login_invalidLoginCredentials)
        passwordInputError.value =
            getApplication<Application>().resources.getString(R.string.textInputLayout_login_invalidLoginCredentials)
    }

    private fun setToken() {
        auth.currentUser?.let { firebaseUser ->
            firebaseUser.getIdToken(true)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        task.result?.let { tokenResult ->
                            token = tokenResult.token
                        }
                    }
                }
        }
    }

    internal fun switchPickingJobStatus(pickingJob: PickingJob) {
        if (pickingJob.status == PickingJob.PICKING_JOB_STATUS_OPEN) {
            pickingJob.status = PickingJob.PICKING_JOB_STATUS_CLOSED
        } else {
            pickingJob.status = PickingJob.PICKING_JOB_STATUS_OPEN
        }
        updatePickingJob(pickingJob)
    }

    private fun updatePickingJob(pickingJob: PickingJob) {
        val pickingJobId = pickingJob.id
        val pickingJobStatus = pickingJob.status
        viewModelScope.launch {
            if (token != null) {
                repository.updatePickingJob(
                    token!!,
                    pickingJob.version,
                    pickingJobId,
                    pickingJobStatus
                )
            } else {
                // access token null
            }

        }
    }

    internal fun getPickingJobsLiveData(): LiveData<List<PickingJob>> {
        val pickingJobs = MutableLiveData<List<PickingJob>>()
        repository.getPickingJobs().addSnapshotListener { snapshot, error ->
            if(error != null) {
                // error
                return@addSnapshotListener
            }
            val pickingJobsLive = mutableListOf<PickingJob>()
            if (snapshot != null) {
                for (document in snapshot) {
                    pickingJobsLive.add(document.toObject(PickingJob::class.java))
                }
                pickingJobs.value = pickingJobsLive
            }
        }
        return pickingJobs
    }

    companion object {
        private const val TAG = "MainViewModel"
    }
}