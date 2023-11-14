package com.example.viewmodel

import androidx.lifecycle.ViewModel
import com.example.model.Repository

class ParametreViewModel : ViewModel() {
    private val repository = Repository.getInstance()
    fun disconnect() = repository.setValueAutoConnect(false)

}