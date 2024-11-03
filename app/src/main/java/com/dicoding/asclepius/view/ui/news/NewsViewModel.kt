package com.dicoding.asclepius.view.ui.news

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.dicoding.asclepius.data.Repository
import com.dicoding.asclepius.data.remote.response.ArticlesItem
import com.dicoding.asclepius.helper.Result

class NewsViewModel(private val repository: Repository) : ViewModel() {

    fun getNews(): LiveData<Result<List<ArticlesItem>>> {
        return repository.getNews()
    }
}