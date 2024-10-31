package com.dicoding.asclepius.view

import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.dicoding.asclepius.R
import com.dicoding.asclepius.databinding.ActivityResultBinding
import java.text.NumberFormat

class ResultActivity : AppCompatActivity() {
    private lateinit var binding: ActivityResultBinding

    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityResultBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val label = intent.getStringExtra(EXTRA_LABEL)
        val confidence = intent.getFloatExtra(EXTRA_CONFIDENCE, 0.0f)
        val confidencePercent = NumberFormat.getPercentInstance().format(confidence).trim()
        val imageUri = intent.getParcelableExtra<Uri>(EXTRA_IMAGE_URI)

        binding.resultText.text = "$label: $confidencePercent"
        binding.resultImage.setImageURI(imageUri)

    }

    companion object {
        const val EXTRA_LABEL = "label"
        const val EXTRA_CONFIDENCE = "confidence"
        const val EXTRA_IMAGE_URI = "uri"
    }
}