package com.example.design.views

import android.annotation.SuppressLint
import android.content.pm.ActivityInfo
import android.graphics.Bitmap
import android.os.Bundle
import android.os.Looper
import android.view.LayoutInflater
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.example.design.R
import com.example.model.data.Item
import com.example.model.data.Player
import com.example.model.tools.Status
import com.example.viewmodel.InvViewModel

class Inventaire : AppCompatActivity() {
    private lateinit var viewModel: InvViewModel
    private lateinit var adapter: ItemInventoryRecycleViewAdapter
    private lateinit var adapterCraft : ItemInventoryRecycleViewAdapter
    interface OnInventoryInteractionListener {
        fun getDetailItem(id : Int) : Item
        fun getImage(id: Int): Bitmap
        fun getPlayer(): Player

        fun clickItem(itemid : Int, text: String, pos :Int)
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

        override fun clickItem(itemid: Int, text: String, pos: Int) {
            try {
                val item = viewModel.getItemDetail(itemid - 1)
                val popup = AlertDialog.Builder(this@Inventaire).create()
                val viewpopup = layoutInflater.inflate(R.layout.popup_item, null)
                popup.setView(viewpopup)
                val imageItem = viewpopup.findViewById<ImageView>(R.id.image_item)
                imageItem.setImageBitmap(viewModel.getImage(itemid - 1))
                val titleItem = viewpopup.findViewById<TextView>(R.id.item_titledesc)
                titleItem.text = text
                val descItem = viewpopup.findViewById<TextView>(R.id.item_desc)
                if(resources.configuration.locales.toString().startsWith("[fr")) descItem.text = item.descFr
                if(resources.configuration.locales.toString().startsWith("[en")) descItem.text = item.descEn
                val btnVendre = viewpopup.findViewById<Button>(R.id.btn_inv_vendre)
                btnVendre.setOnClickListener{
                    val popupsell = AlertDialog.Builder(this@Inventaire).create()
                    val popupView =
                        LayoutInflater.from(this@Inventaire).inflate(R.layout.popup_vendre, null)
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
                        if (prix.text.isNotEmpty() && qte.text.isNotEmpty() && viewModel.getPlayer().items.containsKey(itemid)
                            && prix.text.toString().toInt() > 0
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
                                    viewModel.makePopupMessage(
                                        this@Inventaire,
                                        "${getString(R.string.msg_vente)} ${qte.text} ${item.nom} ${getString(R.string.text_pour)} ${prix.text}"
                                    )
                                    newTextQteDispo = "/ $quant"
                                    tVQteDispo.text = newTextQteDispo
                                    qte.text.clear()
                                    prix.text.clear()
                                    val thread = Thread { viewModel.playerStatus() }
                                    thread.start()
                                    thread.join()
                                    adapter.updateList(viewModel.getPlayer().items)
                                    if(newTextQteDispo.split(" ")[1].toInt() == 0){
                                        adapter.notifyItemRemoved(pos)
                                    }
                                    else{
                                        adapter.notifyItemChanged(pos)
                                    }
                                    adapter.notifyItemRangeChanged(0,viewModel.getPlayer().items.size)
                                }

                                Status.NOITEMS.value -> {
                                    viewModel.makePopupMessage(this@Inventaire, getString(R.string.msg_qte_sup))
                                }

                                else -> {
                                    viewModel.makePopupMessage(this@Inventaire, getString(R.string.msg_vent_prob))
                                }
                            }
                        } else if (prix.text.toString().toInt() <= 1) {
                            viewModel.makePopupMessage(this@Inventaire, "${getString(R.string.msg_prix_min)} : 1")
                        } else if (!viewModel.getPlayer().items.containsKey(itemid)) {
                            viewModel.makePopupMessage(this@Inventaire, "${getString(R.string.msg_stock_vide)} :)")
                        } else {
                            viewModel.makePopupMessage(this@Inventaire, "${getString(R.string.msg_champs_vide)} !)")
                        }
                    }
                    popupsell.show()
                    popup.dismiss()
                }
                popup.show()

            } catch (e: Exception) {
                e.printStackTrace()
            }

        }
    }
    @SuppressLint("SourceLockedOrientationActivity", "NotifyDataSetChanged")
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
        adapter.updateClick(true)

        var currentpick = viewModel.getPlayer().pick
        val newTextPick = "${getString(R.string.text_cur_pick)} : $currentpick"
        textpick.text = newTextPick
        buttonpick.setOnClickListener {
            if(currentpick < 5) {
                var items2 = HashMap<Int, Int>()
                val thread2 = Thread {
                    items2 = viewModel.getCraft(currentpick + 1)
                }
                thread2.start()
                thread2.join()

                val popupcraft = AlertDialog.Builder(this@Inventaire).create()
                val viewpopup = this.layoutInflater.inflate(R.layout.popup_craft, null)
                popupcraft.setView(viewpopup)
                adapterCraft = ItemInventoryRecycleViewAdapter(listenerInventory)
                val recycleCraft : RecyclerView = viewpopup.findViewById(R.id.craft_recycle_view)
                recycleCraft.adapter = adapterCraft
                adapterCraft.updateList(items2)
                val threadcraft = Thread {
                    try {
                        val status = viewModel.craftPickaxe(currentpick + 1)
                        Looper.prepare()
                        when (status) {
                            Status.OK.value -> {
                                viewModel.makePopupMessage(this, getString(R.string.text_pioche_amel))
                                viewModel.playerStatus()
                                adapter.updateList(viewModel.getPlayer().items)
                            }

                            Status.NOITEMS.value -> {
                                viewModel.makePopupMessage(this, getString(R.string.text_item_manquant))
                            }

                            else -> {
                                viewModel.makePopupMessage(this, getString(R.string.text_erreur))
                            }
                        }
                    }catch(e : Exception){
                        e.printStackTrace()
                    }
                }
                val btnCraft = viewpopup.findViewById<Button>(R.id.btn_craft)
                btnCraft.setOnClickListener{
                    threadcraft.start()
                    threadcraft.join()
                    adapter.notifyDataSetChanged()
                    currentpick = viewModel.getPlayer().pick
                    val newText = "${getString(R.string.text_cur_pick)} : $currentpick"
                    textpick.text = newText
                    popupcraft.dismiss()
                }
                popupcraft.show()
            }
            else{
                viewModel.makePopupMessage(this,getString(R.string.text_max_pick))
            }
        }
    }
}