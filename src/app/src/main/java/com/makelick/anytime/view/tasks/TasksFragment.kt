package com.makelick.anytime.view.tasks

import android.os.Bundle
import android.view.View
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
        viewModel.loadTasks()
    }

    private fun setupUI() {
        with(binding) {
            addTaskButton.setOnClickListener {
                navigateToCreateTask()
            }

            tasksRecyclerView.apply {
                adapter = TasksAdapter(viewModel::changeTaskStatus, ::navigateToTaskInfo)
                layoutManager = LinearLayoutManager(requireContext())
            }
        }
    }

    private fun navigateToTaskInfo(task: Task) {
        val action = TasksFragmentDirections.actionTasksFragmentToTaskInfoFragment(task)
        findNavController().navigate(action)
    }

    private fun navigateToCreateTask() {
        findNavController().navigate(R.id.action_tasksFragment_to_editTaskFragment)
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

}