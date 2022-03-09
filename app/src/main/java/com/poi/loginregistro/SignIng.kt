package com.poi.loginregistro

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.poi.loginregistro.Modelos.Carrera
import android.widget.ArrayAdapter
import com.poi.loginregistro.Modelos.users
import com.poi.loginregistro.databinding.ActivityMainBinding
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*

class SignIng: AppCompatActivity(), AdapterView.OnItemClickListener {

    private val database = FirebaseDatabase.getInstance()
    private val usuarioRef = database.getReference("users")
    private val contactoRef = database.getReference("contacto")
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val carrera = resources.getStringArray(R.array.elige_carrera)
        val adapter = ArrayAdapter(this, R.layout.list_item, carrera)

        with(binding.autoCompleteTextView){
            setAdapter(adapter)
            onItemClickListener = this@SignIng
        }


        //setContentView(R.layout.activity_main)

        supportActionBar?.title = "Multimedios Chat"

        val correoText = findViewById<EditText>(R.id.correo_text)
        val userText = findViewById<EditText>(R.id.username_text)
        val password = findViewById<EditText>(R.id.contr_text)
        val button = findViewById<Button>(R.id.btnRegister)


        button.setOnClickListener{

            val email = correoText.text.toString().trim()
            val contra = password.text.toString().trim()
            val username = userText.text.toString().trim()
            val status = "Disponible"
            val encrypted = "deactivated"
            val tasks = ""
            val selected = false
            autoCompleteTextView.text.toString()

            if(username.isEmpty() || email.isEmpty() || contra.isEmpty()) {

                Toast.makeText(this, "Faltan Campos de rellenar", Toast.LENGTH_SHORT).show()
                return@setOnClickListener

            }
            else {
                saveUsertoDatabase(users("", username, email, contra, status, encrypted, selected, tasks, autoCompleteTextView.text.toString() ))
            }

            Log.d("SignIng", "Email is: " + email)
            Log.d("SignIng", "Password:  $contra")

            //registrarse()
            //finish()
        }

    }

    override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {

        val item = parent?.getItemAtPosition(position).toString()
        Toast.makeText(this@SignIng, item, Toast.LENGTH_SHORT).show()
    }


    private fun getNombreCarreras(listaCarrera: List<Carrera>): List<String> {
        val resultado = ArrayList<String>()
        for (carrera in listaCarrera) {
            resultado.add(carrera.nombre)
        }
        return resultado
    }

    private fun saveUsertoDatabase(usuario: users) {

        FirebaseAuth.getInstance().createUserWithEmailAndPassword(usuario.correo, usuario.contraseña)
            .addOnCompleteListener{
                if(!it.isSuccessful) return@addOnCompleteListener

                Log.d("SignIng", "Usuario creado con exito: ${it.result?.user?.uid}")

                usuario.uid = FirebaseAuth.getInstance().currentUser!!.uid
                usuarioRef.child(usuario.uid).setValue(usuario)
                contactoRef.child(usuario.uid).setValue(usuario)
                //val uid = FirebaseAuth.getInstance().uid ?: ""
                //val ref = FirebaseDatabase.getInstance().getReference("/users/ $uid")
                //val user = User(uid, username_text.text.toString())

                //ref.setValue(user)
            .addOnSuccessListener {
                        Log.d("SignIng", "Usuario Guardado")

                        val intent = Intent(this, LatestMessagesA::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                        startActivity(intent)
                    }
            }
            .addOnFailureListener{
                Log.d("SignIng", "No se creo usuario: ${it.message}")
            }


    }






}