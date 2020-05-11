package com.example.bikesharevx


import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import com.google.android.gms.location.*
import io.realm.Realm
import java.io.IOException
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

class EndRideFragment : Fragment() {

    // GUI VARIABLES
    private lateinit var mBikeID: EditText
    private lateinit var mEndButton: Button
    private lateinit var mBackButton: Button
    private var mLon: Double = 0.0
    private var mLat: Double = 0.0

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
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstance: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_start_end_ride, container, false)

        // PERMISSIONS (location)
        mPermissions.add(Manifest.permission.ACCESS_FINE_LOCATION)
        mPermissions.add(Manifest.permission.ACCESS_COARSE_LOCATION)
        val permissionsToRequest = permissionsToRequest(mPermissions)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (permissionsToRequest.size > 0) {
                requestPermissions(permissionsToRequest.toTypedArray(), ALL_PERMISSIONS_RESULT)
            }
        }
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context!!)
        mLocationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                for (location in locationResult.locations) {
                    mLon = location.longitude
                    mLat = location.latitude
                }
            }
        }
        startLocationUpdates()

        // REALM
        mRealm = Realm.getDefaultInstance()
        mBikeID = view.findViewById<EditText>(R.id.s_e_ID_text)
        mEndButton = view.findViewById<Button>(R.id.s_e_ride_button)

        mEndButton.apply { text = "End Ride"; isEnabled = true }


        mEndButton.setOnClickListener {
            mRealm.executeTransactionAsync { realm ->
                if(mBikeID.text.contains(Regex("\\D$"))){
                    mBikeID.setText("No letters here!")
                } else {
                    val bikeID: String = mBikeID.text.toString()
                    var bikeLoc: String = getAddress(mLon,mLat)
                    if (bikeLoc == "NONE") {bikeLoc = "($mLon $mLat)"}

                    if (bikeID.isNotEmpty() && bikeLoc.isNotEmpty()) {
                        var openRide =
                            realm.where(RealmRide::class.java).equalTo("bikeID", bikeID.toInt()).and()
                                .equalTo("endLocation", "").findFirst()

                        if (openRide != null) {
                            openRide.endLocation = bikeLoc
                            openRide.endTime = LocalDateTime.now()
                                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")).toString()
                            realm.copyToRealm(openRide!!)


                            var bike = realm.where(RealmBike::class.java).equalTo("id", bikeID.toInt()).findFirst()
                            bike?.available = true
                            realm.copyToRealm(bike!!)

                            val sT = openRide.startTime
                            val eT = openRide.endTime

                            var funds = realm.where(RealmFunds::class.java).findFirst()
                            if (funds == null) {funds = RealmFunds(0.0)}
                            funds.amount -= (hourDiff(sT,eT) * bike.price)
                            realm.copyToRealm(funds)

                            openRide = null
                            bike = null
                            funds = null

                            mBikeID.setText("")
                            callbacks?.goToBikeShare()
                        } else {
                            mBikeID.setText("No open ride with that bikeID")
                        }
                    }
                }
            }
        }

        mBackButton = view.findViewById(R.id.s_e_back_button)
        mBackButton.setOnClickListener { callbacks?.goToBikeShare() }
        mBackButton.animate().x(0F).y(0F)

        return view
    }

    private fun hourDiff(s: String, e: String): Int {
        var diff = 1

        val y1 = s.substring(0,4).toInt()
        val y2 = e.substring(0,4).toInt()

        diff += ((y2-y1) * 365 * 24)

        val m1 = s.substring(5,7).toInt()
        val m2 = e.substring(5,7).toInt()

        diff += ((m2-m1) * 24 * 30)

        val d1 = s.substring(8,10).toInt()
        val d2 = e.substring(8,10).toInt()

        diff += ((d2-d1) * 24)

        val h1 = s.substring(11,13).toInt()
        val h2 = e.substring(11,13).toInt()

        diff += (h2-h1)

        return diff
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

    private fun checkPermission() =
        ActivityCompat.checkSelfPermission(context!!,
            Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(context!!,
                    Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED

    @SuppressLint("MissingPermission")
    private fun startLocationUpdates() {
        if (checkPermission()) {
            val locationRequest = LocationRequest().apply {
                priority = LocationRequest.PRIORITY_HIGH_ACCURACY
                interval = 20000
                fastestInterval = 10000
            }
            mFusedLocationProviderClient.requestLocationUpdates(locationRequest, mLocationCallback, null)
        }
    }
    private fun stopLocationUpdates() {
        mFusedLocationProviderClient
            .removeLocationUpdates(mLocationCallback)
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
        stopLocationUpdates()
        callbacks = null
    }
}