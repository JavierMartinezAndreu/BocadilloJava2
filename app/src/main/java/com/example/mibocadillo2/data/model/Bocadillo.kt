package com.example.mibocadillo2.data.model

data class Bocadillo(
    val id: Int,
    val tipo: Boolean,
    val descripcion: String,
    val listaAlergenos: List<String>,
    val precio: Double,
    val opcionDescuento: List<String>,
    val dia: String,
    val caliente: Boolean
)
