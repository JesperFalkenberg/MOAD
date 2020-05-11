package com.example.bikesharevx

import io.realm.RealmObject

open class RealmFunds (
    var amount: Double = 0.0
) : RealmObject()