package com.example.design.views

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import com.example.design.R

class Marche : AppCompatActivity() {
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_marche)

        var goGame : ImageButton = findViewById(R.id.marche_retour)
        goGame.setOnClickListener{
            val intent : Intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }
}