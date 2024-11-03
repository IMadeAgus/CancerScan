package com.dicoding.asclepius.view.ui.history

import androidx.lifecycle.ViewModel
import com.dicoding.asclepius.data.Repository
import com.dicoding.asclepius.data.local.entity.HistoryEntity

class HistoryViewModel(private val repository: Repository) : ViewModel() {
    fun getHistory() = repository.getHistory()
    suspend fun setHistory(history: HistoryEntity) = repository.setHistory(history)
}