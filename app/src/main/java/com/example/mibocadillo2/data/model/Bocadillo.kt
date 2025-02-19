package com.example.mibocadillo2.data.model

data class Bocadillo(
    val id: Int,
    val tipo: String,  // "frío" o "caliente"
    val nombre: String,
    val ingredientes: List<String>,
    val alergenos: List<String>,
    val precio: Double,
    val dia: String // Día de la semana en el que se ofrece
)
