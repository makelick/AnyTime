package com.makelick.anytime.view.calendar

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.makelick.anytime.databinding.FragmentCalendarBinding
import com.makelick.anytime.model.entity.Task
import com.makelick.anytime.view.BaseFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@AndroidEntryPoint
class CalendarFragment : BaseFragment<FragmentCalendarBinding>(FragmentCalendarBinding::inflate) {

    private val viewModel: CalendarViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupUI()
        observeViewModel()
    }

    override fun onStart() {
        super.onStart()
        viewModel.loadTasks(
            SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
                .format(binding.calendarView.date)
        )
    }

    private fun setupUI() {
        with(binding) {
            tasksRecyclerView.apply {
                adapter = CalendarAdapter(::navigateToTaskInfo)
                layoutManager = LinearLayoutManager(requireContext())
            }

            calendarView.setOnDateChangeListener { _, year, month, dayOfMonth ->

                val calendar = Calendar.getInstance()
                calendar.set(year, month, dayOfMonth)

                val stringDate = SimpleDateFormat(
                    "dd.MM.yyyy",
                    Locale.getDefault()
                ).format(calendar.time)
                viewModel.selectedDate.value = stringDate
                viewModel.loadTasks(stringDate)
            }
        }
    }

    private fun navigateToTaskInfo(task: Task) {
        val action = CalendarFragmentDirections.actionCalendarFragmentToTaskInfoFragment(task)
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
                (binding.tasksRecyclerView.adapter as CalendarAdapter).submitList(it)
            }
        }

        lifecycleScope.launch {
            viewModel.selectedDate.collect {
                val date = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault()).parse(it)
                if (date != null) {
                    binding.calendarView.date = date.time
                }
            }
        }
    }

}