package com.poi.loginregistro.Modelos

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
class Carrera(
    var uid: String ="",
    var nombre: String =""
    ): Parcelable{
    constructor():this("", "")
}