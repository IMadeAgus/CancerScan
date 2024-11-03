package com.dicoding.asclepius.view.ui.history

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.dicoding.asclepius.data.Repository
import com.dicoding.asclepius.data.local.entity.HistoryEntity
import com.dicoding.asclepius.helper.Result

class HistoryViewModel(private val repository: Repository) : ViewModel() {
    private var _history: LiveData<Result<List<HistoryEntity>>>? = null

    fun getHistory(): LiveData<Result<List<HistoryEntity>>> {
        if (_history == null) {
            _history = repository.getHistory()
        }
        return _history!!
    }
    suspend fun setHistory(history: HistoryEntity) = repository.setHistory(history)
}