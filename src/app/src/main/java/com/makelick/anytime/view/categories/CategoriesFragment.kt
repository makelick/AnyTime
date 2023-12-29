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

            backButton.setOnClickListener {
                findNavController().popBackStack()
            }

            addCategoryButton.setOnClickListener {
                createCategory()
            }

            categoriesRecyclerView.apply {
                adapter = CategoriesAdapter {categoryName ->
                    lifecycleScope.launch {
                        if (!viewModel.deleteCategory(categoryName)) {
                            Toast.makeText(
                                requireContext(),
                                getString(R.string.error_try_again),
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
                layoutManager = LinearLayoutManager(requireContext())
            }
        }
    }

    private fun createCategory() {
        val dialogView = DialogCreateCategoryBinding.inflate(layoutInflater)

        MaterialAlertDialogBuilder(requireContext())
            .setTitle(getString(R.string.create_category_dialog_title))
            .setView(dialogView.root)
            .setPositiveButton(getString(R.string.create)) { _, _ ->
                val categoryName = dialogView.name.text.toString()
                if (categoryName.isNotBlank()) {
                    lifecycleScope.launch {
                        if (viewModel.addCategory(categoryName)) {
                            Toast.makeText(
                                requireContext(),
                                getString(R.string.category_created_message, categoryName),
                                Toast.LENGTH_SHORT
                            ).show()
                        } else {
                            Toast.makeText(
                                requireContext(),
                                getString(R.string.error_try_again),
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                } else {
                    Toast.makeText(
                        requireContext(),
                        getString(R.string.category_name_empty_error_message),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
            .setNegativeButton(getString(R.string.cancel)) { dialog, _ ->
                dialog.cancel()
            }
            .show()
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            viewModel.categories.collect { categories ->
                (binding.categoriesRecyclerView.adapter as CategoriesAdapter).submitList(categories)
            }
        }
    }
}