package com.makelick.anytime.view.profile

import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import coil.load
import coil.transform.CircleCropTransformation
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.makelick.anytime.R
import com.makelick.anytime.databinding.FragmentProfileBinding
import com.makelick.anytime.view.BaseFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ProfileFragment : BaseFragment<FragmentProfileBinding>(FragmentProfileBinding::inflate) {

    private val viewModel: ProfileViewModel by viewModels()

    private val pickImageLauncher: ActivityResultLauncher<PickVisualMediaRequest> =
        registerForActivityResult(ActivityResultContracts.PickVisualMedia()) {
            it?.let { uploadImage(it) }
        }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupUI()
        observeViewModel()
    }

    private fun setupUI() {
        with(binding) {

            viewModel.user?.let { user ->
                profileImage.load(user.photoUrl) {
                    transformations(CircleCropTransformation())
                    fallback(R.drawable.ic_profile)
                    error(R.drawable.ic_profile)
                }
                username.setText(viewModel.user?.displayName)
            }

            edit.setOnClickListener {
                root.clearFocus()
                changeMode()
            }

            username.onFocusChangeListener = View.OnFocusChangeListener { _, _ ->
                usernameLayout.error = null
            }

            profileImage.setOnClickListener {
                if (viewModel.isEditMode.value) {
                    pickImageLauncher.launch(
                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                    )
                }
            }

            buttonManageCategories.setOnClickListener {
                navigateToCategories()
            }

            buttonLogOut.setOnClickListener {
                MaterialAlertDialogBuilder(requireContext()).apply {
                    setTitle(getString(R.string.sign_out))
                    setMessage(getString(R.string.sign_out_message))
                    setPositiveButton(getString(R.string.yes)) { dialog, _ ->
                        viewModel.signOut()
                        navigateToLogin()
                        dialog.dismiss()
                    }
                    setNegativeButton(getString(R.string.no)) { dialog, _ -> dialog.dismiss() }
                    show()
                }
            }
        }
    }

    private fun navigateToCategories() {
        findNavController().navigate(ProfileFragmentDirections.actionProfileFragmentToCategoriesFragment())
    }

    private fun navigateToLogin() {
        findNavController().navigate(ProfileFragmentDirections.actionProfileFragmentToLoginFragment())
    }

    private fun changeMode() {
        if (viewModel.isEditMode.value) {
            if (binding.username.text.toString().isBlank()) {
                binding.usernameLayout.error = getString(R.string.error_empty)
            } else {
                viewModel.applyProfileChanges(binding.username.text.toString())
            }
        } else {
            viewModel.isEditMode.value = true
        }
    }

    private fun uploadImage(uri: Uri?) {
        lifecycleScope.launch {
            binding.imageLoadingBar.visibility = View.VISIBLE
            viewModel.loadNewImage(uri)
            binding.profileImage.load(viewModel.loadedImageUri) {
                transformations(CircleCropTransformation())
                fallback(R.drawable.ic_profile)
                error(R.drawable.ic_profile)
            }
            binding.imageLoadingBar.visibility = View.GONE
        }
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            viewModel.isEditMode.collect { editMode ->
                if (editMode) {
                    binding.edit.text = getString(R.string.save)
                    binding.usernameLayout.isEnabled = true
                    binding.imageText.visibility = View.VISIBLE
                } else {
                    binding.edit.text = getString(R.string.edit)
                    binding.usernameLayout.isEnabled = false
                    binding.imageText.visibility = View.GONE
                }
            }
        }

        lifecycleScope.launch {
            viewModel.completedTasksCount.collect {
                binding.completedTasks.text = it.toString()
            }
        }

        lifecycleScope.launch {
            viewModel.uncompletedTasksCount.collect {
                binding.uncompletedTasks.text = it.toString()
            }
        }

    }
}
