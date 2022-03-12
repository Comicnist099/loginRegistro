package com.poi.loginregistro

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.poi.loginregistro.Modelos.users
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import kotlinx.android.synthetic.main.activity_new_message.*
import kotlinx.android.synthetic.main.fragment_mensajes_priv.*
import kotlinx.android.synthetic.main.user_row_new_message.view.*

class NewMessageActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_message)

        supportActionBar?.title = "Select User"

        //val adapter = GroupAdapter<GroupieViewHolder>()

        //recyclerViewNewM.adapter = adapter

        fetchUsers()

        //recyclerViewNewM.layoutManager = LinearLayoutManager(this)
    }

    companion object {
        val USER_KEY = "USER_KEY"
    }

    private fun fetchUsers(){
       val ref = FirebaseDatabase.getInstance().getReference("/users")
        ref.addListenerForSingleValueEvent(object: ValueEventListener{


                override fun onDataChange(snapshot: DataSnapshot) {
                val adapter = GroupAdapter<GroupieViewHolder>()

                snapshot.children.forEach{
                    Log.d("NewMessage", it.toString())
                    val user = it.getValue(users::class.java)
                    if(user != null){
                        adapter.add(UserItem(user))
                    }

                }

                adapter.setOnItemClickListener { item, view ->

                    val userItem = item as UserItem

                    val intent = Intent(view.context, ChatLogActivity2::class.java)
                    //intent.putExtra(USER_KEY, userItem.user.username)
                    intent.putExtra(USER_KEY, userItem.user)
                    startActivity(intent)

                    finish()
                }

                    recyclerViewNewM.adapter = adapter
            }
            override fun onCancelled(error: DatabaseError) {
            }
        })
    }
}

class UserItem(val user: users?): Item<GroupieViewHolder>(){

    override fun bind(viewHolder: GroupieViewHolder, position: Int) {

        if (user != null) {
            viewHolder.itemView.username_row.text = user.username
        }
        viewHolder.itemView.status_user.text = user!!.status
    }

    override fun getLayout(): Int {

        return R.layout.user_row_new_message
    }
}

/*
class CustomAdapter: RecyclerView.Adapter<RecyclerView.ViewHolder> {
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        TODO("Not yet implemented")
    }
}*/
