package com.example.bikesharev4

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView
import kotlinx.android.synthetic.main.list_item_ride.*

class BikeShareActivity : AppCompatActivity() {

    private lateinit var sButton: Button
    private lateinit var  eButton: Button

    private lateinit var  listView: ListView

    companion object {
        lateinit var sRidesDB: RidesDB
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bike_share)

        // FINDING ITEMS
        sButton = findViewById(R.id.start_ride_activity_button)
        eButton = findViewById(R.id.end_ride_activity_button)
        listView = findViewById(R.id.ride_list)

        // START_BUTTON CHANGE PAGE
        sButton.setOnClickListener(View.OnClickListener {
            // START START_RIDE_ACTIVITY
            val intent = Intent(this, StartRideActivity::class.java)
            startActivity(intent)
        })

        // END_BUTTON CHANGE PAGE
        eButton.setOnClickListener(View.OnClickListener {
            // START END_RIDE_ACTIVITY
            val intent = Intent(this, EndRideActivity::class.java)
            startActivity(intent)
        })

        // GET RIDES_DB
        sRidesDB = RidesDB.get(this)

        // POPULATE RIDELIST
        val rideList = sRidesDB.getRidesDB()
        var listItems = arrayOfNulls<String>(rideList.size)
        for(i in 0 until rideList.size){
            val ride = rideList[i]
            listItems[i] = ride.toString()
        }
        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, listItems)
        listView.adapter = adapter
    }


}
