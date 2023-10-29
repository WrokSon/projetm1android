package com.example.model

class RepoConnexion private constructor() {

    private var autoConnect = true

    companion object {
        @Volatile
        private var INSTANCE: RepoConnexion? = null

        fun getInstance(): RepoConnexion {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: RepoConnexion().also { INSTANCE = it }
            }
        }
    }

    fun getValueAutoConnect() = autoConnect

    fun setValueAutoConnect(newValue : Boolean){
        autoConnect = newValue
    }

    private var session = 0
    private var signature : Long = 0

    fun collectCon(sess : Int, sign : Long){
        session = sess
        signature = sign
    }

    fun getSession(): Int{
        return session
    }

    fun getSignature(): Long{
        return signature
    }
}


