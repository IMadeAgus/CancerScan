package com.dicoding.asclepius.data

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import androidx.lifecycle.map
import com.dicoding.asclepius.data.local.entity.HistoryEntity
import com.dicoding.asclepius.data.local.room.HistoryDao
import com.dicoding.asclepius.data.remote.retrofit.ApiService
import com.dicoding.asclepius.helper.Result

class Repository(
    private val apiService: ApiService,
    private val historyDao: HistoryDao
) {

    suspend fun setHistory(history: HistoryEntity) {
        historyDao.insertHistory(history)
    }

    fun getHistory(): LiveData<Result<List<HistoryEntity>>> = liveData {
        emit(Result.Loading)
        try {
            val localData: List<HistoryEntity> = historyDao.getHistory().value ?: emptyList()
            emit(Result.Success(localData))
        } catch (e: Exception) {
            Log.d("MainRepository", "getCancers(): ${e.message.toString()}")
            emit(Result.Error(e.message.toString()))
        }
        // Save data to room
        val localData: LiveData<Result<List<HistoryEntity>>> =
            historyDao.getHistory().map { Result.Success(it) }
        emitSource(localData)
    }

    companion object {
        @Volatile
        private var instance: Repository? = null
        fun getInstance(
            apiService: ApiService,
            historyDao: HistoryDao
        ): Repository = instance ?: synchronized(this) {
            instance ?: Repository(
                apiService,
                historyDao
            )
        }.also { instance = it }
    }
}