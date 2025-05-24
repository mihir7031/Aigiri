package com.example.aigiri

// AppContainer.kt
object AppContainer {
    private val retrofit = retrofit2.Retrofit.Builder()
        .baseUrl("http://192.168.14.249:3000/")
        .addConverterFactory(retrofit2.converter.gson.GsonConverterFactory.create())
        .build()

    val otpApiService = retrofit.create(com.example.aigiri.service.OtpApiService::class.java)

    val otpRepository = com.example.aigiri.repository.OtpRepository(otpApiService)
}
