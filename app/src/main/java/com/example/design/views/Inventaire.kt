package com.example.design.views

import android.annotation.SuppressLint
import android.content.DialogInterface
import android.content.pm.ActivityInfo
import android.graphics.Bitmap
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
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.example.design.R
import com.example.model.data.Item
import com.example.model.data.Offre
import com.example.model.data.Player
import com.example.model.tools.Status
import com.example.viewmodel.InvViewModel

class Inventaire : AppCompatActivity() {
    private lateinit var viewModel: InvViewModel
    private lateinit var adapter: ItemInventoryRecycleViewAdapter
    interface OnInventoryInteractionListener {
        fun getDetailItem(id : Int) : Item
        fun getImage(id: Int): Bitmap
        fun getPlayer(): Player

        fun clickItem(itemid : Int, text: String)
    }
    private val listenerInventory : OnInventoryInteractionListener = object : OnInventoryInteractionListener {

        override fun getDetailItem(id: Int): Item {
            return viewModel.getItemDetail(id)
        }

        override fun getImage(id: Int): Bitmap {
            return viewModel.getImage(id)
        }

        override fun getPlayer(): Player {
            return viewModel.getPlayer()
        }

        override fun clickItem(itemid: Int, text: String) {
            val item = viewModel.getItemDetail(itemid - 1)
            val popup = AlertDialog.Builder(this@Inventaire)
            popup.setMessage(text + "\n" + item.descFr)
            popup.setPositiveButton("VENDRE") { _: DialogInterface, _: Int ->
                val popupsell = AlertDialog.Builder(this@Inventaire)
                val popupView = LayoutInflater.from(this@Inventaire).inflate(R.layout.popup_vendre, null)
                popupsell.setView(popupView)
                val imagevente: ImageView = popupView.findViewById(R.id.imagevente)
                val btnValidate: Button = popupView.findViewById(R.id.btn_vendre_validate)
                val tVTitle: TextView = popupView.findViewById(R.id.vendre_titre)
                val tVQteDispo: TextView = popupView.findViewById(R.id.vendre_qte_dispo)
                val prix: EditText = popupView.findViewById(R.id.vendre_prix)
                val qte: EditText = popupView.findViewById(R.id.vendre_qte)
                imagevente.setImageBitmap(viewModel.getImage(itemid - 1))
                tVTitle.text = item.nom
                var newTextQteDispo = "/ " + viewModel.getPlayer().items[itemid]!!
                tVQteDispo.text = newTextQteDispo
                btnValidate.setOnClickListener {
                    if (prix.text.isNotEmpty() && qte.text.isNotEmpty() && viewModel.getPlayer().items.containsKey(
                            itemid
                        )
                    ) {
                        var status = ""
                        val thread3 = Thread {
                            status = viewModel.vendre(
                                itemid,
                                prix.text.toString(),
                                qte.text.toString()
                            )
                        }
                        thread3.start()
                        thread3.join()
                        var quant = viewModel.getPlayer().items[itemid]!!.toInt()
                        when (status) {
                            Status.OK.value -> {
                                quant -= qte.text.toString().toInt()
                                viewModel.makePopupMessage(this@Inventaire, "Vous venez de vendre ${qte.text} ${item.nom} a ${prix.text}")
                                newTextQteDispo = "/ $quant"
                                tVQteDispo.text = newTextQteDispo
                                qte.text.clear()
                                prix.text.clear()
                                Thread { viewModel.playerStatus() }.start()
                            }
                            Status.NOITEMS.value -> {
                                viewModel.makePopupMessage(this@Inventaire, "La quantité est superieur a la quantité disponible ")
                            }
                            else -> {
                                viewModel.makePopupMessage(this@Inventaire, "Il y a eu un probleme avec la vente")
                            }
                        }
                    } else if (!viewModel.getPlayer().items.containsKey(itemid)) {
                        viewModel.makePopupMessage(this@Inventaire, "Votre stock est vide :)")
                    } else {
                        viewModel.makePopupMessage(this@Inventaire, "il y a des champs vides !)")
                    }
                }
                popupsell.show()
            }
            popup.show()
            val thread = Thread{
                viewModel.playerStatus()
            }
            thread.start()
            thread.join()
        }


    }
    @SuppressLint("SourceLockedOrientationActivity")
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_inventaire)
        viewModel = ViewModelProvider(this)[InvViewModel::class.java]
        viewModel.initContext(this)
        // Bloquer l'orientation en mode portrait
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        val goGame : ImageButton = findViewById(R.id.inventaire_retour)
        goGame.setOnClickListener{
            onBackPressedDispatcher.onBackPressed()
            finish()
        }
        val textpick: TextView = findViewById(R.id.textpick)
        val buttonpick: Button = findViewById(R.id.buttonpick)

        val thread = Thread{
            viewModel.playerStatus()
        }
        thread.start()
        thread.join()

        adapter = ItemInventoryRecycleViewAdapter(listenerInventory)
        val recycle : RecyclerView = findViewById(R.id.items_recycle_view)
        recycle.adapter = adapter
        adapter.updateList(viewModel.getPlayer().items)
        var currentpick = viewModel.getPlayer().pick
        val newTextPick = "Pioche actuelle : $currentpick"
        textpick.text = newTextPick
        buttonpick.setOnClickListener {
            if(currentpick < 5) {
                var items2 = HashMap<Int, Int>()
                val thread2 = Thread {
                    items2 = viewModel.getCraft(currentpick + 1)
                }
                thread2.start()
                thread2.join()
                val popupcraft = AlertDialog.Builder(this@Inventaire)
                val viewpopup = this.layoutInflater.inflate(R.layout.popup_craft, null)
                popupcraft.setView(viewpopup)
                val layout = viewpopup.findViewById<TableLayout>(R.id.itemscraft)
                for (item in items2) {
                    val details = viewModel.getItemDetail(item.key - 1)
                    val itemIcon = ImageView(applicationContext)
                    itemIcon.setImageBitmap(viewModel.getImage(details.id - 1))
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
                        'A' -> {
                            val newText = itemText.text as String + " (Artéfact)"
                            itemText.text = newText
                        }
                        'M' -> {
                            val newText = itemText.text as String + " (Minerai)"
                            itemText.text = newText
                        }
                    }
                    val newTextItem = itemText.text as String + "\nQuantité : " + item.value.toString()
                    itemText.text = newTextItem
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
                    when (status) {
                        Status.OK.value -> {
                            viewModel.makePopupMessage(this, "Pioche améliorée !")
                            viewModel.playerStatus()
                        }
                        Status.NOITEMS.value -> {
                            viewModel.makePopupMessage(this, "Items manquants")
                        }
                        else -> {
                            viewModel.makePopupMessage(this, "Erreur")
                        }
                    }
                }
                popupcraft.setPositiveButton("CRAFT") { _, _ ->
                    threadcraft.start()
                    threadcraft.join()
                    currentpick = viewModel.getPlayer().pick
                    val newText = "Pioche actuelle : $currentpick"
                    textpick.text = newText
                }
                popupcraft.show()
            }
            else{
                viewModel.makePopupMessage(this,"Pioche maximum atteinte")
            }
        }
    }
}