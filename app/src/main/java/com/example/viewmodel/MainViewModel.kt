package com.example.viewmodel

import android.location.Location
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.ViewModel
import com.example.model.Repository
import com.example.model.data.Item
import com.example.model.data.Player
import com.example.model.tools.Status
import org.w3c.dom.Document
import java.net.URL
import javax.xml.parsers.DocumentBuilderFactory

class MainViewModel : ViewModelSuper() {
    fun updatePos(lon : Float,lat : Float){
        val url = URL(repository.getBaseURL()+"deplace.php?signature="+repository.getSignature()+
        "&session="+repository.getSession()+ "&lon="+lon+"&lat="+lat)
        val connection = url.openConnection()
        val dbf = DocumentBuilderFactory.newInstance()
        val db = dbf.newDocumentBuilder()
        val doc = db.parse(connection.getInputStream())
        val status = doc.getElementsByTagName("STATUS").item(0).textContent
        if(status == Status.OK.value){
            repository.updatePosition(lat,lon)
        }
    }

    fun getLatitude() = repository.getPlayer().lat
    fun getLongitude() = repository.getPlayer().long

    fun distanceEntrePoints(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Float {
        val location1 = Location("")
        location1.latitude = lat1
        location1.longitude = lon1

        val location2 = Location("")
        location2.latitude = lat2
        location2.longitude = lon2

        val distance = FloatArray(1)
        Location.distanceBetween(
            location1.latitude, location1.longitude,
            location2.latitude, location2.longitude,
            distance
        )

        return distance[0]
    }


    fun playerStatus(){
        val url = URL(repository.getBaseURL()+"status_joueur.php?session="+repository.getSession()+
                "&signature=" + repository.getSignature())
        val connection = url.openConnection()
        val dbf = DocumentBuilderFactory.newInstance()
        val db = dbf.newDocumentBuilder()
        val doc = db.parse(connection.getInputStream())
        val status = doc.getElementsByTagName("STATUS").item(0).textContent
        var lat = doc.getElementsByTagName("LATITUDE").item(0).textContent
        var long = doc.getElementsByTagName("LONGITUDE").item(0).textContent
        if(lat == "" || long == ""){
            lat = "0.0"
            long = "0.0"
        }
        if(status == "OK"){
            repository.updatePlayer(
                lat.toFloat(), long.toFloat(),
                doc.getElementsByTagName("MONEY").item(0).textContent.toInt(),
                doc.getElementsByTagName("PICKAXE").item(0).textContent.toInt(),
                //liste vide pour l'instant
                ArrayList<Item>()
            )
        }
    }

    fun creuser(): Document{
        val url = URL(repository.getBaseURL()+"creuse.php?session="+repository.getSession()+
                "&signature=" + repository.getSignature()+"&lon="+getLongitude()+"&lat="+getLatitude())
        val connection = url.openConnection()
        val dbf = DocumentBuilderFactory.newInstance()
        val db = dbf.newDocumentBuilder()
        val doc = db.parse(connection.getInputStream())
        return doc
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