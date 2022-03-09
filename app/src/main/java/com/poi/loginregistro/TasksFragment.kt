package com.poi.loginregistro

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout
import com.poi.loginregistro.adapter.TasksViewAdapter
import kotlinx.android.synthetic.main.fragment_group_fragment.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [TasksFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class TasksFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

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
        val v=inflater.inflate(R.layout.fragment_tasks, container, false)

        initTabLayout(v)
        return v;
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        var botonMensajeNuevo =view.findViewById<View>(R.id.addTask)

        botonMensajeNuevo.setOnClickListener{

            val intent = Intent(activity, CrearTasksActivity::class.java)

            startActivity(intent)

        }

    }

    private fun initTabLayout(v : View) {
        //Aqui estoy cocinando arroz
        val tab =v.findViewById<TabLayout>(R.id.tabLayout)
        val pager= v.findViewById<ViewPager>(R.id.viewPager)
        //Aqui un pana me paso las verduras y las metio a la olla
        pager.isSaveEnabled=false; //Esto hace que ya no truene por el unique id que tenian los otros Fragments ahora unos huevitos con chorizo de vaca
        val adapter= TasksViewAdapter(childFragmentManager)
        adapter.addFragment(AssignedTasks(),"Assigned")
        adapter.addFragment(CompletedTaskFragment(), "Completed")
        pager.adapter=adapter;
        //Aqui junto to/do y a darle con mole de olla
        tab.setupWithViewPager(pager);


    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment TasksFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            TasksFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}