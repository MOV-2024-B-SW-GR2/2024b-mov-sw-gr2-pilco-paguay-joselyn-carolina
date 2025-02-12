package com.example.examenbimestral_pilco

import android.content.Intent
import android.view.View
import android.widget.*
import androidx.activity.OnBackPressedCallback
import androidx.activity.enableEdgeToEdge
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.app.AlertDialog
import android.os.Bundle
import android.view.ContextMenu
import android.view.MenuItem
import android.text.InputType
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplication.MPaciente

class Paciente : AppCompatActivity() {
    private var posicionItemSeleccionado = -1
    private var pk_doc = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        if (BaseDeDatos.tablas == null) {
            BaseDeDatos.tablas = SqliteHelper(this)
        }
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_paciente)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        pk_doc = intent.getIntExtra("pk", 0)
        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {}
        }
        onBackPressedDispatcher.addCallback(this, callback)
        val btnPacienteNuevo = findViewById<Button>(R.id.btn_paciente_nuevo)
        btnPacienteNuevo.setOnClickListener {
            val intentExplicito = Intent(this, CrearEditar_Paciente::class.java)
            intentExplicito.putExtra("Operacion", 1)
            intentExplicito.putExtra("pk", pk_doc)
            startActivity(intentExplicito)
        }
        val btnAtras = findViewById<Button>(R.id.btn_atras_paciente)
        btnAtras.setOnClickListener{
            val intentExplicito = Intent(this, Doctor::class.java)
            startActivity(intentExplicito)
            finish()
        }
        val listView = findViewById<ListView>(R.id.lv_paciente)
        actualizarLista(listView)
        registerForContextMenu(listView)
    }

    private fun actualizarLista(listView: ListView) {
        val pacientes = BaseDeDatos.tablas!!.obtenerPacientesPorDoctor(pk_doc)
        val adaptador = ArrayAdapter(this, android.R.layout.simple_list_item_1, pacientes)
        listView.adapter = adaptador
        adaptador.notifyDataSetChanged()
    }

    override fun onCreateContextMenu(menu: ContextMenu?, v: View?, menuInfo: ContextMenu.ContextMenuInfo?) {
        super.onCreateContextMenu(menu, v, menuInfo)
        menuInflater.inflate(R.menu.menu_paciente, menu)

        val info = menuInfo as AdapterView.AdapterContextMenuInfo
        posicionItemSeleccionado = info.position
    }

    override fun onContextItemSelected(item: MenuItem): Boolean {
        val pacientes = BaseDeDatos.tablas!!.obtenerPacientesPorDoctor(pk_doc)

        if (posicionItemSeleccionado < 0 || posicionItemSeleccionado >= pacientes.size) {
            Toast.makeText(this, "Error: Paciente no encontrado", Toast.LENGTH_SHORT).show()
            return false
        }

        val paciente = pacientes[posicionItemSeleccionado]

        return when (item.itemId) {
            R.id.mi_editar -> {
                AlertDialog.Builder(this)
                    .setTitle("Confirmación")
                    .setMessage("¿Seguro que deseas editar este paciente?")
                    .setPositiveButton("Sí") { _, _ ->
                        mostrarDialogoEditarPaciente(paciente)
                    }
                    .setNegativeButton("No", null)
                    .show()
                true
            }

            R.id.mi_eliminar -> {
                eliminarPaciente(paciente.id)
                true
            }
            else -> super.onContextItemSelected(item)
        }
    }

    private fun mostrarDialogoEditarPaciente(paciente: MPaciente) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Editar Paciente")

        val inputNombre = EditText(this).apply {
            hint = "Nombre"
            setText(paciente.nombre)
        }
        val inputEdad = EditText(this).apply {
            hint = "Edad"
            inputType = InputType.TYPE_CLASS_NUMBER
            setText(paciente.edad.toString())
        }
        val inputAltura = EditText(this).apply {
            hint = "Altura"
            inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL
            setText(paciente.altura.toString())
        }
        val inputSexo = EditText(this).apply {
            hint = "Sexo"
            setText(paciente.sexo)
        }

        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(50, 20, 50, 20)
            addView(inputNombre)
            addView(inputEdad)
            addView(inputAltura)
            addView(inputSexo)
        }

        builder.setView(layout)

        builder.setPositiveButton("Actualizar") { _, _ ->
            val nuevoNombre = inputNombre.text.toString().trim()
            val nuevaEdad = inputEdad.text.toString().toIntOrNull() ?: paciente.edad
            val nuevaAltura = inputAltura.text.toString().toFloatOrNull() ?: paciente.altura.toFloat()
            val nuevoSexo = inputSexo.text.toString().trim()

            if (nuevoNombre.isEmpty() || nuevoSexo.isEmpty()) {
                Toast.makeText(this, "Error: No se permiten campos vacíos", Toast.LENGTH_SHORT).show()
                return@setPositiveButton
            }

            val actualizado = BaseDeDatos.tablas!!.actualizarPaciente(paciente.id, nuevoNombre, nuevaEdad, nuevaAltura, nuevoSexo)
            if (actualizado) {
                Toast.makeText(this, "Paciente actualizado", Toast.LENGTH_SHORT).show()
                actualizarPantalla()
            } else {
                Toast.makeText(this, "Error al actualizar paciente", Toast.LENGTH_SHORT).show()
            }
        }

        builder.setNegativeButton("Cancelar", null)
        builder.create().show()
    }

    private fun eliminarPaciente(idPaciente: Int) {
        AlertDialog.Builder(this)
            .setTitle("Eliminar Paciente")
            .setMessage("¿Estás seguro de que quieres eliminar a este paciente?")
            .setPositiveButton("Eliminar") { _, _ ->
                val eliminado = BaseDeDatos.tablas!!.eliminarPaciente(idPaciente)
                if (eliminado) {
                    Toast.makeText(this, "Paciente eliminado", Toast.LENGTH_SHORT).show()
                    actualizarPantalla()
                } else {
                    Toast.makeText(this, "Error al eliminar paciente", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun actualizarPantalla() {
        finish()
        startActivity(intent)
    }
}
