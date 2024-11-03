package com.dicoding.asclepius.view.ui.news

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.dicoding.asclepius.data.Repository
import com.dicoding.asclepius.di.Injection


class NewsViewModelFactory(private val repository: Repository) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(NewsViewModel::class.java)) {
            return NewsViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
    }

    companion object {
        @Volatile
        private var instance: NewsViewModelFactory? = null

        fun getInstance(context: Context): NewsViewModelFactory {
            return instance ?: synchronized(this) {
                instance ?: NewsViewModelFactory(
                    Injection.provideRepository(context)
                )
            }.also { instance = it }
        }
    }
}