package com.makelick.anytime.view.taskinfo

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import com.makelick.anytime.R
import com.makelick.anytime.databinding.FragmentTaskInfoBinding
import com.makelick.anytime.view.BaseFragment


class TaskInfoFragment : BaseFragment<FragmentTaskInfoBinding>(FragmentTaskInfoBinding::inflate) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupUI()
    }

    private fun setupUI() {
        with(binding) {
            editButton.setOnClickListener {
                navigateToEditTask()
            }
        }
    }

    private fun navigateToEditTask() {
        findNavController().navigate(R.id.action_taskInfoFragment_to_editTaskFragment)
    }

}