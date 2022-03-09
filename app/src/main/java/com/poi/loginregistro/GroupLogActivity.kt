package com.poi.loginregistro

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Base64
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.poi.loginregistro.Modelos.*
import com.poi.loginregistro.group_fragment.Companion.GROUP_KEY
import com.poi.loginregistro.helpers.General
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import kotlinx.android.synthetic.main.activity_chat_group.*
import kotlinx.android.synthetic.main.group_to_row.view.*
import kotlinx.android.synthetic.main.message_group.view.*
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

class GroupLogActivity : AppCompatActivity() {

    private val database = FirebaseDatabase.getInstance()

    val adapter = GroupAdapter<GroupieViewHolder>()
    private var teamUid  :String = ""

    companion object {
        var currentUser: users? = null
        val TAG = "GroupLog"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_group)

        recyclerViewGroupLog.adapter = adapter

        val bundle = intent.extras
        teamUid = bundle!!.getString("teamUid").toString()

        supportActionBar?.title =teamUid.toString()
        //supportActionBar?.title = "Equipo"

        listenFromMessages()

        btnSendGroup.setOnClickListener {
            Log.d(GroupLogActivity.TAG, "Attempt to send message...")
            fetchCurrentUser()
        }

        sendEmailButton.setOnClickListener{
            val intent = Intent(this,SendEmailActivity::class.java)
            intent.putExtra("teamUid",teamUid)
            startActivity(intent)
        }

    }

    private fun listenFromMessages(){

        val bundle = intent.extras
        teamUid = bundle!!.getString("teamUid").toString()

        val uid = FirebaseAuth.getInstance().uid
        val career = teamUid
        val ref = FirebaseDatabase.getInstance().getReference("/grupo-messages/$career")

        ref.addChildEventListener(object: ChildEventListener{

            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {

                currentUser = snapshot.getValue(users::class.java)
                Log.d("LatestMessages", "Current user ${currentUser?.username}")
                val chatGroup = snapshot.getValue(ChatGroup::class.java)
                if (chatGroup != null) {

                    Log.d(GroupLogActivity.TAG, chatGroup.text)

                    //adapter.add(GroupFromItem(chatGroup.text, chatGroup.username))

                    if (chatGroup.fromId == FirebaseAuth.getInstance().uid){

                        adapter.add(GroupToItem(chatGroup.text))
                    }
                    else {
                        adapter.add(GroupFromItem(chatGroup.text, chatGroup.username))
                    }

                }

            }

            /*override fun onDataChange(snapshot: DataSnapshot) {
                currentUser = snapshot.getValue(User::class.java)
                Log.d("LatestMessages", "Current user ${currentUser?.username}")

                rvChats = findViewById(R.id.recyclerViewGroupLog)

                chatAdapter = ChatAdapter(messageList)
                rvChats.adapter = chatAdapter

                findViewById<Button>(R.id.btnSendGroup).setOnClickListener {

                    val message = findViewById<EditText>(R.id.textSendGroup).text.toString()
                    if (message.isNotEmpty()) {

                        val msg = Mensaje("", message, currentUser?.uid.toString(), currentUser?.username.toString(), ServerValue.TIMESTAMP)
                        sendMessage(msg)
                    }
                }

                readMessages()
            }*/

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

    private fun fetchCurrentUser(){

        val uid = FirebaseAuth.getInstance().uid
        val ref = FirebaseDatabase.getInstance().getReference("/users/$uid")
        ref.addListenerForSingleValueEvent(object: ValueEventListener{

            override fun onDataChange(snapshot: DataSnapshot) {
                SettingsActivity.currentUser = snapshot.getValue(users::class.java)
                Log.d("LatestMessages", "Current user ${LatestMessagesA.currentUser?.username}")

                val users = SettingsActivity.currentUser?.encrypted

                if (users.toString().equals("activated")){
                    //send message to firebase

                    val text = textSendGroup.text.toString()

                    val mensajeCifrado = GroupLogActivity.CifradoUtils.cifrar(text, "Yoshikage")


                    val bundle = intent.extras
                    teamUid = bundle!!.getString("teamUid").toString()

                    val uid = FirebaseAuth.getInstance().uid
                    val career = teamUid
                    val username = General.UserInstance.getUserInstance()?.username.toString()
                    //val careerUid = career?.uid
                    //val ref = FirebaseDatabase.getInstance().getReference("/grupo-messages/$uid/$careerUid")


                    if (uid == null) return

                    //val reference = FirebaseDatabase.getInstance().getReference("/messages").push()
                    val reference = FirebaseDatabase.getInstance().getReference("/grupo-messages/$career").push()

                    //val toReference = FirebaseDatabase.getInstance().getReference("/grupo-messages/$careerUid").push()

                    val chatMessage = ChatGroup(reference.key!!, username.toString(), mensajeCifrado, uid, career, System.currentTimeMillis()/1000 )
                    reference.setValue(chatMessage)
                        .addOnSuccessListener {
                            Log.d(GroupLogActivity.TAG, "Saved message: ${reference.key}")
                            textSendGroup.text.clear()
                            recyclerViewGroupLog.scrollToPosition(adapter.itemCount -1)
                        }

                    //toReference.setValue(chatMessage)

                }
                else {
                    //send message to firebase

                    val text = textSendGroup.text.toString()

                    val mensajeCifrado = GroupLogActivity.CifradoUtils.cifrar(text, "Yoshikage")

                    val mensajeDescifrado = GroupLogActivity.CifradoUtils.descifrar(mensajeCifrado, "Yoshikage")


                    val bundle = intent.extras
                    teamUid = bundle!!.getString("teamUid").toString()

                    val uid = FirebaseAuth.getInstance().uid
                    val career = teamUid
                    val career2 = intent.getParcelableExtra<Carrera>(GROUP_KEY)
                    val username = General.UserInstance.getUserInstance()?.username.toString()
                    val careerUid = career2?.uid
                    //val ref = FirebaseDatabase.getInstance().getReference("/grupo-messages/$uid/$careerUid")


                    if (uid == null) return

                    //val reference = FirebaseDatabase.getInstance().getReference("/messages").push()
                    val reference = FirebaseDatabase.getInstance().getReference("/grupo-messages/$career").push()

                    //val toReference = FirebaseDatabase.getInstance().getReference("/grupo-messages/$careerUid").push()

                    val chatMessage = ChatGroup(reference.key!!, username, mensajeDescifrado, uid, career, System.currentTimeMillis()/1000 )
                    reference.setValue(chatMessage)
                        .addOnSuccessListener {
                            Log.d(GroupLogActivity.TAG, "Saved message: ${reference.key}")
                            textSendGroup.text.clear()
                            recyclerViewGroupLog.scrollToPosition(adapter.itemCount -1)
                        }

                    //toReference.setValue(chatMessage)

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

}

class GroupFromItem(val text: String, val username: String): Item<GroupieViewHolder>(){

    override fun bind(viewHolder: GroupieViewHolder, position: Int) {

        viewHolder.itemView.tv_name.text = username
        viewHolder.itemView.tv_message.text = text
    }

    override fun getLayout(): Int {

        return R.layout.message_group
    }

}

class GroupToItem(val text: String): Item<GroupieViewHolder>(){

    override fun bind(viewHolder: GroupieViewHolder, position: Int) {

        viewHolder.itemView.textViewGroup.text = text
    }

    override fun getLayout(): Int {

        return R.layout.group_to_row
    }

}



