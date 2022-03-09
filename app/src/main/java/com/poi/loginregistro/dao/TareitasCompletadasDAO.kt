package com.poi.loginregistro.dao

import com.google.android.gms.tasks.Task
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.poi.loginregistro.Modelos.TareitasCompletadas
import com.poi.loginregistro.helpers.General

class TareitasCompletadasDAO {

    companion object {
        private val databaseReference: DatabaseReference =
            FirebaseDatabase.getInstance().getReference(TareitasCompletadas::class.java.simpleName)

        // CRUD
        fun add(assignment: TareitasCompletadas): Task<Void> {
            return databaseReference.child(General.UserInstance.getUserInstance()?.uid.toString()).child(assignment.id).setValue(assignment)
        }

        fun update(key: String, hashMap: HashMap<String, Any>): Task<Void>? {
            return databaseReference.child(key).updateChildren(hashMap)
        }

    }
}