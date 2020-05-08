package com.example.bikesharevx

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import io.realm.Realm

class MainActivity: AppCompatActivity(), BikeShareFragment.Callbacks, EndRideFragment.Callbacks, StartRideFragment.Callbacks {

    companion object {
        lateinit var mRealm: Realm
    }



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        Realm.init(this)

        val curFragment = supportFragmentManager.findFragmentById(R.id.fragment_container)

        if(curFragment == null) {
            val fragment = BikeShareFragment()
            supportFragmentManager
                .beginTransaction()
                .add(R.id.fragment_container, fragment)
                .commit()
        }
    }

    override fun goToBikeShare() {
        val fragment = BikeShareFragment()
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }

    override fun goToStart() {
        val fragment = StartRideFragment()
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }

    override fun goToEnd() {
        val fragment = EndRideFragment()
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }
}