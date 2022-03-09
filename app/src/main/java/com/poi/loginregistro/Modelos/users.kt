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
    val carrera: String = ""
): Parcelable{
   // constructor():this("","", "", "", "", "", false, "", "")
}


