package com.poi.loginregistro

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.poi.loginregistro.Modelos.users
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder

class LatestMessagesA : AppCompatActivity() {


    companion object {
        var currentUser: users? = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_latest_messages)

        //recycler_view_latest_msg.adapter = adapter

        //setupDummyRows()
        //listenFromLatestMessages()
        fetchCurrentUser()

        supportActionBar?.title = "SunTeams"

        verifyUserIsLoggedIn()

        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.my_navigation)
        //val miDrawer: DrawerLayout = findViewById(R.id.miDrawer)
        val navController = findNavController(R.id.fragment)
        bottomNavigationView.setupWithNavController(navController)
        val appBarConfiguration = AppBarConfiguration(setOf(R.id.menu_new_message, R.id.group_make, R.id.menu_activity))

        setupActionBarWithNavController(navController, appBarConfiguration)





    }


    val adapter = GroupAdapter<GroupieViewHolder>()



    private fun fetchCurrentUser(){

        val uid = FirebaseAuth.getInstance().uid
        val ref = FirebaseDatabase.getInstance().getReference("/users/$uid")
        ref.addListenerForSingleValueEvent(object: ValueEventListener{

            override fun onDataChange(snapshot: DataSnapshot) {
                currentUser = snapshot.getValue(users::class.java)
                Log.d("LatestMessages", "Current user ${currentUser?.username}")
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }

    private fun verifyUserIsLoggedIn(){
        val uid = FirebaseAuth.getInstance().uid
        if (uid == null) {

            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when(item?.itemId){
            R.id.salir_Sign_Out -> {
                FirebaseAuth.getInstance().signOut()
                val intent = Intent(this, MainActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
            }
        }

        when(item?.itemId){
            R.id.editarUsuario -> {
                val intent = Intent(this, SettingsActivity::class.java)
                startActivity(intent)

            }
        }

        return super.onOptionsItemSelected(item)
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.salir_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }


}