package com.example.bikesharevx

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import io.realm.annotations.Required

open class RealmBike(
    @PrimaryKey var id: Int = 0,
    @Required var type: String = "",
    var picture: ByteArray? = null,
    var price: Double = 0.0,
    var lon: Double = 0.0,
    var lat: Double = 0.0,
    var available: Boolean = true,
    var gotPic: Boolean = true
) : RealmObject() {
    val photoFileName get() = "IMG_$id.jpg"
}