package com.makelick.anytime.view.tasks

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat.getColor
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.makelick.anytime.R
import com.makelick.anytime.databinding.ItemTasklistBinding
import com.makelick.anytime.model.entity.Task

class TasksAdapter(
    private val onCheckboxClick: (Task) -> Unit,
    private val onTaskClick: (Task) -> Unit
) : ListAdapter<Task, TasksAdapter.TasksViewHolder>(TaskDiffCallback()) {
    class TaskDiffCallback : DiffUtil.ItemCallback<Task>() {
        override fun areItemsTheSame(oldItem: Task, newItem: Task) =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: Task, newItem: Task) =
            oldItem == newItem
    }

    inner class TasksViewHolder(private val binding: ItemTasklistBinding) :
        ViewHolder(binding.root) {
        fun bind(task: Task) {
            with(binding) {

                taskTitle.text = task.title

                taskPriority.setBackgroundColor(getPriorityColor(task.priority))

                taskCheckBox.isChecked = task.isCompleted == true
                taskCheckBox.setOnClickListener {
                    onCheckboxClick(task)
                }
                root.setOnClickListener {
                    onTaskClick(task)
                }
            }
        }

        private fun getPriorityColor(priority: Int?) = when (priority) {
            1 -> getColor(binding.root.context, R.color.low_priority)
            2 -> getColor(binding.root.context, R.color.medium_priority)
            3 -> getColor(binding.root.context, R.color.high_priority)
            else -> getColor(binding.root.context, R.color.no_priority)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        TasksViewHolder(
            ItemTasklistBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )

    override fun onBindViewHolder(holder: TasksViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}