package com.poi.loginregistro

import android.app.Dialog
import android.content.Intent
import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Gravity
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.poi.loginregistro.Modelos.Tareitas
import com.poi.loginregistro.Modelos.TareitasCompletadas
import com.poi.loginregistro.Modelos.users
import com.poi.loginregistro.dao.ContactDAO
import com.poi.loginregistro.dao.TareitasCompletadasDAO
import com.poi.loginregistro.helpers.General
import kotlinx.android.synthetic.main.activity_deliver_task.*

class DeliverTaskActivity : AppCompatActivity() {

    private var team: String = ""
    private var id: String = ""
    private lateinit var tareas: TareitasCompletadas
    private val databaseReference: DatabaseReference = FirebaseDatabase.getInstance().reference
    lateinit var mediaPlayer: MediaPlayer
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_deliver_task)
        mediaPlayer=MediaPlayer.create(this,R.raw.notify)


        supportActionBar?.title = "Entregar Tarea"

        val bundle = intent.extras
        team = bundle!!.getString("team").toString()
        id = bundle!!.getString("id").toString()



        databaseReference.child(Tareitas::class.java.simpleName)
            .child(General.UserInstance.getUserInstance()?.uid.toString()).child(team).child(id)
            .addListenerForSingleValueEvent(object :
                ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    tareas =
                        snapshot.getValue(TareitasCompletadas::class.java) as TareitasCompletadas
                    taskTitle.text = tareas.name
                    if (tareas.description.equals("")) {
                        taskInstructions.text = "No hay instrucciones"
                    } else {
                        taskInstructions.text = tareas.description
                    }


                }

                override fun onCancelled(error: DatabaseError) {

                }

            })


        deliverTask.setOnClickListener {


            try {

                tareas.date = ServerValue.TIMESTAMP
                TareitasCompletadasDAO.add(tareas).addOnCompleteListener {


                    databaseReference.child(TareitasCompletadas::class.java.simpleName)
                        .child(General.UserInstance.getUserInstance()?.uid.toString())
                        .addListenerForSingleValueEvent(object : ValueEventListener {
                            override fun onDataChange(snapshot: DataSnapshot) {
                                val numberOfTasksCompleted = snapshot.children.count()

                                val contactMap: HashMap<String, Any> = HashMap()

                                contactMap.put("tasksCompleted", numberOfTasksCompleted.toString())

                                Toast.makeText(
                                    this@DeliverTaskActivity,
                                    "Completaste " + numberOfTasksCompleted.toString() + " " + "tareas,Eres todo un crack",
                                    Toast.LENGTH_LONG
                                ).show()
                                ContactDAO.update(
                                    General.UserInstance.getUserInstance()?.uid.toString(), contactMap
                                )?.addOnSuccessListener {
                                    Logro()
                                }
                            }

                            override fun onCancelled(error: DatabaseError) {

                            }

                        })


                }
            } catch (e: Exception) {

                Toast.makeText(this, e.message.toString(), Toast.LENGTH_LONG).show()

            }

        }
    }


    private fun Logro() {
        val uid = FirebaseAuth.getInstance().uid
        val ref = FirebaseDatabase.getInstance().getReference("/users/$uid")
        var cambiarEstado=false
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                SettingsActivity.currentUser = snapshot.getValue(users::class.java)
                val hashMap : java.util.HashMap<String, Any> = java.util.HashMap()

                if(SettingsActivity.currentUser?.Logro_Tarea ==false){
                    hashMap.put("logro_Tarea",true)
                    cambiarEstado=true
                    ContactDAO.update(SettingsActivity.currentUser?.uid.toString(),hashMap)?.addOnSuccessListener {
                    }
                    mediaPlayer.start()
                    val linearLayout = LinearLayout(applicationContext)

                    // populate layout with your image and text
                    // or whatever you want to put in here
                    val imageView = ImageView(applicationContext)

                    // adding image to be shown
                    imageView.setImageResource(R.drawable.logro_tareaentregada)

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

                val intent =
                    Intent(this@DeliverTaskActivity, LatestMessagesA::class.java)
                startActivity(intent)

            } override fun onCancelled(error: DatabaseError) {

            }
        })

    }
}