package Persistencia
import models.Doctor
import models.Paciente
import java.io.File

class DoctorRepository(private val filePath: String) {

    fun getAllDoctores(): List<Doctor> {
        val doctores = mutableListOf<Doctor>()
        val file = File(filePath)

        if (file.exists()) {
            val lines = file.readLines()
            var currentDoctor: Doctor? = null

            for (line in lines) {
                val trimmedLine = line.trim()
                val parts = trimmedLine.split(";")
                when {
                    parts.size == 5 -> {
                        currentDoctor = Doctor.fromString(trimmedLine)
                        doctores.add(currentDoctor!!)
                    }
                    parts.size >= 6 && currentDoctor != null -> {
                        val paciente = Paciente.fromString(trimmedLine)
                        currentDoctor.agregarPaciente(paciente)
                    }
                }
            }
        }
        return doctores
    }
    fun saveDoctores(doctores: List<Doctor>) {
        val file = File(filePath)

        // Si el archivo no existe, lo creamos
        if (!file.exists()) {
            file.createNewFile()
        }

        val lines = mutableListOf<String>()

        for (doctor in doctores) {
            // Guardar información del doctor
            lines.add(doctor.toFileString())
            // Guardar pacientes asociados
            for (paciente in doctor.pacientes) {
                lines.add("    ${paciente.toFileString()}")
            }
        }

        // Escribir las líneas en el archivo
        file.writeText(lines.joinToString("\n"))
    }

}
