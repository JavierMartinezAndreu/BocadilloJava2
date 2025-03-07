package com.example.mibocadillo2.data.api

import com.example.mibocadillo2.data.model.Pedido
import retrofit2.Response
import retrofit2.http.*

interface ApiPedido {
    @GET("Pedido.json")
    suspend fun getPedidos(): Response<Map<String, Pedido>>

    // Firebase devuelve {"name": "CLAVE_GENERADA"}
    data class FirebasePostResponse(val name: String)

    @POST("Pedido.json")
    suspend fun createPedido(@Body pedido: Pedido): Response<FirebasePostResponse>

    @PUT("Pedido/{id}.json")
    suspend fun updatePedido(@Path("id") id: String, @Body pedido: Pedido): Response<Pedido>

    @DELETE("Pedido/{id}.json")
    suspend fun deletePedido(@Path("id") id: String): Response<Void>
}
