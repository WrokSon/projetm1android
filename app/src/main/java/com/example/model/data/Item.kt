package com.example.model.data

data class Item(
    var id : Int, var nom : String, var type : Char, var rarity : Int, var image : String,
    var descEn : String, var descFr : String)
    /**
     * ressemble a ça
     * <PARAMS>
     * <NOM>Calcairium</NOM>
     * <TYPE>M</TYPE>
     * <RARETE>1</RARETE>
     * <IMAGE>calcairium.png</IMAGE>
     * <DESC_EN>A crumbly rock with no other use than to write things with</DESC_EN>
     * <DESC_FR>Une roche friable, sans autre intérêt que de pouvoir écrire des trucs avec</DESC_FR>
     * </PARAMS>
     */
