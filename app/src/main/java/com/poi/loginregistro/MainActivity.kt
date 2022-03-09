package com.poi.loginregistro

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.poi.loginregistro.Modelos.Carrera
import com.poi.loginregistro.Modelos.users
import com.poi.loginregistro.helpers.General
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_main.*

class Pregunta(val contenido: String, val respuesta: Boolean)

class MainActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        supportActionBar?.title = "SunTeams"

        val correoText = findViewById<EditText>(R.id.correo_text2)
        val password = findViewById<EditText>(R.id.contr_text2)
        val button = findViewById<Button>(R.id.btnRegister2)
        val account = findViewById<TextView>(R.id.noAccount)
        val databaseReference : DatabaseReference = FirebaseDatabase.getInstance().reference


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

                    val intent = Intent(this, LatestMessagesA::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Failed to log in: ${it.message}", Toast.LENGTH_SHORT)
                        .show()
                }


        }

    }






}