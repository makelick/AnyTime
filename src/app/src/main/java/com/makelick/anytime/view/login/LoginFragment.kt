package com.makelick.anytime.view.login

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.makelick.anytime.R
import com.makelick.anytime.databinding.FragmentLoginBinding
import com.makelick.anytime.view.BaseFragment
import com.makelick.anytime.view.MainActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class LoginFragment :
    BaseFragment<FragmentLoginBinding>(FragmentLoginBinding::inflate) {

    private val viewModel: LoginViewModel by viewModels()

    private val googleSignInLauncher: ActivityResultLauncher<Intent> =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            try {
                val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
                val account = task.getResult(ApiException::class.java)
                viewModel.signInWithGoogle(account)
            } catch (e: ApiException) {
                Toast.makeText(
                    requireContext(),
                    getString(R.string.google_sign_in_failed),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeViewModel()
        setupUI()
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            viewModel.isLoginMode.collect { changeMode(it) }
        }

        lifecycleScope.launch {
            viewModel.result.collect { handleResult(it) }
        }

        lifecycleScope.launch {
            viewModel.isLoading.collect {
                with(binding) {
                    button.isEnabled = !it
                    googleSignInButton.isEnabled = !it
                    changeMode.isEnabled = !it
                    binding.loadingBar.visibility = if (it) View.VISIBLE else View.GONE
                }
            }
        }
    }

    private fun changeMode(isSignIn: Boolean) {
        with(binding) {
            if (isSignIn) {
                passwordConfirmationLayout.visibility = View.GONE
                button.text = getString(R.string.sign_in)
                changeMode.text = getString(R.string.create_new_account)
            } else {
                passwordConfirmationLayout.visibility = View.VISIBLE
                button.text = getString(R.string.sign_up)
                changeMode.text = getString(R.string.sign_in_to_existing_account)
            }
        }
    }

    private fun handleResult(result: Result<Unit>) {
        if (result.isFailure) {
            with(binding) {
                when (result.exceptionOrNull()) {
                    is FirebaseAuthInvalidUserException -> {
                        emailLayout.error = getString(R.string.invalid_email)
                    }

                    is FirebaseAuthUserCollisionException -> {
                        emailLayout.error = getString(R.string.email_already_in_use)
                    }

                    is FirebaseAuthWeakPasswordException -> {
                        passwordLayout.error = getString(R.string.weak_password)
                    }

                    is FirebaseAuthInvalidCredentialsException -> {
                        emailLayout.error = getString(R.string.invalid_email)
                    }

                    else -> {
                        passwordLayout.error = getString(R.string.invalid_email_or_password)
                        emailLayout.error = getString(R.string.invalid_email_or_password)
                    }
                }
                root.clearFocus()
            }
        } else {
            navigateToTasksFragment()
        }
    }

    private fun navigateToTasksFragment() {
        findNavController().navigate(LoginFragmentDirections.actionLoginFragmentToTasksFragment())
        (activity as MainActivity).changeBottomNavSelectedId(R.id.tasks)
    }

    private fun setupUI() {
        binding.apply {

            email.setOnFocusChangeListener { _, hasFocus ->
                if (hasFocus) emailLayout.error = null
            }

            password.setOnFocusChangeListener { _, hasFocus ->
                if (hasFocus) passwordLayout.error = null
            }

            passwordConfirmation.setOnFocusChangeListener { _, hasFocus ->
                if (hasFocus) passwordConfirmationLayout.error = null
            }

            changeMode.setOnClickListener {
                viewModel.changeMode()

                clearErrors()
                clearInputs()
                root.clearFocus()
            }

            button.setOnClickListener {
                clearErrors()
                root.clearFocus()
                attemptLogin()
            }

            googleSignInButton.setOnClickListener {
                googleSignInLauncher.launch(viewModel.googleSignInIntent)
                root.clearFocus()
            }
        }
    }

    private fun clearErrors() {
        with(binding) {
            emailLayout.error = null
            passwordLayout.error = null
            passwordConfirmationLayout.error = null
        }
    }

    private fun clearInputs() {
        with(binding) {
            email.setText("")
            password.setText("")
            passwordConfirmation.setText("")
        }
    }

    private fun attemptLogin() {
        val emailStr = binding.email.text.toString()
        val passwordStr = binding.password.text.toString()

        if (!viewModel.isValidEmail(emailStr)) {
            binding.emailLayout.error = getString(R.string.invalid_email_format)
            return
        }

        if (viewModel.isLoginMode.value) {
            viewModel.login(emailStr, passwordStr)
            return
        }

        if (passwordStr != binding.passwordConfirmation.text.toString()) {
            binding.passwordConfirmationLayout.error = getString(R.string.passwords_do_not_match)
            return
        }

        viewModel.signUp(emailStr, passwordStr)
    }
}
