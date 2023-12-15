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
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.design.R
import com.example.model.data.Offre

class ItemInventoryRecycleViewAdapter(private val listenerInventory: Inventaire.OnInventoryInteractionListener): RecyclerView.Adapter<ItemInventoryRecycleViewAdapter.ViewHolder>() {
    private var items = mutableListOf<Int>()
    private var qts = mutableListOf<Int>()
    class ViewHolder(mView: View) : RecyclerView.ViewHolder(mView) {
        private val text = mView.findViewById<TextView>(R.id.text_item)
        private val image = mView.findViewById<ImageView>(R.id.img_item)
        fun bind(name: String, img : Bitmap, type : Char, qt : Int, rarity : Int) : String{

            image.setImageBitmap(img)
            image.scaleType = ImageView.ScaleType.FIT_CENTER
            image.adjustViewBounds = true
            image.maxWidth = 80
            image.maxHeight = 80
            var newText = name
            when (rarity) {
                1 -> text.setTextColor(Color.WHITE)
                2 -> text.setTextColor(Color.GREEN)
                3 -> text.setTextColor(Color.BLUE)
                4 -> text.setTextColor(Color.RED)
                5 -> text.setTextColor(Color.YELLOW)
            }
            when (type) {
                'A' -> {
                    newText = "$newText (Artéfact)"
                }
                'M' -> {
                    newText = "$newText (Minerai)"
                }
            }
            newText = "$newText\nQuantité : $qt"
            text.text = newText
            return newText
        }


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_inventory, parent, false)
        return ViewHolder(view)
    }
    @SuppressLint("NotifyDataSetChanged")
    fun updateList(liste : HashMap<Int,Int>){
        items.clear()
        Log.d("ITEMSADAPTER",liste.toString())
        for(item in liste){
            items.add(item.key)
            qts.add(item.value)
        }
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        Log.d("TAILLE",items.size.toString())
        return items.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        Log.d("POSITION",position.toString())
        val detail = listenerInventory.getDetailItem(items[position]-1)
        val img = listenerInventory.getImage(items[position]-1)
        Log.d("TEXTEEEEEE",detail.nom)
        val desc = holder.bind(detail.nom,img,detail.type,qts[position],detail.rarity)
        holder.itemView.setOnClickListener{
            listenerInventory.clickItem(items[position],desc)
            updateList(listenerInventory.getPlayer().items)
        }
    }
}