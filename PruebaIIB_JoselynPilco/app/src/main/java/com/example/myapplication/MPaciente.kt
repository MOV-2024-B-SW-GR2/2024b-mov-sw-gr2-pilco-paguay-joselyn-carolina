package com.example.myapplication

class MPaciente (
    val pk: Int,
    val id: Int,
    val nombre: String?,
    val edad: Int,
    val altura: Double,
    val peso: Float,
){
    override fun toString(): String {
        return "$nombre, $edad, $altura"
    }
}