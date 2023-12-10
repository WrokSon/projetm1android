package com.example.design.views

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ImageButton
import android.widget.Toast
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
        fun getOffreInteraction(item: Offre?)
        fun getDetailItem(id : Int) : Item

        fun getBaseUrlImg() : String
    }
    private val listenerOffre : OnOffreInteractionListener = object : OnOffreInteractionListener {
        override fun getOffreInteraction(item: Offre?) {
            if (item != null) {
                viewModel.selectOffre(item)
                val lOffreLen = viewModel.getListe().size
                //Toast.makeText(applicationContext,"offer id :"+viewModel.getOffreSelect()!!.Offer_ID.toString(),Toast.LENGTH_SHORT).show()
                val thread = Thread{ viewModel.acheter() }
                thread.start()
                thread.join()
                val threadListe = Thread{viewModel.getMarche()}
                threadListe.start()
                threadListe.join()
                Log.d("Hahahahahaha","La taille : "+viewModel.getListe()+ " " +lOffreLen.toString() +" " + viewModel.getListe().size.toString())
                //if (lOffreLen != viewModel.getListe().size) adapter.updateList(viewModel.getListe())
            }

        }

        override fun getDetailItem(id: Int): Item {
            return viewModel.getItemDetail(id)
        }

        override fun getBaseUrlImg(): String = viewModel.getBaseLoginImg()
    }
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_marche)
        viewModel = ViewModelProvider(this).get(MarcheViewModel::class.java)
        viewModel.initContext(this)

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
        adapter.updateList(viewModel.getListe())
    }
}