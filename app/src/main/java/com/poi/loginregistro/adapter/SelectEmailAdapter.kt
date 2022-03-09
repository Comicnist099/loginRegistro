package com.poi.loginregistro.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.R
import com.google.firebase.storage.FirebaseStorage
import com.poi.loginregistro.Modelos.contacto
import com.poi.loginregistro.helpers.General
import kotlinx.android.synthetic.main.contact_select_recycler.view.*

class SelectEmailAdapter(val contacts : List<contacto>) : RecyclerView.Adapter<SelectEmailAdapter.MembersViewHolder>(){
    class MembersViewHolder(val view: View): RecyclerView.ViewHolder(view)
    val databaseReference : DatabaseReference = FirebaseDatabase.getInstance().reference
    var listString : String= ""


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MembersViewHolder {
        return MembersViewHolder(
            LayoutInflater.from(parent.context).inflate(com.poi.loginregistro.R.layout.contact_select_recycler,parent,false)
        );
    }

    fun returnSelectedItems() : List<String>{
        val selectedItems = mutableListOf<String>()
        for(contact in contacts){

            if(!contact.correo.equals(General.UserInstance.getUserInstance()?.correo) && contact.isSelected)
                selectedItems.add(contact.correo)
        }
        return selectedItems
    }
    override fun onBindViewHolder(holder: MembersViewHolder, position: Int) {
        val contact : contacto =contacts[position]

        if(contact.uid.equals(General.UserInstance.getUserInstance()?.uid.toString())){
            holder.view.visibility= View.GONE
        }else{



            holder.view.setOnClickListener{
                contacts[position].isSelected=! contacts[position].isSelected

                if(!contacts[position].isSelected){
                    holder.view.circleSelected.setBackgroundResource(com.poi.loginregistro.R.drawable.ic_circle_state)
                }else{
                    holder.view.circleSelected.setBackgroundResource(com.poi.loginregistro.R.drawable.ic_circle_state_active)
                }


            }

            holder.view.contactName.text= contact.username


        }

        // holder.view.userNameChat.text=contact.userName

    }

    override fun getItemCount()= contacts.size
}