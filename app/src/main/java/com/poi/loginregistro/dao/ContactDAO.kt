package com.poi.loginregistro.dao

import com.google.android.gms.tasks.Task
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.poi.loginregistro.Modelos.users

class ContactDAO {

    companion object{
        private val databaseReference: DatabaseReference = FirebaseDatabase.getInstance().getReference(
            users::class.java.simpleName
        )

        fun add(user: users): Task<Void> {
            return databaseReference.child(user.uid).setValue(user)
        }

        fun update(key: String, hashMap: HashMap<String, Any>): Task<Void>? {
            return databaseReference.child(key).updateChildren(hashMap)
        }
        fun get(key  :String): DatabaseReference {
            return databaseReference.child(key)
        }
        fun remove(key: String): Task<Void>? {
            return databaseReference.child(key).removeValue()
        }
    }
}