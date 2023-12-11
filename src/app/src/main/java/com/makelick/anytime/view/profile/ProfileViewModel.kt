package com.makelick.anytime.view.profile

import androidx.lifecycle.ViewModel
import com.makelick.anytime.model.AccountRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val accountRepository: AccountRepository
) : ViewModel() {

    fun signOut() {
        accountRepository.signOut()
    }
}