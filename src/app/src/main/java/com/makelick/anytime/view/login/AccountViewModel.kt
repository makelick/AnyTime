package com.makelick.anytime.view.login

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
class AccountViewModel @Inject constructor(
    private val accountRepository: AccountRepository
) : ViewModel() {

    val isLoginMode = MutableStateFlow(true)
    val result = MutableSharedFlow<Result<Unit>>()

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