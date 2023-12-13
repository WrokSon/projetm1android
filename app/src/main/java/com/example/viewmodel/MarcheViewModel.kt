package com.example.viewmodel

import android.os.Looper
import android.util.Log
import android.widget.Toast
import com.example.model.data.Item
import com.example.model.data.Offre
import com.example.model.tools.Status
import java.net.ConnectException
import java.net.URL
import java.net.UnknownHostException
import javax.xml.parsers.DocumentBuilderFactory

class MarcheViewModel : ViewModelSuper() {

    private var lesOffres  = mutableListOf<Offre>()

    fun getMarche() {
        try{
            val url = URL(
                repository.getBaseURL() + "/market_list.php?session=" + repository.getSession() +
                        "&signature=" + repository.getSignature()
            )
            val connection = url.openConnection()
            val dbf = DocumentBuilderFactory.newInstance()
            val db = dbf.newDocumentBuilder()
            val doc = db.parse(connection.getInputStream())
            val status = doc.getElementsByTagName("STATUS").item(0).textContent
            val offres = doc.getElementsByTagName("OFFERS").item(0).childNodes
            checkSession(status)
            lesOffres.clear()
            // extaitre les offres le la liste offfres
            for (i in 0..offres.length-1){
                val item = offres.item(i).childNodes
                Log.d("MARCHE","le len = "+item.length)
                val offer_id = item.item(0).textContent
                val item_id = item.item(1).textContent
                val qte = item.item(2).textContent
                val prix = item.item(3).textContent

                val offer = Offre(offer_id.toInt(),item_id.toInt(),qte.toInt(),prix.toInt())

                lesOffres.add(i,offer)

                Log.d("MARCHE","offer id = "+offer_id+" item id = " + item_id + " quantite = "+ qte+ " prix = "+ prix)
            }

        }catch (e : UnknownHostException){
            e.printStackTrace()
            actionNoConnexion(context)
        }catch (e : ConnectException){
            // a gerer
            e.printStackTrace()
            actionNoConnexion(context)
        }
    }

    fun acheter() {
        try{
            // pas de selection
            if (repository.getOffre() == null){
                Toast.makeText(context,"Il y a un probleme de selection, Veuillez reselectionner votre Offre",Toast.LENGTH_SHORT).show()
                return
            }

            // requetes
            val url = URL(
                repository.getBaseURL() + "/market_acheter.php?session=" + repository.getSession() +
                        "&signature=" + repository.getSignature() + "&offer_id= " + repository.getOffre()!!.Offer_ID.toString()
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
                Looper.prepare()
                val nomOffre = getItemDetail(repository.getOffre()!!.Item_ID - 1).nom
                Toast.makeText(context,"Vous venez d'achez ${repository.getOffre()!!.Quantite} $nomOffre a ${repository.getOffre()!!.prix}",Toast.LENGTH_SHORT).show()
                Log.d("MARCHERACHETER","me voici")
            }
            if (status == Status.NOMONEY.value) {
                Log.d("MARCHERACHETER","me voici dedans")
                Looper.prepare()
                Toast.makeText(context,"Vous n'avez pas assez d'argent ",Toast.LENGTH_SHORT).show()
            }

        }catch (e : UnknownHostException){
            e.printStackTrace()
            actionNoConnexion(context)
        }catch (e : ConnectException){
            // a gerer
            e.printStackTrace()
            actionNoConnexion(context)
        }
    }

    fun getOffreSelect() = repository.getOffre()

    fun getListe() = lesOffres

    fun selectOffre(offer : Offre?) = repository.changeSelectOffre(offer)


}