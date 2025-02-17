package com.example.mibocadillo2.data.model

data class Usuario(
    val id: String = "",
    val nombre: String = "",
    val apellidos: String = "",
    val correo: String = "",
    val password: String = "",
    val curso: String = "",
    val alergias: List<String> = listOf(), // Lista de IDs de alérgenos
    val rol: String = "", // "alumno", "cocina", "administrador"
    val estadoBaja: Boolean = false,
    val motivoBaja: String? = null,
    val fechaBaja: String? = null // Formato: "yyyy-MM-dd"
)
