package com.example.design.views

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.ActivityInfo
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.example.design.R
import com.example.viewmodel.ConnViewModel
import com.example.model.tools.SaveLocal
import com.example.model.tools.Status

class Connexion : AppCompatActivity() {
    private lateinit var viewModel: ConnViewModel
    private lateinit var saveData: SaveLocal
    private lateinit var constatus: String

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this).get(ConnViewModel::class.java)
        viewModel.initContext(this)
        saveData = SaveLocal(this)
        // Bloquer l'orientation en mode portrait
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        var constatus = ""
        setContentView(R.layout.activity_connexion)
        val goGame: Button = findViewById(R.id.btn_connexion)
        val ko: TextView = findViewById(R.id.titlecon)
        var login: EditText = findViewById(R.id.user)
        var passwd: EditText = findViewById(R.id.password)

        if (saveData.getLogin().toString() != "") {
            login.setText(this.saveData.getLogin().toString())
            passwd.setText(this.saveData.getPassword().toString())
        }

        goGame.setOnClickListener {

            if (login.text.isNotBlank() || passwd.text.isNotBlank()) {
                var thread = Thread { constatus = viewModel.connexion(login.text.toString(), passwd.text.toString())}
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
            if (!(constatus == Status.OK.value)) {
                if (constatus == Status.WRONGCREDENTIALS.value) {
                    ko.setText("Mauvais identifiants")
                }else{
                  ko.setText("Pas de connexion ou champs vides")
                }
            }

        }
    }

    @SuppressLint("MissingSuperCall")
    override fun onBackPressed() {}
}
