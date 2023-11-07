package com.example.model

data class Item(val nom : String, val type : Char, val rarity : Int, val image : String,
                val desc_en : String, val desc_fr : String)
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
