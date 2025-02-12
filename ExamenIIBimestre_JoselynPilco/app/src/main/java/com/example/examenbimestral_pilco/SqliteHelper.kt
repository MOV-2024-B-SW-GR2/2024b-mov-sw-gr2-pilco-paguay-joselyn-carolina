package com.example.examenbimestral_pilco

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.myapplication.MDoctor
import com.example.myapplication.MPaciente
import com.google.android.gms.maps.model.LatLng

class SqliteHelper(contexto: Context?) : SQLiteOpenHelper(
    contexto,
    "moviles",
    null,
    1
) {
    override fun onCreate(db: SQLiteDatabase?) {
        val scriptSQLCrearTablaDoctor =
            """
                CREATE TABLE Doctor(
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    nombre VARCHAR(25),
                    especialidad VARCHAR(25),
                    aniosExperiencia FLOAT(2),
                    certificadoActivo BOOLEAN,
                    latitud FLOAT,
                    longitud FLOAT
                )
            """.trimIndent()

        val scriptSQLCrearTablaPaciente =
            """
                CREATE TABLE Paciente(
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    nombre VARCHAR(50),
                    edad INTEGER,
                    altura FLOAT(2),
                    sexo VARCHAR(10),
                    id_doctor INTEGER,
                    FOREIGN KEY (id_doctor) REFERENCES Doctor(id) ON DELETE CASCADE
                )
            """.trimIndent()

        db?.execSQL(scriptSQLCrearTablaDoctor)
        db?.execSQL(scriptSQLCrearTablaPaciente)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {}

    fun crearDoctor(
        nombre: String,
        especialidad: String,
        aniosExperiencia: Float,
        certificadoActivo: Boolean,
        ubicacion: LatLng
    ): Boolean {
        val baseDatosEscritura = writableDatabase
        val valoresGuardar = ContentValues().apply {
            put("nombre", nombre)
            put("especialidad", especialidad)
            put("aniosExperiencia", aniosExperiencia)
            put("certificadoActivo", if (certificadoActivo) 1 else 0)
            put("latitud", ubicacion.latitude)
            put("longitud", ubicacion.longitude)
        }
        val resultadoGuardar = baseDatosEscritura.insert("Doctor", null, valoresGuardar)
        baseDatosEscritura.close()
        return resultadoGuardar != -1L
    }

    fun obtenerDoctores(): List<MDoctor> {
        val baseDatosLectura = readableDatabase
        val listaDoctores = mutableListOf<MDoctor>()
        val consulta = "SELECT * FROM Doctor"
        val resultado = baseDatosLectura.rawQuery(consulta, null)
        while (resultado.moveToNext()) {
            listaDoctores.add(
                MDoctor(
                    resultado.getInt(0), // id
                    resultado.getString(1), // nombre
                    resultado.getString(2), // especialidad
                    resultado.getFloat(3), // aniosExperiencia
                    resultado.getInt(4) == 1, // certificadoActivo
                    LatLng(resultado.getDouble(5), resultado.getDouble(6)) // ubicacion
                )
            )
        }
        resultado.close()
        baseDatosLectura.close()
        return listaDoctores
    }

    fun eliminarDoctor(id: Int): Boolean {
        val baseDatosEscritura = writableDatabase
        val resultadoEliminar = baseDatosEscritura.delete("Doctor", "id=?", arrayOf(id.toString()))
        baseDatosEscritura.close()
        return resultadoEliminar != -1
    }

    fun actualizarDoctor(id: Int, nombre: String, especialidad: String, aniosExperiencia: Float, certificadoActivo: Boolean): Boolean {
        val baseDatosEscritura = writableDatabase
        val valoresActualizar = ContentValues().apply {
            put("nombre", nombre)
            put("especialidad", especialidad)
            put("aniosExperiencia", aniosExperiencia)
            put("certificadoActivo", certificadoActivo)
        }
        val resultadoActualizar = baseDatosEscritura.update("Doctor", valoresActualizar, "id=?", arrayOf(id.toString()))
        baseDatosEscritura.close()
        return resultadoActualizar > 0
    }


    fun crearPaciente(nombre: String, edad: Int, altura: Float, sexo: String, idDoctor: Int): Boolean {
        val baseDatosEscritura = writableDatabase
        val valoresGuardar = ContentValues().apply {
            put("nombre", nombre)
            put("edad", edad)
            put("altura", altura)
            put("sexo", sexo)
            put("id_doctor", idDoctor)
        }
        val resultadoGuardar = baseDatosEscritura.insert("Paciente", null, valoresGuardar)
        baseDatosEscritura.close()
        return resultadoGuardar != -1L
    }

    fun obtenerPacientesPorDoctor(idDoctor: Int): List<MPaciente> {
        val baseDatosLectura = readableDatabase
        val listaPacientes = mutableListOf<MPaciente>()
        val consulta = "SELECT * FROM Paciente WHERE id_doctor = ?"
        val resultado = baseDatosLectura.rawQuery(consulta, arrayOf(idDoctor.toString()))
        while (resultado.moveToNext()) {
            listaPacientes.add(
                MPaciente(
                    resultado.getInt(0), // id
                    resultado.getString(1), // nombre
                    resultado.getInt(2), // edad
                    resultado.getDouble(3), // altura
                    resultado.getString(4), // sexo
                )
            )
        }
        resultado.close()
        baseDatosLectura.close()
        return listaPacientes
    }

    fun eliminarPaciente(id: Int): Boolean {
        val baseDatosEscritura = writableDatabase
        val resultadoEliminar = baseDatosEscritura.delete("Paciente", "id=?", arrayOf(id.toString()))
        baseDatosEscritura.close()
        return resultadoEliminar != -1
    }

    fun actualizarPaciente(id: Int, nombre: String, edad: Int, altura: Float, sexo: String): Boolean {
        val baseDatosEscritura = writableDatabase
        val valoresAActualizar = ContentValues().apply {
            put("nombre", nombre)
            put("edad", edad)
            put("altura", altura)
            put("sexo", sexo)
        }
        val resultadoActualizar = baseDatosEscritura.update("Paciente", valoresAActualizar, "id=?", arrayOf(id.toString()))
        baseDatosEscritura.close()
        return resultadoActualizar > 0
    }
}
