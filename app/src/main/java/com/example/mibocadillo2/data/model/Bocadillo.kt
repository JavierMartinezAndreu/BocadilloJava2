package com.example.mibocadillo2.data.model

data class Bocadillo(
    var id: String = "",
    val tipo: String,
    val nombre: String,
    val ingredientes: List<String>,
    val alergenos: List<String>,
    val precio: Double,
    val dia: String
)
