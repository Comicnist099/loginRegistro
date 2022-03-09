package com.poi.loginregistro.dao

import com.google.android.gms.tasks.Task
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.poi.loginregistro.Modelos.Team

class TeamsDAO {

    private val database = FirebaseDatabase.getInstance()
    //private val equipoRef = database.getReference("equipos")


    companion  object {
        private val databaseReference: DatabaseReference = FirebaseDatabase.getInstance().getReference(
            Team::class.java.simpleName)

        // CRUD
        fun add(key : String,team : Team) : Task<Void> {
            //return equipoRef.child(team.name).setValue(team.members)
            return databaseReference.child(team.name ).setValue(team.members)
        }
        fun update(key: String, hashMap: HashMap<String, Any>): Task<Void>? {

            return  databaseReference.child(key).updateChildren(hashMap)
        }

        fun remove(key: String): Task<Void>? {
            return  databaseReference.child(key).removeValue()
        }
    }




}