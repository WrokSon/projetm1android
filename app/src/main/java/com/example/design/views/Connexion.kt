package com.example.design.views

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.ActivityInfo
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import com.example.design.R
import com.example.viewmodel.ConnViewModel
import com.example.model.tools.SaveLocal
import com.example.model.tools.Status

class Connexion : AppCompatActivity() {
    private lateinit var viewModel: ConnViewModel
    private lateinit var saveData: SaveLocal
    //private lateinit var constatus: String

    @SuppressLint("MissingInflatedId", "SourceLockedOrientationActivity")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this)[ConnViewModel::class.java]
        viewModel.initContext(this)
        saveData = SaveLocal(this)
        // Bloquer l'orientation en mode portrait
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        var constatus = ""
        setContentView(R.layout.activity_connexion)
        val goGame: Button = findViewById(R.id.btn_connexion)
        val ko: TextView = findViewById(R.id.titlecon)
        val login: EditText = findViewById(R.id.user)
        val passwd: EditText = findViewById(R.id.password)

        if (saveData.getLogin().toString() != "") {
            login.setText(this.saveData.getLogin().toString())
            passwd.setText(this.saveData.getPassword().toString())
        }

        goGame.setOnClickListener {

            if (login.text.isNotBlank() || passwd.text.isNotBlank()) {
                val thread = Thread { constatus = viewModel.connexion(login.text.toString(), passwd.text.toString())}
                goGame.isEnabled = false
                thread.start()
                thread.join()
                if (constatus == Status.OK.value) {
                    //enregistrer les identifiants
                    saveData.saveUser(login.text.toString(), passwd.text.toString())
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                }
            }
            if (constatus != Status.OK.value) {
                goGame.isEnabled = true
                if (constatus == Status.WRONGCREDENTIALS.value) {
                    ko.text = getString(R.string.mauvais_identifiants)
                }else{
                    val msg = "Pas de connexion ou champs vides"
                    ko.text = msg
                }
            }

        }
    }

    @Deprecated("Deprecated in Java")
    @SuppressLint("MissingSuperCall")
    override fun onBackPressed(){}
}
