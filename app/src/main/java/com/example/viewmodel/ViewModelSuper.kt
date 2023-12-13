package com.example.viewmodel

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import com.example.design.views.Connexion
import com.example.model.Repository
import com.example.model.data.Item
import com.example.model.data.Player
import com.example.model.tools.Status
import org.w3c.dom.Document
import java.net.ConnectException
import java.net.URL
import java.net.UnknownHostException
import javax.xml.parsers.DocumentBuilderFactory

open class ViewModelSuper : ViewModel() {
    protected val repository = Repository.getInstance()
    protected lateinit var context : AppCompatActivity
    fun checkSession(status : String) {
        if (status == Status.SESSIONEXPIRED.value) {
            Looper.prepare()
            Toast.makeText(context, "Session expiré", Toast.LENGTH_SHORT).show()
            val intent = Intent(context, Connexion::class.java)
            context.startActivity(intent)
            context.finish()
        }else if (status == Status.SESSIONINVALID.value){
            Looper.prepare()
            Toast.makeText(context, "Session invalide", Toast.LENGTH_SHORT).show()
            val intent = Intent(context, Connexion::class.java)
            context.startActivity(intent)
            context.finish()
        }else if(status == Status.TECHNICALERROR.value){
            Looper.prepare()
            Toast.makeText(context,"Il y a eu un probeleme avec le seveur",Toast.LENGTH_SHORT).show()
            val intent = Intent(context, Connexion::class.java)
            context.startActivity(intent)
            context.finish()
        }
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
            val items = HashMap<Int, Int>()
            val connection = url.openConnection()
            val dbf = DocumentBuilderFactory.newInstance()
            val db = dbf.newDocumentBuilder()
            val doc = db.parse(connection.getInputStream())
            val status = doc.getElementsByTagName("STATUS").item(0).textContent
            checkSession(status)
            var lat = doc.getElementsByTagName("LATITUDE").item(0).textContent
            var long = doc.getElementsByTagName("LONGITUDE").item(0).textContent
            val inv = doc.getElementsByTagName("ITEMS").item(0).childNodes
            for (i in 0..<inv.length) {
                val id = inv.item(i).childNodes.item(0).textContent.toInt()
                items[id] = inv.item(i).childNodes.item(1).textContent.toInt()
            }
            if (lat == "" || long == "") {
                lat = "0.0"
                long = "0.0"
            }
            checkSession(status)
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

    //fonction qui récupère tout les détails de tout les items afin de pas avoir à appeler à chaque fois le ws
    fun getItemsDetails(){
        var list = ArrayList<Item>()
        for(i in 1 .. 13){
            val url = URL(
                repository.getBaseURL() + "item_detail.php?session=" + repository.getSession() +
                        "&signature=" + repository.getSignature() + "&item_id=" + i)
            val connection = url.openConnection()
            val dbf = DocumentBuilderFactory.newInstance()
            val db = dbf.newDocumentBuilder()
            val doc = db.parse(connection.getInputStream())
            val infos = doc.getElementsByTagName("PARAMS").item(0).childNodes
            val item = Item(-1,"",'_',0,"","","")

            item.id = i
            item.nom = infos.item(0).textContent
            item.type = infos.item(1).textContent[0]
            item.rarity = infos.item(2).textContent.toInt()
            item.image = infos.item(3).textContent
            item.desc_en = infos.item(4).textContent
            item.desc_fr = infos.item(5).textContent

            list.add(item)
        }
        repository.setItemDetailList(list)
    }

    fun getImages(){
        var list = ArrayList<Bitmap>()
        for(i in 1 .. 13){
            val imageurl = URL(getBaseLoginImg() + repository.getItemDetail(i-1).image)
            val bitmap = BitmapFactory.decodeStream(imageurl.openConnection().getInputStream())
            list.add(bitmap)
        }
        repository.setImageList(list)
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

    fun getBaseLoginImg() = repository.getBaseLoginImg()

    fun getPlayer(): Player {
        return repository.getPlayer()
    }

    fun setResetValue(value:Boolean){
        repository.setResetValue(value)
    }

    fun getResetValue() = repository.getResetValue()

    fun getItemDetail(id : Int) = repository.getItemDetail(id)

    fun getImage(id: Int) = repository.getImage(id)
}