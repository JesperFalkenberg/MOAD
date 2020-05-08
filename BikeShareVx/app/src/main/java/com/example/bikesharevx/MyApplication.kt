package com.example.bikesharevx

import android.app.Application
import io.realm.Realm
import io.realm.RealmConfiguration

class MyApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        Realm.init(applicationContext)
        val config = RealmConfiguration.Builder()
            .name("realm_database.realm"
            )
            .schemaVersion(
                1
            )
            .deleteRealmIfMigrationNeeded()
            .build()
        Realm.setDefaultConfiguration(config)
    }

    override fun onTerminate() {
        Realm.getDefaultInstance().close()
        super.onTerminate()
    }
}
