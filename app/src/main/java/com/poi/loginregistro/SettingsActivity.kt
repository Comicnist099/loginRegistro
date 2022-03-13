package com.poi.loginregistro

import android.app.ProgressDialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
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
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.HashMap


class SettingsActivity : AppCompatActivity(), AdapterView.OnItemClickListener {

    companion object {
        var currentUser: users? = null
        val TAG = "SettingsLog"
    }
    lateinit var ImageUri: Uri
    lateinit var UserUid:String
    lateinit var UserNick:String
    private val database = FirebaseDatabase.getInstance()
    private lateinit var storageReference : StorageReference
    private lateinit var binding: ActivitySettingsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_settings)

        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)




        binding.setUserName.isEnabled=false
        fetchCurrentUser()
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
    }

    private fun editarUsuario(){
        val username_u = findViewById<EditText>(R.id.set_user_name)
        val btnSend = findViewById<Button>(R.id.save_user)
        storageReference= FirebaseStorage.getInstance().getReference("users")
        val uid = FirebaseAuth.getInstance().uid
        val ref = FirebaseDatabase.getInstance().getReference("/users/$uid")
        ref.addListenerForSingleValueEvent(object: ValueEventListener {

            override fun onDataChange(snapshot: DataSnapshot) {
                currentUser = snapshot.getValue(users::class.java)


                val storageRef =
                    FirebaseStorage.getInstance().reference.child("images/${UserUid}")
                val localFile = java.io.File.createTempFile("tempImage", "jpg")




                btnSend.setOnClickListener {
                    val username = username_u.text.toString().trim()
                    val cambiarEstado=true
                    autoCompleteTextView_status.text.toString()

                    val hashMap : HashMap<String, Any> = HashMap()
                    hashMap.put("username",username)
                    hashMap.put("status",autoCompleteTextView_status.text.toString())
                    hashMap.put("logro_cambiarEstado",cambiarEstado)
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

            uploadImage()




        }

    }
    private fun fetchCurrentUser() {

        val uid = FirebaseAuth.getInstance().uid
        val ref = FirebaseDatabase.getInstance().getReference("/users/$uid")
        ref.addListenerForSingleValueEvent(object : ValueEventListener {

            override fun onDataChange(snapshot: DataSnapshot) {
                SettingsActivity.currentUser = snapshot.getValue(users::class.java)
                Log.d("LatestMessages", "Current user ${LatestMessagesA.currentUser?.username}")


                UserUid= SettingsActivity.currentUser?.uid.toString()
                UserNick= SettingsActivity.currentUser?.username.toString()

                Log.d("AHUEVO",UserUid)
                val storageRef =
                    FirebaseStorage.getInstance().reference.child("images/${UserUid}")
                val localFile = java.io.File.createTempFile("tempImage", "jpg")

                storageRef.getFile(localFile).addOnSuccessListener {
                    val bitmap = BitmapFactory.decodeFile(localFile.absolutePath)
                    binding.imageView5.setImageBitmap(bitmap)
                    binding.setUserName.setText(UserNick)
                    binding.setUserName.isEnabled=true

                }.addOnFailureListener {
                    Toast.makeText(this@SettingsActivity, "NO JALO el colocamiento de imagen", Toast.LENGTH_SHORT).show()

                }

            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")

            }
        }
        )
    }
    private fun uploadImage() {


        val progressDialog= ProgressDialog(this)
        progressDialog.setMessage("uploading File ...")
        progressDialog.setCancelable(false)
        progressDialog.show()


        val formatter= SimpleDateFormat("yyyy_MM_dd_HH_mm", Locale.getDefault())
        val now= Date()
        val fileName=UserUid
        val storageReference=FirebaseStorage.getInstance().getReference("images/$fileName")



        storageReference.putFile(ImageUri).
        addOnSuccessListener {
            binding.imageView5.setImageURI(null)
            Toast.makeText(this@SettingsActivity,"Successfuly",Toast.LENGTH_SHORT).show()
            finish()
            if(progressDialog.isShowing)progressDialog.dismiss()
        }.addOnFailureListener{

            if(progressDialog.isShowing) progressDialog.dismiss()
            Toast.makeText(this@SettingsActivity,"NO JALO",Toast.LENGTH_SHORT).show()

        }

    }



}
