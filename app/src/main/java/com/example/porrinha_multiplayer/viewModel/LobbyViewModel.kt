package com.example.porrinha_multiplayer.viewModel

import com.example.porrinha_multiplayer.model.Player
import com.example.porrinha_multiplayer.model.Room
import com.example.porrinha_multiplayer.repository.FirebaseRepository
import com.google.firebase.database.DatabaseReference

object LobbyViewModel {
    fun setRoomReference(roomName: String) {
        roomRef = FirebaseRepository.getReference("rooms/$roomName")
    }

    fun setRoomsReference() {
        roomsRef = FirebaseRepository.getReference("rooms")
    }

    fun initRoom(latitude: Double, longitude: Double, name: String, maxPlayers: Int) {
        FirebaseRepository.setValue(roomRef, Room(1,-1,3, emptyMap<String,Player>(), latitude, longitude, name, maxPlayers, false))
    }

    lateinit var roomsRef: DatabaseReference
    lateinit var roomRef: DatabaseReference
}