package com.dicoding.asclepius.view.ui.activity

import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.dicoding.asclepius.data.local.entity.HistoryEntity
import com.dicoding.asclepius.databinding.ActivityResultBinding
import com.dicoding.asclepius.helper.ImageConverter
import com.dicoding.asclepius.view.ui.history.HistoryViewModel
import com.dicoding.asclepius.view.ui.history.HistoryViewModelFactory
import kotlinx.coroutines.launch
import java.text.NumberFormat

class ResultActivity : AppCompatActivity() {
    private lateinit var binding: ActivityResultBinding
    private val viewModel by viewModels<HistoryViewModel> {
        HistoryViewModelFactory.getInstance(application)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityResultBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val label = intent.getStringExtra(EXTRA_LABEL)
        val confidence = intent.getFloatExtra(EXTRA_CONFIDENCE, 0.0f)
        val confidencePercent = NumberFormat.getPercentInstance().format(confidence).trim()
        val imageUri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra(EXTRA_IMAGE_URI, Uri::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getParcelableExtra(EXTRA_IMAGE_URI)
        }

        binding.label.text = label
        binding.resultText.text = confidencePercent
        binding.resultImage.setImageURI(imageUri)

        val history = HistoryEntity(
            label = label,
            presentase = confidence,
            image = ImageConverter.uriToByteArray(contentResolver, imageUri)
        )

        lifecycleScope.launch {
            viewModel.setHistory(history)
        }
    }
    companion object {
        const val EXTRA_LABEL = "label"
        const val EXTRA_CONFIDENCE = "confidence"
        const val EXTRA_IMAGE_URI = "uri"
    }
}