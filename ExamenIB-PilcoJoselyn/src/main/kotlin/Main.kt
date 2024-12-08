import javax.swing.JOptionPane
import models.Doctor
import models.Paciente
import Persistencia.DoctorRepository
import controllers.DoctorController

fun showMenu(): Int {
    val menu = """
        --- Menú CRUD de Doctores y Pacientes --
        1. Crear Doctor
        2. Ver Doctores
        3. Editar Doctor
        4. Eliminar Doctor
        5. Agregar Paciente a Doctor
        6. Actualizar Paciente de Doctor
        7. Eliminar Paciente de Doctor
        8. Ver Pacientes por Doctor
        9. Salir
    """.trimIndent()
    val input = JOptionPane.showInputDialog(menu)
    return input?.toIntOrNull() ?: -1
}

fun main() {
    val filePath = "doctores_pacientes.txt"
    val repository = DoctorRepository(filePath)
    val controller = DoctorController(repository)

    while (true) {
        try {
            val option = showMenu()
            when (option) {
                1 -> crearDoctor(controller)
                2 -> verDoctores(controller)
                3 -> editarDoctor(controller)
                4 -> eliminarDoctor(controller)
                5 -> agregarPaciente(controller)
                6 -> actualizarPaciente(controller)
                7 -> eliminarPaciente(controller)
                8 -> verPacientesPorDoctor(controller)
                9 -> {
                    JOptionPane.showMessageDialog(null, "Saliendo del programa...")
                    return
                }
                else -> JOptionPane.showMessageDialog(null, "Opción no válida. Por favor, ingresa una opción correcta.")
            }
        } catch (e: Exception) {
            JOptionPane.showMessageDialog(null, "Error inesperado: ${e.message}. Por favor, intenta nuevamente.")
        }
    }
}

fun crearDoctor(controller: DoctorController) {
    try {
        val nombre = obtenerInputValido("Nombre del Doctor:", "El nombre no puede estar vacío.") { it.isNotBlank() }
        val especialidad = obtenerInputValido("Especialidad del Doctor:", "La especialidad no puede estar vacía.") { it.isNotBlank() }
        val anosDeExperiencia = obtenerInputValido("Años de experiencia del Doctor:", "Debe ser un número positivo.") {
            it.toFloatOrNull()?.let { it > 0 } == true
        }.toFloat()
        val certificadoActivo = obtenerInputValido("¿Certificado activo? (true/false):", "Debe ser 'true' o 'false'.") {
            it.lowercase() in listOf("true", "false")
        }.toBooleanStrict()

        val idDoctor = controller.obtenerTodosLosDoctores().size + 1
        val doctor = Doctor(idDoctor, nombre, especialidad, anosDeExperiencia, certificadoActivo)
        controller.agregarDoctor(doctor)
        JOptionPane.showMessageDialog(null, "Doctor creado exitosamente.")
    } catch (e: Exception) {
        JOptionPane.showMessageDialog(null, "Error inesperado: ${e.message}")
    }
}

fun obtenerInputValido(mensaje: String, mensajeError: String, validacion: (String) -> Boolean): String {
    var input: String?
    do {
        input = JOptionPane.showInputDialog(mensaje)
        if (input == null || !validacion(input)) {
            JOptionPane.showMessageDialog(null, mensajeError, "Error", JOptionPane.ERROR_MESSAGE)
        }
    } while (input == null || !validacion(input))
    return input
}


fun eliminarDoctor(controller: DoctorController) {
    try {
        val id = JOptionPane.showInputDialog("Ingresa el ID del Doctor a eliminar:")?.toIntOrNull()
            ?: throw IllegalArgumentException("ID inválido.")
        if (controller.eliminarDoctor(id)) {
            JOptionPane.showMessageDialog(null, "Doctor eliminado exitosamente.")
        } else {
            JOptionPane.showMessageDialog(null, "Doctor no encontrado.")
        }
    } catch (e: Exception) {
        JOptionPane.showMessageDialog(null, "Error: ${e.message}. Por favor, verifica los datos ingresados.")
    }
}

fun verDoctores(controller: DoctorController) {
    try {
        val doctores = controller.obtenerTodosLosDoctores()
        if (doctores.isEmpty()) {
            JOptionPane.showMessageDialog(null, "No hay doctores registrados.")
        } else {
            val message = doctores.joinToString("\n") {
                "ID: ${it.id} | Nombre: ${it.nombre} | Especialidad: ${it.especialidad} | Años de Experiencia: ${it.anosDeExperiencia} | Certificado Activo: ${it.certificadoActivo}"
            }
            JOptionPane.showMessageDialog(null, message, "Lista de Doctores", JOptionPane.INFORMATION_MESSAGE)
        }
    } catch (e: Exception) {
        JOptionPane.showMessageDialog(null, "Error al mostrar doctores: ${e.message}")
    }
}

