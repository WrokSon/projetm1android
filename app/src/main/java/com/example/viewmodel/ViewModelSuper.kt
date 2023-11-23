package com.example.viewmodel

import androidx.lifecycle.ViewModel
import com.example.model.Repository
import com.example.model.data.Item
import com.example.model.data.Player
import java.net.URL
import javax.xml.parsers.DocumentBuilderFactory

open class ViewModelSuper : ViewModel() {
    protected val repository = Repository.getInstance()
    fun playerStatus() {
        val url = URL(
            repository.getBaseURL() + "status_joueur.php?session=" + repository.getSession() +
                    "&signature=" + repository.getSignature()
        )
        val connection = url.openConnection()
        val dbf = DocumentBuilderFactory.newInstance()
        val db = dbf.newDocumentBuilder()
        val doc = db.parse(connection.getInputStream())
        val status = doc.getElementsByTagName("STATUS").item(0).textContent
        var lat = doc.getElementsByTagName("LATITUDE").item(0).textContent
        var long = doc.getElementsByTagName("LONGITUDE").item(0).textContent
        if (lat == "" || long == "") {
            lat = "0.0"
            long = "0.0"
        }
        if (status == "OK") {
            repository.updatePlayer(
                lat.toFloat(), long.toFloat(),
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