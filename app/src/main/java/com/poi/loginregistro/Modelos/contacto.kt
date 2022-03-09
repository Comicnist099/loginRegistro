package com.poi.loginregistro.Modelos

import com.google.firebase.database.Exclude
import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class contacto (var uid: String ="",
                     var username: String ="",
                     var correo: String = "",
                     var contraseña: String = "",
                     var status: String = "",
                     var encrypted: String = "",
                     var isSelected : Boolean = false,
                     var tasksCompleted : String = "",
                     val carrera: String = "") {

    @Exclude
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "uid" to uid,
            "username" to username,
            "correo" to correo,
            "contraseña" to contraseña,
            "status" to status,
            "encrypted" to encrypted,
            "isSelected" to isSelected,
            "tasksCompleted" to tasksCompleted,
            "carrera" to carrera
        )
    }
}