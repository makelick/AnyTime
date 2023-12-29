package com.makelick.anytime.view.taskedit

import android.os.Bundle
import android.text.SpannableString
import android.text.style.UnderlineSpan
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat.getColor
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.material.datepicker.MaterialDatePicker
import com.makelick.anytime.R
import com.makelick.anytime.databinding.FragmentEditTaskBinding
import com.makelick.anytime.model.entity.Task
import com.makelick.anytime.view.BaseFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import kotlin.properties.Delegates

@AndroidEntryPoint
class EditTaskFragment : BaseFragment<FragmentEditTaskBinding>(FragmentEditTaskBinding::inflate) {

    private val viewModel: EditTaskViewModel by viewModels()
    private lateinit var task: Task
    private var isCreating by Delegates.notNull<Boolean>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        task = EditTaskFragmentArgs.fromBundle(requireArguments()).task ?: Task()
        isCreating = EditTaskFragmentArgs.fromBundle(requireArguments()).isCreating

        setupUI()
        observeViewModel()
    }

    private fun setupUI() {
        with(binding) {

            backButton.setOnClickListener {
                findNavController().popBackStack()
            }

            fragmentTitle.text =
                if (isCreating) getString(R.string.new_task)
                else getString(R.string.editing_task)

            name.setText(task.title)

            val priorityButtons =
                listOf(noPriorityCard, lowPriorityCard, mediumPriorityCard, highPriorityCard)

            for (i in priorityButtons.indices) {
                priorityButtons[i].setOnClickListener {
                    changePriority(priorityButtons, i)
                }
            }
            changePriority(priorityButtons, task.priority ?: 0)

            date.text = getUnderlinedText(task.date ?: getCurrentDate())
            date.setOnClickListener {
                val calendar = Calendar.getInstance()

                val datePicker = MaterialDatePicker.Builder.datePicker()
                    .setSelection(calendar.timeInMillis)
                    .build()

                datePicker.addOnPositiveButtonClickListener {
                    val selectedDate = Date(it)
                    date.text = getUnderlinedText(
                        SimpleDateFormat(
                            "dd.MM.yyyy",
                            Locale.getDefault()
                        ).format(selectedDate.time)
                    )
                }

                datePicker.show(childFragmentManager, datePicker.toString())
            }

            description.setText(task.description)

            editButton.text =
                if (isCreating) getString(R.string.create)
                else getString(R.string.save)

            editButton.setOnClickListener {
                val newTask = Task(
                    id = task.id,
                    isCompleted = task.isCompleted,
                    title = name.text.toString(),
                    priority = task.priority,
                    category = (spinnerCategory.selectedItem ?: null).toString(),
                    date = date.text.toString(),
                    description = description.text.toString()
                )

                if (isCreating) viewModel.addTask(newTask)
                else viewModel.updateTask(newTask)

            }

        }
    }

    private fun changePriority(views: List<CardView>, priority: Int) {
        views[priority].setCardBackgroundColor(getPriorityColor(priority))

        for (j in views.indices) {
            if (j != priority) {
                views[j].setCardBackgroundColor(getColor(requireContext(), R.color.white))
            }
        }

        task.priority = priority
    }

    private fun getPriorityColor(priority: Int?) = when (priority) {
        1 -> getColor(requireContext(), R.color.low_priority)
        2 -> getColor(requireContext(), R.color.medium_priority)
        3 -> getColor(requireContext(), R.color.high_priority)
        else -> getColor(requireContext(), R.color.no_priority)
    }

    private fun getCurrentDate() = Calendar.getInstance().let {
        SimpleDateFormat(
            "dd.MM.yyyy",
            Locale.getDefault()
        ).format(it.time)
    }

    private fun getUnderlinedText(text: String): SpannableString {
        val res = SpannableString(text)
        res.setSpan(UnderlineSpan(), 0, text.length, 0)
        return res
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            viewModel.isLoading.collect {
                with(binding) {
                    if (it) {

                        name.isEnabled = false
                        spinnerCategory.isEnabled = false
                        description.isEnabled = false
                        editButton.isEnabled = false

                        loadingBar.visibility = View.VISIBLE
                    } else {
                        name.isEnabled = true
                        spinnerCategory.isEnabled = true
                        description.isEnabled = true
                        editButton.isEnabled = true

                        loadingBar.visibility = View.GONE
                    }
                }
            }
        }

        lifecycleScope.launch {
            viewModel.categories.collect {
                val adapter = ArrayAdapter(
                    requireContext(),
                    android.R.layout.simple_spinner_dropdown_item,
                    it
                )
                binding.spinnerCategory.adapter = adapter
                if (task.category != null) {
                    val position = adapter.getPosition(task.category)
                    binding.spinnerCategory.setSelection(position)
                }
            }
        }

        lifecycleScope.launch {
            viewModel.result.collect {
                if (it) {
                    Toast.makeText(
                        requireContext(),
                        getString(R.string.task_saved),
                        Toast.LENGTH_SHORT
                    ).show()
                    findNavController().popBackStack()
                } else {
                    Toast.makeText(
                        requireContext(),
                        getString(R.string.error_try_again),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }
}

