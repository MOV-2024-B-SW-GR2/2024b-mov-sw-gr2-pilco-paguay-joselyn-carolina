package models

data class Doctor(
    val id: Int,
    val nombre: String,
    val especialidad: String,
    val anosDeExperiencia: Float,
    val certificadoActivo: Boolean,
    val pacientes: MutableList<Paciente> = mutableListOf()

) {
    companion object {
        fun fromString(data: String): Doctor {
            val parts = data.split(";")
            val id = parts[0].split(":")[1].trim().toInt()
            val nombre = parts[1].split(":")[1].trim()
            val especialidad = parts[2].split(":")[1].trim()
            val anosDeExperiencia = parts[3].split(":")[1].trim().toFloat()
            val certificadoActivo = parts[4].split(":")[1].trim().toBoolean()
            return Doctor(id, nombre, especialidad, anosDeExperiencia, certificadoActivo)
        }
    }

    fun toFileString(): String {
        return "idDoc: $id; nombreDoc: $nombre; especialidad: $especialidad; anosDeExperiencia: $anosDeExperiencia; certificadoActivo: $certificadoActivo"
    }

    fun agregarPaciente(paciente: Paciente) {
        pacientes.add(paciente)
    }

    fun eliminarPaciente(id: Int): Boolean {
        val paciente = pacientes.find { it.id == id }
        return if (paciente != null) {
            pacientes.remove(paciente)
            true
        } else {
            false
        }
    }

    fun actualizarPaciente(paciente: Paciente): Boolean {
        val index = pacientes.indexOfFirst { it.id == paciente.id }
        return if (index >= 0) {
            pacientes[index] = paciente
            true
        } else {
            false
        }
    }
}
