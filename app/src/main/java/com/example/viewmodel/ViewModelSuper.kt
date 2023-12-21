package com.example.viewmodel

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Looper
import android.view.LayoutInflater
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import com.example.design.R
import com.example.design.views.Connexion
import com.example.model.Repository
import com.example.model.data.Item
import com.example.model.data.Player
import com.example.model.tools.Status
import java.net.ConnectException
import java.net.URL
import java.net.UnknownHostException
import javax.xml.parsers.DocumentBuilderFactory

// Classe de base pour les ViewModels, contient les proprietés communes
open class ViewModelSuper : ViewModel() {
    // Instance du repository partagé entre les ViewModels
    protected val repository = Repository.getInstance()
    // Contexte de l'activité liée au ViewModel
    @SuppressLint("StaticFieldLeak")
    protected lateinit var context : AppCompatActivity

    // Vérifie l'état de la session (invalide/expiré) et du serveur en fonction du statut
    fun checkSessionAndStateServer(status : String) {
        when (status) {
            Status.SESSIONEXPIRED.value -> {
                Looper.prepare()
                Toast.makeText(context, context.getString(R.string.text_session_exp), Toast.LENGTH_SHORT).show()
                val intent = Intent(context, Connexion::class.java)
                context.startActivity(intent)
                context.finish()
            }
            Status.SESSIONINVALID.value -> {
                Looper.prepare()
                Toast.makeText(context, context.getString(R.string.text_session_invalide), Toast.LENGTH_SHORT).show()
                val intent = Intent(context, Connexion::class.java)
                context.startActivity(intent)
                context.finish()
            }
            Status.TECHNICALERROR.value -> {
                Looper.prepare()
                Toast.makeText(context,context.getString(R.string.erreur_serveur),Toast.LENGTH_SHORT).show()
                val intent = Intent(context, Connexion::class.java)
                context.startActivity(intent)
                context.finish()
            }
        }
    }

    // Initialise le contexte du ViewModel avec l'activité actuelle
    fun initContext(contextApp : AppCompatActivity){
        context = contextApp
    }

    // Met à jour l'état du joueur en fonction des données du serveur
    fun playerStatus() {
        try{
            // Construit l'URL pour obtenir le statut du joueur
            val url = URL(
                repository.getBaseURL() + "status_joueur.php?session=" + repository.getSession() +
                        "&signature=" + repository.getSignature()
            )
            val items = HashMap<Int, Int>()
            val connection = url.openConnection()
            // Parsing du document XML de la réponse
            val dbf = DocumentBuilderFactory.newInstance()
            val db = dbf.newDocumentBuilder()
            val doc = db.parse(connection.getInputStream())
            val status = doc.getElementsByTagName("STATUS").item(0).textContent
            // Vérification de l'état du serveur et de la session
            checkSessionAndStateServer(status)
            // Récupération des données du joueur
            var lat = doc.getElementsByTagName("LATITUDE").item(0).textContent
            var long = doc.getElementsByTagName("LONGITUDE").item(0).textContent
            val inv = doc.getElementsByTagName("ITEMS").item(0).childNodes
            for (i in 0..<inv.length) {
                val id = inv.item(i).childNodes.item(0).textContent.toInt()
                items[id] = inv.item(i).childNodes.item(1).textContent.toInt()
            }
            // Correction des valeurs manquantes pour la latitude et la longitude
            if (lat == "" || long == "") {
                lat = "0.0"
                long = "0.0"
            }
            // Met à jour l'état du joueur dans le repository
            if (status == Status.OK.value) {
                repository.updatePlayer(
                    lat.toFloat(), long.toFloat(),
                    doc.getElementsByTagName("MONEY").item(0).textContent.toInt(),
                    doc.getElementsByTagName("PICKAXE").item(0).textContent.toInt(),
                    items
                )
            }
        }catch (e : UnknownHostException){
            // Gestion de l'absence de connexion
            e.printStackTrace()
            actionNoConnexion(context)
        }catch (e : ConnectException){
            // Gestion des erreurs de connexion
            e.printStackTrace()
            actionNoConnexion(context)
        }
    }

    // Fonction qui récupère tout les détails de tout les items afin de pas avoir à appeler à chaque fois le ws
    fun getItemsDetails(){
        val list = ArrayList<Item>()
        for(i in 1 .. 13){
            // Construit l'URL pour obtenir le statut du joueur
            val url = URL(
                repository.getBaseURL() + "item_detail.php?session=" + repository.getSession() +
                        "&signature=" + repository.getSignature() + "&item_id=" + i)
            val connection = url.openConnection()
            // Parsing du document XML de la réponse
            val dbf = DocumentBuilderFactory.newInstance()
            val db = dbf.newDocumentBuilder()
            val doc = db.parse(connection.getInputStream())
            val infos = doc.getElementsByTagName("PARAMS").item(0).childNodes
            val item = Item(-1,"","",0,"","","")

            // Remplissage des détails de l'item
            item.id = i
            item.nom = infos.item(0).textContent
            when(infos.item(1).textContent){
                "A" -> item.type = "Artéfact"
                "M" -> item.type = "Minerai"
            }
            item.rarity = infos.item(2).textContent.toInt()
            item.image = infos.item(3).textContent
            item.descEn = infos.item(4).textContent
            item.descFr = infos.item(5).textContent

            list.add(item)
        }
        // Met à jour la liste des détails des items dans le repository
        repository.setItemDetailList(list)
    }

    // Récupère les images de tous les items du serveur et les stocke dans le repository
    fun getImages(){
        val list = ArrayList<Bitmap>()
        for(i in 1 .. 13){
            val imageurl = URL(getBaseLoginImg() + repository.getItemDetail(i-1).image)
            val bitmap = BitmapFactory.decodeStream(imageurl.openConnection().getInputStream())
            list.add(bitmap)
        }
        // Met à jour la liste des images des items dans le repository
        repository.setImageList(list)
    }

    // Action à effectuer en cas d'absence ou erreur de connexion
    fun actionNoConnexion(context : AppCompatActivity){
        val intent = Intent(context, Connexion::class.java)
        Looper.prepare()
        Toast.makeText(context,context.getString(R.string.text_connexion_perdue),Toast.LENGTH_SHORT).show()
        context.startActivity(intent)
        context.finish()
    }

    // Affiche un popup avec un message personnalisé
    fun makePopupMessage(context : AppCompatActivity, msg : String){
        // Exécute le code sur le thread principal
        context.runOnUiThread {
            val popup = AlertDialog.Builder(context)
            val popupView = LayoutInflater.from(context).inflate(R.layout.popup_message, null)
            popup.setView(popupView)
            val content : TextView = popupView.findViewById(R.id.popup_txt_msg)
            content.text = msg
            popup.show()
        }
    }

    // Obtient la base de l'URL de connexion
    fun getBaseLogin() = repository.getBaseLogin()

    // Obtient la base de l'URL des images
    fun getBaseLoginImg() = repository.getBaseLoginImg()

    // Obtient le joueur
    fun getPlayer(): Player {
        return repository.getPlayer()
    }

    // Changer la valeur de réinitialisation du repository
    fun setResetValue(value:Boolean){
        repository.setResetValue(value)
    }

    // Obtient la valeur de réinitialisation du repository
    fun getResetValue() = repository.getResetValue()

    // Obtient les détails d'un item en fonction de son ID
    fun getItemDetail(id : Int) = repository.getItemDetail(id)

    // Obtient l'image d'un item en fonction de son ID
    fun getImage(id: Int) = repository.getImage(id)
}