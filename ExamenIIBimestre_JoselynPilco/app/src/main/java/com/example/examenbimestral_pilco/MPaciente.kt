package com.example.myapplication

class MPaciente (
    val id: Int,
    var nombre: String?,
    var edad: Int,
    var altura: Double,
    var sexo: String?
){
    override fun toString(): String {
        return "👤 Nombre: ${nombre ?: "Desconocido"}" +
                "\n🎂 Edad: $edad años" +
                "\n📏 Altura: ${String.format("%.2f", altura)} m" +
                "\n🚻 Sexo: ${sexo ?: "No especificado"}"
    }

}