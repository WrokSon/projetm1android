package com.example.design.views

import android.content.DialogInterface
import android.content.pm.ActivityInfo
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.os.Looper
import android.view.LayoutInflater
import android.widget.Button
import android.widget.EditText
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
        viewModel.initContext(this)
        // Bloquer l'orientation en mode portrait
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        var goGame : ImageButton = findViewById(R.id.inventaire_retour)
        goGame.setOnClickListener{
            onBackPressedDispatcher.onBackPressed()
            finish()
        }
        val textpick: TextView = findViewById(R.id.textpick)
        val buttonpick: Button = findViewById(R.id.buttonpick)

        var thread = Thread{
            viewModel.playerStatus()
        }
        thread.start()
        thread.join()
        val itemList: TableLayout = findViewById(R.id.items)
        val items = viewModel.getPlayer().items
        for (item in items) {
            val details = viewModel.getItemDetail(item.key - 1)
            val itemIcon = ImageView(applicationContext)
            Picasso.with(this).load(viewModel.getBaseLoginImg() + details.image)
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
            itemText.text = itemText.text as String + "\nQuantité : " + item.value.toString() + " (" + item.key + ")"
            val line = TableRow(applicationContext)
            line.addView(itemIcon)
            line.addView(itemText)
            line.setOnClickListener {
                val popup = AlertDialog.Builder(this@Inventaire)
                popup.setIcon(Drawable.createFromPath(viewModel.getBaseLoginImg() + details.image))
                popup.setMessage(itemText.text as String + "\n" + details.desc_fr)
                popup.setPositiveButton("VENDRE") { dialog: DialogInterface, which: Int ->
                    val popupsell = AlertDialog.Builder(this@Inventaire)
                    val popupView = LayoutInflater.from(this).inflate(R.layout.popup_vendre, null)
                    popupsell.setView(popupView)
                    val btn_validate : Button = popupView.findViewById(R.id.btn_vendre_validate)
                    val tVTitle : TextView = popupView.findViewById(R.id.vendre_titre)
                    val tVQteDispo : TextView = popupView.findViewById(R.id.vendre_qte_dispo)
                    val prix : EditText = popupView.findViewById(R.id.vendre_prix)
                    val qte : EditText = popupView.findViewById(R.id.vendre_qte)
                    tVTitle.setText(details.nom)
                    tVQteDispo.setText("/ "+viewModel.getPlayer().items[details.id]!!)
                    btn_validate.setOnClickListener{
                        if(prix.text.isNotEmpty() && qte.text.isNotEmpty() && viewModel.getPlayer().items.containsKey(details.id)){
                            var status = ""
                            val thread = Thread {
                                status = viewModel.vendre(details.id, prix.text.toString(), qte.text.toString())
                            }
                            thread.start()
                            thread.join()
                            var quant = viewModel.getPlayer().items[details.id]!!.toInt()
                            if (status == Status.OK.value) {
                                quant -= qte.text.toString().toInt()
                                Toast.makeText(this,"Vous venez de vendre ${qte.text} ${details.nom} a ${prix.text}",Toast.LENGTH_SHORT).show()
                                tVQteDispo.setText("/ "+ quant)
                                qte.text.clear()
                                prix.text.clear()
                                Thread{viewModel.playerStatus()}.start()
                            }else if (status == Status.NOITEMS.value){
                                Toast.makeText(this,"La quatité est superieur a la quantité disponible ",Toast.LENGTH_SHORT).show()
                            }else{
                                Toast.makeText(this,"Il y a eu un probleme avec la vente",Toast.LENGTH_SHORT).show()
                            }
                        }else if(!viewModel.getPlayer().items.containsKey(details.id)){
                            Toast.makeText(this,"Votre stock est vide :)",Toast.LENGTH_SHORT).show()
                        } else{
                            Toast.makeText(this,"il y a des champs vides !)",Toast.LENGTH_SHORT).show()
                        }
                    }

                    popupsell.show()
                }
                popup.show()
            }
            line.setBackgroundColor(Color.LTGRAY)
            itemList.addView(line)
        }

        var currentpick = viewModel.getPlayer().pick
        textpick.text = "Pioche actuelle : " + currentpick
        buttonpick.setOnClickListener {
            if(currentpick < 5) {
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
                    Picasso.with(this).load(viewModel.getBaseLoginImg() + details.image)
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
                    itemText.text =
                        itemText.text as String + "\nQuantité : " + item.value.toString()
                    val line = TableRow(applicationContext)
                    line.addView(itemIcon)
                    line.addView(itemText)
                    line.setBackgroundColor(Color.LTGRAY)
                    layout.addView(line)
                }

                popupcraft.setTitle("Minerais requis : ")
                val threadcraft = Thread {
                    val status = viewModel.craftPickaxe(currentpick + 1)
                    Looper.prepare()
                    if (status == Status.OK.value) {
                        Toast.makeText(applicationContext, "Pioche améliorée !", Toast.LENGTH_SHORT)
                            .show()
                        viewModel.playerStatus()
                    } else if (status == Status.NOITEMS.value) {
                        Toast.makeText(applicationContext, "Items manquants", Toast.LENGTH_SHORT)
                            .show()
                    } else {
                        Toast.makeText(applicationContext, "Erreur", Toast.LENGTH_SHORT).show()
                    }
                }
                popupcraft.setPositiveButton("CRAFT") { dialog, which ->
                    threadcraft.start()
                    threadcraft.join()
                    currentpick = viewModel.getPlayer().pick
                    textpick.text = "Pioche actuelle : " + currentpick
                }
                popupcraft.show()
            }
            else{
                Toast.makeText(this,"Pioche maximum atteinte",Toast.LENGTH_SHORT).show()
            }
        }
    }
}