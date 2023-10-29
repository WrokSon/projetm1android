package com.example.design.views

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.graphics.scale
import androidx.preference.PreferenceManager
import com.example.design.R
import org.osmdroid.api.IMapController
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.ScaleBarOverlay
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay

class MainActivity : AppCompatActivity() {
    private lateinit var map : MapView
    private lateinit var mapController : IMapController
    private lateinit var myLocation: MyLocationNewOverlay
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Configuration.getInstance().load(applicationContext,
            PreferenceManager.getDefaultSharedPreferences(applicationContext))
        setContentView(R.layout.activity_main)

        map = findViewById(R.id.map)
        map.setTileSource(TileSourceFactory.MAPNIK) //type de la map
        map.setMultiTouchControls(true)
        var startPoint : GeoPoint = GeoPoint(47.8467816,1.9265369)
        mapController = map.controller
        mapController.setZoom(18.0)
        mapController.setCenter(startPoint)

        // Ajout de la bar de taille
        val scaleBarOverlay = ScaleBarOverlay(map)
        map.overlays.add(scaleBarOverlay)

        // Configure and add a MyLocation overlay
        myLocation = MyLocationNewOverlay(map)
        myLocation.enableMyLocation() //activation de la localisation
        myLocation.disableFollowLocation() //ne passe centrer sur la carte
        //changer le marqueur (personnage)
        myLocation.setPersonIcon(BitmapFactory.decodeResource(resources,R.drawable.marker_profil).scale(100,100))
        map.overlays.add(myLocation)

        // verifi si on a permisson de la location
        if (checkLocationPermission()) {
            map.post {
                mapController.setZoom(16.0)
                // on centre par sa position
                myLocation.enableFollowLocation()
            }
        } else {
            // demander la permission de la location
            requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 1)
        }


        var goInv : ImageButton = findViewById(R.id.inv)
        goInv.setOnClickListener{
            val intent : Intent = Intent(this, Inventaire::class.java)
            startActivity(intent)
        }

        var goMar : ImageButton = findViewById(R.id.mar)
        goMar.setOnClickListener{
            val intent : Intent = Intent(this, Marche::class.java)
            startActivity(intent)
        }

        var goPro : ImageButton = findViewById(R.id.pro)
        goPro.setOnClickListener{
            val intent : Intent = Intent(this, Profil::class.java)
            startActivity(intent)
        }

        var goPar : ImageButton = findViewById(R.id.par)
        goPar.setOnClickListener{
            val intent : Intent = Intent(this, Parametre::class.java)
            startActivity(intent)
        }

        var btnCreuser : Button = findViewById(R.id.pick)
        btnCreuser.setOnClickListener{
            mapController.setZoom(20.0)
            myLocation.enableFollowLocation()
        }

        var btnMe : ImageButton = findViewById(R.id.btn_me)
        btnMe.setOnClickListener{
            Toast.makeText(this, "c'est fait, mec", Toast.LENGTH_SHORT).show()
            mapController.setZoom(20.0)
            myLocation.enableFollowLocation()
        }

    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1 && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            map.post {
                mapController.setZoom(16.0)
                myLocation.enableFollowLocation()
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
    }

    override fun onBackPressed() {}

    private fun checkLocationPermission(): Boolean {
        val permission = Manifest.permission.ACCESS_FINE_LOCATION
        val granted = PackageManager.PERMISSION_GRANTED

        return ContextCompat.checkSelfPermission(this, permission) == granted
    }
}
