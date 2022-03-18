package com.poi.loginregistro

import android.app.ProgressDialog
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.widget.Toast
import androidx.core.view.isGone
import androidx.core.view.isInvisible
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ListResult
import com.poi.loginregistro.Modelos.ChatMessage
import com.poi.loginregistro.Modelos.users
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import kotlinx.android.synthetic.main.activity_chat_log2.*
import kotlinx.android.synthetic.main.chat_from_row.view.*
import kotlinx.android.synthetic.main.chat_to_row.view.*
import kotlinx.android.synthetic.main.contact_select_recycler.view.*
import java.text.SimpleDateFormat
import java.util.*
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec
var contador=0
var Direccion=""
class ChatLogActivity2 : AppCompatActivity() {

    companion object {
        val TAG = "ChatLog"
        var currentUser: users? = null
        val USER_KEY = "USER_KEY"
    }
    val adapter = GroupAdapter<GroupieViewHolder>()
    private var uid: String = ""
    private var name: String = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_log2)

        recyclerViewChatLog.adapter = adapter


        //val username = intent.getStringExtra(NewMessageActivity.USER_KEY)
        val user = intent.getParcelableExtra<users>(NewMessageActivity.USER_KEY)
        supportActionBar?.title = user?.username

        //setupDummyData()
        val a = intent.getParcelableExtra<users>(NewMessageActivity.USER_KEY)
        val toId = a?.uid
        Direccion=toId.toString()
        listenFromMessages()


        btnSendChat.setOnClickListener {
            Log.d(TAG, "Attempt to send message...")
            performSendMessage()
        }
        btnImagen.setOnClickListener {
            Log.d(TAG, "Hola..")
            selectImage()

        }