fun editarDoctor(controller: DoctorController) {
    try {
        val id = obtenerInputValido(
            mensaje = "Ingresa el ID del Doctor a editar:",
            mensajeError = "ID inválido. Debe ser un número entero válido.",
            validacion = { it.toIntOrNull() != null }
        ).toInt()

        val doctor = controller.obtenerDoctorPorId(id)
            ?: throw IllegalArgumentException("Doctor con ID $id no encontrado.")

        val nuevoNombre = obtenerInputValido(
            mensaje = "Nuevo nombre del Doctor (Actual: ${doctor.nombre}):",
            mensajeError = "El nombre no puede estar vacío.",
            validacion = { it.isNotBlank() }
        )

        val nuevaEspecialidad = obtenerInputValido(
            mensaje = "Nueva especialidad del Doctor (Actual: ${doctor.especialidad}):",
            mensajeError = "La especialidad no puede estar vacía.",
            validacion = { it.isNotBlank() }
        )

        val nuevosAnosDeExperiencia = obtenerInputValido(
            mensaje = "Nuevos años de experiencia del Doctor (Actual: ${doctor.anosDeExperiencia}):",
            mensajeError = "Años de experiencia inválidos. Debe ser un número decimal positivo.",
            validacion = { it.toFloatOrNull()?.let { it >= 0 } == true }
        ).toFloat()

        val nuevoCertificadoActivo = obtenerInputValido(
            mensaje = "¿Certificado activo? (true/false) (Actual: ${doctor.certificadoActivo}):",
            mensajeError = "El valor debe ser 'true' o 'false'.",
            validacion = { it.lowercase() in listOf("true", "false") }
        ).toBooleanStrict()

        val nuevoDoctor = doctor.copy(
            nombre = nuevoNombre,
            especialidad = nuevaEspecialidad,
            anosDeExperiencia = nuevosAnosDeExperiencia,
            certificadoActivo = nuevoCertificadoActivo
        )

        controller.eliminarDoctor(doctor.id)
        controller.agregarDoctor(nuevoDoctor)
        JOptionPane.showMessageDialog(null, "Doctor editado exitosamente.")
    } catch (e: Exception) {
        JOptionPane.showMessageDialog(null, "Error: ${e.message}. Por favor, corrige los datos.")
    }
}

fun agregarPaciente(controller: DoctorController) {
    try {
        val idDoctor = obtenerInputValido(
            mensaje = "Ingresa el ID del Doctor al que deseas agregar un paciente:",
            mensajeError = "ID de Doctor inválido. Debe ser un número entero válido.",
            validacion = { it.toIntOrNull() != null }
        ).toInt()

        val doctor = controller.obtenerDoctorPorId(idDoctor)
            ?: throw IllegalArgumentException("Doctor con ID $idDoctor no encontrado.")

        val nombre = obtenerInputValido(
            mensaje = "Nombre del Paciente:",
            mensajeError = "El nombre no puede estar vacío.",
            validacion = { it.isNotBlank() }
        )

        val edad = obtenerInputValido(
            mensaje = "Edad del Paciente:",
            mensajeError = "Edad inválida. Debe ser un número entero positivo.",
            validacion = { it.toIntOrNull()?.let { it > 0 } == true }
        ).toInt()

        val altura = obtenerInputValido(
            mensaje = "Altura del Paciente (en metros):",
            mensajeError = "Altura inválida. Debe ser un número decimal positivo.",
            validacion = { it.toDoubleOrNull()?.let { it > 0 } == true }
        ).toDouble()

        val peso = obtenerInputValido(
            mensaje = "Peso del Paciente (en kilogramos):",
            mensajeError = "Peso inválido. Debe ser un número decimal positivo.",
            validacion = { it.toFloatOrNull()?.let { it > 0 } == true }
        ).toFloat()

        val historialMedico = obtenerInputValido(
            mensaje = "Historial Médico (separado por comas):",
            mensajeError = "Historial Médico inválido. No puede estar vacío.",
            validacion = { it.isNotBlank() }
        ).split(", ").map { it.trim() }

        val idPaciente = doctor.pacientes.size + 1
        val paciente = Paciente(idPaciente, nombre, edad, altura, peso, historialMedico)
        controller.agregarPacienteAlDoctor(doctor.id, paciente)

        JOptionPane.showMessageDialog(null, "Paciente agregado exitosamente.")
    } catch (e: Exception) {
        JOptionPane.showMessageDialog(null, "Error: ${e.message}. Por favor, corrige los datos.")
    }
}


