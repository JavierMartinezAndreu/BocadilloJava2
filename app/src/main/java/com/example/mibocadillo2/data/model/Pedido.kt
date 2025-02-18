package com.example.mibocadillo2.data.model

data class Pedido(
    val id: Int? = null,
    val usuario: String,
    val precio: Double,
    val bocadillo: String,
    val fecha: String,
    val hora: String,
    val retirado: Boolean
)
