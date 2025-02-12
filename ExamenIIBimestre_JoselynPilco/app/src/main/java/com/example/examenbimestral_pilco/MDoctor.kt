package com.example.myapplication

import com.google.android.gms.maps.model.LatLng

class MDoctor (
    val id: Int,
    var nombre: String?,
    val especialidad: String?,
    val aniosDeExperiencia: Float,
    val certificadoActivo: Boolean,
    val direccion: LatLng
){
    override fun toString(): String {
        return """
        ┌───────────────────────────┐
        │     🏥 Detalles           │
        ├───────────────────────────┤
        │ 👤 Nombre: ${nombre ?: "No disponible"}           
        │ 🩺 Especialidad: ${especialidad ?: "No disponible"} 
        │ 📅 Años de Experiencia: ${aniosDeExperiencia}        
        │ 🎓 Certificado Activo: ${if (certificadoActivo) "✅ Sí" else "❌ No"}    
        │ 📍 Ubicación: ${direccion.latitude}, ${direccion.longitude}        
        └───────────────────────────┘
    """.trimIndent()
    }

}