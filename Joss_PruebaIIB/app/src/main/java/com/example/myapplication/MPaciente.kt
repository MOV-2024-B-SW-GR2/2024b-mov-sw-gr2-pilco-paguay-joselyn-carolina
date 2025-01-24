package com.example.myapplication

class MPaciente (
    val id: Int,
    val nombre: String?,
    val edad: Int,
    val altura: Double,
    val peso: Float,
    val historialMedico: List<String>
){
    override fun toString(): String {
        return "$nombre"
    }
}