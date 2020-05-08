package com.example.bikesharevx


class Ride(var what:String, var where:String, var end:String, var timeStart:String, var timeEnd:String){

    // SETTERS
    fun setWhatBike(Wha: String) {what = Wha}
    fun setWhereFrom(Whe: String) {where = Whe}
    fun setend(ER: String){end = ER}
    fun setTStart(ts: String){timeStart = ts}
    fun setTEnd(te: String){timeEnd = te}

    // GETTERS
    fun getWhatBike():String {return what}
    fun getWhereFrom():String {return where}
    fun getend():String{return end}
    fun getTStart():String{return timeStart}
    fun getTEnd():String{return timeEnd}

    // TO STRING
    override fun toString():String {
        return "$what, from $where at $timeStart to $end at $timeEnd"
    }
}