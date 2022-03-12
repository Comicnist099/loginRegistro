package com.poi.loginregistro


import android.app.ProgressDialog
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.poi.loginregistro.Modelos.Logros
import com.poi.loginregistro.Modelos.users
import com.poi.loginregistro.databinding.ActivityMainBinding
import kotlinx.android.synthetic.main.activity_main.*
import java.text.SimpleDateFormat
import java.util.*

class SignIng: AppCompatActivity(), AdapterView.OnItemClickListener {
    private val File = 1
    lateinit var binding: ActivityMainBinding
    lateinit var ImageUri:Uri
    lateinit var logros: Logros

    var uid=""
    private val database = FirebaseDatabase.getInstance()
    val myRef = database.getReference("user")
    private val usuarioRef = database.getReference("users")
    private val contactoRef = database.getReference("contacto")



    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


        val carrera = resources.getStringArray(R.array.elige_carrera)
        val adapter = ArrayAdapter(this, R.layout.list_item, carrera)


        btnCargarImg.setOnClickListener {
        selectImage()

        //fileUpload()

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
            var logro_createUsuario  = true
            var logro_createGrupo =false
            var logro_login  = false
            var logro_cambiaEstado  = false



            if(username.isEmpty() || email.isEmpty() || contra.isEmpty()) {

                Toast.makeText(this, "Porfavor no deje campos vacios", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            else {
                saveUsertoDatabase(users("", username, email, contra, status, encrypted, selected, tasks, "" ,logro_createUsuario,
                    logro_createGrupo,logro_login,logro_cambiaEstado))


            }

            Log.d("SignIng", "Email is: " + email)
            Log.d("SignIng", "Password:  $contra")
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

            toast.show()
            //registrarse()
            //finish()
        }

    }

    private fun uploadImage() {


        val progressDialog=ProgressDialog(this)
        progressDialog.setMessage("uploading File ...")
        progressDialog.setCancelable(false)
        progressDialog.show()


        val formatter=SimpleDateFormat("yyyy_MM_dd_HH_mm", Locale.getDefault())
        val now=Date()
        val fileName=uid
        val storageReference=FirebaseStorage.getInstance().getReference("images/$fileName")


        storageReference.putFile(ImageUri).
                addOnSuccessListener {
                    binding.Imgid.setImageURI(null)
                    Toast.makeText(this@SignIng,"Successfuly",Toast.LENGTH_SHORT).show()
                    if(progressDialog.isShowing)progressDialog.dismiss()
                }.addOnFailureListener{

                    if(progressDialog.isShowing) progressDialog.dismiss()
            Toast.makeText(this@SignIng,"NO JALO",Toast.LENGTH_SHORT).show()



        }





    }

    private fun selectImage() {

        val intent= Intent()
        intent.type="image/*"
        intent.action=Intent.ACTION_GET_CONTENT

        startActivityForResult(intent,100)

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode==100&& resultCode==RESULT_OK){

            ImageUri=data?.data!!
            binding.Imgid.setImageURI(ImageUri)

        }

    }


    override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {

        val item = parent?.getItemAtPosition(position).toString()
        Toast.makeText(this@SignIng, item, Toast.LENGTH_SHORT).show()
    }


    private fun saveUsertoDatabase(usuario: users) {

        FirebaseAuth.getInstance().createUserWithEmailAndPassword(usuario.correo, usuario.contraseña)
            .addOnCompleteListener{
                if(!it.isSuccessful) return@addOnCompleteListener

                Log.d("SignIng", "Usuario creado con exito: ${it.result?.user?.uid}")




                usuario.uid = FirebaseAuth.getInstance().currentUser!!.uid
                uid= FirebaseAuth.getInstance().currentUser!!.uid
                uploadImage()
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

   /* fun fileUpload() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "*"
        startActivityForResult(intent, File)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == File) {
            if (resultCode == RESULT_OK) {
                val FileUri = data!!.data
                val Folder: StorageReference =
                    FirebaseStorage.getInstance().getReference().child("User")
                val file_name: StorageReference = Folder.child("file" + FileUri!!.lastPathSegment)
                file_name.putFile(FileUri).addOnSuccessListener { taskSnapshot ->
                    file_name.getDownloadUrl().addOnSuccessListener { uri ->
                        val hashMap =
                            HashMap<String, String>()
                        hashMap["link"] = java.lang.String.valueOf(uri)
                        myRef.setValue(hashMap)

                        //

                        Log.d("Mensaje", "Se subió correctamente")
                    }
                }
            }
        }
    }



    fun getFileFromAssets(context: Context, fileName: String): File = File(context.cacheDir, fileName)
        .also {
            if (!it.exists()) {
                it.outputStream().use { cache ->
                    context.assets.open(fileName).use { inputStream ->
                        inputStream.copyTo(cache)
                    }
                }
            }
        }
*/

   }




