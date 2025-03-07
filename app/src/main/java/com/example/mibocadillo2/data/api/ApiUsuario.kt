package com.example.mibocadillo2.data.api

import com.example.mibocadillo2.data.model.Usuario
import retrofit2.Response
import retrofit2.http.*

interface ApiUsuario {
    @GET("Usuario/{uid}.json")
    suspend fun getUsuarioByUid(@Path("uid") uid: String): Usuario?

    @GET("Usuario.json")
    suspend fun getUsuarios(): Response<Map<String, Usuario>>

    // Modelo de respuesta para el POST: Firebase devuelve {"name": "CLAVE_GENERADA"}
    data class FirebasePostResponse(val name: String)

    // Método POST que devuelve FirebasePostResponse
    @POST("Usuario.json")
    suspend fun createUsuario(@Body usuario: Usuario): Response<FirebasePostResponse>

    @PUT("Usuario/{uid}.json")
    suspend fun updateUsuario(@Path("uid") uid: String, @Body usuario: Usuario): Response<Usuario>

    @DELETE("Usuario/{uid}.json")
    suspend fun deleteUsuario(@Path("uid") uid: String): Response<Void>
}
