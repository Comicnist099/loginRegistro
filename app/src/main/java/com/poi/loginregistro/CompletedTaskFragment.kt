package com.poi.loginregistro

import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.*
import com.poi.loginregistro.Modelos.TareitasCompletadas
import com.poi.loginregistro.adapter.TareitasCompletadasAdapter
import com.poi.loginregistro.helpers.General
import kotlinx.android.synthetic.main.fragment_completed_task.*
import java.text.SimpleDateFormat
import java.util.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [CompletedTaskFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class CompletedTaskFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private val databaseReference : DatabaseReference = FirebaseDatabase.getInstance().reference

    private val tareas= mutableListOf<TareitasCompletadas>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_completed_task, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val layoutManager =  LinearLayoutManager(context);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        completedTasksRecyclerView.layoutManager = layoutManager;
        // recyclerViewChats.layoutManager= LinearLayoutManager(activity)
        initAssignment(view)
    }


    private fun initAssignment(view:View) {

        databaseReference.child(TareitasCompletadas::class.java.simpleName)
            .child(General.UserInstance.getUserInstance()?.uid.toString())
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    tareas.clear()
                    snapshot.children.forEach { dbTeam ->

                        val assignment= dbTeam.getValue(TareitasCompletadas::class.java) as TareitasCompletadas

                        var date= assignment.date.toString().toLong()

                        //holder.view.date_message.text =SimpleDateFormat("dd/MM/yyyy - HH:mm:ss", Locale("es", "MX")).format(date)
                        val dateText = SimpleDateFormat("dd/MM/yyyy", Locale("es", "MX")).format(date)

                        assignment.date="Completed " +dateText.toString()

                        tareas.add(assignment)
                    }

                    if (tareas?.size!! > 0 ) {
                        val recycler =view.findViewById<RecyclerView>(R.id.completedTasksRecyclerView)
                        recycler.adapter= TareitasCompletadasAdapter(tareas)
                        recycler.smoothScrollToPosition(tareas!!.size - 1)
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }


            })

    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment CompletedTaskFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            CompletedTaskFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}