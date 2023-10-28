package com.example.design.views

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import com.example.design.R

class Inventaire : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_inventaire)

        var goGame : ImageButton = findViewById(R.id.inventaire_retour)
        goGame.setOnClickListener{
            val intent : Intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }
}