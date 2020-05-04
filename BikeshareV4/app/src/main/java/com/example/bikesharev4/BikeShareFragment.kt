package com.example.bikesharev4

import android.content.Context
import android.content.Intent
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
import kotlinx.android.synthetic.main.activity_bike_share.*
import kotlinx.android.synthetic.main.fragment_bike_share.*
import kotlinx.android.synthetic.main.list_item_ride.*
import java.util.*

class BikeShareFragment : Fragment() {

    //GUI STUFF
    private lateinit var sButton: Button
    private lateinit var eButton: Button
    private lateinit var lButton: Button
    private lateinit var levelAPI: TextView

    companion object {
        lateinit var sRidesDB: RidesDB
    }

    private lateinit var mAdapter: RideAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_bike_share, container, false)

        // GET RIDES_DB
        sRidesDB = RidesDB.get(context!!)

        //GET RIDES
        val rideList = sRidesDB.getRidesDB()

        //CREATE ADAPTER
        mAdapter = RideAdapter(rideList)

        sButton = view.findViewById<Button>(R.id.start_ride_activity_button)
        sButton.setOnClickListener {
            val intent = Intent(activity, StartRideActivity::class.java)
            startActivity(intent)
        }

        eButton = view.findViewById<Button>(R.id.end_ride_activity_button)
        eButton.setOnClickListener {
            val intent = Intent(activity, EndRideActivity::class.java)
            startActivity(intent)
        }

        lButton = view.findViewById<Button>(R.id.list_rides_button)
        lButton.setOnClickListener {
            ride_recycle_view.layoutManager = LinearLayoutManager(context)
            ride_recycle_view.adapter = mAdapter
        }

        //API LEVEL
        levelAPI = view.findViewById(R.id.apilvl)
        levelAPI.text = ("API:" + Build.VERSION.SDK_INT)

        return view
    }

    private inner class RideAdapter(var mRides: List<Ride>) :
        RecyclerView.Adapter<RideViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup,
                                        viewType: Int): RideViewHolder {
            val layout = layoutInflater
                .inflate(R.layout.list_item_ride, parent, false)
            return RideViewHolder(layout)
        }
        override fun getItemCount() = mRides.size
        override fun onBindViewHolder(holder: RideViewHolder,
                                      position: Int) {
            val ride = mRides[position]
            holder.apply {
                mBikeName.text = ride.what
                mStartRide.text = ride.where
                mEndRide.text = ride.end
            }
        }
    }

    private inner class RideViewHolder(view: View) :
        RecyclerView.ViewHolder(view) {
        val mBikeName: TextView = view.findViewById(R.id.bike_ride)
        val mStartRide: TextView = view.findViewById(R.id.start_ride)
        val mEndRide: TextView = view.findViewById(R.id.end_ride)
    }
}
