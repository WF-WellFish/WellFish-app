package com.example.wellfish.ui.setting

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.wellfish.R
import com.example.wellfish.data.helper.ViewModelFactory
import com.example.wellfish.databinding.FragmentChangePasswordBinding
import com.example.wellfish.ui.utils.ResultState

class ChangePasswordFragment : Fragment() {
    private val viewModel: ChangePasswordViewModel by viewModels {
        ViewModelFactory.getInstance(requireContext())
    }

    private var _binding: FragmentChangePasswordBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentChangePasswordBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.findViewById<ImageButton>(R.id.btn_back).setOnClickListener {
            findNavController().navigateUp()
        }

        binding.button.setOnClickListener {
            val oldPassword = binding.editTextCurrentPassword.editText?.text.toString()
            val newPassword = binding.editTextNewPassword.editText?.text.toString()
            val newPasswordConfirmation = binding.editTextRetypeNewPassword.editText?.text.toString()

            if (oldPassword.isBlank()) {
                binding.editTextCurrentPassword.error = "Current password is required"
                return@setOnClickListener
            }

            if (newPassword.isBlank()) {
                binding.editTextNewPassword.error = "New password is required"
                return@setOnClickListener
            }

            if (newPasswordConfirmation.isBlank()) {
                binding.editTextRetypeNewPassword.error = "Please retype the new password"
                return@setOnClickListener
            }

            if (newPassword != newPasswordConfirmation) {
                binding.editTextRetypeNewPassword.error = "Passwords do not match"
                return@setOnClickListener
            }

            viewModel.changePassword(oldPassword, newPassword, newPasswordConfirmation)
        }

        viewModel.changePasswordResult.observe(viewLifecycleOwner) { result ->
            when (result) {
                is ResultState.Loading -> {
                    binding.pbLoading.visibility = View.VISIBLE
                }
                is ResultState.Success -> {
                    binding.pbLoading.visibility = View.GONE
                    Toast.makeText(requireContext(), "Password changed successfully", Toast.LENGTH_SHORT).show()
                }
                is ResultState.Error -> {
                    binding.pbLoading.visibility = View.GONE
                    Toast.makeText(requireContext(), result.error, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}