package com.example.mibocadillo2.data.api

import com.example.mibocadillo2.data.model.Bocadillo
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface ApiBocadillo {
    @GET("Bocadillo.json")
    suspend fun getBocadillos(): Response<Map<String, Bocadillo>>

    // Modelo para la respuesta del POST: Firebase devuelve {"name": "CLAVE_GENERADA"}
    data class FirebasePostResponse(val name: String)

    @POST("Bocadillo.json")
    suspend fun createBocadillo(@Body bocadillo: Bocadillo): Response<FirebasePostResponse>

    @PUT("Bocadillo/{id}.json")
    suspend fun updateBocadillo(@Path("id") id: String, @Body bocadillo: Bocadillo): Response<Bocadillo>

    @DELETE("Bocadillo/{id}.json")
    suspend fun deleteBocadillo(@Path("id") id: String): Response<Void>
}
