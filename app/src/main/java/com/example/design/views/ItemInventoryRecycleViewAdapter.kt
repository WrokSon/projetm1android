package com.example.design.views

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.design.R

class ItemInventoryRecycleViewAdapter(private val listenerInventory: Inventaire.OnInventoryInteractionListener): RecyclerView.Adapter<ItemInventoryRecycleViewAdapter.ViewHolder>() {
    private var items = mutableListOf<Int>()
    private var qts = mutableListOf<Int>()
    private var click = false
    class ViewHolder(mView: View) : RecyclerView.ViewHolder(mView) {
        private val name = mView.findViewById<TextView>(R.id.name_item)
        private val type = mView.findViewById<TextView>(R.id.type_item)
        private val qte = mView.findViewById<TextView>(R.id.qte_item)
        private val image = mView.findViewById<ImageView>(R.id.img_item)
        private var color: Int = Color.WHITE

        private val textName = name.text
        private val textQte = qte.text
        private val textType = type.text
        fun bind(name: String, img : Bitmap, type : String, qt : Int, rarity : Int){
            image.setImageBitmap(img)
            image.scaleType = ImageView.ScaleType.FIT_CENTER
            image.adjustViewBounds = true
            image.maxWidth = 80
            image.maxHeight = 80
            var newText = name
            color = when (rarity) {
                1 -> Color.WHITE
                2 -> Color.GREEN
                3 -> Color.BLUE
                4 -> Color.RED
                5 -> Color.YELLOW
                else -> Color.WHITE
            }
            // changement de couleur
            this.name.setTextColor(color)
            this.type.setTextColor(color)
            this.qte.setTextColor(color)
            // text a afficher
            val newTextName = "$textName : $newText"
            val newTextQte = "$textQte : $qt"
            val newTextType = "$textType : $type"

            this.name.text = newTextName
            this.type.text = newTextType
            this.qte.text = newTextQte
        }


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_inventory, parent, false)
        return ViewHolder(view)
    }
    fun updateClick(value : Boolean){
        click = value
    }
    @SuppressLint("NotifyDataSetChanged")
    fun updateList(liste : HashMap<Int,Int>){
        items.clear()
        qts.clear()
        items.addAll(liste.keys)
        qts.addAll(liste.values)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val detail = listenerInventory.getDetailItem(items[position]-1)
        val img = listenerInventory.getImage(items[position]-1)
        holder.bind(detail.nom,img,detail.type,qts[position],detail.rarity)
        if(click){
            holder.itemView.setOnClickListener {
                listenerInventory.clickItem(items[position], detail.nom, position)
            }
        }


    }
}