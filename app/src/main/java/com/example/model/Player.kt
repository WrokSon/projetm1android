package com.example.model

import java.util.ArrayList

data class Player(val login : String, val lat : Double, val long : Double, val money : Int, val pick : Int, val items : ArrayList<Item>)
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
