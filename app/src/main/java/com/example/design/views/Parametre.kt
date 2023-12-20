package com.example.design.views

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.ActivityInfo
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.ViewModelProvider
import com.example.design.R
import com.example.viewmodel.ParametreViewModel
import com.google.android.material.switchmaterial.SwitchMaterial
import java.util.Locale

@Suppress("DEPRECATION")
class Parametre : AppCompatActivity() {

    @SuppressLint("MissingInflatedId", "SourceLockedOrientationActivity")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_parametre)
        // Initialisation du ViewModel et de son context
        val viewModel = ViewModelProvider(this)[ParametreViewModel::class.java]
        viewModel.initContext(this)
        // Bloquer l'orientation en mode portrait
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        // Gestion du bouton de retour
        val goGame : ImageButton = findViewById(R.id.parametre_retour)
        goGame.setOnClickListener{
            // retour à l'écran précédent
            onBackPressedDispatcher.onBackPressed()
        }

        // Gestion bouton de déconnexion
        val goConnection : Button = findViewById(R.id.btn_deconnexion)
        goConnection.setOnClickListener{
            val intent = Intent(this, Connexion::class.java)
            startActivity(intent)
            finish()
        }

        // Gestion du changement de nom
        // Champ pour changer le nom du joueur
        val changeNameET : EditText = findViewById(R.id.name_player)
        // Gestion bouton de déconnexion
        val changeNameBtn : Button = findViewById(R.id.btn_change_name)
        changeNameBtn.setOnClickListener{
            if (!changeNameET.text.isNullOrBlank()) {
                val name = changeNameET.text.toString()
                // changer le nom dans le serveur
                val thread = Thread {
                    viewModel.changeName(name)
                    changeNameET.text.clear()
                }
                thread.start()
                thread.join()
            }
        }

        // Gestion bouton pour réinitialiser son compte
        val resetBtn : Button = findViewById(R.id.reinisialiser)
        resetBtn.setOnClickListener{
            val popup = AlertDialog.Builder(this).create()
            val popupView = LayoutInflater.from(this).inflate(R.layout.popup_confirm, null)
            popup.setView(popupView)

            // Récupération des éléments graphiques (boutons)
            val yes : Button = popupView.findViewById(R.id.btn_confirm_yes)
            val no : Button = popupView.findViewById(R.id.btn_confirm_no)

            // Gestion du clic sur le bouton "Yes"
            yes.setOnClickListener{
                // réinitialiser son compte sur le serveur (websevice)
                Thread{viewModel.reset()}.start()
                viewModel.setResetValue(true)
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
        // Switch pour changer la langue
        val switchlanguage = findViewById<SwitchMaterial>(R.id.switch_language)
        val res = resources
        val dm = res.displayMetrics
        val conf = res.configuration
        // Vérifier la langue actuelle et activer le switch en conséquence
        if(conf.locales.toString().startsWith("[fr")) switchlanguage.isChecked = false
        if(conf.locales.toString().startsWith("[en") ) switchlanguage.isChecked = true
        // Switch
        switchlanguage.setOnClickListener {
            // Changer la langue en fonction de l'état du switch
            if(switchlanguage.isChecked){
                conf.setLocale(Locale("en"))
            }
            else{
                conf.setLocale(Locale("fr"))
            }
            // Mettre à jour la configuration et redémarrer l'activité
            res.updateConfiguration(conf,dm)
            val refresh = Intent(this,MainActivity::class.java)
            startActivity(refresh)
            finish()
        }

    }

}