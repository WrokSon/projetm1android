package com.example.viewmodel

import java.net.URL
import java.security.MessageDigest
import java.net.ConnectException
import java.net.UnknownHostException
import javax.xml.parsers.DocumentBuilderFactory

class ConnViewModel : ViewModelSuper() {
    fun connexion(login : String, passwd : String): String{
        var status = ""
        //SHA256
        val bytes = passwd.toByteArray()
        val md = MessageDigest.getInstance("SHA-256")
        val digest = md.digest(bytes)


        val hashedpass = digest.fold("") { str, it -> str + "%02x".format(it) }

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
            checkSessionAndStateServer(status)
            //si bon identifiants -> redirection main activity
            if(status == "OK"){
                repository.collectCon(login,doc.getElementsByTagName("SESSION").item(0).textContent.toInt(),
                    doc.getElementsByTagName("SIGNATURE").item(0).textContent.toLong())
                    playerStatus()
            }
        }catch (e : UnknownHostException){
            e.printStackTrace()
            actionNoConnexion(context)
        }catch (e : ConnectException){
            // a gerer
            e.printStackTrace()
            actionNoConnexion(context)
        }
        return status
    }
}