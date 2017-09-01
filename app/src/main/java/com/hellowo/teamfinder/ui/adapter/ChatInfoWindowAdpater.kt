package com.hellowo.teamfinder.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.Marker
import com.hellowo.teamfinder.R
import com.hellowo.teamfinder.ui.activity.MainActivity

class ChatInfoWindowAdpater(private val mInflater: LayoutInflater,
                            private val renderer: MainActivity.PersonRenderer) : GoogleMap.InfoWindowAdapter {
    override fun getInfoWindow(marker: Marker): View? {
        return null
    }

    override fun getInfoContents(marker: Marker): View {
        val popup = mInflater.inflate(R.layout.map_window_chat, null)
        renderer.getClusterItem(marker)?.let {
            popup.findViewById<TextView>(R.id.titleText).text = it.title
        }
        return popup
    }
}