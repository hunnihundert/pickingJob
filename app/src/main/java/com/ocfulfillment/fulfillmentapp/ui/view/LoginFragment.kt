package com.ocfulfillment.fulfillmentapp.ui.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputLayout
import com.ocfulfillment.fulfillmentapp.R
import com.ocfulfillment.fulfillmentapp.data.remote.RetrofitClient
import com.ocfulfillment.fulfillmentapp.databinding.FragmentLoginBinding
import com.ocfulfillment.fulfillmentapp.repository.PickingJobRepository
import com.ocfulfillment.fulfillmentapp.ui.viewmodel.MainViewModel
import com.ocfulfillment.fulfillmentapp.ui.viewmodel.MainViewModelFactory

class LoginFragment : Fragment() {

    private lateinit var binding: FragmentLoginBinding
    private val mainViewModel: MainViewModel by activityViewModels {
        MainViewModelFactory(
            PickingJobRepository(RetrofitClient.getPickingJobsApi()),
            requireActivity().application
        )
    }

    private lateinit var email: TextInputLayout
    private lateinit var password: TextInputLayout
    private lateinit var forgotPassword: TextView
    private lateinit var login: Button
    private lateinit var progressBar: ProgressBar

    override fun onStart() {
        super.onStart()
        requireActivity().findViewById<Toolbar>(R.id.toolbar).visibility = View.GONE
    }

    override fun onStop() {
        super.onStop()
        requireActivity().findViewById<Toolbar>(R.id.toolbar).visibility = View.VISIBLE
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLoginBinding.inflate(inflater, container, false)
        binding.mainViewModel = mainViewModel
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUi()
        initObserver()
    }

    private fun initUi() {
        email = binding.textInputLayoutLoginEmail
        password = binding.textInputLayoutLoginPassword
        forgotPassword = binding.textViewLoginForgotPassword
        login = binding.buttonLoginLogin
        progressBar = binding.progressBarLoginButtonLoading

        password.editText?.setOnEditorActionListener { _, actionId, _ ->
            when(actionId) {
                EditorInfo.IME_ACTION_SEND -> {
                    login.performClick()
                    true
                }
                else -> true
            }
        }

        forgotPassword.setOnClickListener {
            // navigate to forgot password
        }

        login.setOnClickListener {
            mainViewModel.login(requireActivity())
        }
    }

    private fun initObserver() {
        mainViewModel.user.observe(viewLifecycleOwner) { firebaseUser ->
            if (firebaseUser != null) {
                findNavController().navigate(LoginFragmentDirections.actionLoginFragmentToPickJobsFragment())
            }
        }

        mainViewModel.emailInputError.observe(viewLifecycleOwner) { errorString ->
            email.error = errorString
        }

        mainViewModel.passwordInputError.observe(viewLifecycleOwner) { errorString ->
            password.error = errorString
        }

        mainViewModel.progress.observe(viewLifecycleOwner) { progress ->
            when(progress) {
                MainViewModel.Progress.Idle -> {
                    email.isEnabled = true
                    password.isEnabled = true
                    forgotPassword.isClickable = true
                    login.isEnabled = true
                    login.text = getString(R.string.button_login_login)
                    progressBar.visibility = View.GONE
                }
                MainViewModel.Progress.Loading -> {
                    email.isEnabled = false
                    password.isEnabled = false
                    forgotPassword.isClickable = false
                    login.isEnabled = false
                    login.text = ""
                    progressBar.visibility = View.VISIBLE
                }
            }
        }

        mainViewModel.errorMessage.observe(viewLifecycleOwner) { errorMessageEvent ->
            errorMessageEvent.getContentIfNotHandled()?.let { errorMessage ->
                Snackbar.make(
                    requireActivity().findViewById(R.id.nav_host_fragment),
                    errorMessage,
                    Snackbar.LENGTH_SHORT
                ).show()
            }
        }


    }
}