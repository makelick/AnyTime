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

    fun loadTasks(priority: Int, category: String) {
        viewModelScope.launch {
            tasks.emit(emptyList())
            isLoading.value = true
            var result = firestoreRepository.getAllTasks().getOrDefault(emptyList())
            if (priority != -1) {
                result = result.filter { it.priority == priority }
            }
            if (category != "All categories") {
                result = result.filter { it.category == category }
            }

            tasks.emit(result.sortedBy { it.isCompleted })
            isLoading.value = false
        }
    }

    suspend fun loadCategories(): List<String> {
        val result = mutableListOf("All categories")
        result.addAll(firestoreRepository.getCategories().getOrDefault(emptyList()))
        return result
    }

    fun changeTaskStatus(task: Task) {
        viewModelScope.launch {
            task.isCompleted = !task.isCompleted
            firestoreRepository.updateTask(task)
            tasks.emit(tasks.value.sortedBy { it.isCompleted })
        }
    }
}