fun actualizarPaciente(controller: DoctorController) {
    try {
        val idDoctor = obtenerInputValido(
            mensaje = "Ingresa el ID del Doctor:",
            mensajeError = "ID de Doctor inválido. Debe ser un número entero válido.",
            validacion = { it.toIntOrNull() != null }
        ).toInt()

        val doctor = controller.obtenerDoctorPorId(idDoctor)
            ?: throw IllegalArgumentException("Doctor con ID $idDoctor no encontrado.")

        val idPaciente = obtenerInputValido(
            mensaje = "Ingresa el ID del Paciente a actualizar:",
            mensajeError = "ID de Paciente inválido. Debe ser un número entero válido.",
            validacion = { it.toIntOrNull() != null }
        ).toInt()

        val paciente = doctor.pacientes.find { it.id == idPaciente }
            ?: throw IllegalArgumentException("Paciente con ID $idPaciente no encontrado.")

        val nuevoNombre = obtenerInputValido(
            mensaje = "Nuevo nombre del Paciente (Actual: ${paciente.nombre}):",
            mensajeError = "El nombre no puede estar vacío.",
            validacion = { it.isNotBlank() }
        )

        val nuevaEdad = obtenerInputValido(
            mensaje = "Nueva edad del Paciente (Actual: ${paciente.edad}):",
            mensajeError = "Edad inválida. Debe ser un número entero positivo.",
            validacion = { it.toIntOrNull()?.let { it > 0 } == true }
        ).toInt()

        val nuevaAltura = obtenerInputValido(
            mensaje = "Nueva altura del Paciente (Actual: ${paciente.altura}):",
            mensajeError = "Altura inválida. Debe ser un número decimal positivo.",
            validacion = { it.toDoubleOrNull()?.let { it > 0 } == true }
        ).toDouble()

        val nuevoPeso = obtenerInputValido(
            mensaje = "Nuevo peso del Paciente (Actual: ${paciente.peso}):",
            mensajeError = "Peso inválido. Debe ser un número decimal positivo.",
            validacion = { it.toFloatOrNull()?.let { it > 0 } == true }
        ).toFloat()

        val nuevoHistorialMedico = obtenerInputValido(
            mensaje = "Nuevo historial médico (separado por comas) (Actual: ${paciente.historialMedico.joinToString(", ")}):",
            mensajeError = "El historial médico no puede estar vacío.",
            validacion = { it.isNotBlank() }
        ).split(", ").map { it.trim() }

        val pacienteActualizado = paciente.copy(
            nombre = nuevoNombre,
            edad = nuevaEdad,
            altura = nuevaAltura,
            peso = nuevoPeso,
            historialMedico = nuevoHistorialMedico
        )

        controller.actualizarPacienteDeDoctor(doctor.id, pacienteActualizado)
        JOptionPane.showMessageDialog(null, "Paciente actualizado exitosamente.")
    } catch (e: Exception) {
        JOptionPane.showMessageDialog(null, "Error: ${e.message}. Por favor, corrige los datos.")
    }
}


fun eliminarPaciente(controller: DoctorController) {
    try {
        val idDoctor = JOptionPane.showInputDialog("Ingresa el ID del Doctor:")?.toIntOrNull()
            ?: throw IllegalArgumentException("ID de Doctor inválido.")
        val idPaciente = JOptionPane.showInputDialog("Ingresa el ID del Paciente a eliminar:")?.toIntOrNull()
            ?: throw IllegalArgumentException("ID de Paciente inválido.")

        if (controller.eliminarPacienteDeDoctor(idDoctor, idPaciente)) {
            JOptionPane.showMessageDialog(null, "Paciente eliminado exitosamente.")
        } else {
            JOptionPane.showMessageDialog(null, "No se encontró el paciente.")
        }
    } catch (e: Exception) {
        JOptionPane.showMessageDialog(null, "Error: ${e.message}. Por favor, verifica los datos ingresados.")
    }
}

fun verPacientesPorDoctor(controller: DoctorController) {
    try {
        val idDoctor = JOptionPane.showInputDialog("Ingresa el ID del Doctor:")?.toIntOrNull()
            ?: throw IllegalArgumentException("ID inválido.")
        val doctor = controller.obtenerDoctorPorId(idDoctor) ?: throw IllegalArgumentException("Doctor no encontrado.")
        if (doctor.pacientes.isEmpty()) {
            JOptionPane.showMessageDialog(null, "No hay pacientes registrados para este Doctor.")
        } else {
            val message = doctor.pacientes.joinToString("\n") {
                "ID: ${it.id} | Nombre: ${it.nombre} | Edad: ${it.edad} | Altura: ${it.altura} | Peso: ${it.peso} | Historial Médico: ${it.historialMedico.joinToString(", ")}"
            }
            JOptionPane.showMessageDialog(null, message, "Lista de Pacientes", JOptionPane.INFORMATION_MESSAGE)
        }
    } catch (e: Exception) {
        JOptionPane.showMessageDialog(null, "Error: ${e.message}")
    }
}
