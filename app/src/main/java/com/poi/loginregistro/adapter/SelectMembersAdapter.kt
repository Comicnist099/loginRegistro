package com.poi.loginregistro.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.storage.FirebaseStorage
import com.poi.loginregistro.Modelos.contacto
import com.poi.loginregistro.Modelos.users
import com.poi.loginregistro.R
import kotlinx.android.synthetic.main.contact_select_recycler.view.*
import java.nio.file.Paths

class SelectMembersAdapter(val contacts : List<contacto>) : RecyclerView.Adapter<SelectMembersAdapter.MembersViewHolder>() {

    class MembersViewHolder(val view: View): RecyclerView.ViewHolder(view)
    var listString : String= ""
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MembersViewHolder {
        return MembersViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.contact_select_recycler,parent,false)
        );
    }


    fun returnSelectedItems() : MutableList<contacto>{
        val selectedItems = mutableListOf<contacto>()
        for(contact in contacts){

            if(contact.isSelected){
                selectedItems.add(contact)
            }
        }
        return selectedItems
    }
    override fun onBindViewHolder(holder: MembersViewHolder, position: Int) {
        val contact : contacto = contacts[position]


        holder.view.setOnClickListener{
            contacts[position].isSelected=! contacts[position].isSelected

            if(!contacts[position].isSelected){
                holder.view.circleSelected.setBackgroundResource(R.drawable.ic_circle_state)
            }else{
                holder.view.circleSelected.setBackgroundResource(R.drawable.ic_circle_state_active)
            }

        }

        holder.view.contactName.text= contact.username



        // holder.view.userNameChat.text=contact.userName

    }

    override fun getItemCount()= contacts.size
}