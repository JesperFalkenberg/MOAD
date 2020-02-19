package com.example.bikeshare

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText

class BikeShareActivity : AppCompatActivity() {

    // GUI VARIABLES
    private lateinit var mLastRide:EditText
    private lateinit var mWhatText:EditText
    private lateinit var mWhereText:EditText
    private lateinit var mAddButton:Button


    private var mRide:Ride = Ride("","")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bike_share)

        mLastRide = findViewById(R.id.last_ride)

        // EDIT TEXTS
        mWhatText = findViewById(R.id.what_text)
        mWhereText = findViewById(R.id.where_text)

        // BUTTON
        mAddButton = findViewById(R.id.add_button)

        // ADD RIDE CLICK EVENT
        mAddButton.setOnClickListener(View.OnClickListener {
            if (mWhatText.text.isNotEmpty() && mWhereText.text.isNotEmpty())
                mRide.setWhatBike(mWhatText.text.toString().trim())
                mRide.setWhereFrom(mWhereText.text.toString().trim())
                updateUI()
        })

    }

    private fun updateUI(){mLastRide.setText(mRide.toString())}
}
