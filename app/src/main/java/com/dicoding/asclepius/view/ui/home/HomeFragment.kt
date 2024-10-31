package com.dicoding.asclepius.view.ui.home

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
import com.dicoding.asclepius.databinding.FragmentHomeBinding
import com.dicoding.asclepius.helper.ImageClassifierHelper
import com.dicoding.asclepius.view.ResultActivity
import org.tensorflow.lite.task.vision.classifier.Classifications

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private lateinit var imageClassifierHelper: ImageClassifierHelper
    private var currentImageUri: Uri? = null
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.galleryButton.setOnClickListener{
            startGallery()
        }
        binding.analyzeButton.setOnClickListener{
            currentImageUri?.let {
                analyzeImage()
                imageClassifierHelper.classifyStaticImage(currentImageUri!!)
            } ?: run{
                showToast("Gambar tidak boleh kosong")
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

    private fun analyzeImage() {
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
                        results?.let { s ->
                            if (s.isNotEmpty() && s[0].categories.isNotEmpty()) {
                                println(s)
                                val result =
                                    s[0].categories.sortedByDescending { it?.score }.first()
                                moveToResult(
                                    label = result.label,
                                    confidence = result.score,
                                    imageUri = currentImageUri
                                )
                            } else {
                               showToast("Terjadi kesalahan")
                            }
                        }
                    }
                }
            }
        )
    }

    private fun moveToResult(label: String, confidence: Float, imageUri: Uri?) {
        val intent = Intent(requireContext(), ResultActivity::class.java)
        intent.putExtra(ResultActivity.EXTRA_LABEL, label)
        intent.putExtra(ResultActivity.EXTRA_CONFIDENCE, confidence)
        intent.putExtra(ResultActivity.EXTRA_IMAGE_URI, imageUri)
        startActivity(intent)
    }

    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    private val launcherGallery = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            currentImageUri = uri
            showImage()
        } else {
            Log.d("Photo Picker", "No media selected")
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}