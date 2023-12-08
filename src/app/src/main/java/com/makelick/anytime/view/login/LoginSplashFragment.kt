package com.makelick.anytime.view.login

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.makelick.anytime.R
import com.makelick.anytime.databinding.FragmentLoginSplashBinding
import com.makelick.anytime.view.BaseFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class LoginSplashFragment :
    BaseFragment<FragmentLoginSplashBinding>(FragmentLoginSplashBinding::inflate) {

    private val viewModel: AccountViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        lifecycleScope.launch {
            viewModel.isLoginMode.collect {
                if (it) {
                    with(binding) {
                        passwordConfirmationLayout.visibility = View.GONE
                        button.text = getString(R.string.sign_in)
                        changeMode.text = getString(R.string.create_new_account)
                    }
                } else {
                    with(binding) {
                        passwordConfirmationLayout.visibility = View.VISIBLE
                        button.text = getString(R.string.sign_up)
                        changeMode.text = getString(R.string.sign_in_to_existing_account)
                    }
                }
            }
        }

        lifecycleScope.launch {
            viewModel.result.collect { result ->
                if (result.isFailure) {
                    with (binding) {
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
                                passwordLayout.error =
                                    getString(R.string.invalid_email_or_password)
                                emailLayout.error =
                                    getString(R.string.invalid_email_or_password)
                            }
                        }
                        root.clearFocus()
                    }
                } else {
                    Toast.makeText(context, "Success", Toast.LENGTH_SHORT).show()
                }
            }
        }

        binding.apply {

            email.setOnFocusChangeListener { _, hasFocus ->
                if (hasFocus) {
                    emailLayout.error = null
                }
            }

            password.setOnFocusChangeListener { _, hasFocus ->
                if (hasFocus) {
                    passwordLayout.error = null
                }
            }

            passwordConfirmation.setOnFocusChangeListener { _, hasFocus ->
                if (hasFocus) {
                    passwordConfirmationLayout.error = null
                }
            }

            changeMode.setOnClickListener {
                viewModel.isLoginMode.value = !viewModel.isLoginMode.value

                clearErrors()
                email.setText("")
                password.setText("")
                passwordConfirmation.setText("")
                root.clearFocus()
            }

            button.setOnClickListener {
                clearErrors()

                val emailStr = email.text.toString()
                val passwordStr = password.text.toString()
                if (isValidEmail(emailStr)) {
                    if (viewModel.isLoginMode.value) {
                        viewModel.login(emailStr, passwordStr)
                    } else {
                        if (passwordStr != passwordConfirmation.text.toString()) {
                            passwordConfirmationLayout.error =
                                getString(R.string.passwords_do_not_match)
                        } else {
                            viewModel.signUp(emailStr, passwordStr)
                        }
                    }
                } else {
                    emailLayout.error = getString(R.string.invalid_email_format)
                }
            }
        }

    }

    private fun isValidEmail(email: String): Boolean {
        val emailRegex = "^[A-Za-z](.*)(@)(.+)(\\.)(.+)$"
        return email.matches(emailRegex.toRegex())
    }

    private fun clearErrors() {
        with(binding) {
            emailLayout.error = null
            passwordLayout.error = null
            passwordConfirmationLayout.error = null
        }
    }
}