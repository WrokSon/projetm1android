package com.example.viewmodel

import android.os.Looper
import com.example.design.R
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
            checkSessionAndStateServer(status)
            lesOffres.clear()
            // extaitre les offres le la liste offfres
            for (i in 0..<offres.length){
                val item = offres.item(i).childNodes
                val offerID = item.item(0).textContent
                val itemID = item.item(1).textContent
                val qte = item.item(2).textContent
                val prix = item.item(3).textContent

                val offer = Offre(offerID.toInt(),itemID.toInt(),qte.toInt(),prix.toInt())

                lesOffres.add(i,offer)
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
                makePopupMessage(context,context.getString(R.string.msg_pb_select))
                return
            }

            // requetes
            val url = URL(
                repository.getBaseURL() + "/market_acheter.php?session=" + repository.getSession() +
                        "&signature=" + repository.getSignature() + "&offer_id= " + repository.getOffre()!!.offerID.toString()
            )
            val connection = url.openConnection()
            val dbf = DocumentBuilderFactory.newInstance()
            val db = dbf.newDocumentBuilder()
            val doc = db.parse(connection.getInputStream())
            val status = doc.getElementsByTagName("STATUS").item(0).textContent
            //resultat
            if (status == Status.OK.value){
                Looper.prepare()
                val nomOffre = getItemDetail(repository.getOffre()!!.itemID - 1).nom
                makePopupMessage(context,"${context.getString(R.string.msg_achat)} ${repository.getOffre()!!.quantite} $nomOffre ${context.getString(R.string.text_pour)} ${repository.getOffre()!!.prix}")
            }
            if (status == Status.NOMONEY.value) {
                Looper.prepare()
                makePopupMessage(context,context.getString(R.string.msg_no_money))
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

    fun getListe() = lesOffres

    fun selectOffre(offer : Offre?) = repository.changeSelectOffre(offer)


}