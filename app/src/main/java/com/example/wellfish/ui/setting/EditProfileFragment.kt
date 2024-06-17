package com.example.wellfish.ui.setting

import android.net.Uri
import android.os.Bundle
import android.util.Log
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
import com.example.wellfish.data.pref.UserModel
import com.example.wellfish.data.response.EditProfileData
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

        // Set current user data to the view when the fragment is created
        viewModel.currentUser.observe(viewLifecycleOwner) { user ->
            binding.editTextName.setText(user.name)
            binding.editTextUsername.setText(user.username)
            Glide.with(this)
                .load(user.profilePicture)
                .placeholder(R.drawable.ic_place_holder)
                .into(binding.ivProfilePicture)
        }

        // Open gallery when the user click the profile picture
        binding.ivProfilePicture.setOnClickListener {
            openGallery()
        }

        // Open gallery when the user click the edit button
        binding.ibEdit.setOnClickListener {
            openGallery()
        }

        // Update the user profile when the user click the save button
        binding.button.setOnClickListener {
            val newName = binding.editTextName.text.toString().trim()
            val profilePicturePart = imageUri?.let { uri ->
                val file = uriToFile(uri, requireContext()).reduceFileImage()
                val requestFile = file.asRequestBody("image/*".toMediaTypeOrNull())
                MultipartBody.Part.createFormData("profile_picture", file.name, requestFile)
            }

            viewModel.updateProfile(newName, profilePicturePart)
        }

        // Observe the response from the server
        viewModel.editProfileResponse.observe(viewLifecycleOwner) { result ->
            when (result) {
                is ResultState.Success -> {
                    binding.pbLoading.visibility = View.GONE

                    result.data.data?.user.let { user ->
                        if (user != null) {
                            updateUI(user)

                            // save the new user data to the shared preferences
                            // get old user model
                            val oldUser = viewModel.currentUser.value

                            // save the new user model
                            viewModel.saveSession(
                                UserModel(
                                    user.id.toString(),
                                    user.name.toString(),
                                    oldUser?.username.toString(),
                                    oldUser?.password.toString(),
                                    user.profilePicture.toString(),
                                    oldUser?.token.toString(),
                                    oldUser?.isLogin ?: false
                                )
                            )
                        }
                    }
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
            // set the image to the image view
            imageUri = uri
            binding.ivProfilePicture.setImageURI(uri)
            Glide.with(this)
                .load(uri)
                .placeholder(R.drawable.ic_place_holder)
                .into(binding.ivProfilePicture)
        }
    }

    private fun updateUI(data: EditProfileUser) {
        binding.editTextName.setText(data.name)
        Glide.with(this)
            .load(data.profilePicture)
            .placeholder(R.drawable.ic_place_holder)
            .into(binding.ivProfilePicture)

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