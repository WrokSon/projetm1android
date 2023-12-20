package com.example.design.views

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.content.pm.ActivityInfo
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Button
import android.widget.ImageButton
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.example.design.R
import com.example.model.data.Item
import com.example.model.data.Offre
import com.example.viewmodel.MarcheViewModel

class Marche : AppCompatActivity() {

    private lateinit var viewModel : MarcheViewModel
    private lateinit var adapter: OfferRecycleViewAdapter

    // Interface pour les interactions avec les offres
    interface OnOffreInteractionListener {
        fun offreInteraction(item: Offre?)
        fun getDetailItem(id : Int) : Item
        fun getImage(id: Int): Bitmap
        fun getBaseUrlImg() : String
    }

    // Implémentation de l'interface pour gérer les interactions avec les offres
    private val listenerOffre : OnOffreInteractionListener = object : OnOffreInteractionListener {
        override fun offreInteraction(item: Offre?){
            if (item != null) {
                // Création d'un popup
                val popup = AlertDialog.Builder(this@Marche).create()
                val popupView = LayoutInflater.from(this@Marche).inflate(R.layout.popup_confirm, null)
                popup.setView(popupView)

                // recuper les elements graphiques (button)
                val yes : Button = popupView.findViewById(R.id.btn_confirm_yes)
                val no : Button = popupView.findViewById(R.id.btn_confirm_no)

                // Gestion du clic sur le bouton "Yes"
                yes.setOnClickListener{
                    viewModel.selectOffre(item)
                    val lOffreLen = viewModel.getListe().size
                    val thread = Thread{ viewModel.acheter() }
                    thread.start()
                    thread.join()
                    val threadListe = Thread{viewModel.getMarche()}
                    threadListe.start()
                    threadListe.join()
                    // mettre a jour la vue seulement s'il y a eu changement
                    if (lOffreLen != viewModel.getListe().size) adapter.updateList(viewModel.getListe())
                    popup.dismiss()
                }
                // Gestion du clic sur le bouton "No"
                no.setOnClickListener{
                    // fermeture de la popup
                    popup.dismiss()
                }

                // Affichage de la popup
                popup.show()
            }

        }

        override fun getDetailItem(id: Int): Item {
            return viewModel.getItemDetail(id)
        }

        override fun getImage(id: Int): Bitmap {
            return viewModel.getImage(id)
        }

        override fun getBaseUrlImg(): String = viewModel.getBaseLoginImg()
    }
    @SuppressLint("MissingInflatedId", "SourceLockedOrientationActivity")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_marche)
        // Initialisation du ViewModel et de son context
        viewModel = ViewModelProvider(this)[MarcheViewModel::class.java]
        viewModel.initContext(this)
        // Bloquer l'orientation de la vue en mode portrait
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        // Récupération du bouton de retour
        val goGame : ImageButton = findViewById(R.id.marche_retour)
        goGame.setOnClickListener{
            // retour à l'écran précédent
            onBackPressedDispatcher.onBackPressed()
        }

        // Initialisation de l'adapteur pour la RecyclerView
        adapter = OfferRecycleViewAdapter(listenerOffre)
        // Récupération de la RecyclerView et assignation de l'adapteur
        val recycle : RecyclerView = findViewById(R.id.march_recycle_view)
        recycle.adapter = adapter

        // Récupération des offres en ligne
        val thread = Thread{viewModel.getMarche()}
        thread.start()
        thread.join()
        // Mise à jour de la liste des offres dans l'adapteur
        adapter.updateList(viewModel.getListe())
    }
}