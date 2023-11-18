package com.example.viewmodel

import android.location.Location
import androidx.lifecycle.ViewModel
import com.example.model.Repository
import com.example.model.tools.Status
import java.net.URL
import javax.xml.parsers.DocumentBuilderFactory

class MainViewModel : ViewModel() {
    private val repository = Repository.getInstance()
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

}