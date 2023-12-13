package com.example.design.views

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.content.pm.ActivityInfo
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageButton
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.example.design.R
import com.example.model.data.Item
import com.example.model.data.Offre
import com.example.viewmodel.MarcheViewModel

class Marche : AppCompatActivity() {

    private lateinit var viewModel : MarcheViewModel
    private lateinit var adapter: OfferRecycleViewAdapter

    interface OnOffreInteractionListener {
        fun offreInteraction(item: Offre?)
        fun getDetailItem(id : Int) : Item
        fun getImage(id: Int): Bitmap
        fun getBaseUrlImg() : String
    }
    private val listenerOffre : OnOffreInteractionListener = object : OnOffreInteractionListener {
        override fun offreInteraction(item: Offre?){
            if (item != null) {
                viewModel.selectOffre(item)
                val lOffreLen = viewModel.getListe().size
                val thread = Thread{ viewModel.acheter() }
                thread.start()
                thread.join()
                val threadListe = Thread{viewModel.getMarche()}
                threadListe.start()
                threadListe.join()
                if (lOffreLen != viewModel.getListe().size) adapter.updateList(viewModel.getListe())
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
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_marche)
        viewModel = ViewModelProvider(this).get(MarcheViewModel::class.java)
        viewModel.initContext(this)
        // Bloquer l'orientation en mode portrait
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        var goGame : ImageButton = findViewById(R.id.marche_retour)
        goGame.setOnClickListener{
            onBackPressedDispatcher.onBackPressed()
        }

        adapter = OfferRecycleViewAdapter(listenerOffre)
        var recycle : RecyclerView = findViewById(R.id.march_recycle_view)
        recycle.adapter = adapter

        val thread = Thread{viewModel.getMarche()}
        thread.start()
        thread.join()
        //adapter.updateList(viewModel.tempo)
        adapter.updateList(viewModel.getListe())
    }
}