package com.makelick.anytime.view.calendar

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.makelick.anytime.model.FirestoreRepository
import com.makelick.anytime.model.entity.Task
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class CalendarViewModel @Inject constructor(
    private val firestoreRepository: FirestoreRepository
) : ViewModel() {

    val isLoading = MutableStateFlow(true)
    val tasks = MutableStateFlow<List<Task>>(emptyList())
    val selectedDate = MutableStateFlow(
        SimpleDateFormat(
            "dd.MM.yyyy",
            Locale.getDefault()
        ).format(Calendar.getInstance().time)
    )

    fun loadTasks(date: String) {
        viewModelScope.launch {
            tasks.emit(emptyList())
            isLoading.value = true
            tasks.emit(
                firestoreRepository.getUncompletedTasksByDate(date).getOrDefault(emptyList())
            )
            isLoading.value = false
        }
    }

}