package com.example.wellfish.ui.home

import com.bumptech.glide.Glide
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import com.example.wellfish.R
import com.example.wellfish.data.pref.UserPreference
import com.example.wellfish.data.pref.dataStore
import com.example.wellfish.databinding.FragmentHomeBinding
import kotlinx.coroutines.launch

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
        //return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        fetchAndDisplayUserName()
    }

    private fun fetchAndDisplayUserName() {
        val userPreference = UserPreference.getInstance(requireContext().dataStore)
        lifecycleScope.launch {
            userPreference.getSession().collect { user ->
                val greeting = getString(R.string.greeting_home) + " " + user.name
                binding.tvNameHome.text = greeting
                // show profile picture from uri
                if(user.profilePicture != "") {
                    Glide.with(this@HomeFragment)
                        .load(user.profilePicture)
                        .placeholder(R.drawable.ic_place_holder)
                        .into(binding.ivProfilePicture)
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}