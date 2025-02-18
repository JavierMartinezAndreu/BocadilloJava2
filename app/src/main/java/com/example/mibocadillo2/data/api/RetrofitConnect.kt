package com.example.mibocadillo2.data.api

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitConnect {
    private const val BASE_URL = "https://mibocata-4858c-default-rtdb.europe-west1.firebasedatabase.app/" // 🔹 Cambia esto por la URL real de tu API

    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val api: ApiUsuario by lazy {
        retrofit.create(ApiUsuario::class.java)
    }
}
