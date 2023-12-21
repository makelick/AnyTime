package com.makelick.anytime.view.taskinfo

import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat.getColor
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.makelick.anytime.R
import com.makelick.anytime.databinding.FragmentTaskInfoBinding
import com.makelick.anytime.model.entity.Task
import com.makelick.anytime.view.BaseFragment
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class TaskInfoFragment : BaseFragment<FragmentTaskInfoBinding>(FragmentTaskInfoBinding::inflate) {

    private val viewModel: TaskInfoViewModel by viewModels()
    private lateinit var task: Task

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        task = TaskInfoFragmentArgs.fromBundle(requireArguments()).task

        setupUI()
    }

    private fun setupUI() {
        with(binding) {
            backButton.setOnClickListener {
                findNavController().popBackStack()
            }

            editButton.setOnClickListener {
                navigateToEditTask()
            }

            taskCheckBox.isChecked = task.isCompleted
            taskCheckBox.setOnCheckedChangeListener { _, _ ->
                viewModel.changeTaskStatus(task)
            }

            taskTitle.text = task.title

            category.text = task.category

            priority.text = when (task.priority) {
                1 -> getString(R.string.low_priority)
                2 -> getString(R.string.medium_priority)
                3 -> getString(R.string.high_priority)
                else -> getString(R.string.no_priority)
            }
            priorityCard.setCardBackgroundColor( when (task.priority) {
                1 -> getColor(requireContext(), R.color.low_priority)
                2 -> getColor(requireContext(), R.color.medium_priority)
                3 -> getColor(requireContext(), R.color.high_priority)
                else -> getColor(requireContext(), R.color.no_priority)
            })

            date.text = task.date.toString()

            description.text = task.description
        }
    }

    private fun navigateToEditTask() {
        findNavController().navigate(
            TaskInfoFragmentDirections.actionTaskInfoFragmentToEditTaskFragment(
                false,
                task
            )
        )
    }

}