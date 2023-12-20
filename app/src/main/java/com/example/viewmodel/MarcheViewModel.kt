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

    // Méthode pour récupérer la liste des offres sur le marché
    fun getMarche() {
        try{
            // Construction de l'URL pour récupérer la liste des offres
            val url = URL(
                repository.getBaseURL() + "/market_list.php?session=" + repository.getSession() +
                        "&signature=" + repository.getSignature()
            )
            val connection = url.openConnection()
            // Parsing du document XML de la réponse
            val dbf = DocumentBuilderFactory.newInstance()
            val db = dbf.newDocumentBuilder()
            val doc = db.parse(connection.getInputStream())
            // Récupération du statut de l'opération
            val status = doc.getElementsByTagName("STATUS").item(0).textContent
            val offres = doc.getElementsByTagName("OFFERS").item(0).childNodes
            // Vérification de l'état du serveur et de la session
            checkSessionAndStateServer(status)
            // Mise à jour de la liste des offres dans le repository
            repository.getlesOffres().clear()
            // extaitre les offres le la liste offfres
            for (i in 0..<offres.length){
                val item = offres.item(i).childNodes
                val offerID = item.item(0).textContent
                val itemID = item.item(1).textContent
                val qte = item.item(2).textContent
                val prix = item.item(3).textContent

                val offer = Offre(offerID.toInt(),itemID.toInt(),qte.toInt(),prix.toInt())

                repository.getlesOffres().add(i,offer)
            }

        }catch (e : UnknownHostException){
            // Gestion de l'absence de connexion
            e.printStackTrace()
            actionNoConnexion(context)
        }catch (e : ConnectException){
            // Gestion des erreurs de connexion
            e.printStackTrace()
            actionNoConnexion(context)
        }
    }

    // Méthode pour effectuer l'achat d'une offre
    fun acheter() {
        try{
            // Vérification de la sélection d'une offre
            if (repository.getOffre() == null){ // pas de selection
                makePopupMessage(context,context.getString(R.string.msg_pb_select))
                return
            }

            // Construction de l'URL pour l'achat de l'offre sélectionnée
            val url = URL(
                repository.getBaseURL() + "/market_acheter.php?session=" + repository.getSession() +
                        "&signature=" + repository.getSignature() + "&offer_id= " + repository.getOffre()!!.offerID.toString()
            )
            val connection = url.openConnection()
            // Parsing du document XML de la réponse
            val dbf = DocumentBuilderFactory.newInstance()
            val db = dbf.newDocumentBuilder()
            val doc = db.parse(connection.getInputStream())
            // Récupération du statut de l'opération
            val status = doc.getElementsByTagName("STATUS").item(0).textContent
            // Vérification de l'état du serveur et de la session
            checkSessionAndStateServer(status)
            // Résultat de l'opération
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
            // Gestion de l'absence de connexion
            e.printStackTrace()
            actionNoConnexion(context)
        }catch (e : ConnectException){
            // Gestion des erreurs de connexion
            e.printStackTrace()
            actionNoConnexion(context)
        }
    }

    // Méthode pour changer l'offre actuellement sélectionnée
    fun selectOffre(offer : Offre?) = repository.changeSelectOffre(offer)

    // Méthode pour obtenir la liste des offres du repository
    fun getListe() = repository.getlesOffres()


}