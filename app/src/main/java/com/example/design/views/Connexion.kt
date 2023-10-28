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
import androidx.lifecycle.ViewModelProvider
import com.example.design.R
import com.example.model.ConnViewModel

class Connexion : AppCompatActivity() {
    private lateinit var viewModel: ConnViewModel
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_connexion)
        viewModel = ViewModelProvider(this).get(ConnViewModel::class.java)
        val goGame : Button = findViewById(R.id.btn_connexion)
        val ko : TextView = findViewById(R.id.titlecon)
        val login : EditText = findViewById(R.id.user)
        val passwd : EditText = findViewById(R.id.password)
        var constatus = ""
        goGame.setOnClickListener{
            var thread = Thread {
                if (login.text.isNotBlank() || passwd.text.isNotBlank()) {
                    constatus = viewModel.connexion(login.text.toString(), passwd.text.toString())
                    if (constatus == "OK") {
                        val intent: Intent = Intent(this, MainActivity::class.java)
                        startActivity(intent)
                    }

                }
            }
            thread.start()
            if(constatus == "KO - WRONG CREDENTIALS"){
                ko.setText("Mauvais identifiants")
            }
            else{
                ko.setText("Identifiants manquants")
            }

        }
    }

    override fun onBackPressed() {}
}
