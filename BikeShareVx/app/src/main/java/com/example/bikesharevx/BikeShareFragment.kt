package com.example.bikesharevx

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Layout
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationServices
import io.realm.OrderedRealmCollection
import io.realm.Realm
import io.realm.RealmRecyclerViewAdapter
import io.realm.Sort
import kotlinx.android.synthetic.main.fragment_bike_share.*
import kotlinx.android.synthetic.main.list_item_ride.*
import org.w3c.dom.Text
import java.util.*

class BikeShareFragment : Fragment() {

    //GUI STUFF
    private lateinit var rButton: Button
    private lateinit var sButton: Button
    private lateinit var eButton: Button
    private lateinit var fButton: Button
    private lateinit var lButton: Button
    private lateinit var levelAPI: TextView
    private lateinit var rView: RecyclerView

    private val mPermissions: ArrayList<String> = ArrayList()
    private lateinit var mFusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var mLocationCallback: LocationCallback

    companion object {
        lateinit var mRealm: Realm
        private const val ALL_PERMISSIONS_RESULT = 1011
    }

    interface Callbacks {
        fun goToStart()
        fun goToEnd()
        fun goToBike()
        fun goToFunds()
    }
    private var callbacks: Callbacks? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        callbacks = context as Callbacks?
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_bike_share, container, false)

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

        // REALM
        mRealm = Realm.getDefaultInstance()
        val res = mRealm.where(RealmRide::class.java).findAll()
        rView = view.findViewById(R.id.ride_recycle_view)
        rView.layoutManager = LinearLayoutManager(activity)
        rView.adapter = RealmRideAdapter(res!!)

        rButton = view.findViewById<Button>(R.id.register_bike_activity_button)
        rButton.setOnClickListener {
            callbacks?.goToBike()
        }

        sButton = view.findViewById<Button>(R.id.start_ride_activity_button)
        sButton.setOnClickListener {
            callbacks?.goToStart()
        }

        eButton = view.findViewById<Button>(R.id.end_ride_activity_button)
        eButton.setOnClickListener {
            callbacks?.goToEnd()
        }

        fButton = view.findViewById(R.id.funds_activity_button)
        fButton.setOnClickListener {
            callbacks?.goToFunds()
        }

        lButton = view.findViewById<Button>(R.id.list_rides_button)
        lButton.setOnClickListener {
            val results = mRealm.where(RealmRide::class.java).findAll()
            ride_recycle_view.layoutManager = LinearLayoutManager(activity)
            ride_recycle_view.adapter = RealmRideAdapter(results!!)
        }

        //API LEVEL
        levelAPI = view.findViewById(R.id.apilvl)
        levelAPI.text = ("API:" + Build.VERSION.SDK_INT)
        levelAPI.animate().x(0F).y(0F)

        return view
    }

    private inner class RealmRideAdapter
        (data: OrderedRealmCollection<RealmRide>) :
        RealmRecyclerViewAdapter<RealmRide, RealmRideHolder>(data, true) {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int)
                : RealmRideHolder {
            val view = LayoutInflater.from(context)
                .inflate(R.layout.list_item_ride, parent, false)
            return RealmRideHolder(view)
        }

        override fun onBindViewHolder(holder: RealmRideHolder, position: Int) {
            val rride = getItem(position)!!
            val bikeId = rride.bikeID
            val rideId = rride.id
            val bikeType = rride.bikeType
            val startLoc = rride.startLocation
            val startTime = rride.startTime

            var t: String = "$rideId with Bike $bikeId, $bikeType \n From: $startLoc at $startTime"

            val endLoc = rride.endLocation
            val endTime = rride.endTime

            if (endLoc != "") {
                t = t + "\n To: $endLoc at $endTime"
            }

            holder.rideItemText.setText(t)
/*
            holder.itemView.setOnClickListener {
                TODO( "implement popup and deletion of ride")

            }
 */
        }
    }

    private inner class RealmRideHolder(view: View): RecyclerView.ViewHolder(view){
        val rideItemText: TextView = view.findViewById(R.id.ride_item_text)
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

    override fun onDetach() {
        super.onDetach()
        callbacks = null
    }

}
