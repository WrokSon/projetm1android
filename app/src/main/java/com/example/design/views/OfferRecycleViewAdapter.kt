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
    // Liste des offres
    private var listOffres = mutableListOf<Offre>()

    // Création de la vue (élément graphique)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_offer, parent, false)
        return ViewHolder(view)
    }

    // Nombre d'éléments à afficher
    override fun getItemCount(): Int = listOffres.size

    // Action à effectuer lors d'un clic
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val offer = listOffres[position]
        val detail = listenerOffre.getDetailItem(offer.itemID-1)
        val imgOffer = listenerOffre.getImage(detail.id - 1)
        // Appeler la méthode bind du ViewHolder pour mettre à jour l'élément graphique
        holder.bind(imgOffer,detail.nom,detail.type,offer.prix.toString(),offer.quantite.toString())
        // Gestion du clic sur le bouton d'achat
        holder.getButton().setOnClickListener{
            listenerOffre.offreInteraction(listOffres[position])
        }
    }

    // Changement la liste et notifier le changement
    @SuppressLint("NotifyDataSetChanged")
    fun updateList(liste : List<Offre>){
        listOffres.clear()
        listOffres.addAll(liste)
        notifyDataSetChanged()
    }

    // Classe ViewHolder pour représenter la vue d'un élément
    class ViewHolder(mView: View) : RecyclerView.ViewHolder(mView) {
        private val btn: Button = mView.findViewById(R.id.btn_acheter)
        private val img: ImageView = mView.findViewById(R.id.img_offer)
        private val tVName : TextView = mView.findViewById(R.id.name_offer)
        private val tVTyp : TextView = mView.findViewById(R.id.typ_offer)
        private val tVPrice : TextView = mView.findViewById(R.id.price_offer)
        private val tVQte : TextView = mView.findViewById(R.id.qte_offer)

        // Textes d'origine pour chaque TextView avec la traduction
        private val textNom = tVName.text
        private val textType = tVTyp.text
        private val textPrix = tVPrice.text
        private val textQte = tVQte.text

        // Méthode pour lier les données à la vue
        fun bind(image : Bitmap, name : String, type : String, prix : String, quantite : String){
            img.setImageBitmap(image)
            val newTextName = "$textNom : $name"
            val newTextType = "$textType : $type"
            val newTextPrix = "$textPrix : $prix"
            val newTextQte = "$textQte : $quantite"
            tVName.text = newTextName
            tVTyp.text = newTextType
            tVPrice.text = newTextPrix
            tVQte.text = newTextQte
        }

        // Méthode pour récupérer le bouton d'achat
        fun getButton() : Button = btn
    }
}