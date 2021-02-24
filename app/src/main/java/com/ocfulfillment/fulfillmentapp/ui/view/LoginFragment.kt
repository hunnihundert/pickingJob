package com.ocfulfillment.fulfillmentapp.ui.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.textfield.TextInputLayout
import com.ocfulfillment.fulfillmentapp.R
import com.ocfulfillment.fulfillmentapp.databinding.FragmentLoginBinding
import com.ocfulfillment.fulfillmentapp.ui.viewmodel.MainViewModel
import com.ocfulfillment.fulfillmentapp.ui.viewmodel.MainViewModelFactory

class LoginFragment : Fragment() {

    private lateinit var binding: FragmentLoginBinding
    private val mainViewModel: MainViewModel by activityViewModels {
        MainViewModelFactory(
            requireActivity().application
        )
    }

    private lateinit var email: TextInputLayout
    private lateinit var password: TextInputLayout
    private lateinit var forgotPassword: TextView
    private lateinit var login: Button

    override fun onResume() {
        super.onResume()
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

        forgotPassword.setOnClickListener {
            // navigate to forgot password
        }
        login = binding.buttonLoginLogin
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


    }
}