package com.example.bikesharev4

import android . content . Context
class RidesDB private constructor ( context : Context ) {
    private val mAllRides = ArrayList < Ride >()
    private val mLastRide = Ride ("", "", "")
    init {
        mAllRides . add ( Ride (" Chuck Norris Bike ", "ITU", " Fields ") )
        mAllRides . add ( Ride (" Chuck Norris Bike ", " Fields ", " Nyhavn ") )
        mAllRides . add ( Ride (" Bruce Lee Bike ", " Kobenhavns Lufthavn ",
            " Kobenhavns Hovedbanegard ") )
    }
    companion object : RidesDBHolder < RidesDB , Context >(:: RidesDB )
    fun getRidesDB () : List < Ride > {
        return mAllRides
    }
    fun startRide ( what : String , where : String ) {
        // Implement your code here
    }
    fun endRide ( what : String , where : String ) {
        // Implement your code here
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
