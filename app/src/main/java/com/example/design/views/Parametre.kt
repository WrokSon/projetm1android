package com.example.design.views

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.example.design.R
import com.example.viewmodel.ParametreViewModel

class Parametre : AppCompatActivity() {
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_parametre)
        val viewModel = ViewModelProvider(this).get(ParametreViewModel::class.java)

        var goGame : ImageButton = findViewById(R.id.parametre_retour)
        goGame.setOnClickListener{
            onBackPressed()
        }

        var goConnection : Button = findViewById(R.id.btn_deconnexion)
        goConnection.setOnClickListener{
            //annuler auto connexion
            viewModel.disconnect()
            val intent : Intent = Intent(this, Connexion::class.java)
            startActivity(intent)
        }


        var changeNameET : EditText = findViewById(R.id.name_player)
        var changeNameBtn : Button = findViewById(R.id.btn_change_name)
        changeNameBtn.setOnClickListener{
            if (!changeNameET.text.isNullOrBlank()) {
                val thread = Thread {
                    viewModel.changeName(changeNameET.text.toString())
                    changeNameET.text.clear()
                }
                thread.start()
            }
        }

        var resetBtn : Button = findViewById(R.id.reinisialiser)
        resetBtn.setOnClickListener{
            Thread{viewModel.reset()}.start()
            Toast.makeText(this,"frero c'est fait",Toast.LENGTH_LONG).show()
        }

    }
}