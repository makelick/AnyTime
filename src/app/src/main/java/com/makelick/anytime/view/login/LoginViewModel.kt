package com.makelick.anytime.view.login

import android.content.Intent
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.makelick.anytime.model.AccountRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val accountRepository: AccountRepository
) : ViewModel() {

    val isLoading = MutableStateFlow(false)
    val isLoginMode = MutableStateFlow(true)
    val result = MutableSharedFlow<Result<Unit>>()

    val googleSignInIntent: Intent
        get() = accountRepository.getGoogleSignInIntent()

    fun changeMode() {
        isLoginMode.value = !isLoginMode.value
    }

    fun login(email: String, password: String) {
        viewModelScope.launch {
            isLoading.value = true
            result.emit(accountRepository.signIn(email, password))
            isLoading.value = false
        }
    }

    fun signUp(email: String, password: String) {
        viewModelScope.launch {
            isLoading.value = true
            result.emit(accountRepository.signUp(email, password))
            isLoading.value = false
        }
    }

    fun signInWithGoogle(account: GoogleSignInAccount) {
        viewModelScope.launch {
            isLoading.value = true
            result.emit(accountRepository.signInWithGoogle(account))
            isLoading.value = false

        }
    }

    fun isValidEmail(email: String): Boolean {
        val emailRegex = "^[A-Za-z](.*)(@)(.+)(\\.)(.+)$"
        return email.matches(emailRegex.toRegex())
    }
}
