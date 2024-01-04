package com.makelick.anytime.view.taskinfo

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.makelick.anytime.model.FirestoreRepository
import com.makelick.anytime.model.entity.Task
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TaskInfoViewModel @Inject constructor(
    private val firestoreRepository: FirestoreRepository
) : ViewModel() {

    fun changeTaskStatus(task: Task) {
        viewModelScope.launch {
            task.isCompleted = !task.isCompleted
            firestoreRepository.updateTask(task)
        }
    }

    fun deleteTask(task: Task) {
        viewModelScope.launch {
            firestoreRepository.deleteTask(task.id.toString())
        }
    }
}
