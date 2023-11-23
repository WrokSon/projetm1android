package com.example.viewmodel

import java.net.URL
import java.security.MessageDigest
import android.util.Log
import com.example.model.data.Item
import com.example.model.data.Player
import javax.xml.parsers.DocumentBuilderFactory

class ConnViewModel : ViewModelSuper() {
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
}