package com.example.design.views

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.design.R
import com.example.model.RepoConnexion
import com.example.model.SaveLocal
import com.example.viewmodel.ParametreViewModel

class Parametre : AppCompatActivity() {
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_parametre)
        val viewModel = ViewModelProvider(this).get(ParametreViewModel::class.java)

        var goGame : ImageButton = findViewById(R.id.parametre_retour)
        goGame.setOnClickListener{
            val intent : Intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        var goConnection : Button = findViewById(R.id.btn_deconnexion)
        goConnection.setOnClickListener{
            //annuler auto connexion
            viewModel.disconnect()
            val intent : Intent = Intent(this, Connexion::class.java)
            startActivity(intent)
        }

    }
}