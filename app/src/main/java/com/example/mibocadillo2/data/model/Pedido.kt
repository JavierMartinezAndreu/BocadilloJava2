package com.example.mibocadillo2.data.model

data class Pedido(
    var id: String? = null,
    val usuario: String,
    val precio: Double,
    val bocadillo: String,
    val tipo: String,
    val fecha: String,
    val hora: String,
    val retirado: Boolean
)
