package com.poi.loginregistro.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.poi.loginregistro.DeliverTaskActivity
import com.poi.loginregistro.Modelos.Tareitas
import com.poi.loginregistro.R
import kotlinx.android.synthetic.main.tarea_recycler.view.*

class TareitasAdapter(val assignments : List<Tareitas>) : RecyclerView.Adapter<TareitasAdapter.AssignmentsViewHolder>(){
    class AssignmentsViewHolder(val view: View): RecyclerView.ViewHolder(view)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AssignmentsViewHolder {
        return AssignmentsViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.tarea_recycler,parent,false)
        );
    }
    //TODO :Hacer que se despliegue el evento onClick en el adapter, que me lleve a la tarea y que pueda picar en el boton entregar para ingresar a la tabla TasksCompleted
    override fun onBindViewHolder(holder: AssignmentsViewHolder, position: Int) {
        val assignment : Tareitas =assignments[position]

        holder.view.groupNameAssignment.text=assignment.team
        holder.view.assignmentNameAssignment.text=assignment.name

        holder.view.assignmentDate.text=assignment.date.toString()
        holder.view.setOnClickListener{
            val intent = Intent(holder.view.context, DeliverTaskActivity::class.java)
            // intent.putExtra("name",assignment.name)
            // intent.putExtra("description",assignment.description)
            //intent.putExtra("date",assignment.date.toString())
            intent.putExtra("team",assignment.team)
            intent.putExtra("id",assignment.id)


            holder.view.context?.startActivity(intent)
        }
    }

    override fun getItemCount()= assignments.size
}