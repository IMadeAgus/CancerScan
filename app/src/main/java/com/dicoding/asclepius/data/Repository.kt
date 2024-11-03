package com.dicoding.asclepius.data

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import androidx.lifecycle.map
import java.net.UnknownHostException
import java.net.SocketTimeoutException
import java.net.ConnectException
import com.dicoding.asclepius.BuildConfig
import com.dicoding.asclepius.data.local.entity.HistoryEntity
import com.dicoding.asclepius.data.local.room.HistoryDao
import com.dicoding.asclepius.data.remote.response.ArticlesItem
import com.dicoding.asclepius.data.remote.retrofit.ApiService
import com.dicoding.asclepius.helper.NetworkUtils
import com.dicoding.asclepius.helper.Result

class Repository(
    private val apiService: ApiService,
    private val historyDao: HistoryDao,
    private val appContext: Context
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
            Log.d(TAG, "getHistory(): ${e.message.toString()}")
            emit(Result.Error(e.message.toString()))
        }
        // Save data to room
        val localData: LiveData<Result<List<HistoryEntity>>> =
            historyDao.getHistory().map { Result.Success(it) }
        emitSource(localData)
    }

    fun getNews(): LiveData<Result<List<ArticlesItem>>> = liveData {
        emit(Result.Loading)
        try {
            if (!NetworkUtils.isInternetAvailable(appContext)) {
                emit(Result.Error("Tidak ada koneksi internet"))
                return@liveData
            }
            val response = apiService.getNews(apiKey = BuildConfig.APP_ID)
            if (response.isSuccessful) {
                response.body()?.articles?.let {
                    emit(Result.Success(it.filterNotNull()))
                } ?: emit(Result.Error("Tidak ada berita saat ini"))
            } else {
                emit(Result.Error(response.message()))
            }
        } catch (e: Exception) {
            Log.d(TAG, "getNews(): ${e.message.toString()}")
            val errorMessage = when (e) {
                is UnknownHostException -> "Tidak ada koneksi internet"
                is SocketTimeoutException -> "Koneksi timeout"
                is ConnectException -> "Tidak ada koneksi internet"
                else -> e.message ?: "Terjadi kesalahan"
            }
            emit(Result.Error(errorMessage))
        }
    }


    companion object {
        private const val TAG = "Repository"
        @Volatile
        private var instance: Repository? = null
        fun getInstance(
            apiService: ApiService,
            historyDao: HistoryDao,
            appContext: Context
        ): Repository = instance ?: synchronized(this) {
            instance ?: Repository(
                apiService,
                historyDao,
                appContext
            )
        }.also { instance = it }
    }
}