package com.example.mibocadillo2.data.model

data class Pedido(
    val id: String = "",
    val usuarioId: String = "",
    val bocadilloId: String = "",
    val fecha: String = "", // Formato: "yyyy-MM-dd"
    val hora: String = "",  // Formato: "HH:mm"
    val estado: String = "pendiente" // "pendiente" o "retirado"
)
