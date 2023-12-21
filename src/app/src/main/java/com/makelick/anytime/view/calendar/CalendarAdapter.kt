package com.makelick.anytime.view.calendar

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.makelick.anytime.R
import com.makelick.anytime.databinding.ItemCalendarlistBinding
import com.makelick.anytime.model.entity.Task
import com.makelick.anytime.view.tasks.TasksAdapter

class CalendarAdapter(
    private val onTaskClick: (Task) -> Unit
) : ListAdapter<Task, CalendarAdapter.TasksViewHolder>(TasksAdapter.TaskDiffCallback()) {

    inner class TasksViewHolder(private val binding: ItemCalendarlistBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(task: Task) {
            with(binding) {

                taskTitle.text = task.title

                taskPriority.setBackgroundColor(getPriorityColor(task.priority))

                root.setOnClickListener {
                    onTaskClick(task)
                }
            }
        }

        private fun getPriorityColor(priority: Int?) = when (priority) {
            1 -> ContextCompat.getColor(binding.root.context, R.color.low_priority)
            2 -> ContextCompat.getColor(binding.root.context, R.color.medium_priority)
            3 -> ContextCompat.getColor(binding.root.context, R.color.high_priority)
            else -> ContextCompat.getColor(binding.root.context, R.color.no_priority)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        TasksViewHolder(
            ItemCalendarlistBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )

    override fun onBindViewHolder(holder: TasksViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}