package com.example.design.views

import android.content.DialogInterface
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.os.Looper
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.design.R
import com.example.model.tools.Status
import com.example.viewmodel.InvViewModel
import com.squareup.picasso.Picasso

class Inventaire : AppCompatActivity() {
    private lateinit var viewModel: InvViewModel
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_inventaire)
        viewModel = ViewModelProvider(this).get(InvViewModel::class.java)
        var goGame: ImageButton = findViewById(R.id.inventaire_retour)
        goGame.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
            finish()
        }
        val textpick: TextView = findViewById(R.id.textpick)
        val buttonpick: Button = findViewById(R.id.buttonpick)
        var thread = Thread {
            viewModel.playerStatus(this)
        }
        thread.start()
        thread.join()
        val itemList: TableLayout = findViewById(R.id.items)
        val items = viewModel.getPlayer().items
        for (item in items) {
            val details = viewModel.getItemDetail(item.key - 1)
            val itemIcon = ImageView(applicationContext)
            Picasso.with(this).load("https://test.vautard.fr/creuse_imgs/" + details.image)
                .into(itemIcon)
            itemIcon.scaleType = ImageView.ScaleType.FIT_CENTER
            itemIcon.adjustViewBounds = true
            itemIcon.maxWidth = 80
            itemIcon.maxHeight = 80
            val itemText = TextView(applicationContext)
            itemText.text = details.nom
            when (details.rarity) {
                1 -> itemText.setTextColor(Color.WHITE)
                2 -> itemText.setTextColor(Color.GREEN)
                3 -> itemText.setTextColor(Color.BLUE)
                4 -> itemText.setTextColor(Color.RED)
                5 -> itemText.setTextColor(Color.YELLOW)
            }
            when (details.type) {
                'A' -> itemText.text = itemText.text as String + " (Artéfact)"
                'M' -> itemText.text = itemText.text as String + " (Minerai)"
            }
            itemText.text = itemText.text as String + "\nQuantité : " + item.value.toString()
            val line = TableRow(applicationContext)
            line.addView(itemIcon)
            line.addView(itemText)
            line.setOnClickListener {
                val popup = AlertDialog.Builder(this@Inventaire)
                popup.setIcon(Drawable.createFromPath("https://test.vautard.fr/creuse_imgs/" + details.image))
                popup.setTitle(details.nom)
                popup.setMessage(itemText.text as String + "\n" + details.desc_fr)
                popup.show()
            }
            line.setBackgroundColor(Color.LTGRAY)
            itemList.addView(line)
        }
        val currentpick = viewModel.getPlayer().pick
        textpick.text = "Pioche actuelle : " + currentpick
        buttonpick.setOnClickListener {
            var items = HashMap<Int, Int>()
            val thread = Thread {
                items = viewModel.getCraft(currentpick + 1)
            }
            thread.start()
            thread.join()
            val popupcraft = AlertDialog.Builder(this@Inventaire)
            val viewpopup = this.layoutInflater.inflate(R.layout.popup_craft, null)
            popupcraft.setView(viewpopup)
            val layout = viewpopup.findViewById<TableLayout>(R.id.itemscraft)
            for (item in items) {
                val details = viewModel.getItemDetail(item.key - 1)
                val itemIcon = ImageView(applicationContext)
                Picasso.with(this).load("https://test.vautard.fr/creuse_imgs/" + details.image)
                    .into(itemIcon)
                itemIcon.scaleType = ImageView.ScaleType.FIT_CENTER
                itemIcon.adjustViewBounds = true
                itemIcon.maxWidth = 80
                itemIcon.maxHeight = 80
                val itemText = TextView(applicationContext)
                itemText.text = details.nom
                when (details.rarity) {
                    1 -> itemText.setTextColor(Color.WHITE)
                    2 -> itemText.setTextColor(Color.GREEN)
                    3 -> itemText.setTextColor(Color.BLUE)
                    4 -> itemText.setTextColor(Color.RED)
                    5 -> itemText.setTextColor(Color.YELLOW)
                }
                when (details.type) {
                    'A' -> itemText.text = itemText.text as String + " (Artéfact)"
                    'M' -> itemText.text = itemText.text as String + " (Minerai)"
                }
                itemText.text = itemText.text as String + "\nQuantité : " + item.value.toString()
                val line = TableRow(applicationContext)
                line.addView(itemIcon)
                line.addView(itemText)
                line.setBackgroundColor(Color.LTGRAY)
                layout.addView(line)
            }
            popupcraft.setTitle("Minerais requis : ")
            val threadcraft = Thread{
                val status = viewModel.craftPickaxe(currentpick + 1)
                Looper.prepare()
                if(status == Status.OK.value){
                    Toast.makeText(applicationContext,"Pioche améliorée !",Toast.LENGTH_SHORT).show()
                    viewModel.playerStatus(this)
                    onBackPressedDispatcher.onBackPressed()
                }
                else if(status == Status.NOITEMS.value){
                    Toast.makeText(applicationContext,"Items manquants",Toast.LENGTH_SHORT).show()
                }
                else{
                    Toast.makeText(applicationContext,"Erreur",Toast.LENGTH_SHORT).show()
                }
            }
            popupcraft.setPositiveButton("CRAFT") { dialog, which ->
                threadcraft.start()
                threadcraft.join()
            }
            popupcraft.show()
        }
    }
}