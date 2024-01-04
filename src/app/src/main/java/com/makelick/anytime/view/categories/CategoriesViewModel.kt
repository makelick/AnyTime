package com.makelick.anytime.view.categories

import androidx.lifecycle.ViewModel
import com.makelick.anytime.model.FirestoreRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject


@HiltViewModel
class CategoriesViewModel @Inject constructor(
    private val firestoreRepository: FirestoreRepository
) : ViewModel() {

    val categories = firestoreRepository.categories

    suspend fun deleteCategory(category: String): Boolean {
        val newCategories = categories.value.toMutableList()
        newCategories.remove(category)
        return firestoreRepository.updateCategories(newCategories).isSuccess
    }

    suspend fun addCategory(category: String): Boolean {
        val newCategories = categories.value.toMutableList()
        newCategories.add(category)
        return firestoreRepository.updateCategories(newCategories).isSuccess
    }
}
