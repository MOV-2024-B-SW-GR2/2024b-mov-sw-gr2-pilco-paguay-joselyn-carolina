package com.example.myapplication

import android.content.Context
import android.widget.ArrayAdapter

class BaseDeDatosMemoriaDoctor {
    companion object {
        var arregloMDoctor = arrayListOf<MDoctor>()
        init {
            arregloMDoctor.add(MDoctor(1, "Dr. Juan Pérez", "Cardiología", 15.5f, true, ))
            arregloMDoctor.add(MDoctor(2, "Dra. Ana Torres", "Neurología", 10.0f, true, ))
            arregloMDoctor.add(MDoctor(3, "Dr. Luis Gómez", "Pediatría", 8.0f, false, ))
            arregloMDoctor.add(MDoctor(4, "Dra. María López", "Dermatología", 12.0f, true, ))
            arregloMDoctor.add(MDoctor(5, "Dr. Carlos Vega", "Traumatología", 20.0f, true, ))
        }
        var arregloMPaciente = arrayListOf<MPaciente>()
        init {
            arregloMPaciente.add(MPaciente(1, 1, "Pedro García", 30, 1.75, 70.5f))
            arregloMPaciente.add(MPaciente(1, 2, "Laura Sánchez", 25, 1.65, 60.0f))
            arregloMPaciente.add(MPaciente(1, 3, "María Hernández", 40, 1.70, 65.0f))
            arregloMPaciente.add(MPaciente(2, 4, "Carlos López", 50, 1.80, 80.0f))
            arregloMPaciente.add(MPaciente(2, 5, "Ana Martínez", 35, 1.68, 58.0f))
        }
        fun obtenerPacientesPorDoctorId(doctorId: Int): ArrayList<MPaciente> {
            return ArrayList(arregloMPaciente.filter { it.pk == doctorId })
        }

    }

}
