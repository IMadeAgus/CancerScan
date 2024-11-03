package com.dicoding.asclepius.di

import android.content.Context
import com.dicoding.asclepius.data.Repository
import com.dicoding.asclepius.data.local.room.HistoryDatabase
import com.dicoding.asclepius.data.remote.retrofit.ApiConfig

object Injection {
    fun provideRepository(context: Context): Repository {
        val apiService = ApiConfig.getApiService()
        val database = HistoryDatabase.getInstance(context)
        val dao = database.historyDao()
        return Repository.getInstance(apiService, dao, context)
    }
}