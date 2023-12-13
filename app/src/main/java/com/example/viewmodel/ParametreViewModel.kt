package com.example.viewmodel

import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.model.tools.Status
import java.net.ConnectException
import java.net.URL
import java.net.UnknownHostException
import javax.xml.parsers.DocumentBuilderFactory

class ParametreViewModel : ViewModelSuper() {

    fun changeName(name:String){
        try{
            val url = URL(
                repository.getBaseURL() + "changenom.php?signature=" + repository.getSignature() +
                        "&session=" + repository.getSession() + "&nom=" + name
            )
            val connection = url.openConnection()
            val dbf = DocumentBuilderFactory.newInstance()
            val db = dbf.newDocumentBuilder()
            val doc = db.parse(connection.getInputStream())
            val status = doc.getElementsByTagName("STATUS").item(0).textContent
            checkSession(status)
            if (status == Status.OK.value) {
                Log.d("ICI", "je suis passé par là")
                repository.setLogin(name)
            }
        }catch (e : UnknownHostException){
            actionNoConnexion(context)
        }catch (e : ConnectException){
            e.printStackTrace()
            actionNoConnexion(context)
        }
    }

    fun reset(){
        try{
            val url = URL(
                repository.getBaseURL() + "reinit_joueur.php?signature=" + repository.getSignature() +
                        "&session=" + repository.getSession()
            )
            val connection = url.openConnection()
            val dbf = DocumentBuilderFactory.newInstance()
            val db = dbf.newDocumentBuilder()
            val doc = db.parse(connection.getInputStream())
            val status = doc.getElementsByTagName("STATUS").item(0).textContent
            checkSession(status)
            if (status == Status.OK.value) {
                Log.d("IzzCI", "je suis passé par là")
                repository.resetLogin()
                playerStatus()
            }
        }catch (e : UnknownHostException){
            actionNoConnexion(context)
        }catch (e : ConnectException){
            e.printStackTrace()
            actionNoConnexion(context)
        }
    }

}