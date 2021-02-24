package com.ocfulfillment.fulfillmentapp.ui.viewmodel

import android.app.Activity
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.ocfulfillment.fulfillmentapp.R

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private var auth: FirebaseAuth = Firebase.auth

    init {
        auth.signOut()
    }

    val user: LiveData<FirebaseUser?>
        get() = _user
    private var _user = MutableLiveData<FirebaseUser?>(null)

    val email = MutableLiveData<String>()
    val password = MutableLiveData<String>()
    val emailInputError = MutableLiveData("")
    val passwordInputError = MutableLiveData("")


    internal fun login(activity: Activity) {
        resetErrorTexts()
        if(isInputValid()) {
            val email = email.value!!
            val password = password.value!!
            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(activity) { task ->
                    if(task.isSuccessful) {
                        _user.value = auth.currentUser
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
        if(email.value.isNullOrBlank()) {
            emailInputError.value = getApplication<Application>().resources.getString(R.string.textInputLayout_login_emailEmpty)
            inputIsValid = false
        }

        if(password.value.isNullOrBlank()) {
            passwordInputError.value = getApplication<Application>().resources.getString(R.string.textInputLayout_login_passwordEmpty)
            inputIsValid = false
        }

        return inputIsValid
    }

    private fun setInvalidEmailOrPassWord() {
        emailInputError.value = getApplication<Application>().resources.getString(R.string.textInputLayout_login_invalidLoginCredentials)
        passwordInputError.value = getApplication<Application>().resources.getString(R.string.textInputLayout_login_invalidLoginCredentials)
    }
}