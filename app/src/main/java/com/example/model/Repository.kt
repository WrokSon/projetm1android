package com.example.model

import android.util.Log
import com.example.model.data.Item
import com.example.model.data.Offre
import com.example.model.data.Player

class Repository private constructor() {
    companion object {
        @Volatile
        private var INSTANCE: Repository? = null

        fun getInstance(): Repository {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: Repository().also { INSTANCE = it }
            }
        }
    }
    private val baseURL = "https://test.vautard.fr/creuse_srv/"
    private val baseURLImg = "https://test.vautard.fr/creuse_imgs/"
    private var session = 0
    private var signature : Long = 0
    private var baselogin = ""
    private var player : Player = Player("",0.0f,0.0f,0,1,HashMap<Int,Int>())
    private var reset : Boolean = false
    private var itemDetailList = ArrayList<Item>()

    private var currentOfferSelect : Offre? = null

    fun changeSelectOffre(offer : Offre?){
        currentOfferSelect = offer
    }

    fun collectCon(log : String,sess : Int, sign : Long){
        baselogin = log
        player.username = baselogin
        session = sess
        signature = sign
        Log.d("SESSION",session.toString())
        Log.d("SIGNATURE",signature.toString())
    }

    fun resetLogin(){
        player.username = baselogin
    }

    fun getBaseLogin() : String = baselogin

    fun updatePlayer(lat : Float, long : Float, money : Int, pick : Int, items : HashMap<Int,Int>){
        player.lat = lat
        player.long = long
        player.money = money
        player.pick = pick
        player.items = items

    }

    fun setLogin(log : String){
        player.username = log
    }

    fun updatePosition(lat:Float, long: Float){
        player.lat = lat
        player.long = long
    }

    fun getSession(): Int{
        return session
    }

    fun getBaseURL(): String = baseURL

    fun getBaseLoginImg() : String = baseURLImg

    fun getSignature(): Long{
        return signature
    }

    fun getPlayer(): Player {
        return player
    }

    fun setResetValue(value:Boolean){
        reset = value
    }

    fun getResetValue() = reset

    fun setItemDetailList(list : ArrayList<Item>){
        itemDetailList = list
    }

    fun getItemDetail(id : Int): Item{
        return itemDetailList[id]
    }

    fun getOffre() = currentOfferSelect

}


