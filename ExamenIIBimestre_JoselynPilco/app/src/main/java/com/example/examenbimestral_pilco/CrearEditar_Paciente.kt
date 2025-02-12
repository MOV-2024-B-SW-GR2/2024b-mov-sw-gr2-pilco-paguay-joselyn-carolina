package com.example.examenbimestral_pilco

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class CrearEditar_Paciente : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_crear_editar_paciente)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val pk = intent.getIntExtra("pk", 1)
        val btnAccionPaciente = findViewById<Button>(R.id.btn_accionPaciente)
        val btnatras = findViewById<Button>(R.id.btn_atras_crear_paciente)
        val nombrePaciente = findViewById<EditText>(R.id.input_nombrePaciente)
        val edadPaciente = findViewById<EditText>(R.id.input_edadPaciente)
        val alturaPaciente = findViewById<EditText>(R.id.input_alturaPaciente)
        val sexoPaciente = findViewById<EditText>(R.id.input_sexoPaciente)
        btnAccionPaciente.setOnClickListener {
            val nombre = nombrePaciente.text.toString().trim()
            val edad = edadPaciente.text.toString().toIntOrNull()
            val altura = alturaPaciente.text.toString().toFloatOrNull()
            val sexo = sexoPaciente.text.toString().trim()
            if (nombre.isEmpty() || edad == null || altura == null || sexo.isEmpty()) {
                Toast.makeText(
                    this,
                    "Por favor, ingrese todos los datos correctamente",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }
            BaseDeDatos.tablas?.crearPaciente(nombre, edad, altura, sexo, pk)
            Toast.makeText(this, "Paciente agregado exitosamente", Toast.LENGTH_SHORT).show()
            val intentExplicito = Intent(this, Paciente::class.java)
            intentExplicito.putExtra("pk", pk)
            startActivity(intentExplicito)
            finish()
        }
        btnatras.setOnClickListener {
            val intentExplicito = Intent(this, Paciente::class.java)
            intentExplicito.putExtra("pk", pk)
            startActivity(intentExplicito)
            finish()
        }
    }
}
