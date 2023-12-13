package com.example.design.views

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.graphics.drawable.Drawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.core.graphics.scale
import androidx.lifecycle.ViewModelProvider
import androidx.preference.PreferenceManager
import com.example.design.R
import com.example.model.data.Item
import com.example.model.tools.SaveLocal
import com.example.model.tools.Status
import com.example.viewmodel.MainViewModel
import org.osmdroid.api.IMapController
import org.osmdroid.config.Configuration
import org.osmdroid.views.overlay.Marker
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.ScaleBarOverlay
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay
import org.w3c.dom.Document
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {
    private lateinit var viewModel: MainViewModel
    private lateinit var map : MapView
    private lateinit var mapController : IMapController
    private lateinit var myLoc: MyLocationNewOverlay
    private lateinit var doccreuse : Document
    private lateinit var saveData: SaveLocal
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Configuration.getInstance().load(applicationContext,
            PreferenceManager.getDefaultSharedPreferences(applicationContext))
        setContentView(R.layout.activity_main)
        viewModel = ViewModelProvider(this).get(MainViewModel::class.java)
        viewModel.initContext(this)
        saveData = SaveLocal(this)
        // Bloquer l'orientation en mode portrait
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        initMap()
        gestionBtns()
        deplacement()
        loadUsername()
        val thread = Thread(){
            viewModel.playerStatus()
            viewModel.getItemsDetails()
            viewModel.getImages()
        }
        thread.start()
        thread.join()
    }

    private fun gestionBtns() {
        var goInv: ImageButton = findViewById(R.id.inv)
        goInv.setOnClickListener {
            val intent: Intent = Intent(this, Inventaire::class.java)
            startActivity(intent)
        }

        var goMar: ImageButton = findViewById(R.id.mar)
        goMar.setOnClickListener {
            val intent: Intent = Intent(this, Marche::class.java)
            startActivity(intent)
        }

        var goPro: ImageButton = findViewById(R.id.pro)
        goPro.setOnClickListener {
            val intent: Intent = Intent(this, Profil::class.java)
            startActivity(intent)
        }

        var goPar: ImageButton = findViewById(R.id.par)
        goPar.setOnClickListener {
            val intent: Intent = Intent(this, Parametre::class.java)
            startActivity(intent)
        }

        val btnCreuser: Button = findViewById(R.id.pick)
        val prof = findViewById<TextView>(R.id.depth_zone)
        val item = findViewById<TextView>(R.id.item_zone)
        btnCreuser.setOnClickListener {
            mapController.setZoom(20.0)
            mapController.setCenter(myLoc.myLocation)
            myLoc.enableFollowLocation()
            val thread = Thread{
                doccreuse = viewModel.creuser()
            }
            thread.start()
            thread.join()
            val status = doccreuse.getElementsByTagName("STATUS").item(0).textContent
            Log.d("STATUSPICK",status)
            if(status == Status.OUTOFBOUNDS.value){
                Toast.makeText(applicationContext,"t'es pas à l'université",Toast.LENGTH_SHORT).show()
            }
            if(status == Status.BADPICKAXE.value){
                Toast.makeText(applicationContext,"meilleur pioche requise",Toast.LENGTH_SHORT).show()
            }
            if(status.startsWith(Status.TOOFAST.value)){
                val time = 5 - status.takeLast(1).toInt()
                Toast.makeText(applicationContext,"ralenti mon gars, " + time + " secondes",Toast.LENGTH_SHORT).show()
            }
            if(status == Status.OK.value){
                val depth = doccreuse.getElementsByTagName("DEPTH").item(0).textContent + "m"
                prof.text = "Profondeur : " + depth
                if(doccreuse.getElementsByTagName("ITEM_ID").length != 0){
                    val itemid = doccreuse.getElementsByTagName("ITEM_ID").item(0).textContent.toInt()
                    item.text = "Item : " + itemid
                    var itemm : Item? = null
                    val thread = Thread{
                       itemm = viewModel.getItemDetail(itemid - 1)
                    }
                    thread.start()
                    thread.join()
                    val popup = AlertDialog.Builder(this@MainActivity)
                    popup.setIcon(Drawable.createFromPath("https://test.vautard.fr/creuse_imgs/"+itemm?.image))
                    popup.setTitle("Item trouvé : "+itemm?.nom + " ( Profondeur : " + depth + ")")
                    popup.setMessage(itemm?.desc_fr)
                    popup.show()
                }
                else{
                    item.text = "Item : non"
                }

                Toast.makeText(applicationContext,"tout va bien mec",Toast.LENGTH_SHORT).show()

            }
        }

        var btnMe: ImageButton = findViewById(R.id.btn_me)
        btnMe.setOnClickListener {
            Toast.makeText(this, "c'est fait, mec", Toast.LENGTH_SHORT).show()
            mapController.setZoom(20.0)
            myLoc.enableFollowLocation()
        }
    }

    private fun initMap() {
        map = findViewById(R.id.map)
        map.setTileSource(TileSourceFactory.MAPNIK) //type de la map
        map.setMultiTouchControls(true)
        var startPoint: GeoPoint = GeoPoint(47.8467816, 1.9265369)
        mapController = map.controller
        mapController.setZoom(18.0)
        mapController.setCenter(startPoint)

        // Ajout de la bar de taille
        val scaleBarOverlay = ScaleBarOverlay(map)
        map.overlays.add(scaleBarOverlay)

        // Configure and add a MyLocation overlay
        myLoc = MyLocationNewOverlay(GpsMyLocationProvider(this),map)
        myLoc.enableMyLocation() //activation de la localisation
        myLoc.disableFollowLocation() //ne passe centrer sur la carte
        //changer le marqueur (personnage)
        myLoc.setPersonIcon(
            BitmapFactory.decodeResource(resources, R.drawable.marker_profil).scale(100, 100)
        )
        map.overlays.add(myLoc)

        // verifi si on a permisson de la location
        if (checkLocationPermission()) {
            map.post {
                mapController.setZoom(16.0)
                // on centre par sa position
                myLoc.enableFollowLocation()
                mapController.setCenter(myLoc.myLocation)
            }
        } else {
            // demander la permission de la location
            requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 1)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1 && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            map.post {
                mapController.setZoom(16.0)
                myLoc.enableFollowLocation()
                mapController.setCenter(myLoc.myLocation)
            }
        } else {
            Toast.makeText(this, "Location permission is required to show your location.", Toast.LENGTH_SHORT).show()
        }
    }


    override fun onPause() {
        super.onPause()
        map.onPause()
    }

    override fun onResume() {
        super.onResume()
        map.onResume()
        loadUsername()
    }
    @SuppressLint("MissingSuperCall")
    override fun onBackPressed() {}

    private fun checkLocationPermission(): Boolean {
        val permission = Manifest.permission.ACCESS_FINE_LOCATION
        val granted = PackageManager.PERMISSION_GRANTED

        return ContextCompat.checkSelfPermission(this, permission) == granted
    }

    private fun loadUsername(){
        // load le username
        val userNameSave = saveData.getUsername()
        val userNLocal = viewModel.getPlayer().username
        //il y a eu reset
        if(userNLocal == viewModel.getBaseLogin() && viewModel.getResetValue()){
            saveData.saveUserName(userNLocal)
            viewModel.setResetValue(false)
        }
        // il n'y a pas eu d'enregistrement avant
        else if( userNameSave == ""  || userNameSave == null){
            if(userNLocal != viewModel.getBaseLogin()) {
                saveData.saveUserName(userNLocal)
            }
        } else{ // il y a eu enregistrement et pas le meme userName
            // chagement avant
            if(userNLocal != viewModel.getBaseLogin() && userNLocal != userNameSave) {
                saveData.saveUserName(userNLocal)
            }else{ // pas chagement avant
                viewModel.getPlayer().username = userNameSave
            }
        }
    }

    private fun deplacement(){
        val textZone : TextView = findViewById(R.id.texte_zone)
        val depthZone : TextView = findViewById(R.id.depth_zone)
        textZone.setText("lon: ${viewModel.getLongitude()} / lat: ${viewModel.getLatitude()}")
        //met a jour la postion tous les 5 secondes
        var thread = Thread {
            while (true){
                var dernierLoc = myLoc.myLocation
                if(dernierLoc != null) {
                    Log.d("VOILA","lon : " + dernierLoc.longitude + " lat: "+dernierLoc.latitude)
                    val distance = viewModel.distanceEntrePoints(viewModel.getLatitude().toDouble(),viewModel.getLongitude().toDouble(),dernierLoc.latitude,dernierLoc.longitude)
                    if (distance >= 10.0f) {
                        viewModel.updatePos(dernierLoc.longitude.toFloat(), dernierLoc.latitude.toFloat())
                        textZone.setText("lon: ${viewModel.getLongitude()} / lat: ${viewModel.getLatitude()}")
                        depthZone.setText("Profondeur : 0m")
                    }
                }else{
                    Log.d("VOILA","Loc perdu")
                }
                updateVoisinOnMap()
                TimeUnit.SECONDS.sleep(5)
            }
        }
        thread.start()
    }


    fun updateVoisinOnMap(){
        val voisins = viewModel.getListeVoisins()
        Log.d("VOISINHGDBJ","${map.overlays.size} ${voisins}")
        // suprimez pour MAJ les items qui doivent etre dans la map
        // on ne doit par retirer les 2 premiers (bar/echelle + player)
        for (i in map.overlays.size-1 downTo 2){
            map.overlays.removeAt(i)
        }
        //map.overlays.clear()
        // ajoute le voisins
        for (i in 0..voisins.size-1){
            val voisin = voisins[i]
            if (voisin.name == viewModel.getPlayer().username) continue
            val itemVoisin = Marker(map)
            itemVoisin.position = GeoPoint(voisin.lat.toDouble(),voisin.lon.toDouble())
            itemVoisin.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
            itemVoisin.setTextIcon(voisin.name)
            val distance = viewModel.distanceEntrePoints(viewModel.getLatitude().toDouble(),viewModel.getLongitude().toDouble(),voisin.lat.toDouble(),voisin.lon.toDouble())
            itemVoisin.subDescription = "salut, je suis à $distance metres de toi"
            map.overlays.add(itemVoisin)
            //MAJ de la map
            map.invalidate()
        }

    }


}
