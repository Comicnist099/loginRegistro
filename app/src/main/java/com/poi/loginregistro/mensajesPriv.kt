package com.poi.loginregistro

import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import android.os.SystemClock
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.poi.loginregistro.GroupLogActivity.Companion.currentUser
import com.poi.loginregistro.Modelos.users
import com.poi.loginregistro.NewMessageActivity
import com.poi.loginregistro.dao.ContactDAO
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_settings.*
import kotlinx.android.synthetic.main.fragment_mensajes_priv.*


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [mensajesPriv.newInstance] factory method to
 * create an instance of this fragment.
 */
class mensajesPriv : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private var isRunning=true
    lateinit var Logro_bienvenido2 :ImageView
    lateinit var Logro_CambioEstado2 :ImageView
    lateinit var Logro_NuevoUsuario2 :ImageView
    lateinit var Logro_NuevoGrupo2 :ImageView
    lateinit var Logro_Tarea2 :ImageView
    lateinit var Logro_Tilin2 :ImageView
    lateinit var mediaPlayer: MediaPlayer





    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
            mediaPlayer=MediaPlayer.create(context,R.raw.notify)


        }
    }

    fun Logros(){
        val uid = FirebaseAuth.getInstance().uid

        val ref = FirebaseDatabase.getInstance().getReference("/users/$uid")

        ref.addListenerForSingleValueEvent(object: ValueEventListener {

            override fun onDataChange(snapshot: DataSnapshot) {
                currentUser = snapshot.getValue(users::class.java)
                val hashMap : java.util.HashMap<String, Any> = java.util.HashMap()

                if(currentUser?.Logro_esoTilin==true) {
                    Logro_Tilin2.setImageResource(R.drawable.logro_esotilin)

                }

                if(currentUser?.Logro_Tarea==true&&currentUser?.Logro_login==true&&currentUser?.Logro_createUsuario==true&&currentUser?.Logro_createGrupo==true&&currentUser?.Logro_cambiaEstado==true) {
                    if(currentUser?.Logro_esoTilin==false){

                        val linearLayout = LinearLayout(context)

                        // populate layout with your image and text
                        // or whatever you want to put in here
                        val imageView = ImageView(context)

                        // adding image to be shown
                        imageView.setImageResource(R.drawable.logro_esotilin)

                        // adding image to linearlayout
                        linearLayout.addView(imageView)
                        val toast = Toast(context)

                        // showing toast on bottom
                        toast.setGravity(Gravity.BOTTOM, 0, 100)
                        toast.duration = Toast.LENGTH_LONG

                        // setting view of toast to linear layout
                        toast.setView(linearLayout)
                        toast.show()
                        val intent = Intent(context,LatestMessagesA::class.java)
                        startActivity(intent)
                    }

                    hashMap.put("logro_esoTilin",true)
                    ContactDAO.update(SettingsActivity.currentUser?.uid.toString(),hashMap)?.addOnSuccessListener {
                        }
                }

                if(currentUser?.Logro_Tarea==true){
                    Logro_Tarea2.setImageResource(R.drawable.logro_tareaentregada)
                }

                if(currentUser?.Logro_login==true){
                    Logro_bienvenido2.setImageResource(R.drawable.logro_bienvenido)
                }
                if(currentUser?.Logro_createUsuario==true){
                    Logro_NuevoUsuario2.setImageResource(R.drawable.logro_nuevouser)
                }
                if(currentUser?.Logro_createGrupo==true){
                    Logro_NuevoGrupo2.setImageResource(R.drawable.logro_creargrupo)
                }
                if(currentUser?.Logro_cambiaEstado==true){
                    Logro_CambioEstado2.setImageResource(R.drawable.logro_cambioestado)
                }


            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        //return inflater.inflate(R.layout.fragment_mensajes_priv, container, false)
        val vView = inflater.inflate(
            R.layout.fragment_mensajes_priv,
            container,
            false
        )
        var Logro_bienvenido =vView.findViewById<ImageView>(R.id.logro_bienvenido)
        var Logro_CambioEstado =vView.findViewById<ImageView>(R.id.logro_CambioEstado)
        var Logro_NuevoUsuario =vView.findViewById<ImageView>(R.id.logro_NuevoUsuario)
        var Logro_NuevoGrupo =vView.findViewById<ImageView>(R.id.logro_NuevoGrupo)
        var Logro_Tarea =vView.findViewById<ImageView>(R.id.logro_tareaCompleta)
        var Logro_Tilin =vView.findViewById<ImageView>(R.id.logro_Esotilin)
        Logro_NuevoGrupo2=Logro_NuevoGrupo
        Logro_CambioEstado2=Logro_CambioEstado
        Logro_NuevoUsuario2=Logro_NuevoUsuario
        Logro_bienvenido2=Logro_bienvenido
        Logro_Tarea2=Logro_Tarea
        Logro_Tilin2=Logro_Tilin


        var botonMensajeNuevo =vView.findViewById<View>(R.id.new_message)
        botonMensajeNuevo.setOnClickListener(View.OnClickListener {
            val nuevoMensaje = Intent(vView.context, NewMessageActivity::class.java)
            startActivity(nuevoMensaje)

        })
       // var Contador=0
        //while (Contador!=2) { // isRunning is a boolean variable
         //   SystemClock.sleep(1000)
            Logros()
          //  Contador =Contador+1
        //}
        return vView

    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment mensajesPriv.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            mensajesPriv().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}