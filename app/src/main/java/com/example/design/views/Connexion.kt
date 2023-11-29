package com.example.design.views

import android.annotation.SuppressLint
import android.content.Intent
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
        saveData = SaveLocal(this)

        var constatus = ""
        setContentView(R.layout.activity_connexion)
        val goGame: Button = findViewById(R.id.btn_connexion)
        val ko: TextView = findViewById(R.id.titlecon)
        var login: EditText = findViewById(R.id.user)
        var passwd: EditText = findViewById(R.id.password)

        if (saveData.getUsername().toString() != "" && viewModel.getValueAutoConnection()) {
            login.setText(this.saveData.getUsername().toString())
            passwd.setText(this.saveData.getPassword().toString())
        }

        goGame.setOnClickListener {
            var thread = Thread {
                if (login.text.isNotBlank() || passwd.text.isNotBlank()) {
                    constatus = viewModel.connexion(login.text.toString(), passwd.text.toString())
                    if (constatus == Status.OK.value) {
                        //enregistrer les identifiants
                        saveData.saveUser(login.text.toString(), passwd.text.toString())
                        //activer l'autoconnexion
                        viewModel.setValueAutoConnection(true)
                        val intent: Intent = Intent(this, MainActivity::class.java)
                        startActivity(intent)
                        finish()
                    }

                }
            }
            thread.start()
            thread.join()
            if (!(constatus == Status.OK.value)) {

                if (constatus == Status.WRONGCREDENTIALS.value) {
                    ko.setText("Mauvais identifiants")
                }else if(constatus == ""){
                  ko.setText("Pas de connexion")
                } else{
                    ko.setText("Identifiants manquants")
                }
            }

        }
        Toast.makeText(this,"je suis lancé",Toast.LENGTH_SHORT).show()
    }

    @SuppressLint("MissingSuperCall")
    override fun onBackPressed() {}
}
