package com.example.bikeshare

class Ride(var what:String, var where:String){

    // SETTERS
    fun setWhatBike(Wha: String) {what = Wha}
    fun setWhereFrom(Whe: String) {where = Whe}

    // GETTERS
    fun getWhatBike():String {return what}
    fun getWhereFrom():String {return where}

    // TO STRING
    override fun toString():String{return what + " started here: " + where}
}