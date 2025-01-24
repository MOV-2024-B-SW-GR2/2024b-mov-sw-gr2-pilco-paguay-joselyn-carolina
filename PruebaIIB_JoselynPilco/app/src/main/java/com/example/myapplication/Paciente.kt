package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.view.MenuItem
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.ListView
import android.widget.Spinner
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar

class Paciente : AppCompatActivity() {
    private lateinit var adaptador: ArrayAdapter<MPaciente>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_paciente)
        val listView = findViewById<ListView>(R.id.lv_list_view_pacientes)
        val pk = intent.getIntExtra("pk", 0)
        val pacientesDoctor1 = BaseDeDatosMemoriaDoctor.obtenerPacientesPorDoctorId(pk+1)
        adaptador = ArrayAdapter(
            this,
            android.R.layout.simple_list_item_1,
            pacientesDoctor1
        )
        listView.adapter = adaptador
        adaptador.notifyDataSetChanged()

        val botonAnadirListView = findViewById<Button>(R.id.btn_crear_paciente)
        botonAnadirListView.setOnClickListener { abrirDialogoCrearPaciente(adaptador, pk+1) }

        registerForContextMenu(listView)
    }
    override fun onContextItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.mi_editar_paciente -> {
                //abrirDialogoEditarDoctor()
                true
            }
            R.id.mi_eliminar_doctor-> {
                //abrirDialogoEliminarDoctor()
                true
            }
            else -> super.onContextItemSelected(item)
        }
    }
    private fun mostrarSnackbar(texto: String) {
        Snackbar.make(
            findViewById(R.id.cl_doctores),
            texto,
            Snackbar.LENGTH_SHORT
        ).show()
    }

    private fun abrirDialogoCrearPaciente(adaptador: ArrayAdapter<MPaciente>, pkey: Int) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Crear Nuevo Paciente")

        // Layout para los campos de entrada
        val layout = LinearLayout(this)
        layout.orientation = LinearLayout.VERTICAL
        layout.setPadding(50, 40, 50, 10)

        // Campo de entrada para el nombre del paciente
        val inputNombre = EditText(this)
        inputNombre.hint = "Nombre del paciente"
        inputNombre.inputType = InputType.TYPE_CLASS_TEXT
        layout.addView(inputNombre)

        // Campo de entrada para la edad
        val inputEdad = EditText(this)
        inputEdad.hint = "Edad del paciente"
        inputEdad.inputType = InputType.TYPE_CLASS_NUMBER
        layout.addView(inputEdad)

        // Campo de entrada para la altura
        val inputAltura = EditText(this)
        inputAltura.hint = "Altura (ejemplo: 1.75)"
        inputAltura.inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL
        layout.addView(inputAltura)

        // Campo de entrada para el peso
        val inputPeso = EditText(this)
        inputPeso.hint = "Peso (ejemplo: 70.5)"
        inputPeso.inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL
        layout.addView(inputPeso)

        // Botón de aceptar
        builder.setPositiveButton("Crear") { _, _ ->
            val nombre = inputNombre.text.toString()
            val edad = inputEdad.text.toString().toIntOrNull() ?: 0
            val altura = inputAltura.text.toString().toDoubleOrNull() ?: 0.0
            val peso = inputPeso.text.toString().toFloatOrNull() ?: 0.0f


            if (nombre.isNotBlank()) {
                val nuevoId = if (BaseDeDatosMemoriaDoctor.arregloMPaciente.isNotEmpty())
                    BaseDeDatosMemoriaDoctor.arregloMPaciente.maxOf { it.id } + 1 else 1

                val nuevoPaciente = MPaciente(
                    pk = pkey,
                    id = nuevoId,
                    nombre = nombre,
                    edad = edad,
                    altura = altura,
                    peso = peso
                )

                // Agregar el paciente al arreglo global
                BaseDeDatosMemoriaDoctor.arregloMPaciente.add(nuevoPaciente)

                // Notificar al adaptador que los datos han cambiado
                adaptador.add(nuevoPaciente)
                adaptador.notifyDataSetChanged()

                mostrarSnackbar("Paciente creado exitosamente")
            } else {
                mostrarSnackbar("Debe llenar todos los campos obligatorios")
            }
        }

        // Botón de cancelar
        builder.setNegativeButton("Cancelar", null)

        // Mostrar el diálogo
        val dialogo = builder.create()
        dialogo.show()
    }

}