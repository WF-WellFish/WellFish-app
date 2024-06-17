package com.example.wellfish.ui.setting

import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.viewModels
import com.bumptech.glide.Glide
import com.example.wellfish.R
import com.example.wellfish.data.helper.ViewModelFactory
import com.example.wellfish.data.response.EditProfileUser
import com.example.wellfish.databinding.FragmentEditProfileBinding
import com.example.wellfish.ui.utils.ResultState
import com.example.wellfish.ui.utils.reduceFileImage
import com.example.wellfish.ui.utils.uriToFile
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody

class EditProfileFragment : Fragment() {
    private val viewModel: EditProfileViewModel by viewModels {
        ViewModelFactory.getInstance(requireContext())
    }
    private var _binding: FragmentEditProfileBinding? = null
    private val binding get() = _binding!!

    private var imageUri: Uri? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEditProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // nampilin nama dan username
        viewModel.currentUser.observe(viewLifecycleOwner) { user ->
            binding.editTextName.setText(user.name)
            binding.editTextUsername.setText(user.username)
        }

        binding.ivProfilePicture.setOnClickListener {
            openGallery()
        }

        binding.ibEdit.setOnClickListener {
            openGallery()
        }

        binding.button.setOnClickListener {
            val newName = binding.editTextName.text.toString().trim()
            val profilePicturePart = imageUri?.let { uri ->
                val file = uriToFile(uri, requireContext()).reduceFileImage()
                val requestFile = file.asRequestBody("image/*".toMediaTypeOrNull())
                MultipartBody.Part.createFormData("profile_picture", file.name, requestFile)
            }
            viewModel.updateProfile(newName, profilePicturePart)
        }


        viewModel.editProfileResponse.observe(viewLifecycleOwner) { result ->
            when (result) {
                is ResultState.Success -> {
                    binding.pbLoading.visibility = View.GONE
                    result.data.data?.let { user ->
                        updateUI(user)
                    } ?: showToast("Empty user data received")
                }
                is ResultState.Error -> {
                    binding.pbLoading.visibility = View.GONE
                    showToast(result.error)
                }
                is ResultState.Loading -> {
                    binding.pbLoading.visibility = View.VISIBLE
                }
            }
        }

        binding.btnBack.setOnClickListener{
            requireActivity().supportFragmentManager.popBackStack()
        }
    }

    private fun openGallery() {
        launcherGallery.launch("image/*")
    }

    private val launcherGallery = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        if (uri != null) {
            imageUri = uri
            binding.ivProfilePicture.setImageURI(uri)
        }
    }

    private fun updateUI(data: EditProfileUser) {
        binding.editTextName.setText(data.name)
        data.profilePicture?.let {
            Glide.with(this)
                .load(it)
                .placeholder(R.drawable.ic_place_holder)
                .into(binding.ivProfilePicture)
        }
        showToast("Profile updated successfully")
    }

    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}