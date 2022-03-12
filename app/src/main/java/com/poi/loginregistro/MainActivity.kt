package com.poi.loginregistro

import android.content.Intent

import android.os.Bundle

import android.util.Log
import android.view.Gravity

import android.widget.*

import androidx.appcompat.app.AppCompatActivity

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.poi.loginregistro.Modelos.users

import com.poi.loginregistro.helpers.General
import java.io.File


class MainActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {


        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)



        supportActionBar?.title = "Multimedios Chat"

        val correoText = findViewById<EditText>(R.id.correo_text2)
        val password = findViewById<EditText>(R.id.contr_text2)
        val button = findViewById<Button>(R.id.btnRegister2)
        val account = findViewById<TextView>(R.id.noAccount)
        val databaseReference : DatabaseReference = FirebaseDatabase.getInstance().reference
        val ImagenPerfil = findViewById<ImageView>(R.id.Imgid)
        val BtnCargar = findViewById<Button>(R.id.btnCargarImg)



        account.setOnClickListener {

            Log.d("MainActivity", "hola")
            val signPantalla = Intent(this, SignIng::class.java)
            startActivity(signPantalla)
        }

        button.setOnClickListener {


            val email2 = correoText.text.toString().trim()
            val contra2 = password.text.toString().trim()


            Log.d("Login", "Login con email/pw: $email2/***")

            FirebaseAuth.getInstance().signInWithEmailAndPassword(email2, contra2)
                .addOnCompleteListener {
                    if (!it.isSuccessful) return@addOnCompleteListener

                    databaseReference.child(users::class.java.simpleName).addListenerForSingleValueEvent(object :
                        ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            snapshot.children.forEach{ user->

                                if(user.child("correo").value?.equals(email2) == true){

                                    val users = users(uid = user.key.toString())
                                    General.UserInstance.setUserInstance(users)

                                }
                            }
                        }
                        override fun onCancelled(error: DatabaseError) {
                            Toast.makeText(this@MainActivity, error.message, Toast.LENGTH_LONG).show()
                        }

                        })

                    Log.d("Login", "Successfully logged in: ${it.result?.user?.uid}")
                    val linearLayout = LinearLayout(applicationContext)

                    // populate layout with your image and text
                    // or whatever you want to put in here
                    val imageView = ImageView(applicationContext)

                    // adding image to be shown
                    imageView.setImageResource(R.drawable.banner2)

                    // adding image to linearlayout
                    linearLayout.addView(imageView)
                    val toast = Toast(applicationContext)

                    // showing toast on bottom
                    toast.setGravity(Gravity.BOTTOM, 0, 100)
                    toast.duration = Toast.LENGTH_LONG

                    // setting view of toast to linear layout
                    toast.setView(linearLayout)

                    val intent = Intent(this, LatestMessagesA::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)
                    toast.show()
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Failed to log in: ${it.message}", Toast.LENGTH_SHORT)
                        .show()



                }

        }

    }
/////////////////// FOTO



}