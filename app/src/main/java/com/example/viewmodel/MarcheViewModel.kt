package com.example.viewmodel

import android.app.Activity
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.model.data.Item
import com.example.model.data.Offre
import com.example.model.tools.Status
import java.net.URL
import javax.xml.parsers.DocumentBuilderFactory

class MarcheViewModel : ViewModelSuper() {

    private  var lesOffres = ArrayList<Offre>()

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

                lesOffres.add(offer)

                Log.d("MARCHE","offer id = "+offer_id+" item id = " + item_id + " quantite = "+ qte+ " prix = "+ prix)
            }

        }catch (e : Exception){
            e.printStackTrace()
            //actionNoConnexion(context)
        }
    }

    fun acheter(context: AppCompatActivity) {
        try{
            // pas de selection
            if (repository.getOffre() == null){
                Toast.makeText(context,"Il y a un probleme de selection, Veuillez reselectionner votre Offre",Toast.LENGTH_SHORT).show()
                return
            }

            // requetes
            val url = URL(
                repository.getBaseURL() + "/market_acheter.php?session=" + repository.getSession() +
                        "&signature=" + repository.getSignature() + "&offer_id=" + repository.getOffre()!!.Offer_ID.toString()
            )
            val items = HashMap<Item, Int>()
            val connection = url.openConnection()
            val dbf = DocumentBuilderFactory.newInstance()
            val db = dbf.newDocumentBuilder()
            val doc = db.parse(connection.getInputStream())
            val status = doc.getElementsByTagName("STATUS").item(0).textContent
            Log.d("MARCHERACHETER", "me voici'$status'")
            //resultat
            if (status == Status.OK.value){
                Toast.makeText(context,"Vous venez d'achez l'offre" + repository.getOffre()!!.Offer_ID.toString(),Toast.LENGTH_SHORT).show()
                Log.d("MARCHERACHETER","me voici")
            }
            if (status == Status.NOMONEY.value) {
                Log.d("MARCHERACHETER","me voici dedans")
                Toast.makeText(context,"Vous n'avez pas assez d'aergent ",Toast.LENGTH_SHORT).show()

            }



        }catch (e : Exception){
            e.printStackTrace()
            //actionNoConnexion(context)
        }
    }

    fun getOffreSelect() = repository.getOffre()

    fun getListe() = lesOffres

    fun selectOffre(offer : Offre?) = repository.changeSelectOffre(offer)


}