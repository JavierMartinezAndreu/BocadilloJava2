package com.example.mibocadillo2.data.api

import com.example.mibocadillo2.data.model.Usuario
import retrofit2.http.GET
import retrofit2.http.Path

interface ApiUsuario {
    @GET("Usuario/{uid}.json")
    fun getUsuarioByUid(@Path("uid") uid: String):Usuario
}
