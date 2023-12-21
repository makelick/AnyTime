package com.makelick.anytime.view.categories

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
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

    override fun onStart() {
        super.onStart()
        viewModel.loadCategories()
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
                adapter = CategoriesAdapter(viewModel::deleteCategory)
                layoutManager = LinearLayoutManager(requireContext())
            }
        }
    }

    private fun createCategory() {
        Toast.makeText(requireContext(), "Create category", Toast.LENGTH_SHORT).show()
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            viewModel.categories.collect { categories ->
                (binding.categoriesRecyclerView.adapter as CategoriesAdapter).submitList(categories)
            }
        }
    }
}