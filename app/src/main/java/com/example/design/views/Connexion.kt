package com.example.design.views

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View

import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.example.design.R
import com.example.model.ConnViewModel
import com.example.model.SaveLocal

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
        autoConnexion()
        setContentView(R.layout.activity_connexion)
        val goGame: Button = findViewById(R.id.btn_connexion)
        val ko: TextView = findViewById(R.id.titlecon)
        val login: EditText = findViewById(R.id.user)
        val passwd: EditText = findViewById(R.id.password)
        Log.d(
            "ID",
            "login:'${saveData.getUsername().toString()}' password:'${
                saveData.getPassword().toString()
            }'"
        )

        Log.d("CONNEXION","recu = ${Intent().getIntExtra("DECONNEXION", 0)}")
        goGame.setOnClickListener {
            var thread = Thread {
                if (login.text.isNotBlank() || passwd.text.isNotBlank()) {
                    constatus = viewModel.connexion(login.text.toString(), passwd.text.toString())
                    if (constatus == "OK") {
                        saveData.saveUser(login.text.toString(), passwd.text.toString())
                        val intent: Intent = Intent(this, MainActivity::class.java)
                        startActivity(intent)
                    }

                }
            }
            thread.start()
            if (constatus == "KO - WRONG CREDENTIALS") {
                ko.setText("Mauvais identifiants")
            } else {
                ko.setText("Identifiants manquants")
            }

        }
    }

    fun autoConnexion() {
        if (saveData.getUsername().toString() != "") {
            var thread = Thread {
                constatus = viewModel.connexion(
                    saveData.getUsername().toString(),
                    saveData.getPassword().toString()
                )
                if (constatus == "OK") {
                    val intent: Intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                }
            }
            thread.start()
        }
    }

    override fun onBackPressed() {}
}
