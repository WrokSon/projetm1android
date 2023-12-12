package com.example.viewmodel

import android.location.Location
import android.provider.DocumentsContract
import androidx.appcompat.app.AppCompatActivity
import com.example.model.tools.Status
import org.w3c.dom.Document
import java.net.ConnectException
import java.net.URL
import java.net.URLConnection
import java.net.UnknownHostException
import javax.xml.parsers.DocumentBuilderFactory

class MainViewModel : ViewModelSuper() {
    private var doc : Document = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument()
    fun updatePos(lon: Float, lat: Float) {
        try {
            val url = URL(
                repository.getBaseURL() + "deplace.php?signature=" + repository.getSignature() +
                        "&session=" + repository.getSession() + "&lon=" + lon + "&lat=" + lat
            )
            val connection = url.openConnection()
            val dbf = DocumentBuilderFactory.newInstance()
            val db = dbf.newDocumentBuilder()
            val doc = db.parse(connection.getInputStream())
            val status = doc.getElementsByTagName("STATUS").item(0).textContent
            repository.getVoisins(doc)
            if (status == Status.OK.value) {
                repository.updatePosition(lat, lon)
            }
        } catch (e: UnknownHostException) {
            actionNoConnexion(context)
        }catch (e : ConnectException){
            // a gerer
            e.printStackTrace()
            actionNoConnexion(context)
        }
    }

    fun getLatitude() = repository.getPlayer().lat
    fun getLongitude() = repository.getPlayer().long

    fun getListeVoisins() = repository.getListeVoisins()

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

    fun creuser(): Document {
        try {
            val url = URL(
                repository.getBaseURL() + "creuse.php?session=" + repository.getSession() +
                        "&signature=" + repository.getSignature() + "&lon=" + getLongitude() + "&lat=" + getLatitude()
            )
            val connection: URLConnection = url.openConnection()
            val dbf = DocumentBuilderFactory.newInstance()
            val db = dbf.newDocumentBuilder()
            doc = db.parse(connection.getInputStream())
            return doc
        } catch (e: UnknownHostException) {
            actionNoConnexion(context)
        }catch (e : ConnectException){
            // a gerer
            e.printStackTrace()
            actionNoConnexion(context)
        }
        return doc
    }
}