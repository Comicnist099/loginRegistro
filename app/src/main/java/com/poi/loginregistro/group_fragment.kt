package com.poi.loginregistro

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.*
import com.poi.loginregistro.Modelos.Team
import com.poi.loginregistro.Modelos.contacto
import com.poi.loginregistro.Modelos.users
import com.poi.loginregistro.adapter.GroupsAdapter
import com.poi.loginregistro.helpers.General
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import kotlinx.android.synthetic.main.fragment_group_fragment.*
import kotlinx.android.synthetic.main.lista_salon.view.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [group_fragment.newInstance] factory method to
 * create an instance of this fragment.
 */


class group_fragment : Fragment() {


    private val databaseReference : DatabaseReference = FirebaseDatabase.getInstance().reference
    private var teams = mutableListOf<Team>()
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //fetchGroup()

        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)

        }
    }

    /*

    private fun fetchGroup(){
        val ref = FirebaseDatabase.getInstance().getReference("/Team")
        ref.addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val adapter = GroupAdapter<GroupieViewHolder>()

                snapshot.children.forEach{
                    //Log.d("group_fragment", it.toString())
                    val career = it.getValue(Team::class.java)
                    if(career != null){
                        adapter.add(UserItem(career))
                    }

                }
                adapter.setOnItemClickListener { item, view ->

                    val careerItem = item as UserItem

                    val intent = Intent(view.context, GroupLogActivity::class.java)
                    //intent.putExtra(USER_KEY, userItem.user.username)
                    //intent.putExtra(GROUP_KEY, careerItem.career)
                    startActivity(intent)

                }

                rv_salones.adapter = adapter
            }
            override fun onCancelled(error: DatabaseError) {
            }
        })

    }

    class UserItem(val career: Team): Item<GroupieViewHolder>(){

        override fun bind(viewHolder: GroupieViewHolder, position: Int) {

            viewHolder.itemView.textNombreSalon.text = career.name
        }

        override fun getLayout(): Int {

            return R.layout.lista_salon
        }
    }
*/

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        /* // Inflate the layout for this fragment
       //return inflater.inflate(R.layout.fragment_group_fragment, container, false)
       val vView = inflater.inflate(
           R.layout.fragment_group_fragment,
           container,
           false
       )

     var botonMensajeNuevo =vView.findViewById<View>(R.id.new_group)
       botonMensajeNuevo.setOnClickListener(View.OnClickListener {
           val nuevoMensaje = Intent(vView.context, CreateGroupActivity::class.java)
           startActivity(nuevoMensaje)

       })*/

        return inflater.inflate(R.layout.fragment_group_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        var layoutManager =  LinearLayoutManager(context)
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL)
        rv_salones.layoutManager = layoutManager

        initGroups(view)

        var botonMensajeNuevo =view.findViewById<View>(R.id.new_group)

        botonMensajeNuevo.setOnClickListener{

            val intent = Intent(activity, CreateGroupActivity::class.java)

            startActivity(intent)

        }

    }


    private fun initGroups(view : View)
    {

        databaseReference.child(Team::class.java.simpleName).addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                teams.clear()

                snapshot.children.forEach({ team ->

                    val contactList = mutableListOf<contacto>()
                    team.children.forEach({contact->
                        val contact = contacto  (
                            contact.child("uid").value.toString(),
                            contact.child("username").value.toString(),
                            contact.child("status").value.toString(),
                            contact.child("encrypted").value.toString(),
                            contact.child("carrera").value.toString()
                                )
                        contactList.add(contact)
                    })

                    val aux =Team(team.key.toString(),contactList)

                    currentUser = snapshot.getValue(users::class.java)


                    for(member in aux.members){
                        if(member.uid.equals(General.UserInstance.getUserInstance()?.uid.toString())){
                            teams.add(aux)
                        }
                    }
                })

                val recycler=view.findViewById<RecyclerView>(R.id.rv_salones)
                if (teams?.size!! > 0 ) {
                    recycler.adapter= GroupsAdapter(teams)
                    recycler.smoothScrollToPosition(teams!!.size - 1)
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
         * @return A new instance of fragment group_fragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            group_fragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }

        const val GROUP_KEY = "GROUP_KEY"

        var currentUser: users? = null
    }
}