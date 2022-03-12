package com.poi.loginregistro

import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
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
    lateinit var ImageUri: Uri

    private val database = FirebaseDatabase.getInstance()
    private lateinit var storageReference : StorageReference
    private lateinit var binding: ActivitySettingsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_settings)

        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)




binding.Prueba.setOnClickListener{
    fetchCurrentUser()
}

        val status = resources.getStringArray(R.array.status_usuario)
        val adapter = ArrayAdapter(this, R.layout.list_item_status, status)

        binding.imageView5.setOnClickListener{
            selectImage()
        }

        storageReference= FirebaseStorage.getInstance().getReference("users")

        with(binding.autoCompleteTextViewStatus){
            setAdapter(adapter)
            onItemClickListener = this@SettingsActivity
        }

        editarUsuario()
        val storageRef =
            FirebaseStorage.getInstance().reference.child("images/W5oi5pRKXffbbAyaLzi5THFhPDL2")
        val localFile = java.io.File.createTempFile("tempImage", "jpg")

        storageRef.getFile(localFile).addOnSuccessListener {
            val bitmap = BitmapFactory.decodeFile(localFile.absolutePath)
            binding.imageView5.setImageBitmap(bitmap)

        }.addOnFailureListener {
            Toast.makeText(this@SettingsActivity, "NO JALO el colocamiento de imagen", Toast.LENGTH_SHORT).show()

        }

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


    private fun selectImage() {

        val intent= Intent()
        intent.type="image/*"
        intent.action= Intent.ACTION_GET_CONTENT

        startActivityForResult(intent,100)

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode==100&& resultCode==RESULT_OK){

            ImageUri=data?.data!!
            binding.imageView5.setImageURI(ImageUri)

        }

    }
    private fun fetchCurrentUser() {

        val uid = FirebaseAuth.getInstance().uid
        val ref = FirebaseDatabase.getInstance().getReference("/users/$uid")
        ref.addListenerForSingleValueEvent(object : ValueEventListener {

            override fun onDataChange(snapshot: DataSnapshot) {
                SettingsActivity.currentUser = snapshot.getValue(users::class.java)
                Log.d("LatestMessages", "Current user ${LatestMessagesA.currentUser?.username}")

                val users = SettingsActivity.currentUser?.encrypted

                if (users.toString().equals("activated")) {
                    //send message to firebase
                }else{
                    Log.d("AHUEVO","JALO")

                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")

            }
        }
        )
    }



}