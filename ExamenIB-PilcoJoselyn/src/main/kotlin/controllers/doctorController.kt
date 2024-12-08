package controllers
import models.Doctor
import models.Paciente
import Persistencia.DoctorRepository

class DoctorController(private val repository: DoctorRepository) {

    // Convertimos la lista a mutableListOf para poder agregar y remover doctores
    private val doctores = mutableListOf<Doctor>().apply { addAll(repository.getAllDoctores()) }

    // Metodo para agregar un doctor
    fun agregarDoctor(doctor: Doctor) {
        doctores.add(doctor)  // Agrega el doctor a la lista mutable
        repository.saveDoctores(doctores)
    }

    // Metodo para eliminar un doctor
    fun eliminarDoctor(id: Int): Boolean {
        val doctor = doctores.find { it.id == id }  // Buscar el doctor por ID
        return if (doctor != null) {
            doctores.remove(doctor)  // Elimina el doctor de la lista mutable
            repository.saveDoctores(doctores)  // Guarda los cambios
            true
        } else {
            false
        }
    }

    // Metodo para obtener un doctor por su ID
    fun obtenerDoctorPorId(id: Int): Doctor? {
        return doctores.find { it.id == id }  // Busca y retorna el doctor
    }

    // Metodo para obtener todos los doctores
    fun obtenerTodosLosDoctores(): List<Doctor> = doctores  // Retorna la lista mutable de doctores

    // Metodo para agregar un paciente a un doctor
    fun agregarPacienteAlDoctor(idDoctor: Int, paciente: Paciente): Boolean {
        val doctor = doctores.find { it.id == idDoctor }  // Busca al doctor
        return if (doctor != null) {
            doctor.agregarPaciente(paciente)  // Agrega el paciente al doctor
            repository.saveDoctores(doctores)  // Guarda los cambios
            true
        } else {
            false
        }
    }

    // Metodo para actualizar un paciente de un doctor
    fun actualizarPacienteDeDoctor(idDoctor: Int, paciente: Paciente): Boolean {
        val doctor = doctores.find { it.id == idDoctor }  // Busca al doctor
        return if (doctor != null) {
            val result = doctor.actualizarPaciente(paciente)  // Actualiza el paciente
            if (result) {
                repository.saveDoctores(doctores)  // Guarda los cambios
            }
            result
        } else {
            false
        }
    }

    // Metodo para eliminar un paciente de un doctor
    fun eliminarPacienteDeDoctor(idDoctor: Int, idPaciente: Int): Boolean {
        val doctor = doctores.find { it.id == idDoctor }  // Busca al doctor
        return if (doctor != null) {
            val result = doctor.eliminarPaciente(idPaciente)  // Elimina el paciente
            if (result) {
                repository.saveDoctores(doctores)  // Guarda los cambios
            }
            result
        } else {
            false
        }
    }
}
