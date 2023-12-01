package com.example.viewmodel

import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.model.data.Item
import com.example.model.data.Offre
import java.net.URL
import javax.xml.parsers.DocumentBuilderFactory

class MarcheViewModel : ViewModelSuper() {

    var

    fun getMarche(context : AppCompatActivity) {
        try{
            val url = URL(
                repository.getBaseURL() + "/market_list.php?session=" + repository.getSession() +
                        "&signature=" + repository.getSignature()
            )
            val items = HashMap<Item, Int>()
            val connection = url.openConnection()
            val dbf = DocumentBuilderFactory.newInstance()
            val db = dbf.newDocumentBuilder()
            val doc = db.parse(connection.getInputStream())
            val status = doc.getElementsByTagName("STATUS").item(0).textContent
            val offres = doc.getElementsByTagName("OFFERS").item(0).childNodes

            // extaitre les offres le la liste offfres
            for (i in 0..offres.length){
                val item = offres.item(i).childNodes
                Log.d("MARCHE","le len = "+item.length)
                val offer_id = item.item(0).textContent
                val item_id = item.item(1).textContent
                val qte = item.item(2).textContent
                val prix = item.item(3).textContent

                val offer : Offre = Offre(offer_id.toInt(),item_id.toInt(),qte.toInt(),prix.toInt())

                Log.d("MARCHE","offer id = "+offer_id+" item id = " + item_id + " quantite = "+ qte+ " prix = "+ prix)
            }

        }catch (e : Exception){
            e.printStackTrace()
            //actionNoConnexion(context)
        }
    }


}