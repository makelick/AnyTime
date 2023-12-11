package com.makelick.anytime.view.login

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.makelick.anytime.R
import com.makelick.anytime.databinding.FragmentLoginBinding
import com.makelick.anytime.view.BaseFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class LoginFragment :
    BaseFragment<FragmentLoginBinding>(FragmentLoginBinding::inflate) {

    private val viewModel: AccountViewModel by activityViewModels()
    private lateinit var googleSignInClient: GoogleSignInClient

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

        setupGoogleSignIn()
        observeViewModel()
        setupUI()
    }

    private fun setupGoogleSignIn() {
        val options = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.google_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(requireActivity(), options)
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            viewModel.isLoginMode.collect { changeMode(it) }
        }

        lifecycleScope.launch {
            viewModel.result.collect { handleResult(it) }
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
            Toast.makeText(context, "Success", Toast.LENGTH_SHORT).show()
        }
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
                viewModel.isLoginMode.value = !viewModel.isLoginMode.value

                clearErrors()
                clearInputs()
                root.clearFocus()
            }

            button.setOnClickListener {
                clearErrors()
                attemptLogin()
            }

            googleSignInButton.setOnClickListener {
                signInWithGoogle()
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
        with(binding) {
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

    private fun isValidEmail(email: String): Boolean {
        val emailRegex = "^[A-Za-z](.*)(@)(.+)(\\.)(.+)$"
        return email.matches(emailRegex.toRegex())
    }

    private fun signInWithGoogle() {
        googleSignInLauncher.launch(googleSignInClient.signInIntent)
    }
}