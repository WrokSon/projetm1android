package com.example.viewmodel

import android.content.Context
import android.content.Intent
import android.os.Looper
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import com.example.design.views.Connexion
import com.example.model.Repository
import com.example.model.data.Item
import com.example.model.data.Player
import com.example.model.tools.Status
import java.net.ConnectException
import java.net.URL
import java.net.UnknownHostException
import javax.xml.parsers.DocumentBuilderFactory

open class ViewModelSuper : ViewModel() {
    protected val repository = Repository.getInstance()
    protected lateinit var context : AppCompatActivity
    fun checkSession(status : String): String{
        if(status == Status.SESSIONEXPIRED.value || status == Status.SESSIONINVALID.value){
            return "Session de merde"
        }
        if(status == Status.TECHNICALERROR.value){
            return "Erreur serveur"
        }
        return "OK"
    }

    fun initContext(contextApp : AppCompatActivity){
        context = contextApp
    }
    fun playerStatus() {
        try{
            val url = URL(
                repository.getBaseURL() + "status_joueur.php?session=" + repository.getSession() +
                        "&signature=" + repository.getSignature()
            )
            val items = HashMap<Item, Int>()
            val connection = url.openConnection()
            val dbf = DocumentBuilderFactory.newInstance()
            val db = dbf.newDocumentBuilder()
            val doc = db.parse(connection.getInputStream())
            val status = doc.getElementsByTagName("STATUS").item(0).textContent
            var lat = doc.getElementsByTagName("LATITUDE").item(0).textContent
            var long = doc.getElementsByTagName("LONGITUDE").item(0).textContent
            val inv = doc.getElementsByTagName("ITEMS").item(0).childNodes
            for (i in 0..<inv.length) {
                var item = Item(-1, "", '_', 0, "", "", "")
                val thread = Thread {
                    item = getItemDetail(inv.item(i).childNodes.item(0).textContent.toInt())
                }
                thread.start()
                thread.join()
                items[item] = inv.item(i).childNodes.item(1).textContent.toInt()
            }
            if (lat == "" || long == "") {
                lat = "0.0"
                long = "0.0"
            }
            if (status == "OK") {
                repository.updatePlayer(
                    lat.toFloat(), long.toFloat(),
                    doc.getElementsByTagName("MONEY").item(0).textContent.toInt(),
                    doc.getElementsByTagName("PICKAXE").item(0).textContent.toInt(),
                    items
                )
            }
        }catch (e : UnknownHostException){
            actionNoConnexion(context)
        }catch (e : ConnectException){
            // a gerer
            e.printStackTrace()
            actionNoConnexion(context)
        }
    }

    fun getItemDetail(id: Int): Item{
        val url = URL(
            repository.getBaseURL() + "item_detail.php?session=" + repository.getSession() +
                    "&signature=" + repository.getSignature() + "&item_id=" + id)
        val connection = url.openConnection()
        val dbf = DocumentBuilderFactory.newInstance()
        val db = dbf.newDocumentBuilder()
        val doc = db.parse(connection.getInputStream())
        val infos = doc.getElementsByTagName("PARAMS").item(0).childNodes
        val item = Item(-1,"",'_',0,"","","")

        item.id = id
        item.nom = infos.item(0).textContent
        item.type = infos.item(1).textContent[0]
        item.rarity = infos.item(2).textContent.toInt()
        item.image = infos.item(3).textContent
        item.desc_en = infos.item(4).textContent
        item.desc_fr = infos.item(5).textContent

        return item
    }

    fun actionNoConnexion(context : AppCompatActivity){
        val intent: Intent = Intent(context, Connexion::class.java)
        Looper.prepare()
        Toast.makeText(context,"Connexion perdue",Toast.LENGTH_SHORT).show()
        context.startActivity(intent)
        context.finish()
    }
    fun getSession(): Int{
        return repository.getSession()
    }

    fun getSignature(): Long{
        return repository.getSignature()
    }

    fun getBaseLogin() = repository.getBaseLogin()

    fun getPlayer(): Player {
        return repository.getPlayer()
    }

    fun setResetValue(value:Boolean){
        repository.setResetValue(value)
    }

    fun getResetValue() = repository.getResetValue()
}