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
import com.makelick.anytime.R
import com.makelick.anytime.databinding.FragmentProfileBinding
import com.makelick.anytime.view.BaseFragment
import com.makelick.anytime.view.MainActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ProfileFragment : BaseFragment<FragmentProfileBinding>(FragmentProfileBinding::inflate) {

    private val viewModel: ProfileViewModel by viewModels()

    private val pickImageLauncher: ActivityResultLauncher<PickVisualMediaRequest> =
        registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uploadImage(it) }

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

            edit.setOnClickListener { changeMode() }

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
                viewModel.signOut()
                navigateToLogin()
            }
        }
    }

    private fun navigateToCategories() {
        findNavController().navigate(R.id.action_profileFragment_to_categoriesFragment)
        (activity as MainActivity).disableBottomNav()
    }

    private fun navigateToLogin() {
        findNavController().navigate(R.id.loginFragment)
        (activity as MainActivity).disableBottomNav()
    }

    private fun changeMode() {
        if (viewModel.isEditMode.value) {
            viewModel.applyProfileChanges(binding.username.text.toString())
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

    }
}
