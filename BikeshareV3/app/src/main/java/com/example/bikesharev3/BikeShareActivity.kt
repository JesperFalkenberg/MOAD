package com.example.bikesharev3

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button

class BikeShareActivity : AppCompatActivity() {

    private lateinit var sButton: Button
    private lateinit var  eButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bike_share)

        // FINDING BUTTONS
        sButton = findViewById(R.id.start_ride_activity_button)
        eButton = findViewById(R.id.end_ride_activity_button)

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
    }


}
