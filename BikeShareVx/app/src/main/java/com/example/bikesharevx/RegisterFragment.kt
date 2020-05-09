package com.example.bikesharevx


import android.Manifest
import android.annotation.SuppressLint
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
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.widget.ImageViewCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.location.*
import io.realm.OrderedRealmCollection
import io.realm.Realm
import io.realm.RealmRecyclerViewAdapter
import java.io.IOException
import java.util.*

class RegisterFragment : Fragment() {

    // GUI VARIABLES
    private lateinit var mBikeType: EditText
    private lateinit var mBikePrice: EditText
    private lateinit var mRegisterButton: Button
    private lateinit var mListButton: Button
    private lateinit var mBackButton: Button
    private lateinit var rView: RecyclerView
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
        val view = inflater.inflate(R.layout.fragment_register, container, false)

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
        val res = mRealm.where(RealmBike::class.java).findAll()
        rView = view.findViewById(R.id.r_bike_recycle_view)
        rView.layoutManager = LinearLayoutManager(activity)
        rView.adapter = RealmBikeAdapter(res!!)

        mBikeType = view.findViewById<EditText>(R.id.r_type)
        mBikePrice = view.findViewById<EditText>(R.id.r_price)

        mRegisterButton = view.findViewById(R.id.r_register_button)
        mRegisterButton.setOnClickListener {
            if (mBikePrice.text.contains(Regex("\\D$"))){
                mBikePrice.setText("No letters!")
            } else {
                mRealm.executeTransactionAsync { realm ->
                    var maxID = realm.where(RealmBike::class.java).max("id")
                    if (maxID == null) {maxID = 0}
                    val pic = null
                    val bike = RealmBike((maxID.toInt()+1),mBikeType.text.toString(), pic,mBikePrice.text.toString().toDouble(),mLon,mLat,true)
                    realm.copyToRealm(bike)
                }
            }
        }

        mListButton = view.findViewById<Button>(R.id.r_list_button)
        mListButton.setOnClickListener {
            val results = mRealm.where(RealmBike::class.java).findAll()
            rView = view.findViewById(R.id.r_bike_recycle_view)
            rView.layoutManager = LinearLayoutManager(activity)
            rView.adapter = RealmBikeAdapter(results!!)
        }

        mBackButton = view.findViewById(R.id.r_back_button)
        mBackButton.setOnClickListener { callbacks?.goToBikeShare() }
        mBackButton.animate().x(0F).y(0F)

        return view
    }

    private inner class RealmBikeAdapter
        (data: OrderedRealmCollection<RealmBike>) :
        RealmRecyclerViewAdapter<RealmBike, RealmBikeHolder>(data, true) {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int)
                : RealmBikeHolder {
            val view = LayoutInflater.from(context)
                .inflate(R.layout.list_item_bike, parent, false)
            return RealmBikeHolder(view)
        }

        override fun onBindViewHolder(holder: RealmBikeHolder, position: Int) {
            val rbike = getItem(position)!!
            val type = rbike.type
            val id = rbike.id
            val price = rbike.price
            val lon = rbike.lon
            val lat = rbike.lat
            var address = getAddress(lon,lat)
            var stat = ""
            if (rbike.available) {stat = "Available"} else { stat = "Unavailable"}


            if (address == "NONE") { address = "($lon $lat)"}
            holder.bikeText.text = "ID: $id $type, Price: $price/hour - $stat - Currently at $address"

            holder.itemView.setOnClickListener {
                //
            }
        }
    }

    private inner class RealmBikeHolder(view: View): RecyclerView.ViewHolder(view){
        val bikePic = view.findViewById<ImageView>(R.id.bike_item_pic)
        val bikeText = view.findViewById<TextView>(R.id.bike_item_text)
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
        callbacks = null
        stopLocationUpdates()
    }
}