package models

data class Paciente(
    val id: Int,
    val nombre: String,
    val edad: Int,
    val altura: Double,
    val peso: Float,
    val historialMedico: List<String>
) {
    companion object {
        fun fromString(data: String): Paciente {
            val parts = data.split(";")
            val id = parts[0].split(":")[1].trim().toInt()
            val nombre = parts[1].split(":")[1].trim()
            val edad = parts[2].split(":")[1].trim().toInt()
            val altura = parts[3].split(":")[1].trim().toDouble()
            val peso = parts[4].split(":")[1].trim().toFloat()
            val historialMedico = parts[5].split(":")[1].trim().split(", ").toList()
            return Paciente(id, nombre, edad, altura, peso, historialMedico)
        }
    }

    fun toFileString(): String {
        return "idPac: $id; nombrePac: $nombre; edad: $edad; altura: $altura; peso: $peso; historialMedico: ${historialMedico.joinToString(", ")}"
    }
}
