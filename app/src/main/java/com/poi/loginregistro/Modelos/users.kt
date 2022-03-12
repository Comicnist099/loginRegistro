package com.poi.loginregistro.Modelos

import android.os.Parcelable
import com.google.firebase.database.Exclude
import com.google.firebase.database.IgnoreExtraProperties
import kotlinx.android.parcel.Parcelize

@Parcelize
data class users(
    var uid: String ="",
    var username: String ="",
    var correo: String = "",
    var contraseña: String = "",
    var status: String = "",
    var encrypted: String = "",
    var isSelected : Boolean = false,
    var tasksCompleted : String = "",
    val carrera: String = "",
    var Logro_createUsuario : Boolean = false,
    var Logro_createGrupo : Boolean = false,
    var Logro_login : Boolean = false,
    var Logro_cambiaEstado : Boolean = false
): Parcelable{
   // constructor():this("","", "", "", "", "", false, "", "")
}