////
////


    }

    private fun selectImage() {

        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT

        startActivityForResult(intent, 100)

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 100 && resultCode == RESULT_OK) {

            ImageUri = data?.data!!
            uploadImage()
            //binding.Imgid.setImageURI(ImageUri)

        }

    }
    private fun uploadImage() {
        //val progressDialog= ProgressDialog(this)
        //progressDialog.setMessage("uploading File ...")
        //progressDialog.setCancelable(false)
        //progressDialog.show()
        val fromId = FirebaseAuth.getInstance().uid
        val formatter= SimpleDateFormat("yyyy_MM_dd_HH_mm", Locale.getDefault())
        val now= Date()
        val user = intent.getParcelableExtra<users>(NewMessageActivity.USER_KEY)
        val toId = user?.uid
        Direccion=toId.toString()
        val storageReference=FirebaseStorage.getInstance().getReference("/user-messages/$fromId/$toId/$contador")
        storageReference.putFile(ImageUri).addOnSuccessListener {
            Log.d(TAG,"Se subio la imagen de chat")

        }.addOnFailureListener {

        }
    }

    private fun listenFromMessages() {

        val fromId = FirebaseAuth.getInstance().uid
        val user = intent.getParcelableExtra<users>(NewMessageActivity.USER_KEY)
        val toId = user?.uid
        val ref = FirebaseDatabase.getInstance().getReference("/user-messages/$fromId/$toId")
        val Fotosref = FirebaseStorage.getInstance().getReference("/user-messages/$fromId/$toId")

        val idFoto = toId.toString()

        ref.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val chatMessage = snapshot.getValue(ChatMessage::class.java)

                if (chatMessage != null) {

                    Log.d(TAG, chatMessage.text)



                    if (chatMessage.fromId == FirebaseAuth.getInstance().uid) {
                        contador++
                        adapter.add(ChatToItem(chatMessage.text))


                    } else {
                        adapter.add(ChatFromItem(chatMessage.text, idFoto))
                    }

                }

            }

            override fun onCancelled(error: DatabaseError) {


            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {


            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {


            }

            override fun onChildRemoved(snapshot: DataSnapshot) {


            }

        })
    }


    private fun performSendMessage() {

        val uid = FirebaseAuth.getInstance().uid
        val ref = FirebaseDatabase.getInstance().getReference("/users/$uid")
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                //send message to firebase

                SettingsActivity.currentUser = snapshot.getValue(users::class.java)

                val users = SettingsActivity.currentUser?.encrypted

                if (users.toString().equals("activated")) {

                    val text = textSendChat.text.toString()

                    val mensajeCifrado = CifradoUtils.cifrar(text, "Hola")


                    val fromId = FirebaseAuth.getInstance().uid
                    val user = intent.getParcelableExtra<users>(NewMessageActivity.USER_KEY)
                    val toId = user?.uid

                    if (fromId == null) return

                    //val reference = FirebaseDatabase.getInstance().getReference("/messages").push()
                    val reference =
                        FirebaseDatabase.getInstance().getReference("/user-messages/$fromId/$toId")
                            .push()

                    val toReference =
                        FirebaseDatabase.getInstance().getReference("/user-messages/$toId/$fromId")
                            .push()


                    val chatMessage = ChatMessage(
                        reference.key!!,
                        mensajeCifrado,
                        fromId,
                        toId.toString(),
                        "",
                        "",
                        "",
                        ServerValue.TIMESTAMP
                    )


                    reference.setValue(chatMessage)
                        .addOnSuccessListener {
                            Log.d(TAG, "Saved message: ${reference.key}")
                            textSendChat.text.clear()
                            recyclerViewChatLog.scrollToPosition(adapter.itemCount - 1)
                        }

                    toReference.setValue(chatMessage)

                } else {
                    val text = textSendChat.text.toString()

                    val fromId = FirebaseAuth.getInstance().uid
                    val user = intent.getParcelableExtra<users>(NewMessageActivity.USER_KEY)
                    val toId = user?.uid

                    val mensajeCifrado = CifradoUtils.cifrar(text, "Hola")

                    val mensajeDescifrado = CifradoUtils.descifrar(mensajeCifrado, "Hola")

                    if (fromId == null) return
                    //val reference = FirebaseDatabase.getInstance().getReference("/messages").push()
                    val reference =
                        FirebaseDatabase.getInstance().getReference("/user-messages/$fromId/$toId")
                            .push()

                    val toReference =
                        FirebaseDatabase.getInstance().getReference("/user-messages/$toId/$fromId")
                            .push()

                    val chatMessage = ChatMessage(
                        reference.key!!,
                        mensajeDescifrado,
                        fromId,
                        toId.toString(),
                        "",
                        "",
                        "",

                                ServerValue.TIMESTAMP
                    )

                    reference.setValue(chatMessage)
                        .addOnSuccessListener {
                            Log.d(TAG, "Saved message: ${reference.key}")
                            textSendChat.text.clear()
                            recyclerViewChatLog.scrollToPosition(adapter.itemCount - 1)
                        }

                    toReference.setValue(chatMessage)

                    /*val latestMessagesRef = FirebaseDatabase.getInstance().getReference("/latest-messages/$fromId/$toId")
                    latestMessagesRef.setValue(chatMessage)

                    val latestMessagesToRef = FirebaseDatabase.getInstance().getReference("/latest-messages/$toId/$fromId")
                    latestMessagesToRef.setValue(chatMessage) */
                }

            }

            override fun onCancelled(error: DatabaseError) {

            }

        })


    }

    object CifradoUtils {

        private const val CIPHER_TRANSFORM = ("AES/CBC/PKCS5Padding")

        fun cifrar(textoPlano: String, llaveSecreta: String): String {

            val cipher = Cipher.getInstance(CIPHER_TRANSFORM)

            val llavesBytesFinal = ByteArray(16)
            val llavesBytesOriginal = llaveSecreta.toByteArray(charset("UTF-8"))

            System.arraycopy(
                llavesBytesOriginal, 0, llavesBytesFinal, 0,
                Math.min(llavesBytesOriginal.size, llavesBytesFinal.size)
            )

            val secretKeySpec = SecretKeySpec(llavesBytesFinal, "AES")

            val initVector = IvParameterSpec(llavesBytesFinal)

            cipher.init(
                Cipher.ENCRYPT_MODE,
                secretKeySpec,
                initVector
            )

            val textoCifrado = cipher.doFinal(textoPlano.toByteArray(charset("UTF-8")))

            val resultadoBase64 = String(Base64.encode(textoCifrado, Base64.NO_PADDING))

            return resultadoBase64
        }

        fun descifrar(textoCifrado: String, llaveSecreta: String): String {

            val textoCifradoBytes = Base64.decode(textoCifrado, Base64.NO_PADDING)

            val cipher = Cipher.getInstance(CIPHER_TRANSFORM)

            val llavesBytesFinal = ByteArray(16)
            val llavesBytesOriginal = llaveSecreta.toByteArray(charset("UTF-8"))

            System.arraycopy(
                llavesBytesOriginal, 0, llavesBytesFinal, 0,
                Math.min(llavesBytesOriginal.size, llavesBytesFinal.size)
            )

            val secretKeySpec = SecretKeySpec(llavesBytesFinal, "AES")

            val initVector = IvParameterSpec(llavesBytesFinal)

            cipher.init(
                Cipher.DECRYPT_MODE,
                secretKeySpec,
                initVector
            )

            val textPlanoRec = String(cipher.doFinal(textoCifradoBytes))

            return textPlanoRec
        }

    }

    /*private fun setupDummyData(){
        val adapter = GroupAdapter<GroupieViewHolder>()

        adapter.add(ChatFromItem("FROM MESSAGE"))
        adapter.add(ChatToItem("TO MESSAGE\nTOMESSAGE"))

        recyclerViewChatLog.adapter = adapter

    }*/


    class ChatFromItem(val text: String, val idfoto: String) : Item<GroupieViewHolder>() {

        override fun bind(viewHolder: GroupieViewHolder, position: Int) {


            val localFile = java.io.File.createTempFile("tempImage", "jpg")
            val storageReference =
                FirebaseStorage.getInstance().getReference("images/$idfoto")
            storageReference.getFile(localFile).addOnSuccessListener {
                val bitmap = BitmapFactory.decodeFile(localFile.absolutePath)
                viewHolder.itemView.PerfilImageChat.setImageBitmap(bitmap)

            }.addOnFailureListener {

            }

            viewHolder.itemView.textView_from.text = text

        }

        override fun getLayout(): Int {

            return R.layout.chat_from_row
        }

    }





    class ChatToItem(val text: String) : Item<GroupieViewHolder>() {
        val fromId = FirebaseAuth.getInstance().uid.toString()
        val localFile = java.io.File.createTempFile("tempImage", "jpg")

        val toId = Direccion.toString()


        override fun bind(viewHolder: GroupieViewHolder, position: Int) {

            val  storageReference= FirebaseStorage.getInstance().getReference("/user-messages/$fromId/$toId/$position")
            storageReference.getFile(localFile).addOnSuccessListener {
                val bitmap = BitmapFactory.decodeFile(localFile.absolutePath)
                viewHolder.itemView.FotoMandar.setImageBitmap(bitmap)


            }.addOnFailureListener{
                viewHolder.itemView.FotoMandar.isGone=true
            }

            viewHolder.itemView.textView_to.text = text
        }

        override fun getLayout(): Int {

            return R.layout.chat_to_row
        }


    }
}