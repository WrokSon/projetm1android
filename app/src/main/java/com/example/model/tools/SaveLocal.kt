package com.example.model.tools

import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity

class SaveLocal(activity: AppCompatActivity) {

    companion object {
        const val LOGIN = "Username"
        const val PASSWD = "Password"
    }

    //recuperer les SharedPreferences en mode privé
    private var sharedPreference :  SharedPreferences = activity.getPreferences(Context.MODE_PRIVATE)

    fun saveUser(login:String, password:String){
        putString(LOGIN,login)
        putString(PASSWD,password)
    }

    fun getUsername() = getString(LOGIN)

    fun getPassword() = getString(PASSWD)

    fun putString(key: String, value: String?){
        sharedPreference.edit().putString(key,value).apply()
    }
    fun getString(key: String) = sharedPreference.getString(key,"")

}