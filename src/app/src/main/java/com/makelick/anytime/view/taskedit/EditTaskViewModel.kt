package com.makelick.anytime.view.taskedit

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.makelick.anytime.model.FirestoreRepository
import com.makelick.anytime.model.entity.Task
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EditTaskViewModel @Inject
constructor(
    private val firestoreRepository: FirestoreRepository
) : ViewModel() {

    val isLoading = MutableStateFlow(false)
    val result = MutableSharedFlow<Boolean>()
    val categories = firestoreRepository.categories

    fun addTask(task: Task) {
        viewModelScope.launch {
            isLoading.value = true
            result.emit(firestoreRepository.addTask(task).isSuccess)
            isLoading.value = false
        }
    }


    fun updateTask(task: Task) {
        viewModelScope.launch {
            isLoading.value = true
            result.emit(firestoreRepository.updateTask(task).isSuccess)
            isLoading.value = false
        }
    }
}
