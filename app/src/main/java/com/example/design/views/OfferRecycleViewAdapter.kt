package com.example.design.views


import androidx.recyclerview.widget.RecyclerView
import android.view.ViewGroup
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.example.design.R
import com.example.model.data.Offre
import com.squareup.picasso.Picasso

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
        val offer = listOffres[position]
        val detail = listenerOffre.getDetailItem(offer.Item_ID-1)
        val imgOffer = listenerOffre.getBaseUrlImg()+detail.image
        holder.bind(imgOffer,detail.nom,detail.type.toString(),offer.prix.toString(),offer.Quantite.toString())
        holder.getButton().setOnClickListener{
            val response = listenerOffre.offreInteraction(listOffres[position])
            if (response){
                listOffres.removeAt(position)
                notifyItemRemoved(position)
                notifyItemRangeChanged(position,listOffres.size)
            }
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
        val btn: Button = mView.findViewById(R.id.btn_acheter)
        val img: ImageView = mView.findViewById(R.id.img_offer)
        val tVName : TextView = mView.findViewById(R.id.name_offer)
        val tVTyp : TextView = mView.findViewById(R.id.typ_offer)
        val tVPrice : TextView = mView.findViewById(R.id.price_offer)
        val tVQte : TextView = mView.findViewById(R.id.qte_offer)

        fun bind(image : String, name : String, type : String, prix : String, quantite : String){
            Picasso.with(mView.context).load(image).into(img)
            tVName.setText("Nom : ${name}")
            tVTyp.setText("Type : ${type}")
            tVPrice.setText("Prix : ${prix}")
            tVQte.setText("Quantité : ${quantite}")
        }

        fun getButton() : Button = btn
    }
}