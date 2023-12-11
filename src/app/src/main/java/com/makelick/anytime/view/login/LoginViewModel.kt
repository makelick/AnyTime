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

    val isLoginMode = MutableStateFlow(true)
    val result = MutableSharedFlow<Result<Unit>>()

    val googleSignInIntent: Intent
        get() = accountRepository.getGoogleSignInIntent()

    fun changeMode() {
        isLoginMode.value = !isLoginMode.value
    }

    fun login(email: String, password: String) {
        viewModelScope.launch {
            result.emit(accountRepository.signIn(email, password))
        }
    }

    fun signUp(email: String, password: String) {
        viewModelScope.launch {
            result.emit(accountRepository.signUp(email, password))
        }
    }

    fun signInWithGoogle(account: GoogleSignInAccount) {
        viewModelScope.launch {
            result.emit(accountRepository.signInWithGoogle(account))
        }
    }
}