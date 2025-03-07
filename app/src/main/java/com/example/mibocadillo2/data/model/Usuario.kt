package com.example.mibocadillo2.data.model


data class Usuario(
    var id: String,
    val nombre: String,
    val apellidos: String,
    val correo: String,
    val password: String,
    val curso: String,
    val rol: String
)
