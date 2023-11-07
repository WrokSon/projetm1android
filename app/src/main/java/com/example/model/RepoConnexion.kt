package com.example.model

import android.util.Log
import java.util.ArrayList

class RepoConnexion private constructor() {

    private var autoConnect = true

    companion object {
        @Volatile
        private var INSTANCE: RepoConnexion? = null

        fun getInstance(): RepoConnexion {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: RepoConnexion().also { INSTANCE = it }
            }
        }
    }

    fun getValueAutoConnect() = autoConnect

    fun setValueAutoConnect(newValue : Boolean){
        autoConnect = newValue
    }

    private var session = 0
    private var signature : Long = 0
    private var login = ""
    private lateinit var player : Player

    fun collectCon(log : String,sess : Int, sign : Long){
        login = log
        session = sess
        signature = sign
        Log.d("SESSION",session.toString())
        Log.d("SIGNATURE",signature.toString())
    }

    fun updatePlayer(lat : Double, long : Double, money : Int, pick : Int, items : ArrayList<Item>){
        player = Player(login,lat,long,money,pick,items)
    }

    fun getSession(): Int{
        return session
    }

    fun getSignature(): Long{
        return signature
    }

    fun getPlayer(): Player{
        return player
    }
}


