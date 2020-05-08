package com.example.bikesharevx

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import io.realm.annotations.Required

open class RealmBike(
    @PrimaryKey var id: Int = 0,
    @Required var type: String = "",
    var picture: String = "",
    var price: Double = 0.0
) : RealmObject()