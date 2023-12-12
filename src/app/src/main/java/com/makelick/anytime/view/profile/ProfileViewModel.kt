package com.makelick.anytime.view.profile

import android.net.Uri
import androidx.lifecycle.ViewModel
import com.makelick.anytime.model.AccountRepository
import com.makelick.anytime.model.StorageRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val accountRepository: AccountRepository,
    private val storageRepository: StorageRepository
) : ViewModel() {

    val user = accountRepository.getUser()
    val isEditMode = MutableStateFlow(false)

    var loadedImageUri: Uri? = null

    fun signOut() {
        accountRepository.signOut()
    }

    fun applyProfileChanges(username: String) {
        accountRepository.updateProfile(username, loadedImageUri ?: user?.photoUrl)
        isEditMode.value = false
    }

    suspend fun loadNewImage(file: Uri?) {
        if (user != null && file != null) {
            val result = storageRepository.uploadImage(user.uid, file)
            loadedImageUri = result.getOrNull().takeIf { result.isSuccess }
        }
    }
}