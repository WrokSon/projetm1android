package com.example.design.views


import androidx.recyclerview.widget.RecyclerView
import android.view.ViewGroup
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.example.design.R
import com.example.model.data.Offre

class OfferRecycleViewAdapter(private val listenerOffre: Marche.OnOffreInteractionListener) : RecyclerView.Adapter<OfferRecycleViewAdapter.ViewHolder>() {

    private var listOffres : ArrayList<Offre> =  ArrayList<Offre>()

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
        holder.bind(listOffres[position])
        holder.getButton().setOnClickListener{
            listenerOffre.getOffreInteraction(listOffres[position])
        }
    }

    // ajouter une offre dans la liste
    fun updateList(liste : ArrayList<Offre>){
        listOffres.clear()
        listOffres = liste
        notifyDataSetChanged()
    }

    // la view (element graphqiue)
    class ViewHolder(val mView: View) : RecyclerView.ViewHolder(mView) {
        val txt: TextView = mView.findViewById(R.id.txt)
        val btn: Button = mView.findViewById(R.id.button)

        fun bind(offer : Offre){
            val texte = offer.Offer_ID.toString() + " / " + offer.Item_ID.toString() + " / " + offer.Quantite.toString() + " / " + offer.prix.toString()
            txt.setText(texte)
        }

        fun getButton() : Button = btn
    }
}