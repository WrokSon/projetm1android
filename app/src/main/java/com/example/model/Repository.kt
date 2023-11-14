package com.example.model

import android.util.Log
import com.example.model.data.Item
import com.example.model.data.Player
import java.util.ArrayList

class Repository private constructor() {

    private var autoConnect = true
    private val baseURL = "https://test.vautard.fr/creuse_srv/"

    companion object {
        @Volatile
        private var INSTANCE: Repository? = null

        fun getInstance(): Repository {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: Repository().also { INSTANCE = it }
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

    fun updatePlayer(lat : Float, long : Float, money : Int, pick : Int, items : ArrayList<Item>){
        player = Player(login,lat,long,money,pick,items)
    }

    fun updatePosition(lat:Float, long: Float){
        player.lat = lat
        player.long = long
    }

    fun getSession(): Int{
        return session
    }

    fun getBaseURL(): String = baseURL

    fun getSignature(): Long{
        return signature
    }

    fun getPlayer(): Player {
        return player
    }

}


