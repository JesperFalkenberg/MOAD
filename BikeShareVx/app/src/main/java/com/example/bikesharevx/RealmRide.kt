package com.example.bikesharevx

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import io.realm.annotations.Required

open class RealmRide(
    @PrimaryKey var id: Int = 0,
    var bikeID: Int = 0,
    var bikeType: String = "",
    @Required var startLocation: String = "",
    @Required var startTime: String = "",
    var endLocation: String = "",
    var endTime: String = ""
) : RealmObject()