package com.makelick.anytime.view.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
            val loginResult = accountRepository.signIn(email, password)
            result.emit(loginResult)
        }
    }

    fun signUp(email: String, password: String) {
        viewModelScope.launch {
            val signUpResult = accountRepository.signUp(email, password)
            result.emit(signUpResult)
        }
    }
}