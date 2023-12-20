package com.example.viewmodel

import java.net.ConnectException
import java.net.URL
import java.net.UnknownHostException
import javax.xml.parsers.DocumentBuilderFactory


class InvViewModel : ViewModelSuper() {
    fun getCraft(id: Int): HashMap<Int,Int>{
        val url = URL(
            repository.getBaseURL() + "recettes_pioches.php?session=" + repository.getSession() +
                    "&signature=" + repository.getSignature()
        )

        val connection = url.openConnection()
        val dbf = DocumentBuilderFactory.newInstance()
        val db = dbf.newDocumentBuilder()
        val doc = db.parse(connection.getInputStream())
        val crafts = doc.getElementsByTagName("UPGRADES").item(0).childNodes
        //première recette id = 2
        val nextcraft = crafts.item(id-2).childNodes.item(1).childNodes
        val items = HashMap<Int,Int>()
        for(i in 0..<nextcraft.length){
            val idItem = nextcraft.item(i).childNodes.item(0).textContent.toInt()
            items[idItem] = nextcraft.item(i).childNodes.item(1).textContent.toInt()
        }
        return items
    }

    fun craftPickaxe(id: Int): String {
        val url = URL(
            repository.getBaseURL() + "maj_pioche.php?session=" + repository.getSession() +
                    "&signature=" + repository.getSignature() + "&pickaxe_id=" + id
        )

        val connection = url.openConnection()
        val dbf = DocumentBuilderFactory.newInstance()
        val db = dbf.newDocumentBuilder()
        val doc = db.parse(connection.getInputStream())
        return doc.getElementsByTagName("STATUS").item(0).textContent
    }

    fun vendre(id : Int, prix : String, quantite : String) : String{
        var status = ""
        try{
            val url = URL(
                repository.getBaseURL() + "market_vendre.php?session=" + repository.getSession() +
                        "&signature=" + repository.getSignature() + "&item_id=" + id + "&quantite=" + quantite + "&prix=" + prix
            )

            val connection = url.openConnection()
            val dbf = DocumentBuilderFactory.newInstance()
            val db = dbf.newDocumentBuilder()
            val doc = db.parse(connection.getInputStream())
            status = doc.getElementsByTagName("STATUS").item(0).textContent
            checkSessionAndStateServer(status)
        }catch (e : UnknownHostException){
            actionNoConnexion(context)
        }catch (e : ConnectException){
            actionNoConnexion(context)
        }
        return status

    }
}