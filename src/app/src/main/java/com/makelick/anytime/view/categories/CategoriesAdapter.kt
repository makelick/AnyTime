package com.makelick.anytime.view.categories

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.makelick.anytime.databinding.ItemCategoryBinding

class CategoriesAdapter(
    private val onButtonClick: (String) -> Unit
) : ListAdapter<String, CategoriesAdapter.CategoriesViewHolder>(CategoriesDiffCallback()) {
    class CategoriesDiffCallback : DiffUtil.ItemCallback<String>() {
        override fun areItemsTheSame(oldItem: String, newItem: String) = oldItem == newItem

        override fun areContentsTheSame(oldItem: String, newItem: String) = oldItem == newItem
    }

    inner class CategoriesViewHolder(private val binding: ItemCategoryBinding) :
        ViewHolder(binding.root) {
        fun bind(category: String) {
            with(binding) {

                categoryTitle.text = category

                deleteButton.setOnClickListener {
                    onButtonClick(category)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        CategoriesViewHolder(
            ItemCategoryBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )

    override fun onBindViewHolder(holder: CategoriesViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}