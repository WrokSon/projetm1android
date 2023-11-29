package com.example.design.views

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.ListView
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.size
import androidx.lifecycle.ViewModelProvider
import com.example.design.R
import com.example.viewmodel.InvViewModel
import com.example.viewmodel.MainViewModel

class Inventaire : AppCompatActivity() {
    private lateinit var viewModel: InvViewModel
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_inventaire)
        viewModel = ViewModelProvider(this).get(InvViewModel::class.java)
        var goGame : ImageButton = findViewById(R.id.inventaire_retour)
        goGame.setOnClickListener{
            onBackPressedDispatcher.onBackPressed()
        }
        var thread = Thread{
            viewModel.playerStatus(this)
        }
        thread.start()
        val itemList : TableLayout = findViewById(R.id.items)
        val items = viewModel.getPlayer().items
        for(i in items){
            var itemText = TextView(applicationContext)
            itemText.text = i.key.nom
            when(i.key.rarity){
                1 -> itemText.setTextColor(Color.WHITE)
                2 -> itemText.setTextColor(Color.GREEN)
                3 -> itemText.setTextColor(Color.BLUE)
                4 -> itemText.setTextColor(Color.RED)
                5 -> itemText.setTextColor(Color.YELLOW)
            }
            when(i.key.type){
                'A' -> itemText.text = itemText.text as String + " (Artéfact)"
                'M' -> itemText.text = itemText.text as String + " (Minerai)"
            }
            itemText.text = itemText.text as String + "\nQuantité : "+i.value.toString()
            val line = TableRow(applicationContext)
            line.addView(itemText)
            line.setOnClickListener {
                Toast.makeText(applicationContext,i.key.nom,Toast.LENGTH_SHORT).show()
            }
            line.setBackgroundColor(Color.LTGRAY)
            itemList.addView(line)
        }
    }
}