package com.poi.loginregistro.Modelos
import android.os.Parcelable
import com.google.firebase.database.Exclude
import com.google.firebase.database.IgnoreExtraProperties
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Logros(
    var createUsuario : Boolean = false,
    var createGrupo : Boolean = false,
    var login : Boolean = false,
    var cambiaEstado : Boolean = false

): Parcelable{
   // constructor():this("","", "", "", "", "", false, "", "")
}


