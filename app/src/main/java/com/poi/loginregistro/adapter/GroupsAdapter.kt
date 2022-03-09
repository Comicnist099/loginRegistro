package com.poi.loginregistro.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.poi.loginregistro.GroupLogActivity
import com.poi.loginregistro.Modelos.Team
import com.poi.loginregistro.R
import kotlinx.android.synthetic.main.lista_salon.view.*

class GroupsAdapter (val teams : List<Team>) : RecyclerView.Adapter<GroupsAdapter.GroupsViewHolder>(){
    class GroupsViewHolder(val view: View): RecyclerView.ViewHolder(view)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GroupsViewHolder {
        return GroupsViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.lista_salon,parent,false)
        );
    }


    override fun onBindViewHolder(holder: GroupsViewHolder, position: Int) {
        val team : Team =teams[position]

        holder.view.textNombreSalon.text=team.name


        holder.view.setOnClickListener{
            val intent= Intent(holder.view.context, GroupLogActivity::class.java)
            intent.putExtra("teamUid",team.name)
            holder.view.context.startActivity(intent)
        }
    }

    override fun getItemCount()= teams.size
}