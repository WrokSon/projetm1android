package com.example.design.views


import android.annotation.SuppressLint
import android.graphics.Bitmap
import androidx.recyclerview.widget.RecyclerView
import android.view.ViewGroup
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.example.design.R
import com.example.model.data.Offre

class OfferRecycleViewAdapter(private val listenerOffre: Marche.OnOffreInteractionListener) : RecyclerView.Adapter<OfferRecycleViewAdapter.ViewHolder>() {

    private var listOffres = mutableListOf<Offre>()

    //creation de la view
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_offer, parent, false)
        return ViewHolder(view)
    }

    //nombre d'element a afficher
    override fun getItemCount(): Int = listOffres.size

    // action a faire
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val offer = listOffres[position]
        val detail = listenerOffre.getDetailItem(offer.itemID-1)
        val imgOffer = listenerOffre.getImage(detail.id - 1)
        holder.bind(imgOffer,detail.nom,detail.type.toString(),offer.prix.toString(),offer.quantite.toString())
        holder.getButton().setOnClickListener{
            listenerOffre.offreInteraction(listOffres[position])
        }
    }

    // ajouter une offre dans la liste
    @SuppressLint("NotifyDataSetChanged")
    fun updateList(liste : List<Offre>){
        listOffres.clear()
        listOffres.addAll(liste)
        notifyDataSetChanged()
    }

    // la view (element graphqiue)
    class ViewHolder(mView: View) : RecyclerView.ViewHolder(mView) {
        private val btn: Button = mView.findViewById(R.id.btn_acheter)
        private val img: ImageView = mView.findViewById(R.id.img_offer)
        private val tVName : TextView = mView.findViewById(R.id.name_offer)
        private val tVTyp : TextView = mView.findViewById(R.id.typ_offer)
        private val tVPrice : TextView = mView.findViewById(R.id.price_offer)
        private val tVQte : TextView = mView.findViewById(R.id.qte_offer)

        fun bind(image : Bitmap, name : String, type : String, prix : String, quantite : String){
            img.setImageBitmap(image)
            val newTextName = "Nom : $name"
            val newTextType = "Type : $type"
            val newTextPrix = "Prix : $prix"
            val newTextQte = "Quantité : $quantite"
            tVName.text = newTextName
            tVTyp.text = newTextType
            tVPrice.text = newTextPrix
            tVQte.text = newTextQte
        }

        fun getButton() : Button = btn
    }
}