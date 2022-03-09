package com.poi.loginregistro.helpers

import com.poi.loginregistro.Modelos.contacto
import com.poi.loginregistro.Modelos.users

object General {

    object UserInstance{
        private var user : users? = null
        private var userUID : String?=null

        fun setUserInstance(user: users?){
            if (user != null) {
                this.user = user
            }
        }

        fun deleteUserInstance(){
            this.user=null
        }

        fun getUserInstance() : users?{
            return user
        }

        fun updateInstance(hashMap: HashMap<String, Any>){
            user?.username =hashMap.get("username").toString()
            user?.correo =hashMap.get("correo").toString()
            user?.contraseña = hashMap.get("contraseña").toString()
            user?.status = hashMap.get("status").toString()
        }

    }
}