package com.example.porrinha_multiplayer.viewHolder

import android.content.Intent
import android.content.SharedPreferences
import android.location.Location
import androidx.recyclerview.widget.RecyclerView
import com.example.porrinha_multiplayer.GameActivity
import com.example.porrinha_multiplayer.LobbyActivity
import com.example.porrinha_multiplayer.databinding.RoomBinding
import com.example.porrinha_multiplayer.model.Player
import com.example.porrinha_multiplayer.model.Room
import com.example.porrinha_multiplayer.model.User
import com.example.porrinha_multiplayer.viewModel.GameViewModel

class RoomsViewHolder(private val binding: RoomBinding) :
    RecyclerView.ViewHolder(binding.root) {
    var roomName: String = ""
    var playersCount: String = "0"
    var distance: String = "0 km"
    var roomLocation = Location("")
    var actualUser = User("", 0.0, 0.0)
    lateinit var preferences: SharedPreferences

    init {
        binding.root.setOnClickListener { //Colocando o onclick listener em cada sala
            val c = binding.roomName.context
            val intent = Intent(c, GameActivity::class.java)
            intent.putExtra("roomName", roomName)
            setReferences()
            val editor = preferences.edit()
            editor.putString("roomName", roomName) // seta o valor user
            editor.apply() // adicionou o username na cache, qnd logar de novo ele vai estar lá
            c.startActivity(intent)
            val activity: LobbyActivity = c as LobbyActivity
            activity.finish()
        }
    }

    fun setUser(user: User) {
        actualUser = user
    }

    fun setReferences() { //Atualizando as referências para passar a informação de entrada na sala para o firebase
        GameViewModel.setPlayerReference(roomName, actualUser.username!!)
        GameViewModel.setPlayerReferenceValue(
            Player(
                actualUser.username!!,
                0,
                -1,
                3,
                false,
                false,
                true
            )
        ) //rooms/{roomName}/players/playerName
    }

    fun setCachePreferences(preferences: SharedPreferences) {
        this.preferences = preferences
    }

    fun bindTo(room: Room, location: Location) {
        //Fazendo o binding da sala no rooms linearlayout
        roomName = room.name
        if (room.players != null) {
            playersCount = room.players!!.size.toString()
        } else {
            playersCount = "0"
        }
        roomLocation.latitude = room.latitude
        roomLocation.longitude = room.longitude
        distance = (Math.floor(location.distanceTo(roomLocation) / 1000.0)).toString() + " km"

        binding.roomName.text = roomName
        binding.distance.text = distance
        binding.playersCount.text = playersCount
    }
}