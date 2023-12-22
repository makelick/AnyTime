package com.makelick.anytime.view.tasks

import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.makelick.anytime.R
import com.makelick.anytime.databinding.FragmentTasksBinding
import com.makelick.anytime.model.entity.Task
import com.makelick.anytime.view.BaseFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class TasksFragment : BaseFragment<FragmentTasksBinding>(FragmentTasksBinding::inflate) {

    private val viewModel: TasksViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupUI()
        observeViewModel()
    }

    override fun onStart() {
        super.onStart()
        loadTasks()
    }

    private fun setupUI() {
        with(binding) {

            lifecycleScope.launch {
                spinnerCategory.adapter = ArrayAdapter(
                    requireContext(),
                    android.R.layout.simple_spinner_dropdown_item,
                    viewModel.loadCategories()
                )
            }

            spinnerPriority.onItemSelectedListener = SpinnerOnItemSelectedListener()
            spinnerCategory.onItemSelectedListener = SpinnerOnItemSelectedListener()

            tasksRecyclerView.apply {
                adapter = TasksAdapter(viewModel::changeTaskStatus, ::navigateToTaskInfo)
                layoutManager = LinearLayoutManager(requireContext())
            }

            addTaskButton.setOnClickListener {
                navigateToCreateTask()
            }
        }
    }

    private fun loadTasks() {
        try {
            viewModel.loadTasks(
                binding.spinnerPriority.selectedItem.toString().convertPriorityToInt(),
                binding.spinnerCategory.selectedItem.toString()
            )
        } catch (e: Exception) {
            viewModel.loadTasks(-1, "All categories")
        }
    }

    private fun String.convertPriorityToInt(): Int {
        return when (this) {
            getString(R.string.high_priority) -> 3
            getString(R.string.medium_priority) -> 2
            getString(R.string.low_priority) -> 1
            getString(R.string.no_priority) -> 0
            else -> -1
        }
    }

    private fun navigateToTaskInfo(task: Task) {
        val action = TasksFragmentDirections.actionTasksFragmentToTaskInfoFragment(task)
        findNavController().navigate(action)
    }

    private fun navigateToCreateTask() {
        val action = TasksFragmentDirections.actionTasksFragmentToEditTaskFragment(true, null)
        findNavController().navigate(action)
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            viewModel.isLoading.collect {
                binding.tasksLoadingBar.visibility = if (it) View.VISIBLE else View.GONE
            }
        }

        lifecycleScope.launch {
            viewModel.tasks.collect {
                (binding.tasksRecyclerView.adapter as TasksAdapter).submitList(it)
            }
        }
    }

    inner class SpinnerOnItemSelectedListener : AdapterView.OnItemSelectedListener {
        override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
            loadTasks()
        }

        override fun onNothingSelected(p0: AdapterView<*>?) {
            loadTasks()
        }
    }

}