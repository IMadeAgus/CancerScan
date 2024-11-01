package com.dicoding.asclepius.data.remote.retrofit

import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {

    @GET("everything")
    suspend fun getNews(
        @Query("q") query: String = "cancer",
        @Query("sortBy") sortBy: String = "relevancy",
        @Query("page") page: Int = 50,
        @Query("pageSize") pageSize: Int = 1,
        @Query("apiKey") apiKey: String = "da75561de52045dfb384ac224d3616fd"
    )
}