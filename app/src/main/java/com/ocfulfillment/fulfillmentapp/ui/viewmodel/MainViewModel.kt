package com.ocfulfillment.fulfillmentapp.ui.viewmodel

import android.app.Activity
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.ktx.Firebase
import com.ocfulfillment.fulfillmentapp.R
import com.ocfulfillment.fulfillmentapp.data.model.PickingJob
import com.ocfulfillment.fulfillmentapp.repository.PickingJobRepository
import com.ocfulfillment.fulfillmentapp.util.Event
import kotlinx.coroutines.launch
import retrofit2.HttpException

class MainViewModel(private val repository: PickingJobRepository, application: Application) :
    AndroidViewModel(application) {

    private var auth: FirebaseAuth = Firebase.auth
    private var token: String? = null

    private var _user = MutableLiveData<FirebaseUser?>(null)
    val user: LiveData<FirebaseUser?> = _user

    private var _errorMessage = MutableLiveData<Event<String>>()
    val errorMessage: LiveData<Event<String>> = _errorMessage

    val email = MutableLiveData<String>()
    val password = MutableLiveData<String>()
    val emailInputError = MutableLiveData("")
    val passwordInputError = MutableLiveData("")

    private lateinit var snapShotListener: ListenerRegistration

    sealed class Progress {
        object Loading : Progress()
        object Idle : Progress()
    }

    private var _progress = MutableLiveData<Progress>(Progress.Idle)
    val progress: LiveData<Progress> = _progress

    internal fun login(activity: Activity) {
        _progress.value = Progress.Loading
        resetErrorTexts()
        if (isInputValid()) {
            val email = email.value!!
            val password = password.value!!
            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(activity) { task ->
                    if (task.isSuccessful) {
                        _user.value = auth.currentUser
                        setToken()
                        this.email.value = ""
                        this.password.value = ""
                    } else {
                        setInvalidEmailOrPassWord()
                    }
                    _progress.value = Progress.Idle
                }
        } else {
            _progress.value = Progress.Idle
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
        _errorMessage.value =
            Event(getApplication<Application>().resources.getString(R.string.textInputLayout_login_invalidLoginCredentials))
        emailInputError.value =
            getApplication<Application>().resources.getString(R.string.textInputLayout_login_invalidEmail)
        passwordInputError.value =
            getApplication<Application>().resources.getString(R.string.textInputLayout_login_invalidPassword)
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
            try {
                if (token != null) {
                    repository.updatePickingJob(
                        token!!,
                        pickingJob.version,
                        pickingJobId,
                        pickingJobStatus
                    )
                }
            } catch (throwable: Throwable) {
                var errorMessage =
                    getApplication<Application>().resources.getString(R.string.errorMessage_unknownError)
                when (throwable) {
                    is HttpException -> {
                        val errorCode = throwable.code()
                        throwable.localizedMessage?.let {
                            errorMessage = it
                        }
                        _errorMessage.value = Event("$errorCode: $errorMessage")
                    }
                    else -> {
                        throwable.localizedMessage?.let {
                            errorMessage = it
                        }
                        _errorMessage.value = Event(errorMessage)

                    }
                }
            }

        }
    }

    internal fun getPickingJobsLiveData(): LiveData<List<PickingJob>> {
        _progress.value = Progress.Loading
        val pickingJobs = MutableLiveData<List<PickingJob>>()
        try {
            snapShotListener = repository.getPickingJobs().addSnapshotListener { snapshot, error ->
                if (error != null) {
                    val errorCode = error.code
                    var errorMessage =
                        getApplication<Application>().resources.getString(R.string.errorMessage_unknownError)
                    error.localizedMessage?.let {
                        errorMessage = it
                    }
                    _errorMessage.value = Event("$errorCode: $errorMessage")
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
        } catch (throwable: Throwable) {
            var errorMessage =
                getApplication<Application>().resources.getString(R.string.errorMessage_unknownError)
            when (throwable) {
                is HttpException -> {
                    val errorCode = throwable.code()
                    throwable.localizedMessage?.let {
                        errorMessage = it
                    }
                    _errorMessage.value = Event("$errorCode: $errorMessage")
                }
                else -> {
                    throwable.localizedMessage?.let {
                        errorMessage = it
                    }
                    _errorMessage.value = Event(errorMessage)

                }
            }
        }
        _progress.value = Progress.Idle
        return pickingJobs
    }

    internal fun signOut() {
        snapShotListener.remove()
        _user.value = null
        auth.signOut()
    }

    companion object {
        private const val TAG = "MainViewModel"
    }
}