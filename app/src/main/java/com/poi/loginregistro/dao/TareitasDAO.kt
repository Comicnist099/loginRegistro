package com.poi.loginregistro.dao

import com.google.android.gms.tasks.Task
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.poi.loginregistro.Modelos.Tareitas
import com.poi.loginregistro.helpers.General

class TareitasDAO {
    companion object {
        private val databaseReference: DatabaseReference =
            FirebaseDatabase.getInstance().getReference(Tareitas::class.java.simpleName)

        // CRUD
        fun add(assignment: Tareitas,teamKey : String): Task<Void> {
            return databaseReference.child(General.UserInstance.getUserInstance()?.uid.toString()).child(teamKey).child(assignment.id.toString()).setValue(assignment)
        }

        fun update(key: String, hashMap: HashMap<String, Any>): Task<Void>? {
            return databaseReference.child(key).updateChildren(hashMap)
        }

    }
}