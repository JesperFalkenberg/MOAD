package com.example.bikesharev4

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.Fragment

class StartRideFragment : Fragment() {

    // GUI VARIABLES
    private lateinit var mLastRide:TextView
    private lateinit var mWhatText:EditText
    private lateinit var mWhereText:EditText
    private lateinit var mStartButton:Button

    private lateinit var mRide:Ride

    companion object {
        lateinit var sRidesDB: RidesDB
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?) :View? {
        val view = inflater.inflate(R.layout.fragment_start_end_ride, container, false)

        mLastRide = view.findViewById<TextView>(R.id.s_e_last_ride)
        mWhatText = view.findViewById<EditText>(R.id.s_e_what_text)
        mWhereText = view.findViewById<EditText>(R.id.s_e_where_text)
        mStartButton = view.findViewById<Button>(R.id.s_e_ride_button)

        mStartButton.apply { text = "Start Ride"; isEnabled = false }

        sRidesDB = activity?.let{RidesDB.get(it)}!!

        //TODO change this stuff to fit
        // ADD RIDE CLICK EVENT
        mStartButton.setOnClickListener(View.OnClickListener {
            if (mWhatText.text.isNotEmpty() && mWhereText.text.isNotEmpty()){
                sRidesDB.startRide(mWhatText.text.toString(),mWhereText.text.toString())
            }
            updateUI()
        })
        return view
    }

    private fun updateUI(){mLastRide.text = sRidesDB.getLastRideInfo()}
}