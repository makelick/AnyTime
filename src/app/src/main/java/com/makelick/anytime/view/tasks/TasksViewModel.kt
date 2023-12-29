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
    val selectedPriority = MutableStateFlow(-1)
    val selectedCategory = MutableStateFlow("All categories")

    val tasks = MutableStateFlow<List<Task>>(emptyList())
    val categories = MutableStateFlow<List<String>>(emptyList())

    init {
        viewModelScope.launch {
            firestoreRepository.allTasks.collect {
                loadTasks()
            }
        }

        viewModelScope.launch {
            firestoreRepository.categories.collect {
                categories.value = loadCategories()
            }
        }
    }

    fun loadTasks() {
        viewModelScope.launch {
            tasks.emit(filterTasks(firestoreRepository.allTasks.value))
            isLoading.value = false
        }
    }

    private fun filterTasks(tasks: List<Task>): List<Task> {
        return tasks.filter { task ->
            (selectedPriority.value == -1 || task.priority == selectedPriority.value) &&
                    (selectedCategory.value == "All categories" || task.category == selectedCategory.value)

        }.sortedBy { it.isCompleted }
    }

    private fun loadCategories(): List<String> {
        val result = mutableListOf("All categories")
        result.addAll(firestoreRepository.categories.value)
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
