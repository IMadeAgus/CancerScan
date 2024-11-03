package com.dicoding.asclepius.view.ui.home

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.dicoding.asclepius.databinding.FragmentHomeBinding
import com.dicoding.asclepius.helper.ImageClassifierHelper
import com.dicoding.asclepius.view.ui.activity.ResultActivity
import com.yalantis.ucrop.UCrop
import org.tensorflow.lite.task.vision.classifier.Classifications
import java.io.File

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private lateinit var imageClassifierHelper: ImageClassifierHelper
    private var currentImageUri: Uri? = null
    private val binding get() = _binding!!
    private val viewModel: HomeViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initializeImageClassifier()
        setupClickListeners()

        viewModel.currentImageUri.observe(viewLifecycleOwner) { uri ->
            currentImageUri = uri
            showImage()  // Display the image if URI is not null
        }
    }

    private fun initializeImageClassifier() {
        imageClassifierHelper = ImageClassifierHelper(
            context = requireContext(),
            classifierListener = object : ImageClassifierHelper.ClassifierListener {
                override fun onError(error: String) {
                    activity?.runOnUiThread {
                        showToast(error)
                    }
                }

                override fun onResults(results: List<Classifications>?, inferenceTime: Long) {
                    activity?.runOnUiThread {
                        results?.let { classifications ->
                            if (classifications.isNotEmpty() && classifications[0].categories.isNotEmpty()) {
                                val result =
                                    classifications[0].categories.maxByOrNull { it.score }!!
                                moveToResult(
                                    label = result.label,
                                    confidence = result.score,
                                    imageUri = currentImageUri
                                )
                            } else {
                                showToast("Tidak dapat mengklasifikasikan gambar")
                            }
                        } ?: showToast("Hasil klasifikasi kosong")
                    }
                }
            }
        )
    }

    private fun setupClickListeners() {
        binding.apply {
            galleryButton.setOnClickListener { startGallery() }
            analyzeButton.setOnClickListener {
                currentImageUri?.let {
                    imageClassifierHelper.classifyStaticImage(it)
                } ?: showToast("Pilih gambar terlebih dahulu")
            }
        }
    }

    private fun startGallery() {
        launcherGallery.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }

    private fun showImage() {
        currentImageUri?.let {
            Log.d("Image URI", "showImage: $it")
            binding.previewImageView.setImageURI(it)
        }
    }

    private fun launchUCrop(sourceUri: Uri) {
        val destinationUri = Uri.fromFile(
            File(requireContext().cacheDir, "cropped_${System.currentTimeMillis()}.jpg")
        )
        UCrop.of(sourceUri, destinationUri)
            .start(requireContext(), this)
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == RESULT_OK && requestCode == UCrop.REQUEST_CROP) {
            val resultUri = UCrop.getOutput(data!!)
            resultUri?.let {
                currentImageUri = it
                showImage()
            }
        } else if (resultCode == UCrop.RESULT_ERROR) {
            val cropError = UCrop.getError(data!!)
            showToast("Crop error: ${cropError?.message}")
        }
    }

    private fun moveToResult(label: String, confidence: Float, imageUri: Uri?) {
        val intent = Intent(requireContext(), ResultActivity::class.java).apply {
            putExtra(ResultActivity.EXTRA_LABEL, label)
            putExtra(ResultActivity.EXTRA_CONFIDENCE, confidence)
            putExtra(ResultActivity.EXTRA_IMAGE_URI, imageUri)
        }
        startActivity(intent)
    }

    private fun showToast(message: String?) {
        Toast.makeText(requireContext(), message ?: "Unknown error", Toast.LENGTH_SHORT).show()
    }

    private val launcherGallery = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
          viewModel.setImageUri(uri)
            launchUCrop(uri)
        } else {
            Log.d("Photo Picker", "No media selected")
            showToast("Tidak ada gambar yang dipilih")
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}