package com.makelick.anytime.view.tasks

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.makelick.anytime.model.FirestoreRepository
import com.makelick.anytime.model.entity.Task
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TasksViewModel @Inject constructor(
    private val firestoreRepository: FirestoreRepository
) : ViewModel() {

    val isLoading = MutableStateFlow(true)
    val tasks = MutableStateFlow<List<Task>>(emptyList())

    fun loadTasks() {
        viewModelScope.launch {
            tasks.emit(emptyList())
            isLoading.value = true
            val result = firestoreRepository.getAllTasks()
            tasks.emit(result.getOrNull()?.sortedBy { it.isCompleted } ?: emptyList())
            isLoading.value = false
        }
    }

    fun changeTaskStatus(task: Task) {
        viewModelScope.launch {
            task.isCompleted = !task.isCompleted
            firestoreRepository.updateTask(task)
            tasks.emit(tasks.value.sortedBy { it.isCompleted })
        }
    }
}