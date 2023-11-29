package com.example.viewmodel

import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.model.tools.Status
import java.net.URL
import javax.xml.parsers.DocumentBuilderFactory

class ParametreViewModel : ViewModelSuper() {

    fun changeName(context:AppCompatActivity,name:String){
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
            if (status == Status.OK.value) {
                Log.d("ICI", "je suis passé par là")
                repository.setLogin(name)
            }
        }catch (e : Exception){
            actionNoConnexion(context)
        }
    }

    fun reset(context:AppCompatActivity){
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
            if (status == Status.OK.value) {
                Log.d("IzzCI", "je suis passé par là")
                repository.resetLogin()
                playerStatus(context)
            }
        }catch (e : Exception){
            actionNoConnexion(context)
        }
    }

}