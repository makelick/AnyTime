package com.makelick.anytime.view.categories

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.makelick.anytime.model.FirestoreRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class CategoriesViewModel @Inject constructor(
    private val firestoreRepository: FirestoreRepository
) : ViewModel() {

    val categories = MutableStateFlow<List<String>>(emptyList())

    fun loadCategories() {
        viewModelScope.launch {
            categories.emit(firestoreRepository.getCategories().getOrDefault(emptyList()))
        }
    }

    fun deleteCategory(category: String) {
        viewModelScope.launch {
            val newCategories = categories.value.toMutableList()
            newCategories.remove(category)
            firestoreRepository.updateCategories(newCategories)
            categories.emit(newCategories)
        }
    }
}