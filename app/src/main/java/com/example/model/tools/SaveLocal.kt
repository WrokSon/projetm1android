package com.example.model.tools

import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity

class SaveLocal(activity: AppCompatActivity) {

    companion object {
        // Constantes pour les clés SharedPreferences
        const val LOGIN = "Login"
        const val PASSWD = "Password"
        const val USERNAME = "Username"
    }

    // Récupérer les SharedPreferences en mode privé
    private var sharedPreference :  SharedPreferences = activity.getPreferences(Context.MODE_PRIVATE)

    // Méthode pour sauvegarder le login et mot de passe de l'utilisateur
    fun saveUser(login:String, password:String){
        putString(LOGIN,login)
        putString(PASSWD,password)
    }

    // Méthode pour récupérer le login d'utilisateur
    fun getLogin() = getString(LOGIN)

    // Méthode pour récupérer le mot de passe d'utilisateur
    fun getPassword() = getString(PASSWD)

    // Méthode pour récupérer le nom d'utilisateur
    fun getUsername() = getString(USERNAME)

    // Méthode pour sauvegarder le nom d'utilisateur
    fun saveUserName(name:String) = putString(USERNAME,name)

    // Méthode privée pour mettre une valeur String dans les SharedPreferences
    private fun putString(key: String, value: String?){ sharedPreference.edit().putString(key,value).apply()}

    // Méthode privée pour récupérer une valeur String des SharedPreferences
    private fun getString(key: String) = sharedPreference.getString(key,"")

}