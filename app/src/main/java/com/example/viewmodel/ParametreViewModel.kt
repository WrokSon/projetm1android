package com.example.viewmodel

import androidx.lifecycle.ViewModel
import com.example.model.RepoConnexion

class ParametreViewModel : ViewModel() {
    private val repoConnect = RepoConnexion.getInstance()

    fun disconnect() = repoConnect.setValueAutoConnect(false)

}