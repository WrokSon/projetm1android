package com.example.design.views

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.AppCompatCheckBox
import androidx.lifecycle.ViewModelProvider
import com.example.design.R
import com.example.model.tools.SaveLocal
import com.example.viewmodel.ParametreViewModel
import java.util.Locale

class Parametre : AppCompatActivity() {

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_parametre)
        val viewModel = ViewModelProvider(this).get(ParametreViewModel::class.java)
        viewModel.initContext(this)
        // Bloquer l'orientation en mode portrait
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        var goGame : ImageButton = findViewById(R.id.parametre_retour)
        goGame.setOnClickListener{
            onBackPressedDispatcher.onBackPressed()
            Log.d("PARAMETREMECOUILLE","oui on est la")
        }

        var goConnection : Button = findViewById(R.id.btn_deconnexion)
        goConnection.setOnClickListener{
            val intent : Intent = Intent(this, Connexion::class.java)
            startActivity(intent)
            finish()
        }


        var changeNameET : EditText = findViewById(R.id.name_player)
        var changeNameBtn : Button = findViewById(R.id.btn_change_name)
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

        var resetBtn : Button = findViewById(R.id.reinisialiser)
        resetBtn.setOnClickListener{
            Thread{viewModel.reset()}.start()
            viewModel.setResetValue(true)
            Toast.makeText(this,"frero c'est fait",Toast.LENGTH_LONG).show()
        }
        // Langue

        /*
        val languageSpinner : Spinner = findViewById(R.id.spinner)

        // Adapter pour le Spinner
        val adapter = ArrayAdapter.createFromResource(
            this,
            R.array.languages,
            android.R.layout.simple_spinner_item
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        languageSpinner.adapter = adapter

        // Gérer les sélections du Spinner
        languageSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                // Changer la langue en fonction de la sélection du Spinner
                changeLanguage(position)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Ne rien faire dans ce cas
            }
        }*/
    }

    private fun changeLanguage(position: Int) {
        val localeCode = when (position) {
            0 -> "en" // Anglais
            1 -> "fr" // Français
            else -> "fr"
        }

        val locale = Locale(localeCode)
        Locale.setDefault(locale)

        val configuration = Configuration(resources.configuration)
        configuration.setLocale(locale)

        // Mettre à jour la configuration de l'application
        baseContext.resources.updateConfiguration(
            configuration,
            baseContext.resources.displayMetrics
        )

        // Redémarrer l'activité pour appliquer la nouvelle langue
        recreate()
    }

}