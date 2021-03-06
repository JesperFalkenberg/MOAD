package com.example.bikesharev4

import android . content . Context
import android.os.Build
import androidx.annotation.RequiresApi
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

import kotlin.collections.ArrayList

class RidesDB private constructor ( context : Context ) {
    private val mAllRides = ArrayList < Ride >()
    private val mLastRide = Ride ("", "", "","","")
    init {
        mAllRides . add ( Ride (" Gazelle ", "ITU", " Fields ","","") )
        mAllRides . add ( Ride (" Gazelle ", " Fields ", " Nyhavn ","","") )
        mAllRides . add ( Ride (" Mustang ", " Kobenhavns Lufthavn ",
            " Kobenhavns Hovedbanegard ","","") )
    }
    companion object : RidesDBHolder < RidesDB , Context >(:: RidesDB )
    fun getRidesDB () : List < Ride > {
        return mAllRides
    }
    @RequiresApi(Build.VERSION_CODES.O)
    fun startRide (what : String, where : String ) {
        // Implement your code here
        mAllRides.add(Ride(what, where,"",
            LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")).toString(), ""))
    }
    @RequiresApi(Build.VERSION_CODES.O)
    fun endRide (what : String, where : String ) {
        // Implement your code here
        val ride = mAllRides.find { r -> (r.what == what && r.end == "") }
        mAllRides.remove(ride)
        mAllRides.add(Ride(ride!!.what, ride.where, where, ride.timeStart,
            LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")).toString()))
    }
    fun getLastRideInfo () : String {
        return mAllRides.last().toString()
    }
}
open class RidesDBHolder < out T : Any , in A >( creator : ( A ) -> T ) {
    private var creator : (( A ) -> T ) ? = creator
    @Volatile private var instance : T ? = null
    fun get ( arg : A ) : T {
        val checkInstance = instance
        if ( checkInstance != null )
            return checkInstance
        return synchronized ( this ) {
            val checkInstanceAgain = instance
            if ( checkInstanceAgain != null )
                checkInstanceAgain
            else {
                val created = creator !!( arg )
                instance = created
                creator = null
                created
            }
        }
    }
}
