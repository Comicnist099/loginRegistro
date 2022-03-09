package com.poi.loginregistro.dao

import com.google.android.gms.tasks.Task
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.poi.loginregistro.Modelos.users

class UserDAO {

    companion  object {
        private val databaseReference: DatabaseReference = FirebaseDatabase.getInstance().getReference(
            users::class.java.simpleName)

        fun add(user : users) : Task<Void> {
            return databaseReference.push().setValue(user)
        }
        fun update(key: String, hashMap: HashMap<String, Any>): Task<Void>? {

            val contactMap: HashMap<String, Any> = HashMap()

            if(hashMap.containsKey("username")){
                contactMap.put("username",hashMap.getValue("username").toString())
            }

            if(hashMap.containsKey("status")){
                contactMap.put("status",hashMap.getValue("status").toString())
            }


            ContactDAO.update(key, contactMap)

            return  databaseReference.child(key).updateChildren(hashMap)
        }

        fun remove(key: String): Task<Void>? {
            return  databaseReference.child(key).removeValue()
        }
    }


}