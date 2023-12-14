package com.example.design.views

import android.annotation.SuppressLint
import android.content.pm.ActivityInfo
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageButton
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import com.example.design.R
import com.example.viewmodel.MainViewModel

class Profil : AppCompatActivity() {
    private lateinit var viewModel: MainViewModel

    @SuppressLint("SourceLockedOrientationActivity")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profil)
        viewModel = ViewModelProvider(this)[MainViewModel::class.java]
        viewModel.initContext(this)
        // Bloquer l'orientation en mode portrait
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        val goGame : ImageButton = findViewById(R.id.profil_retour)
        goGame.setOnClickListener{
            onBackPressedDispatcher.onBackPressed()
        }
        val thread = Thread {
            viewModel.playerStatus()
        }
        thread.start()
        thread.join()
        val login: TextView = findViewById(R.id.profil_name)
        val money: TextView = findViewById(R.id.profil_money)
        val picklevel: TextView = findViewById(R.id.profil_level)
        val player = viewModel.getPlayer()

        login.text = player.username
        money.text = player.money.toString()
        picklevel.text = player.pick.toString()
    }
}