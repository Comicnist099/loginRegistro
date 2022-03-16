package com.poi.loginregistro

import android.content.Intent
import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Gravity
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.basgeekball.awesomevalidation.AwesomeValidation
import com.basgeekball.awesomevalidation.ValidationStyle
import com.basgeekball.awesomevalidation.utility.RegexTemplate
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.poi.loginregistro.Modelos.*
import com.poi.loginregistro.adapter.SelectMembersAdapter
import com.poi.loginregistro.dao.ContactDAO
import com.poi.loginregistro.dao.TeamsDAO
import com.poi.loginregistro.helpers.General
import kotlinx.android.synthetic.main.activity_create_group.*


class CreateGroupActivity : AppCompatActivity() {

    var contactList : ArrayList<contacto>? = ArrayList();
    val databaseReference : DatabaseReference = FirebaseDatabase.getInstance().reference

    lateinit var mediaPlayer: MediaPlayer


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_group)

        supportActionBar?.title = "Create Group"

        var layoutManager =  LinearLayoutManager(this)
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL)
        select_members_recycler.layoutManager = layoutManager
        mediaPlayer=MediaPlayer.create(this,R.raw.notify)

        fetchUsers()

        val validator : AwesomeValidation = AwesomeValidation(ValidationStyle.BASIC)
        validator.addValidation(this, R.id.groupNameView, RegexTemplate.NOT_EMPTY, R.string.empty_name)

        createGroupBtn.setOnClickListener{

            var contactsSelected= SelectMembersAdapter(contactList!!).returnSelectedItems()

            if(validator.validate()){

                if(contactsSelected.size>0){

                    val key=System.currentTimeMillis().toString()

                    val userContact= contacto (
                        General.UserInstance.getUserInstance()?.uid.toString(),
                        General.UserInstance.getUserInstance()?.username.toString(),
                        General.UserInstance.getUserInstance()?.correo.toString(),
                        General.UserInstance.getUserInstance()?.contraseña.toString(),
                        "active",
                        General.UserInstance.getUserInstance()?.encrypted.toString(),
                        true,
                        General.UserInstance.getUserInstance()?.carrera.toString())
                    contactsSelected.add(userContact)
                    val group= Team(groupNameView.text.toString(),contactsSelected)

                    TeamsDAO.add(key,group).addOnSuccessListener {
                        Logro()
                        val intent = Intent(this,GroupLogActivity::class.java)
                        intent.putExtra("teamUid",group.name)
                        startActivity(intent)
                        finish()
                    }
                }else{
                    Toast.makeText(this,"Agrega al menos un usuario", Toast.LENGTH_SHORT).show()
                }


            }
        }


    }


    private fun fetchUsers(){

        databaseReference.child(contacto::class.java.simpleName).addValueEventListener(object :
            ValueEventListener {
            override fun onCancelled(snapshotError: DatabaseError) {

            }
            override fun onDataChange(snapshot: DataSnapshot) {
                if(contactList!=null){
                    contactList?.clear()
                }
                val activeUid : String = General.UserInstance.getUserInstance()?.uid.toString()
                snapshot!!.children.forEach {
                    val contact :contacto? =  it.getValue(contacto::class.java) as contacto
                    if (contact != null) {

                        if(!contact.uid.equals(activeUid)){
                            contactList?.add(contact)
                        }

                    }


                }
                select_members_recycler.adapter= SelectMembersAdapter(contactList!!)

            }
        }
        )


        /*val ref = FirebaseDatabase.getInstance().getReference("/users")
        ref.addListenerForSingleValueEvent(object: ValueEventListener{


            override fun onDataChange(snapshot: DataSnapshot) {
                val adapter = GroupAdapter<GroupieViewHolder>()

                snapshot.children.forEach{
                    Log.d("NewMessage", it.toString())
                    val user = it.getValue(users::class.java)
                    if(user != null){
                        adapter.add(UserItem(user))
                    }

                }

                select_members_recycler.adapter = adapter

            }
            override fun onCancelled(error: DatabaseError) {
            }
        })*/
    }
    private fun Logro() {
        val uid = FirebaseAuth.getInstance().uid
        val ref = FirebaseDatabase.getInstance().getReference("/users/$uid")
        var cambiarEstado=false
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                SettingsActivity.currentUser = snapshot.getValue(users::class.java)
                val hashMap : java.util.HashMap<String, Any> = java.util.HashMap()

                if(SettingsActivity.currentUser?.Logro_createGrupo ==false){
                    hashMap.put("logro_createGrupo",true)
                    cambiarEstado=true
                    ContactDAO.update(SettingsActivity.currentUser?.uid.toString(),hashMap)?.addOnSuccessListener {
                    }
                    mediaPlayer.start()
                    val linearLayout = LinearLayout(applicationContext)

                    // populate layout with your image and text
                    // or whatever you want to put in here
                    val imageView = ImageView(applicationContext)

                    // adding image to be shown
                    imageView.setImageResource(R.drawable.logro_creargrupo)

                    // adding image to linearlayout
                    linearLayout.addView(imageView)
                    val toast = Toast(applicationContext)

                    // showing toast on bottom
                    toast.setGravity(Gravity.BOTTOM, 0, 100)
                    toast.duration = Toast.LENGTH_LONG

                    // setting view of toast to linear layout
                    toast.setView(linearLayout)
                    toast.show()
                }


            } override fun onCancelled(error: DatabaseError) {

            }
        })

    }


}