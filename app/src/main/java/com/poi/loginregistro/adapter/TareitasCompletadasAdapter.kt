package com.poi.loginregistro.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.poi.loginregistro.Modelos.TareitasCompletadas
import com.poi.loginregistro.R
import kotlinx.android.synthetic.main.tarea_recycler.view.*

class TareitasCompletadasAdapter (val assignments : List<TareitasCompletadas>) : RecyclerView.Adapter<TareitasCompletadasAdapter.AssignmentsViewHolder>(){
    class AssignmentsViewHolder(val view: View): RecyclerView.ViewHolder(view)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AssignmentsViewHolder {
        return AssignmentsViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.tarea_recycler,parent,false)
        );
    }
    //TODO :Hacer que se despliegue el evento onClick en el adapter, que me lleve a la tarea y que pueda picar en el boton entregar para ingresar a la tabla TasksCompleted
    override fun onBindViewHolder(holder: AssignmentsViewHolder, position: Int) {
        val assignment : TareitasCompletadas =assignments[position]

        holder.view.groupNameAssignment.text=assignment.team.split("-")[0]
        holder.view.assignmentNameAssignment.text=assignment.name
        holder.view.assignmentDate.text=assignment.date.toString()
    }

    override fun getItemCount()= assignments.size
}