package com.example.porrinha_multiplayer.viewHolder

import android.content.SharedPreferences
import android.location.Location
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.porrinha_multiplayer.databinding.RoomBinding
import com.example.porrinha_multiplayer.model.Room
import com.example.porrinha_multiplayer.model.User

class RoomsAdapter(
    private val rooms: MutableList<Room>,
    private val user: User,
    private val preferences: SharedPreferences,
    private val inflater: LayoutInflater
) :
    RecyclerView.Adapter<RoomsViewHolder>() {
    override fun getItemCount(): Int = rooms.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RoomsViewHolder {
        val binding = RoomBinding.inflate(inflater, parent, false)
        return RoomsViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RoomsViewHolder, position: Int) {
        var location = Location("")
        location.longitude = user.longitude!!
        location.latitude = user.latitude!!
        holder.bindTo(rooms[position], location)
        holder.setUser(user)
        holder.setCachePreferences(preferences)
    }
}