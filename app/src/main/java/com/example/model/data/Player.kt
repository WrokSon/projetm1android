package com.example.model.data

data class Player(var username : String, var lat : Float, var long : Float, var money : Int, var pick : Int, var items : HashMap<Int,Int>)