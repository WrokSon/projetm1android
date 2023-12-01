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
            onBackPressedDispatcher.onBackPressed()
        }
    }
}

/*
<root>
<STATUS>OK</STATUS>
<PARAMS>
<OFFERS>
<item0>
<OFFER_ID>41</OFFER_ID>
<ITEM_ID>10</ITEM_ID>
<QUANTITE>1</QUANTITE>
<PRIX>12000</PRIX>
</item0>
</OFFERS>
</PARAMS>
</root>
 */