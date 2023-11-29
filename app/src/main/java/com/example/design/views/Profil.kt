package com.example.design.views

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import com.example.design.R
import com.example.viewmodel.ConnViewModel
import com.example.viewmodel.MainViewModel
import org.w3c.dom.Document
import org.w3c.dom.Text

class Profil : AppCompatActivity() {
    private lateinit var viewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profil)
        viewModel = ViewModelProvider(this).get(MainViewModel::class.java)
        var goGame : ImageButton = findViewById(R.id.profil_retour)
        goGame.setOnClickListener{
            onBackPressedDispatcher.onBackPressed()
        }
        val thread = Thread {
            viewModel.playerStatus()
        }
        thread.start()
        val login: TextView = findViewById(R.id.profil_name)
        val money: TextView = findViewById(R.id.profil_money)
        val picklevel: TextView = findViewById(R.id.profil_level)
        val player = viewModel.getPlayer()

        login.setText(player.login)
        money.setText(player.money.toString())
        picklevel.setText(player.pick.toString())
    }
}