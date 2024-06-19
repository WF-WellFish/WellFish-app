package com.example.wellfish.ui.history

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.wellfish.data.api.ApiConfig
import com.example.wellfish.data.helper.ViewModelFactory
import com.example.wellfish.data.pref.UserPreference
import com.example.wellfish.data.pref.dataStore
import com.example.wellfish.data.repository.UserRepository
import com.example.wellfish.databinding.FragmentHistoryBinding
import com.example.wellfish.ui.utils.ResultState
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class HistoryFragment : Fragment(), HistoryAdapter.OnItemClickListener {

    private lateinit var historyViewModel: HistoryViewModel
    private lateinit var userRepository: UserRepository
    private var _binding: FragmentHistoryBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHistoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.rvHistory.layoutManager = LinearLayoutManager(requireContext())
        binding.rvHistory.setHasFixedSize(true)

        initializeUserRepositoryAndViewModel()
    }

    private fun initializeUserRepositoryAndViewModel() {
        lifecycleScope.launch {
            val userPreference = UserPreference.getInstance(requireContext().dataStore)
            val userSession = userPreference.getSession().first()
            val token = userSession.token

            if (token.isNotEmpty()) {
                val apiService = ApiConfig.getApiService(token)
                userRepository = UserRepository.getInstance(userPreference, apiService)
                historyViewModel = ViewModelProvider(
                    this@HistoryFragment,
                    ViewModelFactory(userRepository)
                ).get(HistoryViewModel::class.java)

                fetchHistory()
            } else {
                //
            }
        }
    }

    private fun fetchHistory() {
        historyViewModel.getClassificationHistory().observe(viewLifecycleOwner) { result ->
            when (result) {
                is ResultState.Loading -> {
                    binding.pbLoading.visibility = View.VISIBLE
                }
                is ResultState.Success -> {
                    binding.pbLoading.visibility = View.GONE
                    val historyList = result.data.data
                    if (historyList != null) {
                        binding.rvHistory.adapter = HistoryAdapter(historyList.filterNotNull(), this@HistoryFragment)
                    }
                }
                is ResultState.Error -> {
                    binding.pbLoading.visibility = View.GONE
                }
            }
        }
    }

    override fun onItemClick(historyId: String) {
        val action = HistoryFragmentDirections.actionHistoryFragmentToHistoryDetailFragment(historyId)
        findNavController().navigate(action)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
