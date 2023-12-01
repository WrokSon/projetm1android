package com.example.design.views

import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.design.R
import com.example.viewmodel.InvViewModel
import com.squareup.picasso.Picasso

class Inventaire : AppCompatActivity() {
    private lateinit var viewModel: InvViewModel
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_inventaire)
        viewModel = ViewModelProvider(this).get(InvViewModel::class.java)
        var goGame : ImageButton = findViewById(R.id.inventaire_retour)
        goGame.setOnClickListener{
            onBackPressedDispatcher.onBackPressed()
            finish()
        }
        var thread = Thread{
            viewModel.playerStatus(this)
        }
        thread.start()
        thread.join()
        val itemList : TableLayout = findViewById(R.id.items)
        val items = viewModel.getPlayer().items
        for(i in items){
            val itemIcon = ImageView(applicationContext)
            Picasso.with(this).load("https://test.vautard.fr/creuse_imgs/"+i.key.image).into(itemIcon)
            itemIcon.scaleType = ImageView.ScaleType.FIT_CENTER
            itemIcon.adjustViewBounds = true
            itemIcon.maxWidth = 80
            itemIcon.maxHeight = 80
            val itemText = TextView(applicationContext)
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
            line.addView(itemIcon)
            line.addView(itemText)
            line.setOnClickListener {
                val popup = AlertDialog.Builder(this@Inventaire)
                popup.setIcon(Drawable.createFromPath("https://test.vautard.fr/creuse_imgs/"+i.key.image))
                popup.setTitle(i.key.nom)
                popup.setMessage(itemText.text as String + "\n" + i.key.desc_fr)
                popup.show()
            }
            line.setBackgroundColor(Color.LTGRAY)
            itemList.addView(line)
        }
    }
}