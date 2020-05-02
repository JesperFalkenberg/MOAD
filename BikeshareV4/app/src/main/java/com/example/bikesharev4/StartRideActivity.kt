package com.example.bikesharev4

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView

class StartRideActivity : AppCompatActivity() {

        // GUI VARIABLES
        private lateinit var mLastRide:TextView
        private lateinit var mWhatText:EditText
        private lateinit var mWhereText:EditText
        private lateinit var mStartButton:Button

        private lateinit var mRide:Ride

        companion object {
            lateinit var sRidesDB: RidesDB
        }

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            setContentView(R.layout.activity_start_ride)

            mLastRide = findViewById(R.id.s_last_ride)

            // EDIT TEXTS
            mWhatText = findViewById(R.id.s_what_text)
            mWhereText = findViewById(R.id.s_where_text)

            // BUTTON
            mStartButton = findViewById(R.id.start_ride_button)

            sRidesDB = RidesDB.get(this)

            //TODO change this stuff to fit
            // ADD RIDE CLICK EVENT
            mStartButton.setOnClickListener(View.OnClickListener {
                if (mWhatText.text.isNotEmpty() && mWhereText.text.isNotEmpty()){
                    sRidesDB.startRide(mWhatText.text.toString(),mWhereText.text.toString())
                }
                updateUI()
            })

        }

        private fun updateUI(){mLastRide.text = sRidesDB.getLastRideInfo()}
    }