package com.example.wellfish.ui.camera

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.example.wellfish.R
import com.example.wellfish.data.helper.ViewModelFactory
import com.example.wellfish.data.response.ClassificationFishResponse
import com.example.wellfish.databinding.FragmentFishBinding
import com.example.wellfish.ui.utils.ResultState
import com.example.wellfish.ui.utils.reduceFileImage
import com.example.wellfish.ui.utils.uriToFile
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody

class FishFragment : Fragment(R.layout.fragment_fish) {

    private var _binding: FragmentFishBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: ClassificationViewModel
    private var imageUri: Uri? = null

    private val requestPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
        if (isGranted) {
            showToast("Permission request granted")
        } else {
            showToast("Permission request denied")
        }
    }

    private fun allPermissionGranted() =
        ContextCompat.checkSelfPermission(requireContext(), REQUIRED_PERMISSION) == PackageManager.PERMISSION_GRANTED

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFishBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (!allPermissionGranted()) {
            requestPermissionLauncher.launch(REQUIRED_PERMISSION)
        }

        viewModel = ViewModelProvider(this, ViewModelFactory.getInstance(requireContext()))[ClassificationViewModel::class.java]

        binding.btnGallery.setOnClickListener { startGallery() }
        binding.btnSubmit.setOnClickListener {
            imageUri?.let { uri -> classifyImage(uri) } ?: showToast("No image selected")
        }

        observeViewModel()
    }

    private fun observeViewModel() {
        viewModel.classificationResult.observe(viewLifecycleOwner) { result ->
            when (result) {
                is ResultState.Loading -> showLoading(true)
                is ResultState.Success -> {
                    showLoading(false)
                    updateUI(result.data)
                    binding.btnSubmit.isEnabled = false
                }
                is ResultState.Error -> {
                    showLoading(false)
                    showToast(result.error)
                }
            }
        }
    }

    private fun startGallery() {
        launcherGallery.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }

    private val launcherGallery = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri: Uri? ->
        if (uri != null) {
            imageUri = uri
            binding.ivPreview.setImageURI(uri)
            binding.btnSubmit.isEnabled = true
        } else {
            Log.d("Photo Picker", "No media selected")
        }
    }

    private fun classifyImage(uri: Uri) {
        try {
            val file = uriToFile(uri, requireContext()).reduceFileImage()
            val requestFile = file.asRequestBody("image/*".toMediaTypeOrNull())
            val body = MultipartBody.Part.createFormData("image", file.name, requestFile)

            Log.d("FishFragment", "classifyImage: file name = ${file.name}, file size = ${file.length()}")

            viewModel.classifyFish(body)
        } catch (e: Exception) {
            showToast("Failed to convert URI to File")
            Log.e("FishFragment", "Failed to convert URI to File", e)
        }
    }

    private fun updateUI(data: ClassificationFishResponse) {
        Log.d("FishFragment", "API response: $data")
        data.data?.let { fishData ->
            binding.tvName.apply {
                visibility = View.VISIBLE
                text = "Name: ${fishData.name}"
            }
            binding.tvType.apply {
                visibility = View.VISIBLE
                text = "Type: ${fishData.type}"
            }
            binding.tvDescription.apply {
                visibility = View.VISIBLE
                text = "Description: ${fishData.description}"
            }
            binding.tvFood.apply {
                visibility = View.VISIBLE
                text = "Food: ${fishData.food}"
            }
            binding.ivResultImage.apply {
                visibility = View.VISIBLE
                Glide.with(this@FishFragment)
                    .load(fishData.picture)
                    .placeholder(R.drawable.ic_place_holder)
                    .into(this)
            }
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.pbLoading.visibility = if (isLoading) View.VISIBLE else View.GONE

        if (isLoading) {
            binding.tvName.visibility = View.GONE
            binding.tvType.visibility = View.GONE
            binding.tvDescription.visibility = View.GONE
            binding.tvFood.visibility = View.GONE
            binding.ivResultImage.visibility = View.GONE
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val REQUIRED_PERMISSION = Manifest.permission.CAMERA
    }
}
