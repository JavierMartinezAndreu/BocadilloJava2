package com.example.mibocadillo2.data.model

data class Bocadillo(
    val id: String = "",
    val nombre: String = "",
    val tipo: String = "", // "caliente" o "frio"
    val descripcion: String = "",
    val alergenos: List<String> = listOf(), // Lista de IDs de alérgenos
    val coste: Double = 0.0
)
