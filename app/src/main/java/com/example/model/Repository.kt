package com.example.model

import android.graphics.Bitmap
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

    // Propriétés pour les URLs, la session, la signature, etc.
    private val baseURL = "https://test.vautard.fr/creuse_srv/"
    private val baseURLImg = "https://test.vautard.fr/creuse_imgs/"
    private var session = 0
    private var signature : Long = 0
    private var baselogin = ""
    private var player : Player = Player("",0.0f,0.0f,0,1,HashMap())
    private var reset : Boolean = false
    private var itemDetailList = ArrayList<Item>()
    private var itemImages = ArrayList<Bitmap>()
    private var voisins = ArrayList<Voisin>()
    private var lesOffres  = mutableListOf<Offre>()
    private var currentOfferSelect : Offre? = null

    // Méthode pour changer l'offre actuellement sélectionnée
    fun changeSelectOffre(offer : Offre?){
        currentOfferSelect = offer
    }

    // Méthode pour collecter les informations de connexion
    fun collectCon(log : String,sess : Int, sign : Long){
        baselogin = log
        player.username = baselogin
        session = sess
        signature = sign
    }

    // Méthode pour réinitialiser le nom d'utilisateur (objet Player)
    fun resetLogin(){
        player.username = baselogin
    }

    // Méthode pour récupérer le nom d'utilisateur par defaut
    fun getBaseLogin() : String = baselogin

    // Méthode pour mettre à jour les informations du joueur
    fun updatePlayer(lat : Float, long : Float, money : Int, pick : Int, items : HashMap<Int,Int>){
        player.lat = lat
        player.long = long
        player.money = money
        player.pick = pick
        player.items = items

    }

    // Méthode pour récupérer les voisins à partir d'un document XML
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

    // Méthode pour récupérer les offres
    fun getlesOffres() = lesOffres

    // Méthode pour définir le nom d'utilisateur (objet Player)
    fun setLogin(log : String){
        player.username = log
    }

    // Méthode pour mettre à jour la position du joueur
    fun updatePosition(lat:Float, long: Float){
        player.lat = lat
        player.long = long
    }

    // Méthode pour récupérer la session
    fun getSession(): Int{
        return session
    }

    // Méthode pour récupérer l'URL du serveur
    fun getBaseURL(): String = baseURL

    // Méthode pour récupérer l'URL pour les images
    fun getBaseLoginImg() : String = baseURLImg

    // Méthode pour récupérer la signature
    fun getSignature(): Long{
        return signature
    }

    // Méthode pour récupérer le joueur
    fun getPlayer(): Player {
        return player
    }

    // Méthode pour changer la valeur de réinitialisation
    // pour forcer le changement de nom en local
    fun setResetValue(value:Boolean){
        reset = value
    }

    // Méthode pour récupérer la valeur de réinitialisation
    fun getResetValue() = reset

    // Méthode pour changer la liste des détails des items
    fun setItemDetailList(list : ArrayList<Item>){
        itemDetailList = list
    }
    // Méthode pour récupérer les détails d'un item
    fun getItemDetail(id : Int): Item{
        return itemDetailList[id]
    }

    // Méthode pour récupérer l'offre actuelle
    fun getOffre() = currentOfferSelect

    // Méthode pour récupérer la liste des voisins
    fun getListeVoisins() = voisins

    // Méthode pour récupérer une image par son ID
    fun getImage(id: Int): Bitmap = itemImages[id]

    // Méthode pour changer la liste des images d'articles
    fun setImageList(list: ArrayList<Bitmap>){
        itemImages = list
    }

}


