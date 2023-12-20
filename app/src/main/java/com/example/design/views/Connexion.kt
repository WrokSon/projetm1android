package com.example.design.views

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.ActivityInfo
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

import android.widget.Button
import android.widget.EditText
import androidx.lifecycle.ViewModelProvider
import com.example.design.R
import com.example.viewmodel.ConnViewModel
import com.example.model.tools.SaveLocal
import com.example.model.tools.Status

class Connexion : AppCompatActivity() {
    private lateinit var viewModel: ConnViewModel
    private lateinit var saveData: SaveLocal

    @SuppressLint("MissingInflatedId", "SourceLockedOrientationActivity")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //initialisation du viewmodel
        viewModel = ViewModelProvider(this)[ConnViewModel::class.java]
        viewModel.initContext(this)
        saveData = SaveLocal(this)
        // Bloquer l'orientation en mode portrait
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        var constatus = ""
        setContentView(R.layout.activity_connexion)
        //récupération des views
        val goGame: Button = findViewById(R.id.btn_connexion)
        val login: EditText = findViewById(R.id.user)
        val passwd: EditText = findViewById(R.id.password)

        //pré-remplit les champs si une connexion a été faite
        if (saveData.getLogin().toString() != "") {
            login.setText(this.saveData.getLogin().toString())
            passwd.setText(this.saveData.getPassword().toString())
        }
        //clic du bouton de connexion
        goGame.setOnClickListener {
            //si les champs remplis
            if (login.text.isNotBlank() || passwd.text.isNotBlank()) {
                //appel de la fonction connexion dans le ws + récupère le status
                val thread = Thread { constatus = viewModel.connexion(login.text.toString(), passwd.text.toString())}
                //blocage du bouton évitant de multiplier les requêtes
                goGame.isEnabled = false
                thread.start()
                thread.join()
                //si ok alors enregistrement des champs et redirection sur main
                if (constatus == Status.OK.value) {
                    //enregistrer les identifiants
                    saveData.saveUser(login.text.toString(), passwd.text.toString())
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                }
            }
            //sinon afficher erreur
            if (constatus != Status.OK.value) {
                goGame.isEnabled = true
                if (constatus == Status.WRONGCREDENTIALS.value) {
                    viewModel.makePopupMessage(this,getString(R.string.mauvaise_identifiants))
                }else{
                    viewModel.makePopupMessage(this, getString(R.string.connexion_pas_de_connexion))
                }
            }

        }
    }

    @Deprecated("Deprecated in Java")
    @SuppressLint("MissingSuperCall")
    override fun onBackPressed(){}
}
