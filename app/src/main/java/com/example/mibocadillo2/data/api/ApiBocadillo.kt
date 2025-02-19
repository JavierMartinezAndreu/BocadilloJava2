package com.example.mibocadillo2.data.api

import com.example.mibocadillo2.data.model.Bocadillo
import retrofit2.Call
import retrofit2.http.GET

interface ApiBocadillo {
    @GET("Bocadillo.json") // 🔹 Esto obtiene todos los bocadillos de la base de datos
    fun getBocadillos(): Call<Map<String, Bocadillo>>
}
