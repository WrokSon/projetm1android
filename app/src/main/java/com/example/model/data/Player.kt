package com.example.model.data

data class Player(var username : String, var lat : Float, var long : Float, var money : Int, var pick : Int, var items : HashMap<Int,Int>)
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
