package com.example.bikesharev4

class Ride(var what:String, var where:String, var end:String){

    // SETTERS
    fun setWhatBike(Wha: String) {what = Wha}
    fun setWhereFrom(Whe: String) {where = Whe}
    fun setend(ER: String){end = ER}

    // GETTERS
    fun getWhatBike():String {return what}
    fun getWhereFrom():String {return where}
    fun getend():String{return end}

    // TO STRING
    override fun toString():String {
        return "$what, from $where to $end"
    }
}