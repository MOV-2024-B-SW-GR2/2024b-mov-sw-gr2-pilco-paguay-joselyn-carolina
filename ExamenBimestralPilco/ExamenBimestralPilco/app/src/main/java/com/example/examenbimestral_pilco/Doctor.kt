package com.example.examenbimestral_pilco

import android.content.Intent
import android.os.Bundle
import android.view.ContextMenu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.Button
import android.widget.ListView
import android.widget.ArrayAdapter
import androidx.activity.OnBackPressedCallback
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class Doctor : AppCompatActivity() {
    private var posicionItemSeleccionado = -1
    override fun onCreate(savedInstanceState: Bundle?) {
        if (BaseDeDatos.tablas == null) {
            BaseDeDatos.tablas = SqliteHelper(this)
        }
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_doctor)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed(){}
        }
        onBackPressedDispatcher.addCallback(this, callback)
        val btn_doctorNuevo = findViewById<Button>(R.id.btn_doctor_nuevo)
        btn_doctorNuevo.setOnClickListener {
            val intentExplicito = Intent(this, CrearEditar_Doctor::class.java)
            intentExplicito.putExtra("Operacion", 1)
            startActivity(intentExplicito)
        }
        val btn_doctorAtras = findViewById<Button>(R.id.btn_atras)
        btn_doctorAtras.setOnClickListener{
            irActividad(MainActivity::class.java)
        }
        val listView = findViewById<ListView>(R.id.lv_doctor)
        val adaptador = ArrayAdapter(
            this, // Contexto
            android.R.layout.simple_list_item_1,
            BaseDeDatos.tablas!!.obtenerDoctores()
        )
        listView.adapter = adaptador
        adaptador.notifyDataSetChanged()
        registerForContextMenu(listView)
    }

    override fun onCreateContextMenu(
        menu: ContextMenu?,
        v: View?,
        menuInfo: ContextMenu.ContextMenuInfo?
    ) {
        super.onCreateContextMenu(menu, v, menuInfo)
        val inflater = menuInflater
        inflater.inflate(R.menu.menu_doctor , menu)
        val info = menuInfo as AdapterView.AdapterContextMenuInfo
        val posicion = info.position
        posicionItemSeleccionado = posicion
    }

    fun irActividad (clase: Class<*>){
        startActivity(Intent(this, clase))
    }
    override fun onContextItemSelected(item: MenuItem): Boolean {
        val pk = BaseDeDatos.tablas!!.obtenerDoctores()[posicionItemSeleccionado].id
        val doctor = BaseDeDatos.tablas!!.obtenerDoctores()[posicionItemSeleccionado]
        return when (item.itemId) {
            R.id.mi_editar_doctor -> {
                val intentExplicito = Intent(this, CrearEditar_Doctor::class.java)
                intentExplicito.putExtra("Operacion", 2)
                intentExplicito.putExtra("pk", posicionItemSeleccionado)
                startActivity(intentExplicito)
                return true
            }

            R.id.mi_eliminar_doctor-> {
                BaseDeDatos.tablas!!.eliminarDoctor(pk)
                irActividad(Doctor::class.java)
                return true
            }

            R.id.mi_ver_paciente_doctor-> {
                val intentExplicito = Intent(this, Paciente::class.java)
                intentExplicito.putExtra("pk", pk)
                startActivity(intentExplicito)
                return true
            }
            R.id.mi_ver_ubicacion-> {
                val intentExplicito = Intent(this, GoogleMaps::class.java)
                intentExplicito.putExtra("latitud", doctor.direccion.latitude.toDouble())
                intentExplicito.putExtra("longitud", doctor.direccion.longitude.toDouble())
                startActivity(intentExplicito)
                true
            }

            else -> super.onContextItemSelected(item)
        }
    }

}