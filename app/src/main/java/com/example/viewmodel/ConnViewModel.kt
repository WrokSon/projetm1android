package com.example.viewmodel

import androidx.lifecycle.ViewModel
import java.net.URL
import java.security.MessageDigest
import android.util.Log
import com.example.model.RepoConnexion
import javax.xml.parsers.DocumentBuilderFactory

class ConnViewModel : ViewModel() {
    private val repository = RepoConnexion.getInstance()

    fun connexion(login : String, passwd : String): String{
        //SHA256
        val bytes = passwd.toByteArray()
        val md = MessageDigest.getInstance("SHA-256")
        val digest = md.digest(bytes)


        val hashedpass = digest.fold("") { str, it -> str + "%02x".format(it) }
        Log.d("statushash",hashedpass)

        //connexion
        val url = URL("https://test.vautard.fr/creuse_srv/connexion.php?login="+login+"&passwd="
        + hashedpass)
        val connection = url.openConnection()
        val dbf = DocumentBuilderFactory.newInstance()
        val db = dbf.newDocumentBuilder()
        val doc = db.parse(connection.getInputStream())
        val status = doc.getElementsByTagName("STATUS").item(0).textContent
        //si bon identifiants -> redirection main activity
        if(status == "OK"){
            repository.collectCon(doc.getElementsByTagName("SESSION").item(0).textContent.toInt(),
                 doc.getElementsByTagName("SIGNATURE").item(0).textContent.toLong())
        }
        return status
    }

    fun playerStatus(){
        val url = URL("https://test.vautard.fr/creuse_srv/status_joueur.php?session="+getSession()+
                "&signature=" + getSignature())
        val connection = url.openConnection()
        val dbf = DocumentBuilderFactory.newInstance()
        val db = dbf.newDocumentBuilder()
        val doc = db.parse(connection.getInputStream())
        val status = doc.getElementsByTagName("STATUS").item(0).textContent
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
}