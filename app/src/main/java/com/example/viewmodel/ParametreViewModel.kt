package com.example.viewmodel

import com.example.design.R
import com.example.model.tools.Status
import java.net.ConnectException
import java.net.URL
import java.net.UnknownHostException
import javax.xml.parsers.DocumentBuilderFactory

class ParametreViewModel : ViewModelSuper() {

    fun changeName(name:String){
        try{
            // Construction de l'URL pour changer le nom
            val url = URL(
                repository.getBaseURL() + "changenom.php?signature=" + repository.getSignature() +
                        "&session=" + repository.getSession() + "&nom=" + name
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
            // Si le changement réussi
            if (status == Status.OK.value) {
                makePopupMessage(context,"${context.getString(R.string.msg_change_name)} $name")
                repository.setLogin(name)
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

    fun reset(){
        try{
            // Construction de l'URL pour changer reinitialiser le compte
            val url = URL(
                repository.getBaseURL() + "reinit_joueur.php?signature=" + repository.getSignature() +
                        "&session=" + repository.getSession()
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
            // Si la réinitialisation est réussie
            if (status == Status.OK.value) {
                makePopupMessage(context,context.getString(R.string.msg_reset))
                repository.resetLogin()
                // Récupération des changements
                playerStatus()
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

}