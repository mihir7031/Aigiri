package com.example.aigiri.network


import com.example.aigiri.service.ChatApiService
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object Chatbot {
    private const val BASE_URL = "https://your-ml-api.com" // Replace with real API base URL

    val api: ChatApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ChatApiService::class.java)
    }
}