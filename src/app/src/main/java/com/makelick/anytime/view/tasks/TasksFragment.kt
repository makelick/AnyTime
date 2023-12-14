package com.makelick.anytime.view.tasks

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import com.makelick.anytime.R
import com.makelick.anytime.databinding.FragmentTasksBinding
import com.makelick.anytime.view.BaseFragment
import com.makelick.anytime.view.MainActivity

class TasksFragment : BaseFragment<FragmentTasksBinding>(FragmentTasksBinding::inflate) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupUI()
    }

    private fun setupUI() {
        with(binding) {
            addTaskButton.setOnClickListener {
                navigateToCreateTask()
            }
        }
    }

    private fun navigateToCreateTask() {
        findNavController().navigate(R.id.action_tasksFragment_to_editTaskFragment)
        (activity as MainActivity).disableBottomNav()
    }

}