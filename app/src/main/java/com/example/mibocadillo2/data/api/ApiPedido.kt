package com.example.mibocadillo2.data.api

import com.example.mibocadillo2.data.model.Pedido
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface ApiPedido {
    @POST("Pedido.json")
    suspend fun createPedido(@Body pedido: Pedido): Response<Pedido>

    @GET("Pedido.json")
    suspend fun getPedidos(): Response<Map<String, Pedido>>
}
