package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.view.ContextMenu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.ListView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.snackbar.Snackbar

class Doctor : AppCompatActivity() {

    private lateinit var adaptador: ArrayAdapter<MDoctor>
    private var posicionItemSeleccionado = -1 // Variable global para la posición seleccionada

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_doctor)

        val listView = findViewById<ListView>(R.id.lv_list_view_doctores)
        adaptador = ArrayAdapter(
            this,
            android.R.layout.simple_list_item_1,
            BaseDeDatosMemoriaDoctor.arregloMDoctor
        )
        listView.adapter = adaptador
        adaptador.notifyDataSetChanged()

        val botonAnadirListView = findViewById<Button>(R.id.btn_crear_doctor)
        botonAnadirListView.setOnClickListener { abrirDialogoCrearDoctor(adaptador) }
        registerForContextMenu(listView)
    }

    override fun onCreateContextMenu(
        menu: ContextMenu?,
        v: View?,
        menuInfo: ContextMenu.ContextMenuInfo?
    ) {
        super.onCreateContextMenu(menu, v, menuInfo)
        menuInflater.inflate(R.menu.menu_doctor, menu)
        val info = menuInfo as AdapterView.AdapterContextMenuInfo
        posicionItemSeleccionado = info.position
    }

    override fun onContextItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.mi_editar_doctor -> {
                abrirDialogoEditarDoctor()
                true
            }
            R.id.mi_eliminar_doctor -> {
                abrirDialogoEliminarDoctor()
                true
            }
            R.id.mi_verPacientes_doctor-> {
                val intentExplicito = Intent(this, Paciente::class.java)
                intentExplicito.putExtra("pk", posicionItemSeleccionado)
                startActivity(intentExplicito)
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

    private fun abrirDialogoEditarDoctor() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Editar nombre del doctor")

        val input = EditText(this)
        input.hint = "Ingrese el nuevo nombre"
        input.inputType = InputType.TYPE_CLASS_TEXT
        builder.setView(input)

        builder.setPositiveButton("Aceptar") { _, _ ->
            val nuevoNombre = input.text.toString()
            if (nuevoNombre.isNotBlank()) {
                editarNombreDoctor(posicionItemSeleccionado, nuevoNombre)
            } else {
                mostrarSnackbar("Debe ingresar un nombre válido")
            }
        }

        builder.setNegativeButton("Cancelar", null)
        builder.create().show()
    }

    private fun editarNombreDoctor(posicion: Int, nuevoNombre: String) {
        if (posicion in 0 until BaseDeDatosMemoriaDoctor.arregloMDoctor.size) {
            BaseDeDatosMemoriaDoctor.arregloMDoctor[posicion].nombre = nuevoNombre
            adaptador.notifyDataSetChanged()
            mostrarSnackbar("Nombre actualizado a: $nuevoNombre")
        } else {
            mostrarSnackbar("Posición inválida")
        }
    }

    private fun abrirDialogoEliminarDoctor() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Eliminar doctor")
        builder.setMessage("¿Está seguro de que desea eliminar este doctor?")
        builder.setPositiveButton("Aceptar") { _, _ ->
            eliminarDoctor(posicionItemSeleccionado)
        }
        builder.setNegativeButton("Cancelar", null)
        builder.create().show()
    }

    private fun eliminarDoctor(posicion: Int) {
        if (posicion in 0 until BaseDeDatosMemoriaDoctor.arregloMDoctor.size) {
            BaseDeDatosMemoriaDoctor.arregloMDoctor.removeAt(posicion)
            adaptador.notifyDataSetChanged()
            mostrarSnackbar("Doctor eliminado")
        } else {
            mostrarSnackbar("Posición inválida")
        }
    }

    private fun abrirDialogoCrearDoctor(adaptador: ArrayAdapter<MDoctor>) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Crear Nuevo Doctor")

        // Layout para los campos de entrada
        val layout = LinearLayout(this)
        layout.orientation = LinearLayout.VERTICAL
        layout.setPadding(50, 40, 50, 10)

        // Campo de entrada para el nombre
        val inputNombre = EditText(this)
        inputNombre.hint = "Nombre del doctor"
        inputNombre.inputType = InputType.TYPE_CLASS_TEXT
        layout.addView(inputNombre)

        // Campo de entrada para la especialidad
        val inputEspecialidad = EditText(this)
        inputEspecialidad.hint = "Especialidad"
        inputEspecialidad.inputType = InputType.TYPE_CLASS_TEXT
        layout.addView(inputEspecialidad)

        // Campo de entrada para los años de experiencia
        val inputExperiencia = EditText(this)
        inputExperiencia.hint = "Años de experiencia (ejemplo: 5.0)"
        inputExperiencia.inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL
        layout.addView(inputExperiencia)

        // Checkbox para indicar si el certificado está activo
        val checkCertificado = CheckBox(this)
        checkCertificado.text = "Certificado Activo"
        layout.addView(checkCertificado)

        builder.setView(layout)

        // Botón de aceptar
        builder.setPositiveButton("Crear") { _, _ ->
            val nombre = inputNombre.text.toString()
            val especialidad = inputEspecialidad.text.toString()
            val anosExperiencia = inputExperiencia.text.toString().toFloatOrNull() ?: 0.0f
            val certificadoActivo = checkCertificado.isChecked

            if (nombre.isNotBlank() && especialidad.isNotBlank()) {
                val nuevoId = if (BaseDeDatosMemoriaDoctor.arregloMDoctor.isNotEmpty())
                    BaseDeDatosMemoriaDoctor.arregloMDoctor.maxOf { it.id } + 1 else 1

                val nuevoDoctor = MDoctor(
                    id = nuevoId,
                    nombre = nombre,
                    especialidad = especialidad,
                    anosDeExperiencia = anosExperiencia,
                    certificadoActivo = certificadoActivo
                )
                BaseDeDatosMemoriaDoctor.arregloMDoctor.add(nuevoDoctor)
                adaptador.notifyDataSetChanged()
                mostrarSnackbar("Doctor creado exitosamente")
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