package com.example.bikesharevx


import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationServices
import io.realm.Realm
import java.io.IOException
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

class StartRideFragment : Fragment() {

    // GUI VARIABLES
    private lateinit var mBikeID:EditText
    private lateinit var mStartButton:Button
    private lateinit var mBackButton: Button
    private var funds: RealmFunds? = null

    private val mPermissions: ArrayList<String> = ArrayList()
    private lateinit var mFusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var mLocationCallback: LocationCallback

    companion object {
        lateinit var mRealm: Realm
        private const val ALL_PERMISSIONS_RESULT = 1011
    }

    interface Callbacks {
        fun goToBikeShare()
    }
    private var callbacks: Callbacks? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        callbacks = context as Callbacks?
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?) :View? {
        val view = inflater.inflate(R.layout.fragment_start_end_ride, container, false)

        // PERMISSIONS (location)
        mPermissions.add(Manifest.permission.ACCESS_FINE_LOCATION)
        mPermissions.add(Manifest.permission.ACCESS_COARSE_LOCATION)
        val permissionsToRequest = permissionsToRequest(mPermissions)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (permissionsToRequest.size > 0) {
                requestPermissions(permissionsToRequest.toTypedArray(),ALL_PERMISSIONS_RESULT)
            }
        }
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context!!)

        // REALM
        mRealm = Realm.getDefaultInstance()
        mBikeID = view.findViewById<EditText>(R.id.s_e_ID_text)
        mStartButton = view.findViewById<Button>(R.id.s_e_ride_button)

        mStartButton.apply { text = "Start Ride"; isEnabled = true }

        mStartButton.setOnClickListener{
            mRealm.executeTransactionAsync{ realm ->
                // TODO

                funds = realm.where(RealmFunds::class.java).findFirst()
                if (funds == null) funds = RealmFunds(0.0)
                if(mBikeID.text.contains(Regex("\\D$"))){
                    mBikeID.setText("No letters here!")
                } else if (funds!!.amount < 0) {
                    mBikeID.setText("Not enough funds for starting a ride!")
                } else {
                    val bikeID = mBikeID.text.toString().toInt()

                    var maxID = realm.where(RealmRide::class.java).max("id")
                    if (maxID == null) {maxID = 0}

                    var bike = realm.where(RealmBike::class.java).equalTo("id",bikeID).and().equalTo("available", true).findFirst()
                    if(bike != null){
                        var bikeType = bike.type
                        val bLon = bike.lon
                        val bLat = bike.lat
                        var bikeLoc: String = getAddress(bLon,bLat)
                        if (bikeLoc == "NONE") {bikeLoc = "($bLon $bLat)"}

                        bike.available = false

                        val startTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")).toString()
                        val newRide = RealmRide((maxID.toInt()+1), bikeID, bikeType, bikeLoc ,startTime, "", "" )

                        realm.copyToRealm(newRide)
                        realm.copyToRealm(bike)

                        bike = null
                        mBikeID.setText("")
                        callbacks?.goToBikeShare()
                    } else { mBikeID.setText("No available bike with that ID.")}
                }
            }
        }

        mBackButton = view.findViewById(R.id.s_e_back_button)
        mBackButton.setOnClickListener { callbacks?.goToBikeShare() }
        mBackButton.animate().x(0F).y(0F)

        return view
    }

    private fun permissionsToRequest(
        permissions: ArrayList<String>): ArrayList<String> {
        val result: ArrayList<String> = ArrayList()
        for (permission in permissions)
            if (!hasPermission(permission))
                result.add(permission)
        return result
    }

    private fun hasPermission(permission: String) =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            activity?.checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED
        } else {
            true
        }

    private fun getAddress(longitude: Double, latitude: Double): String {
        val geocoder = Geocoder(context, Locale.getDefault())
        val stringBuilder = StringBuilder()
        try {
            val addresses: List<Address> = geocoder.getFromLocation(latitude, longitude, 1)
            if (addresses.isNotEmpty()) {
                val address: Address = addresses[0]
                stringBuilder.apply{
                    append(address.getAddressLine(0)).append("\n")
                    append(address.locality).append("\n")
                    append(address.postalCode).append("\n")
                    append(address.countryName)
                }
            } else return "NONE"
        } catch (ex: IOException) { ex.printStackTrace() }
        return stringBuilder.toString()
    }

    override fun onDetach() {
        super.onDetach()
        callbacks = null
    }
}