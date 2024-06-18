package com.example.wellfish.ui.setting

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.wellfish.R
import com.example.wellfish.data.helper.ViewModelFactory
import com.example.wellfish.databinding.FragmentSettingBinding
import com.example.wellfish.ui.utils.ResultState
import com.example.wellfish.ui.welcome.WelcomeActivity

class SettingFragment : Fragment() {

    private val viewModel: SettingFragmentViewModel by viewModels {
        ViewModelFactory.getInstance(requireContext())
    }
    private var _binding: FragmentSettingBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.rlEditProfile.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.frame_layout, EditProfileFragment())
                .addToBackStack(null)
                .commit()
        }

        binding.rlPassword.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.frame_layout, ChangePasswordFragment())
                .addToBackStack(null)
                .commit()
        }

        binding.rlLogout.setOnClickListener {
            viewModel.logout()
        }

        observeLogout()
    }

    private fun observeLogout() {
        viewModel.logoutResult.observe(viewLifecycleOwner) { result ->
            when (result) {
                is ResultState.Loading -> {
                    binding.pbLoading.visibility = View.VISIBLE
                }
                is ResultState.Success -> {
                    binding.pbLoading.visibility = View.GONE
                    navigateToWelcomeScreen()
                }
                is ResultState.Error -> {
                    binding.pbLoading.visibility = View.GONE
                }
            }
        }
    }

    private fun navigateToWelcomeScreen() {
        val intent = Intent(requireContext(), WelcomeActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
        requireActivity().finish()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
