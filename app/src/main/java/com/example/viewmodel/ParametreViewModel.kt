package com.example.viewmodel

import androidx.lifecycle.ViewModel
import com.example.model.Repository

class ParametreViewModel : ViewModel() {
    private val repository = Repository.getInstance()

    fun getName() : String = repository.getPlayer().login
    fun getPick() : Int = repository.getPlayer().pick

    fun getMoney() : Int = repository.getPlayer().money
    fun disconnect() = repository.setValueAutoConnect(false)

}