package com.example.model

import android.graphics.Bitmap
import android.util.Log
import com.example.model.data.Item
import com.example.model.data.Offre
import com.example.model.data.Player
import com.example.model.data.Voisin
import org.w3c.dom.Document

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
    private var itemImages = ArrayList<Bitmap>()
    private var voisins = ArrayList<Voisin>()

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
        Log.d("ITEMS",items.toString())
        player.items = items

    }

    //fonction qui recupere les voisins
    fun getVoisins(doc : Document){
        val listeVoisins = doc.getElementsByTagName("VOISINS").item(0).childNodes
        voisins.clear()
        for (i in 0..<listeVoisins.length){
            val voisin = listeVoisins.item(i).childNodes
            //nom
            val nom = voisin.item(0).textContent
            //position
            val position = voisin.item(1).childNodes
            val lon = position.item(0).textContent
            val lat = position.item(1).textContent
            voisins.add(Voisin(nom,lon.toFloat(),lat.toFloat()))
        }
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

    fun getListeVoisins() = voisins

    fun getImage(id: Int): Bitmap = itemImages[id]

    fun setImageList(list: ArrayList<Bitmap>){
        itemImages = list
    }

}


