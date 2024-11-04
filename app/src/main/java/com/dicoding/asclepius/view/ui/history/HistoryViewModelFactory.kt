package com.dicoding.asclepius.view.ui.history

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.dicoding.asclepius.data.Repository
import com.dicoding.asclepius.di.Injection

class HistoryViewModelFactory(
    private val repository: Repository
) : ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HistoryViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return HistoryViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown viewmodel class: " + modelClass.name)
    }

    companion object {
        @Volatile
        private var instance: HistoryViewModelFactory? = null
        fun getInstance(context: Context): HistoryViewModelFactory =
            instance ?: synchronized(this) {
                val repository = Injection.provideRepository(context)
                instance ?: HistoryViewModelFactory(repository)
            }.also { instance = it }
    }
}