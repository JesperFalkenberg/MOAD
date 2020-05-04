package com.example.bikesharev4

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.Fragment

class EndRideFragment : Fragment() {

    // GUI VARIABLES
    private lateinit var mLastRide:TextView
    private lateinit var mWhatText:EditText
    private lateinit var mWhereText:EditText
    private lateinit var mEndButton:Button

    private lateinit var mRide:Ride

    companion object {
        lateinit var sRidesDB: RidesDB
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstance: Bundle?) :View? {
        val view = inflater.inflate(R.layout.fragment_start_end_ride, container, false)

        mLastRide = view.findViewById<EditText>(R.id.s_e_last_ride)
        mWhatText = view.findViewById<EditText>(R.id.s_e_what_text)
        mWhereText = view.findViewById<EditText>(R.id.s_e_where_text)
        mEndButton = view.findViewById<Button>(R.id.s_e_ride_button)

        mEndButton.apply { text = "End Ride"; isEnabled = false }

        sRidesDB = activity?.let{RidesDB.get(it)}!!

        //TODO change this stuff to fit
        // ADD RIDE CLICK EVENT
        mEndButton.setOnClickListener(View.OnClickListener {
            if (mWhatText.text.isNotEmpty() && mWhereText.text.isNotEmpty()) {
                sRidesDB.endRide(mWhatText.text.toString(),mWhereText.text.toString())
            }
            updateUI()
        })

        return view
    }

    private fun updateUI(){mLastRide.text = sRidesDB.getLastRideInfo()}
}