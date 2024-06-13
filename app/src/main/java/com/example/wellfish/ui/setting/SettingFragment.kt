package com.example.wellfish.ui.setting

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.wellfish.R
import com.example.wellfish.data.helper.ViewModelFactory
import com.example.wellfish.ui.welcome.WelcomeActivity
import kotlinx.coroutines.launch

class SettingFragment : Fragment() {
    private lateinit var editProfileLayout: ConstraintLayout
    private lateinit var changePasswordLayout: ConstraintLayout
    private lateinit var logoutLayout: ConstraintLayout

    private val viewModel: SettingFragmentViewModel by viewModels {
        ViewModelFactory.getInstance(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_setting, container, false)

        editProfileLayout = view.findViewById(R.id.rl_editProfile)
        changePasswordLayout = view.findViewById(R.id.rl_password)
        logoutLayout = view.findViewById(R.id.rl_logout)


        editProfileLayout.setOnClickListener {

        }

        changePasswordLayout.setOnClickListener {

        }

        logoutLayout.setOnClickListener {
            lifecycleScope.launch {
                viewModel.logout()
                navigateToWelcomeScreen()
            }
        }

        return view
    }

    private fun navigateToWelcomeScreen() {
        val intent = Intent(requireContext(), WelcomeActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
        requireActivity().finish()
    }
}
