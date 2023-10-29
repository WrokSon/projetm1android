package com.example.design.views

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import com.example.design.R
import com.example.model.SaveLocal

class Parametre : AppCompatActivity() {
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_parametre)

        var goGame : ImageButton = findViewById(R.id.parametre_retour)
        goGame.setOnClickListener{
            val intent : Intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        var goConnection : Button = findViewById(R.id.btn_deconnexion)
        goConnection.setOnClickListener{
            val intent : Intent = Intent(this, Connexion::class.java)
            startActivity(intent)
        }

    }
}