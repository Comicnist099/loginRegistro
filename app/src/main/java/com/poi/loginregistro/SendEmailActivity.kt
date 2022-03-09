package com.poi.loginregistro

import android.app.Dialog
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.basgeekball.awesomevalidation.AwesomeValidation
import com.basgeekball.awesomevalidation.ValidationStyle
import com.basgeekball.awesomevalidation.utility.RegexTemplate
import com.google.firebase.database.*
import com.poi.loginregistro.Modelos.Team
import com.poi.loginregistro.Modelos.contacto
import com.poi.loginregistro.Modelos.users
import com.poi.loginregistro.adapter.SelectEmailAdapter
import com.poi.loginregistro.adapter.SelectMembersAdapter
import com.poi.loginregistro.helpers.General
import kotlinx.android.synthetic.main.activity_create_group.*
import kotlinx.android.synthetic.main.activity_send_email.*

class SendEmailActivity : AppCompatActivity() {

    private var teamUid  :String = ""
    private lateinit var dialog : Dialog
    val databaseReference : DatabaseReference = FirebaseDatabase.getInstance().reference
    var contactList : ArrayList<contacto>? = ArrayList();

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_send_email)

        supportActionBar?.title = "Enviar Correo"

        val bundle = intent.extras
        teamUid = bundle!!.getString("teamUid").toString()
        var layoutManager =  LinearLayoutManager(this)
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL)
        select_members_email_recycler.layoutManager = layoutManager
        val validator : AwesomeValidation = AwesomeValidation(ValidationStyle.BASIC)

        validator.addValidation(this,R.id.emailSubjectView, RegexTemplate.NOT_EMPTY, R.string.empty_name)

        validator.addValidation(this,R.id.emailContentView, RegexTemplate.NOT_EMPTY, R.string.empty_name)
        initMembers()

        sendEmailButton.setOnClickListener{
            val emailList= SelectEmailAdapter(contactList!!).returnSelectedItems()

            if(emailList.size>0){

                if(validator.validate()){
                    var emailString=""
                    for(email in emailList){
                        emailString+=email+","
                    }
                    emailString.dropLast(1)

                    sendEmail(emailSubjectView.text.toString(),emailContentView.text.toString(),emailString)
                    //Toast.makeText(this,emailString,Toast.LENGTH_SHORT).show()
                }


            }else{
                Toast.makeText(this,"Selecciona minimo un miembro", Toast.LENGTH_SHORT).show()
            }
        }

    }

    private fun sendEmail(subject: String, content: String, emails: String) {
        val mIntent= Intent(Intent.ACTION_SEND)

        mIntent.data= Uri.parse("mailto:")
        mIntent.type="text/plain"
        mIntent.putExtra(Intent.EXTRA_EMAIL,arrayOf(emails))
        mIntent.putExtra(Intent.EXTRA_SUBJECT,subject)
        mIntent.putExtra(Intent.EXTRA_TEXT,content)

        try{
            startActivity(Intent.createChooser(mIntent,"Choose email client: "))
        }catch (e: Exception){
            Toast.makeText(this,"One email may not exist",Toast.LENGTH_SHORT).show()
        }

    }

    private fun initMembers() {


        databaseReference.child(contacto::class.java.simpleName).addValueEventListener(object :
            ValueEventListener {
            override fun onCancelled(snapshotError: DatabaseError) {

            }

            override fun onDataChange(snapshot: DataSnapshot) {
                if (contactList != null) {
                    contactList?.clear()
                }
                val activeUid: String = General.UserInstance.getUserInstance()?.uid.toString()
                snapshot!!.children.forEach {
                    val contact: contacto? = it.getValue(contacto::class.java) as contacto
                    if (contact != null) {

                        if (!contact.uid.equals(activeUid)) {
                            contactList?.add(contact)
                        }

                    }


                }
                select_members_email_recycler.adapter = SelectEmailAdapter(contactList!!)

            }
        })
    }



}