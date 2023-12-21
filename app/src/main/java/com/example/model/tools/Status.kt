package com.example.model.tools

enum class Status(val value : String) {
    OK("OK"),
    TECHNICALERROR("KO - TECHNICAL ERROR"),
    SESSIONINVALID("KO - SESSION INVALID"),
    SESSIONEXPIRED("KO - SESSION EXPIRED"),
    WRONGCREDENTIALS("KO - WRONG CREDENTIALS"),
    // pas utiliser cas on convertit la localisation en float avant de faire le webservice
    //BADLOCATIONFORMAT("KO - BAD LOCATION FORMAT"),
    TOOFAST("KO  - TOO FAST"),
    BADPICKAXE("KO  - BAD PICKAXE"),
    OUTOFBOUNDS("KO - OUT OF BOUNDS"),
    //UNKNOWNID("KO - UNKNOWN ID"),
    NOITEMS("KO - NO ITEMS"),
    NOMONEY("KO - NO MONEY")
}