package com.example.model.data

import java.util.ArrayList

data class Player(val login : String, var lat : Float, var long : Float, var money : Int, var pick : Int, var items : ArrayList<Item>)
    //TODO player object
    /** ressemble à ça
     * <PARAMS>
     * <POSITION>
     * <LATITUDE/>
     * <LONGITUDE/>
     * </POSITION>
     * <MONEY>0</MONEY>
     * <PICKAXE>1</PICKAXE>
     * <ITEMS/>
     * </PARAMS>
     */
