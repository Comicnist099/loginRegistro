package com.poi.loginregistro

import android.app.Dialog
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.ImageButton
import com.basgeekball.awesomevalidation.AwesomeValidation
import com.basgeekball.awesomevalidation.ValidationStyle
import com.basgeekball.awesomevalidation.utility.RegexTemplate
import com.google.firebase.database.*
import com.poi.loginregistro.Modelos.Tareitas
import com.poi.loginregistro.Modelos.Team
import com.poi.loginregistro.Modelos.contacto
import com.poi.loginregistro.adapter.SpinerAdapter
import com.poi.loginregistro.dao.TareitasDAO
import com.poi.loginregistro.databinding.ActivityCrearTasksBinding
import com.poi.loginregistro.databinding.ActivitySettingsBinding
import com.poi.loginregistro.helpers.General
import kotlinx.android.synthetic.main.activity_crear_tasks.*
import java.util.*

class CrearTasksActivity : AppCompatActivity(), AdapterView.OnItemClickListener {

    private val databaseReference : DatabaseReference = FirebaseDatabase.getInstance().reference
    private var selectedTeam : String =""
    private var teamsList = mutableListOf<SpinerAdapter>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_crear_tasks)

        supportActionBar?.title = "Crear Tarea"
        initTeams(this)


        val validator : AwesomeValidation = AwesomeValidation(ValidationStyle.BASIC)
        validator.addValidation(this,R.id.taskTitle, RegexTemplate.NOT_EMPTY,R.string.empty_name)
        validator.addValidation(this,R.id.selectTeams, RegexTemplate.NOT_EMPTY, R.string.empty_name)

        val adapter= ArrayAdapter(this,R.layout.team_names_select,teamsList)
        val select=findViewById<AutoCompleteTextView>(R.id.selectTeams)

        with(select){
            setAdapter(adapter)
            onItemClickListener= this@CrearTasksActivity
        }

        addTask.setOnClickListener{

            if(validator.validate()){
                val key: UUID = UUID.randomUUID()
                val randomUUIDString: String = key.toString()

                val assignment = Tareitas(selectedTeam,taskTitle.text.toString(),
                    ServerValue.TIMESTAMP,taskDescription.text.toString(),randomUUIDString)
                TareitasDAO.add(assignment,selectedTeam).addOnSuccessListener {

                    val intent= Intent(this,LatestMessagesA::class.java)
                    startActivity(intent)
                    finish()
                }

            }
        }

    }

    private fun initTeams(context : Context){


        teamsList.clear()
        databaseReference.child(Team::class.java.simpleName).child(General.UserInstance.getUserInstance()?.carrera.toString()).addValueEventListener(object :
            ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                snapshot.children.forEach({team->

                    val contactList = mutableListOf<contacto>()
                    team.children.forEach({contact->
                        val contact = contacto(
                            contact.child("uid").value.toString(),
                            contact.child("username").value.toString(),
                            contact.child("correo").value.toString(),
                            contact.child("status").value.toString(),
                            contact.child("encrypted").value.toString(),
                            contact.child("carrera").value.toString())

                        contactList.add(contact)
                    })

                    val aux = Team(team.key.toString(),contactList)

                    for(member in aux.members){
                        if(member.uid.equals(General.UserInstance.getUserInstance()?.uid.toString())){

                            val adapter=SpinerAdapter(aux.name,aux.name)
                            teamsList.add(adapter)
                        }
                    }

                })

            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }


        })

    }

    override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {

        selectedTeam=teamsList[position].key

    }

}