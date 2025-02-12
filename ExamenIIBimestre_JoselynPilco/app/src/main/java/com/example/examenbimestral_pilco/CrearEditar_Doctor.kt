package com.example.examenbimestral_pilco

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Switch
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.gms.maps.model.LatLng

class CrearEditar_Doctor : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        if (BaseDeDatos.tablas == null) {
            BaseDeDatos.tablas = SqliteHelper(this)
        }
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        if (BaseDeDatos.tablas == null) {
            BaseDeDatos.tablas = SqliteHelper(this)
        }
        setContentView(R.layout.activity_crear_editar_doctor)
        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed(){}
        }
        onBackPressedDispatcher.addCallback(this, callback)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val operacion = intent.getIntExtra("Operacion", 0)
        val pk =  intent.getIntExtra("pk", 1)
        val btnAccionDoctor = findViewById<Button>(R.id.btn_accionDoctor)
        val textAccionDoctor = findViewById<TextView>(R.id.accionPaciente)
        val nombreDoctor = findViewById<EditText>(R.id.input_nombreDoctor)
        val especialidadDoctor = findViewById<EditText>(R.id.input_especialidadDoctor)
        val aniosExpDoctor = findViewById<EditText>(R.id.input_aniosExpDoctor)
        val certificadoActivoDoctor = findViewById<Switch>(R.id.switch_certificadoActivoDoctor)
        val latlong = findViewById<EditText>(R.id.input_direccionDoctor)

        if (operacion == 1) {
            btnAccionDoctor.text = "Crear"
            textAccionDoctor.text = "Ingrese doctor"
            btnAccionDoctor.setOnClickListener {
                val nombre = nombreDoctor.text.toString().trim()
                val especialidad = especialidadDoctor.text.toString().trim()
                val aniosExp = aniosExpDoctor.text.toString().toFloatOrNull() ?: 0f
                val certificadoActivo = certificadoActivoDoctor.isChecked
                val latlongTexto = latlong.text.toString().trim()
                val partes = latlongTexto.split(",") // Divide el string por la coma

                if (partes.size == 2) {
                    val latitud = partes[0].toDoubleOrNull()
                    val longitud = partes[1].toDoubleOrNull()

                    if (latitud != null && longitud != null) {
                        val direccion = LatLng(latitud, longitud) // Crear objeto LatLng
                        BaseDeDatos.tablas?.crearDoctor(
                            nombre,
                            especialidad,
                            aniosExp,
                            certificadoActivo,
                            direccion
                        )
                        Toast.makeText(this, "Doctor agregado exitosamente", Toast.LENGTH_SHORT)
                            .show()
                        startActivity(Intent(this, Doctor::class.java))
                    } else {
                        Toast.makeText(this, "Formato de dirección inválido", Toast.LENGTH_SHORT)
                            .show()
                    }
                } else {
                    Toast.makeText(
                        this,
                        "Ingrese la dirección en formato: latitud, longitud",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        } else if (operacion == 2) {
            val doctor = BaseDeDatos.tablas!!.obtenerDoctores()[pk]
            nombreDoctor.setText(doctor.nombre)
            especialidadDoctor.setText(doctor.especialidad)
            aniosExpDoctor.setText(doctor.aniosDeExperiencia.toString())
            btnAccionDoctor.text = "Editar"
            textAccionDoctor.text = "Edite al doctor: ${doctor.nombre}"
            btnAccionDoctor.setOnClickListener {
                val nombre = nombreDoctor.text.toString().trim()
                val especialidad = especialidadDoctor.text.toString().trim()
                val aniosExp = aniosExpDoctor.text.toString().toFloatOrNull() ?: 0f
                BaseDeDatos.tablas?.actualizarDoctor(
                    doctor.id, nombre, especialidad, aniosExp, certificadoActivoDoctor.isChecked
                )
                Toast.makeText(this, "Doctor editado exitosamente", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this, Doctor::class.java))
            }
        }
    }
}