package dev.ageev.lab3.api

import dev.ageev.lab3.api.dto.NewsData
import okhttp3.Interceptor
import okhttp3.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

interface NewsApi {

    @GET("news")
    suspend fun news(
        @Query("apikey") apiKey: String,
        @Query("q") query: String,
        @Query("language") language: String
    ): NewsData

    companion object Factory {
        fun create(): NewsApi {
            val retrofit = Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl("https://newsdata.io/api/1/")
                .build()

            return retrofit.create(NewsApi::class.java);
        }
    }
}