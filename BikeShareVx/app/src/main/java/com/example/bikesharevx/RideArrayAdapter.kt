package com.example.bikesharevx


import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView

class RideArrayAdapter(context: Context, rides: List<Ride>) :
    ArrayAdapter<Ride>(context, R.layout.list_item_ride, rides) {
    // View holder is used to prevent findViewById calls.
    private class RideItemViewHolder {
        internal var bikeName: TextView? = null
        internal var startRide: TextView? = null
        internal var endRide: TextView? = null
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var view = convertView
        val viewHolder: RideItemViewHolder

        if (view == null) {
            val inflater = LayoutInflater.from(context)
            view = inflater.inflate(R.layout.list_item_ride, parent, false)
            viewHolder = RideItemViewHolder()
/*
            viewHolder.bikeName = view.findViewById<View>(R.id.bike_ride) as TextView
            viewHolder.startRide = view.findViewById<View>(R.id.start_ride) as TextView
            viewHolder.endRide = view.findViewById<View>(R.id.end_ride) as TextView
 */
        } else {
            viewHolder = view.tag as RideItemViewHolder
        }

        val ride = getItem(position)
        viewHolder.bikeName!!.text = ride?.what
        viewHolder.startRide!!.text = ride?.where
        viewHolder.endRide!!.text = ride?.end
        view?.tag = viewHolder
        return view!!
    }
}