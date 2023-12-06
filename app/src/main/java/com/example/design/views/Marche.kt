package com.example.design.views

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageButton
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.example.design.R
import com.example.model.data.Offre
import com.example.viewmodel.MarcheViewModel

class Marche : AppCompatActivity() {

    private lateinit var viewModel : MarcheViewModel
    private lateinit var adapter: OfferRecycleViewAdapter
    private val myContextApp = this

    interface OnOffreInteractionListener {
        fun getOffreInteraction(item: Offre?)
    }
    private val listenerOffre : OnOffreInteractionListener = object : OnOffreInteractionListener {
        override fun getOffreInteraction(item: Offre?) {
            if (item != null) {
                viewModel.selectOffre(item)
                //Toast.makeText(applicationContext,"offer id :"+viewModel.getOffreSelect()!!.Offer_ID.toString(),Toast.LENGTH_SHORT).show()
                val thread = Thread{ viewModel.acheter(myContextApp) }
                thread.start()
                thread.join()
            }

        }
    }
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_marche)
        viewModel = ViewModelProvider(this).get(MarcheViewModel::class.java)

        var goGame : ImageButton = findViewById(R.id.marche_retour)
        goGame.setOnClickListener{
            onBackPressedDispatcher.onBackPressed()
        }

        adapter = OfferRecycleViewAdapter(listenerOffre)
        var recycle : RecyclerView = findViewById(R.id.march_recycle_view)
        recycle.adapter = adapter

        val thread = Thread{viewModel.getMarche(this)}
        thread.start()
        thread.join()
        adapter.updateList(viewModel.getListe())
    }
}

/*
<root>
    <STATUS>OK</STATUS>
    <PARAMS>
        <OFFERS>
            <item0>
                <OFFER_ID>41</OFFER_ID>
                <ITEM_ID>10</ITEM_ID>
                <QUANTITE>1</QUANTITE>
                <PRIX>12000</PRIX>
            </item0>
            <item1>
                <OFFER_ID>41</OFFER_ID>
                <ITEM_ID>10</ITEM_ID>
                <QUANTITE>1</QUANTITE>
                <PRIX>12000</PRIX>
            </item0>
        </OFFERS>
    </PARAMS>
</root>
 */