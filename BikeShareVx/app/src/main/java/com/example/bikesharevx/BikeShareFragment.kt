package com.example.bikesharevx

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
import io.realm.OrderedRealmCollection
import io.realm.Realm
import io.realm.RealmRecyclerViewAdapter
import io.realm.Sort
import kotlinx.android.synthetic.main.activity_bike_share.*
import kotlinx.android.synthetic.main.fragment_bike_share.*
import kotlinx.android.synthetic.main.list_item_ride.*
import org.w3c.dom.Text
import java.util.*

class BikeShareFragment : Fragment() {

    //GUI STUFF
    private lateinit var sButton: Button
    private lateinit var eButton: Button
    private lateinit var lButton: Button
    private lateinit var levelAPI: TextView
    private lateinit var rView: RecyclerView

    companion object {
        lateinit var mRealm: Realm
    }

    interface Callbacks {
        fun goToStart()
        fun goToEnd()
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
        mRealm = Realm.getDefaultInstance()

        val res = mRealm.where(RealmRide::class.java).findAll()
        rView = view.findViewById(R.id.ride_recycle_view)
        rView.layoutManager = LinearLayoutManager(activity)
        rView.adapter = RealmRideAdapter(res!!)

        sButton = view.findViewById<Button>(R.id.start_ride_activity_button)
        sButton.setOnClickListener {
            callbacks?.goToStart()
        }

        eButton = view.findViewById<Button>(R.id.end_ride_activity_button)
        eButton.setOnClickListener {
            callbacks?.goToEnd()
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
            val id = rride.bikeID
            val rideId = rride.id
            val combId = "B$id-R$rideId"
            holder.IDrideID.text = combId
            holder.bikeType.text = rride.bikeType
            holder.startLoc.text = rride.startLocation
            holder.startTime.text = rride.startTime
            holder.endLoc.text = rride.endLocation
            holder.endTime.text = rride.endTime

            holder.itemView.setOnClickListener {
                //
            }
        }
    }

    private inner class RealmRideHolder(view: View): RecyclerView.ViewHolder(view){
        val IDrideID: TextView = view.findViewById(R.id.id_ride_id)
        val bikeType: TextView = view.findViewById(R.id.bike_type)
        val startLoc: TextView = view.findViewById(R.id.start_location)
        val startTime: TextView = view.findViewById(R.id.start_time)
        val endLoc: TextView = view.findViewById(R.id.end_location)
        val endTime: TextView = view.findViewById(R.id.end_time)
    }

    override fun onDetach() {
        super.onDetach()
        callbacks = null
    }

}
