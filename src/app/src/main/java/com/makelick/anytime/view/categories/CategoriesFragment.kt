package com.makelick.anytime.view.categories

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.makelick.anytime.R
import com.makelick.anytime.databinding.DialogCreateCategoryBinding
import com.makelick.anytime.databinding.FragmentCategoriesBinding
import com.makelick.anytime.view.BaseFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class CategoriesFragment :
    BaseFragment<FragmentCategoriesBinding>(FragmentCategoriesBinding::inflate) {

    private val viewModel: CategoriesViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUI()
        observeViewModel()
    }

    private fun setupUI() {
        with(binding) {
            backButton.setOnClickListener { findNavController().popBackStack() }
            addCategoryButton.setOnClickListener { createCategory() }
            categoriesRecyclerView.apply {
                adapter = CategoriesAdapter { categoryName -> handleCategoryClick(categoryName) }
                layoutManager = LinearLayoutManager(requireContext())
            }
        }
    }

    private fun handleCategoryClick(categoryName: String) {
        lifecycleScope.launch {
            val message = when {
                viewModel.categories.value.size == 1 -> R.string.category_last_message
                viewModel.deleteCategory(categoryName) -> null
                else -> R.string.error_try_again
            }
            message?.let { showToast(it) }
        }
    }

    private fun createCategory() {
        val dialogView = DialogCreateCategoryBinding.inflate(layoutInflater)
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.create_category_dialog_title)
            .setView(dialogView.root)
            .setPositiveButton(R.string.create) { _, _ -> handleCreate(dialogView.name.text.toString()) }
            .setNegativeButton(R.string.cancel) { dialog, _ -> dialog.cancel() }
            .show()
    }

    private fun handleCreate(categoryName: String) {
        if (categoryName.isNotBlank()) {
            lifecycleScope.launch {
                val message = if (viewModel.addCategory(categoryName)) {
                    R.string.category_created_message
                } else {
                    R.string.error_try_again
                }
                showToast(message, categoryName)
            }
        } else {
            showToast(R.string.category_name_empty_error_message)
        }
    }

    private fun showToast(message: Int, categoryName: String = "") {
        Toast.makeText(requireContext(), getString(message, categoryName), Toast.LENGTH_SHORT).show()
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            viewModel.categories.collect { categories ->
                (binding.categoriesRecyclerView.adapter as CategoriesAdapter).submitList(categories)
            }
        }
    }
}