package com.example.design

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        var goInv : Button = findViewById(R.id.inv)
        goInv.setOnClickListener{
            val intent : Intent = Intent(this,Inventaire::class.java)
            startActivity(intent)
        }

        var goMar : Button = findViewById(R.id.mar)
        goMar.setOnClickListener{
            val intent : Intent = Intent(this,Marche::class.java)
            startActivity(intent)
        }

        var goPro : Button = findViewById(R.id.pro)
        goPro.setOnClickListener{
            val intent : Intent = Intent(this,Profil::class.java)
            startActivity(intent)
        }

        var goPar : Button = findViewById(R.id.par)
        goPar.setOnClickListener{
            val intent : Intent = Intent(this,Parametre::class.java)
            startActivity(intent)
        }

    }

}