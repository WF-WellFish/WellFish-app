package com.example.wellfish.ui.history

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.wellfish.R
import com.example.wellfish.data.helper.ViewModelFactory
import com.example.wellfish.data.response.ClassificationHistoryDetailResponse
import com.example.wellfish.ui.utils.ResultState

class HistoryDetailFragment : Fragment() {

    private lateinit var viewModel: HistoryDetailViewModel
    private lateinit var tvName: TextView
    private lateinit var tvType: TextView
    private lateinit var tvFood: TextView
    private lateinit var tvDescription: TextView
    private lateinit var ivPreview: ImageView
    private lateinit var pbLoading: ProgressBar

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_history_detail, container, false)
        tvName = view.findViewById(R.id.tvName)
        tvType = view.findViewById(R.id.tvType)
        tvFood = view.findViewById(R.id.tvFood)
        tvDescription = view.findViewById(R.id.tvDescription)
        ivPreview = view.findViewById(R.id.ivPreview)
        pbLoading = view.findViewById(R.id.pbLoading)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val factory = ViewModelFactory.getInstance(requireContext())
        viewModel = ViewModelProvider(this, factory)[HistoryDetailViewModel::class.java]

        val id = arguments?.getString("history_id") ?: return

        view.findViewById<ImageButton>(R.id.btn_back).setOnClickListener {
            findNavController().navigateUp()
        }

        observeHistoryDetail(id)
    }

    private fun observeHistoryDetail(id: String) {
        viewModel.getClassificationHistoryDetail(id).observe(viewLifecycleOwner) { result ->
            when (result) {
                is ResultState.Loading -> {
                    pbLoading.visibility = View.VISIBLE
                }
                is ResultState.Success -> {
                    pbLoading.visibility = View.GONE
                    displayHistoryDetail(result.data)
                }
                is ResultState.Error -> {
                    pbLoading.visibility = View.GONE
                    Toast.makeText(requireContext(), result.error, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun displayHistoryDetail(data: ClassificationHistoryDetailResponse) {
        tvName.text = "Name: ${data.data?.name}"
        tvType.text = "Type: ${data.data?.type}"
        tvFood.text = "Food: ${data.data?.food}"
        tvDescription.text = "Description: ${data.data?.description}"

        val pictureUrl = data.data?.picture
        if (!pictureUrl.isNullOrBlank()) {
            Glide.with(this)
                .load(pictureUrl)
                .placeholder(R.drawable.ic_place_holder)
                .into(ivPreview)
        }
    }

}
