package com.example.design.views

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.ActivityInfo
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import androidx.lifecycle.ViewModelProvider
import com.example.design.R
import com.example.viewmodel.ParametreViewModel

class Parametre : AppCompatActivity() {

    @SuppressLint("MissingInflatedId", "SourceLockedOrientationActivity")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_parametre)
        val viewModel = ViewModelProvider(this)[ParametreViewModel::class.java]
        viewModel.initContext(this)
        // Bloquer l'orientation en mode portrait
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        val goGame : ImageButton = findViewById(R.id.parametre_retour)
        goGame.setOnClickListener{
            onBackPressedDispatcher.onBackPressed()
            Log.d("PARAMETREMECOUILLE","oui on est la")
        }

        val goConnection : Button = findViewById(R.id.btn_deconnexion)
        goConnection.setOnClickListener{
            val intent = Intent(this, Connexion::class.java)
            startActivity(intent)
            finish()
        }


        val changeNameET : EditText = findViewById(R.id.name_player)
        val changeNameBtn : Button = findViewById(R.id.btn_change_name)
        changeNameBtn.setOnClickListener{
            if (!changeNameET.text.isNullOrBlank()) {
                val name = changeNameET.text.toString()
                val thread = Thread {
                    viewModel.changeName(name)
                    changeNameET.text.clear()
                }
                thread.start()
                thread.join()
            }
        }

        val resetBtn : Button = findViewById(R.id.reinisialiser)
        resetBtn.setOnClickListener{
            Thread{viewModel.reset()}.start()
            viewModel.setResetValue(true)
        }
    }

}