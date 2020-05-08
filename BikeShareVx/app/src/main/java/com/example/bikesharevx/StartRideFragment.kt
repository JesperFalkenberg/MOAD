package com.example.bikesharevx


import android.content.Context
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import io.realm.Realm
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class StartRideFragment : Fragment() {

    // GUI VARIABLES
    private lateinit var mBikeID:EditText
    private lateinit var mBikeLoc:EditText
    private lateinit var mStartButton:Button
    private lateinit var mBackButton: Button

    companion object {
        lateinit var mRealm: Realm
    }

    interface Callbacks {
        fun goToBikeShare()
    }
    private var callbacks: Callbacks? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        callbacks = context as Callbacks?
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?) :View? {
        val view = inflater.inflate(R.layout.fragment_start_end_ride, container, false)
        mRealm = Realm.getDefaultInstance()

        mBikeID = view.findViewById<EditText>(R.id.s_e_ID_text)
        mBikeLoc = view.findViewById<EditText>(R.id.s_e_location_text)
        mStartButton = view.findViewById<Button>(R.id.s_e_ride_button)

        mStartButton.apply { text = "Start Ride"; isEnabled = true }

        mStartButton.setOnClickListener{
            mRealm.executeTransactionAsync{ realm ->
                // TODO
                val bikeID = mBikeID.text.toString().toInt()
                val bikeLoc = mBikeLoc.text.toString()

                var maxID = realm.where(RealmRide::class.java).max("id")
                if (maxID == null) {maxID = 0}

                val bike = realm.where(RealmBike::class.java).equalTo("id",bikeID).findFirst()
                var bikeType = bike?.type
                if(bikeType == null){bikeType = ""}

                val startTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")).toString()
                val newRide = RealmRide((maxID.toInt()+1), bikeID, bikeType, bikeLoc ,startTime, "", "" )

                realm.copyToRealm(newRide)

                mBikeID.setText("")
                mBikeLoc.setText("")
            }
        }

        mBackButton = view.findViewById(R.id.back_button)
        mBackButton.setOnClickListener { callbacks?.goToBikeShare() }
        mBackButton.animate().x(0F).y(0F)

        return view
    }

    override fun onDetach() {
        super.onDetach()
        callbacks = null
    }
}