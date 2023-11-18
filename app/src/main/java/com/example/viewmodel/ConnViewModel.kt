package com.example.viewmodel

import androidx.lifecycle.ViewModel
import java.net.URL
import java.security.MessageDigest
import android.util.Log
import com.example.model.data.Item
import com.example.model.data.Player
import com.example.model.Repository
import javax.xml.parsers.DocumentBuilderFactory

class ConnViewModel : ViewModel() {
    private val repository = Repository.getInstance()

    fun connexion(login : String, passwd : String): String{
        //SHA256
        val bytes = passwd.toByteArray()
        val md = MessageDigest.getInstance("SHA-256")
        val digest = md.digest(bytes)


        val hashedpass = digest.fold("") { str, it -> str + "%02x".format(it) }
        Log.d("statushash",hashedpass)

        //connexion
        val url = URL(repository.getBaseURL()+"connexion.php?login="+login+"&passwd="
        + hashedpass)
        val connection = url.openConnection()
        val dbf = DocumentBuilderFactory.newInstance()
        val db = dbf.newDocumentBuilder()
        val doc = db.parse(connection.getInputStream())
        val status = doc.getElementsByTagName("STATUS").item(0).textContent
        //si bon identifiants -> redirection main activity
        if(status == "OK"){
            repository.collectCon(login,doc.getElementsByTagName("SESSION").item(0).textContent.toInt(),
                 doc.getElementsByTagName("SIGNATURE").item(0).textContent.toLong())
            playerStatus()
        }
        return status
    }

    fun playerStatus(){
        val url = URL(repository.getBaseURL()+"status_joueur.php?session="+getSession()+
                "&signature=" + getSignature())
        val connection = url.openConnection()
        val dbf = DocumentBuilderFactory.newInstance()
        val db = dbf.newDocumentBuilder()
        val doc = db.parse(connection.getInputStream())
        val status = doc.getElementsByTagName("STATUS").item(0).textContent
        if(status == "OK"){
            repository.updatePlayer(
                //mettre le vrai code des qu'il y a la récupération des positions
                doc.getElementsByTagName("LATITUDE").item(0).textContent.toFloat(),
                doc.getElementsByTagName("LONGITUDE").item(0).textContent.toFloat(),
                doc.getElementsByTagName("MONEY").item(0).textContent.toInt(),
                doc.getElementsByTagName("PICKAXE").item(0).textContent.toInt(),
                //liste vide pour l'instant
                ArrayList<Item>()
            )
        }
    }

    fun getValueAutoConnection () = repository.getValueAutoConnect()
    fun setValueAutoConnection(newValue:Boolean){
        repository.setValueAutoConnect(newValue)
    }
    fun getSession(): Int{
        return repository.getSession()
    }

    fun getSignature(): Long{
        return repository.getSignature()
    }

    fun getPlayer(): Player {
        return repository.getPlayer()
    }
}