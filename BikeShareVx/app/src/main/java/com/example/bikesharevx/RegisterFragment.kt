package com.example.bikesharevx


import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.graphics.Bitmap
import android.graphics.BitmapFactory
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

class RegisterFragment : Fragment() {

    // GUI VARIABLES
    private lateinit var mBikeType: EditText
    private lateinit var mBikePrice: EditText
    private lateinit var mRegisterButton: Button
    private lateinit var mImageButton: ImageButton
    private lateinit var mBackButton: Button
    private lateinit var rView: RecyclerView
    private var mLon: Double = 0.0
    private var mLat: Double = 0.0

    // PHOTO
    private lateinit var mListButton: Button
    private lateinit var mImageView: ImageView
    private lateinit var mPhotoFile: File
    private lateinit var mPhotoUri: Uri
    private val REQUEST_CONTACT = 1
    private val REQUEST_PHOTO = 2
    // private val DATE_FORMAT = "EEE, MM, dd"

    private var mBikeToAdd = RealmBike()

    private lateinit var filesDir: File
    fun getPhotoFile(r: RealmBike): File = File(filesDir, r.photoFileName)

    private val mPermissions: ArrayList<String> = ArrayList()
    private lateinit var mFusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var mLocationCallback: LocationCallback

    companion object {
        lateinit var mRealm: Realm
        private const val ALL_PERMISSIONS_RESULT = 1011
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
        val view = inflater.inflate(R.layout.fragment_register, container, false)
        filesDir = context!!.applicationContext.filesDir
        // PERMISSIONS (location)
        mPermissions.add(Manifest.permission.ACCESS_FINE_LOCATION)
        mPermissions.add(Manifest.permission.ACCESS_COARSE_LOCATION)
        val permissionsToRequest = permissionsToRequest(mPermissions)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (permissionsToRequest.size > 0) {
                requestPermissions(permissionsToRequest.toTypedArray(), ALL_PERMISSIONS_RESULT)
            }
        }
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context!!)
        mLocationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                for (location in locationResult.locations) {
                    mLon = location.longitude
                    mLat = location.latitude
                }
            }
        }
        startLocationUpdates()

        // REALM
        mRealm = Realm.getDefaultInstance()
        val res = mRealm.where(RealmBike::class.java).findAll()
        rView = view.findViewById(R.id.r_bike_recycle_view)
        rView.layoutManager = LinearLayoutManager(activity)
        rView.adapter = RealmBikeAdapter(res!!)

        mBikeType = view.findViewById<EditText>(R.id.r_type)
        mBikePrice = view.findViewById<EditText>(R.id.r_price)

        mImageView = view.findViewById(R.id.r_image_view)
        mImageButton = view.findViewById(R.id.r_image_button)
        mImageButton.apply {
            val packageManager: PackageManager = requireActivity().packageManager
            val captureImage = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            val resolvedActivity: ResolveInfo? = packageManager.resolveActivity(captureImage, PackageManager.MATCH_DEFAULT_ONLY)
            if (resolvedActivity == null) {
                isEnabled = false
            }
            setOnClickListener {
                mPhotoFile = File(context.applicationContext.filesDir, mBikeToAdd.photoFileName)
                mPhotoUri = FileProvider.getUriForFile(
                    requireActivity(),
                    "com.example.bikesharevx.fileprovider",
                    mPhotoFile
                )
                captureImage.putExtra(MediaStore.EXTRA_OUTPUT, mPhotoUri)
                val cameraActivities: List<ResolveInfo> = packageManager.queryIntentActivities(captureImage, PackageManager.MATCH_DEFAULT_ONLY)
                for (ca in cameraActivities) {
                    requireActivity().grantUriPermission(
                        ca.activityInfo.packageName,
                        mPhotoUri,
                        Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                    )
                }
                startActivityForResult(captureImage, REQUEST_PHOTO)
            }
        }

        mRegisterButton = view.findViewById(R.id.r_register_button)
        mRegisterButton.setOnClickListener {
            if(mBikeType.text.isNotEmpty() && mBikePrice.text.isNotEmpty()) {
                if (mBikePrice.text.contains(Regex("\\D$"))){
                    mBikePrice.setText("Numbers only!")
                } else {
                    val price = mBikePrice.text.toString().toDouble()
                    val pic = mBikeToAdd.picture
                    val gotPic: Boolean = pic != null

                    mRealm.executeTransactionAsync { realm ->
                        var maxID = realm.where(RealmBike::class.java).max("id")
                        if (maxID == null) {maxID = 0}

                        val newID = maxID.toInt()+1

                        val bike = RealmBike(newID,mBikeType.text.toString(), pic, price, mLon, mLat,true, gotPic)
                        realm.copyToRealm(bike)
                    }
                    mImageView.setImageBitmap(null)
                    mBikeType.setText("")
                    mBikePrice.setText("")
                }
            } else {
                mBikePrice.setText("Put the hourly price here!")
                mBikeType.setText("Put the type of bike here!")
                //
            }
        }

        mListButton = view.findViewById<Button>(R.id.r_list_button)
        mListButton.setOnClickListener {
            val results = mRealm.where(RealmBike::class.java).findAll()
            rView = view.findViewById(R.id.r_bike_recycle_view)
            rView.layoutManager = LinearLayoutManager(activity)
            rView.adapter = RealmBikeAdapter(results!!)
        }

        mBackButton = view.findViewById(R.id.r_back_button)
        mBackButton.setOnClickListener { callbacks?.goToBikeShare() }
        mBackButton.animate().x(0F).y(0F)

        return view
    }

    private inner class RealmBikeAdapter
        (data: OrderedRealmCollection<RealmBike>) :
        RealmRecyclerViewAdapter<RealmBike, RealmBikeHolder>(data, true) {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int)
                : RealmBikeHolder {
            val view = LayoutInflater.from(context)
                .inflate(R.layout.list_item_bike, parent, false)
            return RealmBikeHolder(view)
        }

        override fun onBindViewHolder(holder: RealmBikeHolder, position: Int) {
            val rbike = getItem(position)!!
            val type = rbike.type
            val id = rbike.id
            val price = rbike.price
            val lon = rbike.lon
            val lat = rbike.lat
            var address = getAddress(lon,lat)
            var stat = ""
            if (rbike.available) {stat = "Available"} else { stat = "Unavailable"}


            if (address == "NONE" || address == "") { address = "($lon $lat)"}
            holder.bikeText.text = " BikeID: $id $type, Price: $price/hour \n $stat - Currently at $address"

            if (rbike.gotPic) {
                holder.bikePic.setImageBitmap(BitmapFactory.decodeByteArray(rbike.picture, 0, rbike.picture!!.size))
            }


            holder.itemView.setOnClickListener {
                //
            }
        }
    }

    private inner class RealmBikeHolder(view: View): RecyclerView.ViewHolder(view){
        val bikePic = view.findViewById<ImageView>(R.id.bike_item_pic)
        val bikeText = view.findViewById<TextView>(R.id.bike_item_text)
    }

    private fun permissionsToRequest(
        permissions: ArrayList<String>): ArrayList<String> {
        val result: ArrayList<String> = ArrayList()
        for (permission in permissions)
            if (!hasPermission(permission))
                result.add(permission)
        return result
    }

    private fun hasPermission(permission: String) =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            activity?.checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED
        } else {
            true
        }

    private fun checkPermission() =
        ActivityCompat.checkSelfPermission(context!!,
            Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(context!!,
                    Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED

    @SuppressLint("MissingPermission")
    private fun startLocationUpdates() {
        if (checkPermission()) { return }
        val locationRequest = LocationRequest().apply {
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
            interval = 200
            fastestInterval = 100
        }
        mFusedLocationProviderClient.requestLocationUpdates(locationRequest, mLocationCallback, null)
    }
    private fun stopLocationUpdates() {
        mFusedLocationProviderClient
            .removeLocationUpdates(mLocationCallback)
    }

    private fun getAddress(longitude: Double, latitude: Double): String {
        val geocoder = Geocoder(context, Locale.getDefault())
        val stringBuilder = StringBuilder()
        try {
            val addresses: List<Address> = geocoder.getFromLocation(latitude, longitude, 1)
            if (addresses.isNotEmpty()) {
                val address: Address = addresses[0]
                stringBuilder.apply{
                    append(address.getAddressLine(0)).append("\n")
                    append(address.locality).append("\n")
                    append(address.postalCode).append("\n")
                    append(address.countryName)
                }
            } else return "NONE"
        } catch (ex: IOException) { ex.printStackTrace() }
        return stringBuilder.toString()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            REQUEST_PHOTO -> {
//                requireActivity().revokeUriPermission(mPhotoUri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
                updateBikePhoto()
            }
        }
    }

    private fun updateBikePhoto() {
        if (mPhotoFile.exists()) {
            val bitMap =
                getScaledBitMap(mPhotoFile.absolutePath, requireActivity())
            mImageView.setImageBitmap(bitMap)

            val stream = ByteArrayOutputStream()
            bitMap.compress(Bitmap.CompressFormat.PNG, 100, stream)
            val byteArray = stream.toByteArray()
            mBikeToAdd.picture = byteArray
        }
    }

    override fun onDetach() {
        super.onDetach()
        callbacks = null
        stopLocationUpdates()
//        requireActivity().revokeUriPermission(mPhotoUri,Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
    }
}