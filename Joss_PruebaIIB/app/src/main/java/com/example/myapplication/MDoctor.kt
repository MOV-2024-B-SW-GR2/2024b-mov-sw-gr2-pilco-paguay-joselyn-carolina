package com.example.myapplication

class MDoctor (
    val id: Int,
    val nombre: String?,
    val especialidad: String?,
    val anosDeExperiencia: Float,
    val certificadoActivo: Boolean,
    val pacientes: MutableList<MPaciente> = mutableListOf()
){
    override fun toString(): String {
        return "$nombre"
    }
}