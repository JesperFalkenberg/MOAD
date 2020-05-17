package com.example.bikesharevx


import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.graphics.Bitmap
import android.location.Address
import android.location.Geocoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.location.*
import io.realm.OrderedRealmCollection
import io.realm.Realm
import io.realm.RealmRecyclerViewAdapter
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.IOException
import java.util.*

class FundsFragment : Fragment() {

    // GUI VARIABLES
    private lateinit var mCurFunds: TextView
    private lateinit var mFundsToAdd: EditText
    private lateinit var mFundsButton: Button
    private lateinit var mBackButton: Button
    private var fundsAmount: Double = 0.0

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
        val view = inflater.inflate(R.layout.fragment_funds, container, false)

        mRealm = Realm.getDefaultInstance()

        var funds: RealmFunds? = RealmFunds(0.0)
        mRealm.executeTransaction{ realm: Realm ->
            funds = realm.where(RealmFunds::class.java).findFirst()
        }
        if (funds == null) {funds = RealmFunds(0.0)}

        mCurFunds = view.findViewById(R.id.f_cur_funds)
        mCurFunds.setText(funds!!.amount.toString())
        fundsAmount = funds!!.amount

        mFundsToAdd = view.findViewById(R.id.f_funds)

        mFundsButton = view.findViewById(R.id.f_button)
        mFundsButton.setOnClickListener {
            if (mFundsToAdd.text.toString() == "" || mFundsToAdd.text.contains(Regex("\\D$"))){
                mFundsToAdd.setText("Input an amount in numbers.")
            } else {
                fundsAmount += mFundsToAdd.text.toString().toDouble()
                mCurFunds.setText(fundsAmount.toString())
                mFundsToAdd.setText("")
            }
        }

        mBackButton = view.findViewById(R.id.f_back_button)
        mBackButton.setOnClickListener { callbacks?.goToBikeShare() }
        mBackButton.animate().x(0F).y(0F)

        return view
    }

    override fun onDetach() {
        super.onDetach()
        callbacks = null
        fundsAmount = mCurFunds.text.toString().toDouble()
        mRealm.executeTransaction { realm ->
            var funds: RealmFunds? = realm.where(RealmFunds::class.java).findFirst()
            if (funds == null) {funds = RealmFunds(fundsAmount)} else {funds!!.amount = fundsAmount}
            realm.copyToRealm(funds)
        }
    }
}