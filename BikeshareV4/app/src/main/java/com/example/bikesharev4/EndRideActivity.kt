package com.example.bikesharev4

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText

class EndRideActivity : AppCompatActivity() {

    // GUI VARIABLES
    private lateinit var mLastRide:EditText
    private lateinit var mWhatText:EditText
    private lateinit var mWhereText:EditText
    private lateinit var mEndButton:Button

    private lateinit var mRide:Ride

    companion object {
        lateinit var sRidesDB: RidesDB
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_end_ride)

        mLastRide = findViewById(R.id.e_last_ride)

        // EDIT TEXTS
        mWhatText = findViewById(R.id.e_what_text)
        mWhereText = findViewById(R.id.e_where_text)

        // BUTTON
        mEndButton = findViewById(R.id.end_ride_button)

        //TODO change this stuff to fit
        // ADD RIDE CLICK EVENT
        mEndButton.setOnClickListener(View.OnClickListener {
            if (mWhatText.text.isNotEmpty() && mWhereText.text.isNotEmpty())
                mRide.setWhatBike(mWhatText.text.toString().trim())
            mRide.setWhereFrom(mWhereText.text.toString().trim())
            updateUI()
        })

        sRidesDB = RidesDB.get(this)
    }

    private fun updateUI(){mLastRide.setText(mRide.toString())}
}