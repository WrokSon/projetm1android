package com.example.viewmodel

import androidx.lifecycle.ViewModel
import java.net.URL
import java.security.MessageDigest
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.model.data.Item
import com.example.model.data.Player
import com.example.model.Repository
import javax.xml.parsers.DocumentBuilderFactory

class ConnViewModel : ViewModelSuper() {
    fun connexion(context:AppCompatActivity, login : String, passwd : String): String{
        var status = ""
        //SHA256
        val bytes = passwd.toByteArray()
        val md = MessageDigest.getInstance("SHA-256")
        val digest = md.digest(bytes)


        val hashedpass = digest.fold("") { str, it -> str + "%02x".format(it) }
        Log.d("statushash",hashedpass)

        //connexion
        try{
            val url = URL(
                repository.getBaseURL() + "connexion.php?login=" + login + "&passwd="
                        + hashedpass
            )
            val connection = url.openConnection()
            val dbf = DocumentBuilderFactory.newInstance()
            val db = dbf.newDocumentBuilder()
            val doc = db.parse(connection.getInputStream())
            status = doc.getElementsByTagName("STATUS").item(0).textContent
            //si bon identifiants -> redirection main activity
            if(status == "OK"){
                repository.collectCon(login,doc.getElementsByTagName("SESSION").item(0).textContent.toInt(),
                    doc.getElementsByTagName("SIGNATURE").item(0).textContent.toLong())
                playerStatus(context)
            }
        }catch (e : Exception){
            Log.d("ERREURWEBSERVICE","Pas de connexion")
        }
        return status
    }
}