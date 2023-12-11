package com.makelick.anytime.view.splash

import androidx.lifecycle.ViewModel
import com.makelick.anytime.model.AccountRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val accountRepository: AccountRepository
) : ViewModel() {
    fun isUserLoggedIn() = accountRepository.user != null
}