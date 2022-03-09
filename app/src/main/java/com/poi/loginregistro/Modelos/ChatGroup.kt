package com.poi.loginregistro.Modelos

class ChatGroup (val id: String, val username: String, val text: String, val fromId: String, val toId: String, val timestamp: Long){

    constructor(): this("", "", "", "", "",-1)
}