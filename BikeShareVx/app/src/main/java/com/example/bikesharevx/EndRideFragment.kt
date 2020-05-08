package com.example.bikesharevx


import android.content.Context
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import io.realm.Realm
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class EndRideFragment : Fragment() {

    // GUI VARIABLES
    private lateinit var mBikeID: EditText
    private lateinit var mBikeLoc: EditText
    private lateinit var mEndButton: Button
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
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstance: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_start_end_ride, container, false)
        mRealm = Realm.getDefaultInstance()

        mBikeID = view.findViewById<EditText>(R.id.s_e_ID_text)
        mBikeLoc = view.findViewById<EditText>(R.id.s_e_location_text)
        mEndButton = view.findViewById<Button>(R.id.s_e_ride_button)

        mEndButton.apply { text = "End Ride"; isEnabled = true }


        mEndButton.setOnClickListener {
            mRealm.executeTransactionAsync { realm ->
                val bikeID: String = mBikeID.text.toString()
                val bikeLoc: String = mBikeLoc.text.toString()
                if (bikeID.isNotEmpty() && bikeLoc.isNotEmpty()) {
                    var openRide =
                        realm.where(RealmRide::class.java).equalTo("bikeID", bikeID.toInt()).and()
                            .equalTo("endLocation", "").findFirst()

                    if (openRide != null) {
                        openRide?.endLocation = bikeLoc
                        openRide?.endTime = LocalDateTime.now()
                            .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")).toString()
                        realm.copyToRealm(openRide!!)
                    } else {
                        // TODO: POPUP DU ER EN NAR
                    }

                    mBikeID.setText("")
                    mBikeLoc.setText("")
                }
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