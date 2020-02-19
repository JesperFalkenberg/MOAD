package com.example.bikesharev3

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText

class StartRideActivity : AppCompatActivity() {

        // GUI VARIABLES
        private lateinit var mLastRide:EditText
        private lateinit var mWhatText:EditText
        private lateinit var mWhereText:EditText
        private lateinit var mStartButton:Button


        private lateinit var mRide:Ride

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            setContentView(R.layout.activity_bike_share)

            mLastRide = findViewById(R.id.last_ride)

            // EDIT TEXTS
            mWhatText = findViewById(R.id.what_text)
            mWhereText = findViewById(R.id.where_text)

            // BUTTON
            mStartButton = findViewById(R.id.start_ride_button)

            //TODO change this stuff to fit
            // ADD RIDE CLICK EVENT
            mStartButton.setOnClickListener(View.OnClickListener {
                if (mWhatText.text.isNotEmpty() && mWhereText.text.isNotEmpty())
                    mRide.setWhatBike(mWhatText.text.toString().trim())
                mRide.setWhereFrom(mWhereText.text.toString().trim())
                updateUI()
            })

        }

        private fun updateUI(){mLastRide.setText(mRide.toString())}
    }