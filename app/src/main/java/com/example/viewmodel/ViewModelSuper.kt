package com.example.viewmodel

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Looper
import android.view.LayoutInflater
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import com.example.design.R
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
    @SuppressLint("StaticFieldLeak")
    protected lateinit var context : AppCompatActivity
    fun checkSessionAndStateServer(status : String) {
        when (status) {
            Status.SESSIONEXPIRED.value -> {
                Looper.prepare()
                Toast.makeText(context, context.getString(R.string.text_session_exp), Toast.LENGTH_SHORT).show()
                val intent = Intent(context, Connexion::class.java)
                context.startActivity(intent)
                context.finish()
            }
            Status.SESSIONINVALID.value -> {
                Looper.prepare()
                Toast.makeText(context, context.getString(R.string.text_session_invalide), Toast.LENGTH_SHORT).show()
                val intent = Intent(context, Connexion::class.java)
                context.startActivity(intent)
                context.finish()
            }
            Status.TECHNICALERROR.value -> {
                Looper.prepare()
                Toast.makeText(context,context.getString(R.string.erreur_serveur),Toast.LENGTH_SHORT).show()
                val intent = Intent(context, Connexion::class.java)
                context.startActivity(intent)
                context.finish()
            }
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
            checkSessionAndStateServer(status)
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
            checkSessionAndStateServer(status)
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
        val list = ArrayList<Item>()
        for(i in 1 .. 13){
            val url = URL(
                repository.getBaseURL() + "item_detail.php?session=" + repository.getSession() +
                        "&signature=" + repository.getSignature() + "&item_id=" + i)
            val connection = url.openConnection()
            val dbf = DocumentBuilderFactory.newInstance()
            val db = dbf.newDocumentBuilder()
            val doc = db.parse(connection.getInputStream())
            val infos = doc.getElementsByTagName("PARAMS").item(0).childNodes
            val item = Item(-1,"","",0,"","","")

            item.id = i
            item.nom = infos.item(0).textContent
            when(infos.item(1).textContent){
                "A" -> item.type = "Artéfact"
                "M" -> item.type = "Minerai"
            }
            item.rarity = infos.item(2).textContent.toInt()
            item.image = infos.item(3).textContent
            item.descEn = infos.item(4).textContent
            item.descFr = infos.item(5).textContent

            list.add(item)
        }
        repository.setItemDetailList(list)
    }

    fun getImages(){
        val list = ArrayList<Bitmap>()
        for(i in 1 .. 13){
            val imageurl = URL(getBaseLoginImg() + repository.getItemDetail(i-1).image)
            val bitmap = BitmapFactory.decodeStream(imageurl.openConnection().getInputStream())
            list.add(bitmap)
        }
        repository.setImageList(list)
    }

    fun actionNoConnexion(context : AppCompatActivity){
        val intent = Intent(context, Connexion::class.java)
        Looper.prepare()
        Toast.makeText(context,context.getString(R.string.text_connexion_perdue),Toast.LENGTH_SHORT).show()
        context.startActivity(intent)
        context.finish()
    }

    fun makePopupMessage(context : AppCompatActivity, msg : String){
        // execute le code sur le thread principal
        context.runOnUiThread {
            val popup = AlertDialog.Builder(context)
            val popupView = LayoutInflater.from(context).inflate(R.layout.popup_message, null)
            popup.setView(popupView)
            val content : TextView = popupView.findViewById(R.id.popup_txt_msg)
            content.text = msg
            popup.show()
        }
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