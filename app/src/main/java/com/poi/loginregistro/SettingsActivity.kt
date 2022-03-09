package com.poi.loginregistro

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.poi.loginregistro.dao.ContactDAO
import com.poi.loginregistro.Modelos.users
import com.poi.loginregistro.databinding.ActivitySettingsBinding
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_settings.*


class SettingsActivity : AppCompatActivity(), AdapterView.OnItemClickListener {

    companion object {
        var currentUser: users? = null
        val TAG = "SettingsLog"
    }

    private val database = FirebaseDatabase.getInstance()
    private lateinit var storageReference : StorageReference
    private lateinit var binding: ActivitySettingsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_settings)

        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val status = resources.getStringArray(R.array.status_usuario)
        val adapter = ArrayAdapter(this, R.layout.list_item_status, status)

        storageReference= FirebaseStorage.getInstance().getReference("users")

        with(binding.autoCompleteTextViewStatus){
            setAdapter(adapter)
            onItemClickListener = this@SettingsActivity
        }

        editarUsuario()

    }

    private fun editarUsuario(){

        val username_u = findViewById<EditText>(R.id.set_user_name)
        val btnSend = findViewById<Button>(R.id.save_user)

        val uid = FirebaseAuth.getInstance().uid
        val ref = FirebaseDatabase.getInstance().getReference("/users/$uid")
        ref.addListenerForSingleValueEvent(object: ValueEventListener {

            override fun onDataChange(snapshot: DataSnapshot) {
                currentUser = snapshot.getValue(users::class.java)


                btnSend.setOnClickListener {

                    val username = username_u.text.toString().trim()
                    autoCompleteTextView_status.text.toString()

                    val hashMap : HashMap<String, Any> = HashMap()
                    hashMap.put("username",username)
                    hashMap.put("status",autoCompleteTextView_status.text.toString())

                    ContactDAO.update(currentUser?.uid.toString(),hashMap)?.addOnSuccessListener {

                        finish()

                    }

                }

                encryptPasswordSwitch.setOnClickListener{
                    if(encryptPasswordSwitch.isChecked){
                        val hashMap : HashMap<String, Any> = HashMap()
                        hashMap.put("encrypted","activated")

                        ContactDAO.update(currentUser?.uid.toString(),hashMap)?.addOnSuccessListener {
                            Log.d(TAG, "Encriptado")
                        }
                    }
                    else {
                        val hashMap : HashMap<String, Any> = HashMap()
                        hashMap.put("encrypted","deactivated")

                        ContactDAO.update(currentUser?.uid.toString(),hashMap)?.addOnSuccessListener {
                            Log.d(TAG, "Desencriptado")
                        }
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })


    }


    override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {

        val item = parent?.getItemAtPosition(position).toString()
        Toast.makeText(this@SettingsActivity, item, Toast.LENGTH_SHORT).show()
    }





}