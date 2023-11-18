package com.example.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.model.Repository
import com.example.model.tools.Status
import java.net.URL
import javax.xml.parsers.DocumentBuilderFactory

class ParametreViewModel : ViewModel() {
    private val repository = Repository.getInstance()
    fun disconnect() = repository.setValueAutoConnect(false)

    fun changeName(name:String){
        val url = URL(repository.getBaseURL()+"changenom.php?signature="+repository.getSignature()+
                "&session="+repository.getSession()+ "&nom="+name)
        val connection = url.openConnection()
        val dbf = DocumentBuilderFactory.newInstance()
        val db = dbf.newDocumentBuilder()
        val doc = db.parse(connection.getInputStream())
        val status = doc.getElementsByTagName("STATUS").item(0).textContent
        if (status == Status.OK.value){
            Log.d("ICI","je suis passé par là")
            repository.getPlayer().login = name
        }
    }

}